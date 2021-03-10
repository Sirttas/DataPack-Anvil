package sirttas.dpanvil.api.tag;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.IResourceType;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import net.minecraftforge.common.extensions.IForgeTagBuilder;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;

public abstract class DataTagsProvider<T> implements IDataProvider {
	
	   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	   
	   protected final DataGenerator generator;
	   protected final IDataManager<T> manager;
	   protected final Map<ResourceLocation, ITag.Builder> tagToBuilder = Maps.newLinkedHashMap();
	   protected final String modId;
	   protected final ExistingFileHelper existingFileHelper;
	   private final IResourceType resourceType;

	   protected DataTagsProvider(DataGenerator generatorIn, IDataManager<T> manager, String modId, @javax.annotation.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
	      this.generator = generatorIn;
	      this.manager = manager;
	      this.modId = modId;
	      this.existingFileHelper = existingFileHelper;
	      this.resourceType = new ResourceType(net.minecraft.resources.ResourcePackType.SERVER_DATA, ".json", DataPackAnvilApi.TAGS_FOLDER + getTagFolder());
	   }

	   protected abstract void registerTags();

	   /**
	    * Performs this provider's action.
	    */
	   @Override
	public void act(DirectoryCache cache) {
	      this.tagToBuilder.clear();
	      this.registerTags();
	      ITag<T> itag = Tag.getEmptyTag();
	      Function<ResourceLocation, ITag<T>> function = key -> this.tagToBuilder.containsKey(key) ? itag : null;
	      Function<ResourceLocation, T> function1 = key -> this.manager.getOptional(key).orElse((T)null);
	      
	      this.tagToBuilder.forEach((tagName, builder) -> {
	         List<ITag.Proxy> list = builder.getProxyTags(function, function1).filter(this::missing).collect(Collectors.toList());
	         if (!list.isEmpty()) {
	            throw new IllegalArgumentException(String.format("Couldn't define tag %s as it is missing following references: %s", tagName, list.stream().map(Objects::toString).collect(Collectors.joining(","))));
	         } else {
	            JsonObject jsonobject = builder.serialize();
	            Path path = this.makePath(tagName);
	            if (path == null) return; 

	            try {
	               String s = GSON.toJson(jsonobject);
	               String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
	               if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
	                  Files.createDirectories(path.getParent());

	                  try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
	                     bufferedwriter.write(s);
	                  }
	               }

	               cache.recordHash(path, s1);
	            } catch (IOException ioexception) {
	               DataPackAnvilApi.LOGGER.error("Couldn't save tags to {}", path, ioexception);
	            }

	         }
	      });
	   }

	   private boolean missing(ITag.Proxy reference) {
	      ITag.ITagEntry entry = reference.getEntry();
	      if (entry instanceof ITag.TagEntry) {
	         return existingFileHelper == null || !existingFileHelper.exists(((ITag.TagEntry)entry).getId(), resourceType);
	      }
	      return false;
	   }

	   protected String getTagFolder() {
	      return manager.getFolder();
	   }

	   /**
	    * Resolves a Path for the location to save the given tag.
	    */
	   protected Path makePath(ResourceLocation id) {
		   return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/" + DataPackAnvilApi.TAGS_FOLDER + getTagFolder() + "/" + id.getPath() + ".json");
	   }

	   protected Builder getOrCreateBuilder(ITag.INamedTag<T> tag) {
	      return new Builder(this.createBuilderIfAbsent(tag), modId);
	   }

	   protected ITag.Builder createBuilderIfAbsent(ITag.INamedTag<T> tag) {
	      return this.tagToBuilder.computeIfAbsent(tag.getName(), key -> {
	         existingFileHelper.trackGenerated(key, resourceType);
	         return new ITag.Builder();
	      });
	   }

	   public class Builder implements IForgeTagBuilder<T> {
	      private final ITag.Builder builder;
	      private final String id;

	      private Builder(ITag.Builder builder, String id) {
	         this.builder = builder;
	         this.id = id;
	      }

	      public Builder addItemEntry(T item) {
	         this.builder.addItemEntry(manager.getId(item), this.id);
	         return this;
	      }

	      public Builder addTag(ITag.INamedTag<T> tag) {
	         this.builder.addTagEntry(tag.getName(), this.id);
	         return this;
	      }

	      @SafeVarargs
	      public final Builder add(T... toAdd) {
	         Stream.of(toAdd).map(manager::getId).forEach(key -> this.builder.addItemEntry(key, this.id));
	         return this;
	      }
	      
	      @SafeVarargs
	      public final Builder add(ResourceLocation... toAdd) {
	         Stream.of(toAdd).forEach(key -> this.builder.addItemEntry(key, this.id));
	         return this;
	      }

	      public Builder add(ITag.ITagEntry tag) {
	          builder.addTag(tag, id);
	          return this;
	      }

	      public ITag.Builder getInternalBuilder() {
	          return builder;
	      }

	      public String getModID() {
	          return id;
	      }
	   }
	}
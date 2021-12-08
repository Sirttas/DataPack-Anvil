package sirttas.dpanvil.api.tag;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.IResourceType;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import net.minecraftforge.common.extensions.IForgeTagAppender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractDataTagsProvider<T> implements DataProvider {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

	protected final DataGenerator generator;
	protected final Map<ResourceLocation, Tag.Builder> builders = Maps.newLinkedHashMap();
	protected final String modId;
	protected final String folder;
	protected final ExistingFileHelper existingFileHelper;
	private final IResourceType resourceType;
	private final IDataManager<T> manager;

	protected AbstractDataTagsProvider(DataGenerator generator, IDataManager<T> manager, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		this(generator, manager, modId, existingFileHelper, null);
	}

	protected AbstractDataTagsProvider(DataGenerator generator, IDataManager<T> manager, String modId, @Nullable ExistingFileHelper existingFileHelper, @Nullable String folder) {
		this.generator = generator;
		this.manager = manager;
		this.modId = modId;
		this.existingFileHelper = existingFileHelper;
		if (folder == null) {
			folder = manager.getFolder();
		}
		this.folder = folder;
		this.resourceType = new ResourceType(PackType.SERVER_DATA, ".json", DataPackAnvilApi.TAGS_FOLDER + this.folder);
	}

	protected abstract void addTags();

	@Override
	public void run(@NotNull HashCache cache) {
		this.builders.clear();
		this.addTags();
		this.builders.forEach((location, builder) -> {
			List<Tag.BuilderEntry> list = builder.getEntries()
					.filter(entry -> !entry.getEntry().verifyIfPresent(manager.getData()::containsKey, this.builders::containsKey))
					.filter(this::missing)
					.toList();
			
			if (!list.isEmpty()) {
				throw new IllegalArgumentException(
						String.format("Couldn't define tag %s as it is missing following references: %s", location, list.stream().map(Objects::toString).collect(Collectors.joining(","))));
			} else {
				JsonObject jsonobject = builder.serializeToJson();
				Path path = this.getPath(location);
				if (path == null) {
					return;
				}
				try {
					String s = GSON.toJson(jsonobject);
					String s1 = SHA1.hashUnencodedChars(s).toString();
					
					if (!Objects.equals(cache.getHash(path), s1) || !Files.exists(path)) {
						Files.createDirectories(path.getParent());

						try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
							bufferedwriter.write(s);
						}
					}

					cache.putNew(path, s1);
				} catch (IOException ioexception) {
					LOGGER.error("Couldn't save tags to {}", path, ioexception);
				}

			}
		});
	}

	private boolean missing(Tag.BuilderEntry reference) {
		Tag.Entry entry = reference.getEntry();
		// We only care about non-optional tag entries, this is the only type that can
		// reference a resource and needs validation
		// Optional tags should not be validated
		if (entry instanceof Tag.TagEntry tagEntry) {
			return existingFileHelper == null || !existingFileHelper.exists(tagEntry.getId(), resourceType);
		}
		return false;
	}

	protected Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/" + DataPackAnvilApi.TAGS_FOLDER + folder + "/" + id.getPath() + ".json");
	}

	protected DataTagAppender tag(Tag.Named<T> tag) {
		Tag.Builder tagBuilder = this.getOrCreateRawBuilder(tag);

		return new DataTagAppender(tagBuilder, modId);
	}

	protected Tag.Builder getOrCreateRawBuilder(Tag.Named<T> tag) {
		return this.builders.computeIfAbsent(tag.getName(), id -> {
			existingFileHelper.trackGenerated(id, resourceType);
			return new Tag.Builder();
		});
	}

	public class DataTagAppender implements IForgeTagAppender<T> {
		private final Tag.Builder tagBuilder;
		private final String id;

		private DataTagAppender(Tag.Builder builder, String id) {
			this.tagBuilder = builder;
			this.id = id;
		}

		public DataTagAppender addItemEntry(T item) {
			this.tagBuilder.addElement(manager.getId(item), this.id);
			return this;
		}

		public DataTagAppender addTag(Tag.Named<T> tag) {
			this.tagBuilder.addTag(tag.getName(), this.id);
			return this;
		}

		@SafeVarargs
		public final DataTagAppender add(T... toAdd) {
			Stream.of(toAdd).map(manager::getId).forEach(key -> this.tagBuilder.addElement(key, this.id));
			return this;
		}

		@SafeVarargs
		public final DataTagAppender add(ResourceLocation... toAdd) {
			Stream.of(toAdd).forEach(key -> this.tagBuilder.addElement(key, this.id));
			return this;
		}

		public DataTagAppender add(Tag.Entry tag) {
			tagBuilder.add(tag, id);
			return this;
		}

		public Tag.Builder getInternalBuilder() {
			return tagBuilder;
		}

		public String getModID() {
			return id;
		}
	}
}

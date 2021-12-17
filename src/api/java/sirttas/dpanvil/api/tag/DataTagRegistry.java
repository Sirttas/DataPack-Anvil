package sirttas.dpanvil.api.tag;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Named;
import net.minecraft.tags.TagCollection;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class DataTagRegistry<T> {

	private TagCollection<T> collection;

	private final List<DataTag> tags = Lists.newArrayList();
	
	public Named<T> makeWrapperTag(ResourceLocation id) {
		return tags.stream().filter(tag -> tag.id.equals(id)).findAny().orElseGet(() -> {
			DataTag tag = new DataTag(id);
			
			tags.add(tag);
			return tag;
		});
	}
	
	public Tag<T> getTag(ResourceLocation id) {
		return collection.getTag(id);
	}
	
	public Lazy<Tag<T>> getLazyTag(ResourceLocation id) {
		return Lazy.of(() -> collection.getTag(id));
	}
	
	public Optional<Tag<T>> getOptionalTag(ResourceLocation id) {
		return Optional.ofNullable(collection.getTag(id));
	}
	
	public void setCollection(TagCollection<T> collection) {
		this.collection = collection;
		tags.forEach(tag -> tag.refresh(collection));
	}
	
	private class DataTag implements Named<T> {

		private final ResourceLocation id;
		private Tag<T> containedTag;
		
		private DataTag(ResourceLocation id) {
			this.id = id;
			containedTag = null;
		}
		
		private void refresh(TagCollection<T> collection) {
			containedTag = collection.getTag(id);
		}
		
		@Override
		public boolean contains(@Nonnull T element) {
			if (this.containedTag != null) {
				return containedTag.contains(element);
			}
			return false;
		}

		@Override
		public @Nonnull List<T> getValues() {
			if (this.containedTag != null) {
				return containedTag.getValues();
			}
			return Collections.emptyList();
		}

		@Override
		public @Nonnull ResourceLocation getName() {
			return id;
		}
		
	}
}

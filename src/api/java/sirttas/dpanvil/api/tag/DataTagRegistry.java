package sirttas.dpanvil.api.tag;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;

public final class DataTagRegistry<T> {

	private ITagCollection<T> collection;

	private final List<Tag> tags = Lists.newArrayList();
	
	/**
	 * @deprecated use {@link makeWrapperTag}
	 */
	@Deprecated
	public INamedTag<T> createWrapperTag(ResourceLocation id) {
		return makeWrapperTag(id);
	}
	
	public INamedTag<T> makeWrapperTag(ResourceLocation id) {
		return tags.stream().filter(tag -> tag.id.equals(id)).findAny().orElseGet(() -> {
			Tag tag = new Tag(id);
			
			tags.add(tag);
			return tag;
		});
	}
	
	public ITag<T> getTag(ResourceLocation id) {
		return collection.get(id);
	}
	
	public Lazy<ITag<T>> getLazyTag(ResourceLocation id) {
		return Lazy.of(() -> collection.get(id));
	}
	
	public Optional<ITag<T>> getOptionalTag(ResourceLocation id) {
		return Optional.ofNullable(collection.get(id));
	}
	
	public void setCollection(ITagCollection<T> collection) {
		this.collection = collection;
		tags.forEach(tag -> tag.refresh(collection));
	}
	
	private class Tag implements INamedTag<T> {

		private final ResourceLocation id;
		private ITag<T> containedTag;
		
		private Tag(ResourceLocation id) {
			this.id = id;
			containedTag = null;
		}
		
		private void refresh(ITagCollection<T> collection) {
			containedTag = collection.get(id);
		}
		
		@Override
		public boolean contains(T element) {
			if (this.containedTag != null) {
				return containedTag.contains(element);
			}
			return false;
		}

		@Override
		public List<T> getAllElements() {
			if (this.containedTag != null) {
				return containedTag.getAllElements();
			}
			return Collections.emptyList();
		}

		@Override
		public ResourceLocation getName() {
			return id;
		}
		
	}
}

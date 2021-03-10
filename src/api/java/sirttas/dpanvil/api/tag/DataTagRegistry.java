package sirttas.dpanvil.api.tag;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;

public final class DataTagRegistry<T> {

	private ITagCollection<T> collection;

	private final List<Tag> tags = Lists.newArrayList();
	
	public INamedTag<T> createWrapperTag(ResourceLocation id) {
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
		
		private ITag<T> getTag() {
			if (this.containedTag == null) {
				throw new IllegalStateException("Tag " + this.id + " used before it was bound");
			} else {
				return this.containedTag;
			}
		}
		
		private void refresh(ITagCollection<T> collection) {
			containedTag = collection.get(id);
		}
		
		@Override
		public boolean contains(T element) {
			return getTag().contains(element);
		}

		@Override
		public List<T> getAllElements() {
			return getTag().getAllElements();
		}

		@Override
		public ResourceLocation getName() {
			return id;
		}
		
	}
}

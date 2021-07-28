package sirttas.dpanvil.data.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.codec.CodecHelper;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.tag.DataTagManager;

public class TagsMessage {

	private final Tag tagNBT;

	public TagsMessage() {
		this(CodecHelper.encode(DataPackAnvil.DATA_TAG_MANAGER.getCodec(), NbtOps.INSTANCE, DataPackAnvil.DATA_TAG_MANAGER.getData()));
	}
	
	public TagsMessage(Tag tagNBT) {
		this.tagNBT = tagNBT;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt((CompoundTag) tagNBT);
	}
	
	public static TagsMessage decode(FriendlyByteBuf buf) {
		return new TagsMessage(buf.readNbt());
	}

	public void process() {
		try {
			DataPackAnvil.DATA_TAG_MANAGER.setData(CodecHelper.decode(DataPackAnvil.DATA_TAG_MANAGER.getCodec(), tagNBT));
		} catch (Exception e) {
			DataManagerWrapper.logManagerException(DataTagManager.ID, e);
		}
	}
	
}

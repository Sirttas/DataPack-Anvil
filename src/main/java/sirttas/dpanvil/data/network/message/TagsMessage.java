package sirttas.dpanvil.data.network.message;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.codec.CodecHelper;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.tag.DataTagManager;

public class TagsMessage {

	private final INBT tagNBT;

	public TagsMessage() {
		this(CodecHelper.encode(DataPackAnvil.DATA_TAG_MANAGER.getCodec(), NBTDynamicOps.INSTANCE, DataPackAnvil.DATA_TAG_MANAGER.getData()));
	}
	
	public TagsMessage(INBT tagNBT) {
		this.tagNBT = tagNBT;
	}

	public void encode(PacketBuffer buf) {
		buf.writeNbt((CompoundNBT) tagNBT);
	}
	
	public static TagsMessage decode(PacketBuffer buf) {
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

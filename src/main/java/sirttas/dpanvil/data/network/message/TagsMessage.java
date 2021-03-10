package sirttas.dpanvil.data.network.message;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.codec.CodecHelper;

public class TagsMessage {

	private final INBT tagNBT;

	public TagsMessage() {
		this(CodecHelper.encode(DataPackAnvil.DATA_TAG_MANAGER.getCodec(), NBTDynamicOps.INSTANCE, DataPackAnvil.DATA_TAG_MANAGER.getData()));
	}
	
	public TagsMessage(INBT tagNBT) {
		this.tagNBT = tagNBT;
	}

	public void encode(PacketBuffer buf) {
		buf.writeCompoundTag((CompoundNBT) tagNBT);
	}
	
	public static TagsMessage decode(PacketBuffer buf) {
		return new TagsMessage(buf.readCompoundTag());
	}

	public void process() {
		DataPackAnvil.DATA_TAG_MANAGER.setData(CodecHelper.decode(DataPackAnvil.DATA_TAG_MANAGER.getCodec(), tagNBT));
	}
	
}

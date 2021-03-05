package sirttas.dpanvil.api.codec.capability;

import com.mojang.serialization.Codec;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import sirttas.dpanvil.api.codec.CodecHelper;

/**
 * Don't use this it was a terrible idea of mine
 */
@Deprecated
public class CodecCapabilityProvider<I, T extends I> implements ICapabilitySerializable<INBT> {

	private final Codec<T> codec;
	private final Capability<I> capability;
	private T value;
	
	public CodecCapabilityProvider(Capability<I> capability, Codec<T> codec, T initValue) {
		this.capability = capability;
		this.codec = codec;
		this.value = initValue;
	}

	@Override
	public <U> LazyOptional<U> getCapability(Capability<U> cap, Direction side) {
		return capability.orEmpty(cap, value != null ? LazyOptional.of(() -> value) : LazyOptional.empty());
	}

	@Override
	public INBT serializeNBT() {
		return CodecHelper.encode(codec, NBTDynamicOps.INSTANCE, value);
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		value = CodecHelper.decode(codec, nbt);
	}

}

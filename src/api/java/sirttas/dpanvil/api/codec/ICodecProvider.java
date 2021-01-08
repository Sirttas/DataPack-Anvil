package sirttas.dpanvil.api.codec;

import com.mojang.serialization.Codec;

public interface ICodecProvider<T> {

	Codec<T> getCodec();
}

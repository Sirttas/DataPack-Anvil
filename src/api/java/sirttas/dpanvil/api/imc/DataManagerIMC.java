package sirttas.dpanvil.api.imc;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.InterModComms;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataManagerIMC<T> {

	public static final String METHOD = "data_manager";

	private final ResourceKey<IDataManager<T>> key;
	private final IDataManager<T> manager;
	private Codec<T> codec;
	private Function<JsonElement, T> readJson;
	private Function<FriendlyByteBuf, T> readPacket;
	private BiConsumer<FriendlyByteBuf, T> writePacket;

	@Deprecated(since = "1.18.2-3.3.3", forRemoval = true)
	public DataManagerIMC(ResourceLocation id, IDataManager<T> manager) {
		this(IDataManager.createManagerKey(id), manager);
	}

	public DataManagerIMC(ResourceKey<IDataManager<T>> key, IDataManager<T> manager) {
		this.key = key;
		this.manager = manager;
	}

	public ResourceKey<IDataManager<T>> getKey() {
		return key;
	}

	@Deprecated(since = "1.18.2-3.3.3", forRemoval = true)
	public ResourceLocation getId() {
		return key.location();
	}

	public IDataManager<T> getManager() {
		return manager;
	}

	public Codec<T> getCodec() {
		return codec;
	}

	public Function<JsonElement, T> getReadJson() {
		return readJson;
	}

	public Function<FriendlyByteBuf, T> getReadPacket() {
		return readPacket;
	}

	public BiConsumer<FriendlyByteBuf, T> getWritePacket() {
		return writePacket;
	}

	public DataManagerIMC<T> withCodec(Codec<T> codec) {
		this.codec = codec;
		this.readJson = null;
		this.readPacket = null;
		this.writePacket = null;
		return this;
	}

	public DataManagerIMC<T> withSerializer(Function<JsonElement, T> readJson, Function<FriendlyByteBuf, T> readPacket, BiConsumer<FriendlyByteBuf, T> writePacket) {
		this.codec = null;
		this.readJson = readJson;
		this.readPacket = readPacket;
		this.writePacket = writePacket;
		return this;
	}
	
	public DataManagerIMC<T> withSerializer(Function<FriendlyByteBuf, T> readPacket, BiConsumer<FriendlyByteBuf, T> writePacket) {
		return withSerializer(null, readPacket, writePacket);
	}

	public static <T> void enqueue(Supplier<DataManagerIMC<T>> supplier) {
		InterModComms.sendTo(DataPackAnvilApi.MODID, METHOD, supplier);
	}
}

package sirttas.dpanvil.api.imc;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.InterModComms;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;

public class DataManagerIMC<T> {

	public static final String METHOD = "data_manager";

	private final ResourceLocation id;
	private final IDataManager<T> manager;
	private Codec<T> codec;
	private Function<JsonElement, T> readJson;
	private Function<PacketBuffer, T> readPacket;
	private BiConsumer<PacketBuffer, T> writePacket;

	public DataManagerIMC(ResourceLocation id, IDataManager<T> manager) {
		this.id = id;
		this.manager = manager;
	}

	public ResourceLocation getId() {
		return id;
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

	public Function<PacketBuffer, T> getReadPacket() {
		return readPacket;
	}

	public BiConsumer<PacketBuffer, T> getWritePacket() {
		return writePacket;
	}

	public DataManagerIMC<T> withCodec(Codec<T> codec) {
		this.codec = codec;
		this.readJson = null;
		this.readPacket = null;
		this.writePacket = null;
		return this;
	}

	public DataManagerIMC<T> withSerializer(Function<JsonElement, T> readJson, Function<PacketBuffer, T> readPacket, BiConsumer<PacketBuffer, T> writePacket) {
		this.codec = null;
		this.readJson = readJson;
		this.readPacket = readPacket;
		this.writePacket = writePacket;
		return this;
	}

	public static <T> void enqueue(Supplier<DataManagerIMC<T>> supplier) {
		InterModComms.sendTo(DataPackAnvilApi.MODID, METHOD, supplier);
	}
}
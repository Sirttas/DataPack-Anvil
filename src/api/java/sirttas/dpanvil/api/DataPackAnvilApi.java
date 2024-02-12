package sirttas.dpanvil.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.remap.RemapKeys;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;

public class DataPackAnvilApi {

	public static final String MODID = "dpanvil";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final ResourceLocation ID_NONE = new ResourceLocation(MODID, "none");
	public static final ResourceLocation DATA_MANAGER_ROOT = new ResourceLocation(MODID, "data_managers");

	public static final ResourceKey<IDataManager<RemapKeys>> REMAP_KEYS_MANAGER_KEY = IDataManager.createManagerKey(new ResourceLocation(MODID, RemapKeys.NAME));
	public static final IDataManager<RemapKeys> REMAP_KEYS_MANAGER = IDataManager.builder(RemapKeys.class, REMAP_KEYS_MANAGER_KEY)
			.merged(RemapKeys::merge)
			.withDefault(RemapKeys.EMPTY)
			.build();

	private static IDataPackAnvilService service;

	private DataPackAnvilApi() {}

	public static synchronized IDataPackAnvilService service() {
		if (service == null) {
				ServiceLoader<IDataPackAnvilService> loader = ServiceLoader.load(IDataPackAnvilService.class);

				service = loader.findFirst().orElseGet(() -> {
					LOGGER.warn("Couldn't find service, using default");
					try {
						Constructor<?> constructor = Class.forName("sirttas.dpanvil.DataPackAnvilService", true, DataPackAnvilApi.class.getClassLoader()).getDeclaredConstructor();

						return (IDataPackAnvilService) constructor.newInstance();
					} catch (Exception e) {
						throw new IllegalStateException("Couldn't get constructor", e);
					}
				});
		}
		return service;
	}

	@SuppressWarnings("unchecked")
	public static <T> ResourceKey<T> createResourceKey(ResourceLocation dataManagerId, ResourceLocation id) {
		try {
			return (ResourceKey<T>) ObfuscationReflectionHelper.findMethod(ResourceKey.class, "create", ResourceLocation.class, ResourceLocation.class).invoke(null, dataManagerId, id);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Reflection error", e);
		}
	}

	public static ResourceLocation createRL(String name) {
		if (name.contains(":")) {
			return new ResourceLocation(name);
		}
		return new ResourceLocation(DataPackAnvilApi.MODID, name);
	}
}

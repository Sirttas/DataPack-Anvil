package sirttas.dpanvil.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.preprocessor.MergeDataPreprocessor;
import sirttas.dpanvil.api.data.remap.RemapKeys;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ServiceLoader;

public class DataPackAnvilApi {

	private static final Method CREATE_RESOURCE_KEY = ObfuscationReflectionHelper.findMethod(ResourceKey.class, "create", ResourceLocation.class, ResourceLocation.class);

	public static final String MODID = "dpanvil";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final ResourceKey<IDataManager<RemapKeys>> REMAP_KEYS_MANAGER_KEY = IDataManager.createManagerKey(DPAnvilNames.ResourceLocations.create(RemapKeys.NAME));
	public static final IDataManager<RemapKeys> REMAP_KEYS_MANAGER = IDataManager.builder(RemapKeys.class, REMAP_KEYS_MANAGER_KEY)
			.preprocessor(new MergeDataPreprocessor())
			.defaultPreprocessors()
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
			return (ResourceKey<T>) CREATE_RESOURCE_KEY.invoke(null, dataManagerId, id);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Reflection error", e);
		}
	}
}

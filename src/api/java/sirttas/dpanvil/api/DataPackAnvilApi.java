package sirttas.dpanvil.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DataPackAnvilApi {

	public static final String MODID = "dpanvil";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final ResourceLocation ID_NONE = new ResourceLocation(MODID, "none");
	public static final ResourceLocation DATA_MANAGER_ROOT = new ResourceLocation(MODID, "data_managers");

	private static IDataPackAnvilService service;
	
	private DataPackAnvilApi() {}

	public static synchronized IDataPackAnvilService service() {
		if (service == null) {
			try {
				Constructor<?> constructor = Class.forName("sirttas.dpanvil.DataPackAnvilService", true, DataPackAnvilApi.class.getClassLoader()).getDeclaredConstructor();

				service = (IDataPackAnvilService) constructor.newInstance();
			} catch (Exception e) {
				throw new IllegalStateException("Couldn't get constructor", e);
			}
		}
		return service;
	}

	@SuppressWarnings("unchecked")
	public static <T> ResourceKey<T> createResourceKey(ResourceLocation dataManagerId, ResourceLocation id) {
		try {
			return (ResourceKey<T>) ObfuscationReflectionHelper.findMethod(ResourceKey.class, "m_135790_", ResourceLocation.class, ResourceLocation.class).invoke(null, dataManagerId, id);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Reflection error", e);
		}
	}
}

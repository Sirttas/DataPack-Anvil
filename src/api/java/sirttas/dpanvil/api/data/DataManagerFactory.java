package sirttas.dpanvil.api.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import sirttas.dpanvil.api.DataPackAnvilApi;

public class DataManagerFactory {
	
	private static final Constructor<?> SIMPLE_CONSTRUCTOR = getConstructor("data.SimpleDataManager", Class.class, String.class);
	private static final Constructor<?> DEFAULTED_CONSTRUCTOR = getConstructor("data.DefaultedDataManager", Class.class, String.class, Object.class);
	

	public static <T, M extends IDataManager<T>> M simple(Class<T> type, String folder) {
		return construct(SIMPLE_CONSTRUCTOR, type, folder);
	}


	public static <T, M extends IDataManager<T>> M defaulted(Class<T> type, String folder, T defaultValue) {
		return construct(DEFAULTED_CONSTRUCTOR, type, folder, defaultValue);
	}

	private static Constructor<?> getConstructor(String className, Class<?>... classes) {
		try {
			return Class.forName("sirttas.dpanvil." + className, true, DataManagerFactory.class.getClassLoader()).getConstructor(classes);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			DataPackAnvilApi.LOGGER.error("Couldn't get constructor", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T, M extends IDataManager<T>> M construct(Constructor<?> constructor, Object... objects) {
		try {
			return constructor != null ? (M) constructor.newInstance(objects) : null;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			DataPackAnvilApi.LOGGER.error("Couldn't construct new data manager", e);
			return null;
		}
	}

}

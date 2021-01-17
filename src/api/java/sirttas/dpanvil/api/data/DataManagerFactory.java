package sirttas.dpanvil.api.data;

/**
 * @deprecated use {@link IDataManager.Builder}
 */
@Deprecated
public class DataManagerFactory {
	
	public static <T> IDataManager<T> simple(Class<T> type, String folder) {
		return IDataManager.builder(type, folder).build();
	}

	public static <T> IDataManager<T> defaulted(Class<T> type, String folder, T defaultValue) {
		return IDataManager.builder(type, folder).withDefault(defaultValue).build();
	}

}

package sirttas.dpanvil.annotation;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.objectweb.asm.Type;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.annotation.DataHolder;
import sirttas.dpanvil.api.data.DataManager;

public class DataHolderProcessor {

	private static final Type DATA_HOLDER = Type.getType(DataHolder.class);
	private static Map<Field, ResourceLocation> dataHolders;

	public static void setup() {
		ImmutableMap.Builder<Field, ResourceLocation> builder = ImmutableMap.builder();

		ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(a -> DATA_HOLDER.equals(a.getAnnotationType()))
				.map(AnnotationData::getClassType).distinct().map(DataHolderProcessor::getClass).filter(Objects::nonNull).map(Class::getDeclaredFields).flatMap(Stream::of)
				.filter(field -> field.isAnnotationPresent(DataHolder.class)).forEach(field -> {
					field.setAccessible(true);
					builder.put(field, new ResourceLocation(field.getAnnotation(DataHolder.class).value()));
				});
		dataHolders = builder.build();
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> getClass(Type type) {
		try {
			return (Class<T>) Class.forName(type.getClassName(), false, DataHolderProcessor.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static void apply() {
		dataHolders.forEach((field, id) -> {
			DataManager<?> manager = DataPackAnvil.WRAPPER.getManager(field.getType());

			if (manager != null) {
				try {
					field.set(null, manager.get(id));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					DataPackAnvilApi.LOGGER.error(() -> "Error while setting field " + field.getName(), e);
				}
			} else {
				DataPackAnvilApi.LOGGER.warn("Couldn't find DataManager for class {} of field {}", field.getType().getName(), field.getName());
			}
		});
	}
}

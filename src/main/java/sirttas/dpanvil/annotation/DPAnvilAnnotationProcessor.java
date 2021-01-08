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
import sirttas.dpanvil.api.data.IDataManager;

public class DPAnvilAnnotationProcessor {

	private static final Type DATA_HOLDER = Type.getType(DataHolder.class);
	private Map<Field, ResourceLocation> dataHolders;

	public void setup() {
		ImmutableMap.Builder<Field, ResourceLocation> dataHoldersBuilder = ImmutableMap.builder();

		ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream)
				.filter(a -> DATA_HOLDER.equals(a.getAnnotationType())).map(AnnotationData::getClassType).distinct().map(this::getClass).filter(Objects::nonNull).map(Class::getDeclaredFields)
				.flatMap(Stream::of).filter(field -> field.isAnnotationPresent(DataHolder.class)).forEach(field -> {
					field.setAccessible(true);
					dataHoldersBuilder.put(field, new ResourceLocation(field.getAnnotation(DataHolder.class).value()));
				});
		dataHolders = dataHoldersBuilder.build();
	}

	@SuppressWarnings("unchecked")
	private <T> Class<T> getClass(Type type) {
		try {
			return (Class<T>) Class.forName(type.getClassName(), false, DPAnvilAnnotationProcessor.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public void applyDataHolder() {
		dataHolders.forEach((field, id) -> {
			IDataManager<?> manager = DataPackAnvil.WRAPPER.getManager(field.getType());

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

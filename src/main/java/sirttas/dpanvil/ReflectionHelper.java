package sirttas.dpanvil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import sirttas.dpanvil.api.DataPackAnvilApi;

public class ReflectionHelper {

	private ReflectionHelper() {}

	public static void setAccesible(Field field) {
		try {
			field.setAccessible(true);
			if (Modifier.isFinal(field.getModifiers())) {
				Field modifiersField;
				modifiersField = Field.class.getDeclaredField("modifiers");

				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			DataPackAnvilApi.LOGGER.error(() -> "Error while making field:  " + field.getName() + " accesible", e);
		}
	}
}

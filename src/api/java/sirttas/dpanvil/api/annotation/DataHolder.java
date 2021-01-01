package sirttas.dpanvil.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import sirttas.dpanvil.api.data.DataManager;

/**
 * <p>
 * DataHolder can be used to automatically populate static final fields with
 * entries from the {@link DataManager}. These values can then be referred
 * within mod code directly similar to
 * {@link net.minecraftforge.registries.ObjectHolder}.
 * </p>
 * <p>
 * <b>Note: if the data is absent in the pack this will be null</b>
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataHolder {

	/**
	 * It represents a name. to be retreived from {@link DataManager}
	 *
	 * @return A name
	 */
	String value();
}

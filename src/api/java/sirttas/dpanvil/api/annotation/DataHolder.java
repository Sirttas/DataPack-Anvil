package sirttas.dpanvil.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import sirttas.dpanvil.api.data.IDataManager;

/**
 * <p>
 * DataHolder can be used to automatically populate static final fields with
 * entries from the {@link IDataManager}. These values can then be referred
 * within mod code directly similar to
 * {@link net.minecraftforge.registries.ObjectHolder}.
 * </p>
 * <p>
 * <b>Note: if the data is absent in the pack this will be null</b>
 * </p>
 * @deprecated use {@link IDataManager#getWrapper(net.minecraft.resources.ResourceLocation)} instead.
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataHolder {

	/**
	 * It represents a name. to be retreived from {@link IDataManager}
	 *
	 * @return A name
	 */
	String value();
}

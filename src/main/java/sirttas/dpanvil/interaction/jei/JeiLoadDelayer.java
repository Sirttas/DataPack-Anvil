package sirttas.dpanvil.interaction.jei;

import java.lang.reflect.Field;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent.CustomTagTypes;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import sirttas.dpanvil.ReflectionHelper;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.data.DataHandler;

public class JeiLoadDelayer {
	
	public static final boolean IS_JEI_PRESENT = ModList.get().isLoaded("jei");
	
	private static boolean isSetup = false;
	
	private JeiLoadDelayer() {}
	
	@SuppressWarnings("unchecked")
	public static void setup() {
		if (IS_JEI_PRESENT) {
			try {
				Class<?> serverTypeClass = Class.forName("mezz.jei.startup.ClientLifecycleHandler$ServerType", false, JeiLoadDelayer.class.getClassLoader());
				Field listenerClassField = serverTypeClass.getField("listenerClass");
				Field moddedRemoteField = serverTypeClass.getField("MODDED");
				
				ReflectionHelper.setAccesible(listenerClassField);
				ReflectionHelper.setAccesible(moddedRemoteField);
				
				Object moddedRemote = moddedRemoteField.get(null);
				Class<? extends Event> listenerClass = (Class<? extends Event>) listenerClassField.get(moddedRemote);
				
				if (!CustomTagTypes.class.equals(listenerClass)) {
					DataHandler.addEvent(listenerClass);
				}
				listenerClassField.set(moddedRemote, JeiLoadDelayerEvent.class);
				DataPackAnvilApi.LOGGER.info("JEI loading delay setup");
				isSetup = true;
			} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				DataPackAnvilApi.LOGGER.error("Couldn't delay JEI loading: ", e);
			}	
		}
	}
	
	public static void loadJEI() {
		if (IS_JEI_PRESENT && isSetup) {
			MinecraftForge.EVENT_BUS.post(new JeiLoadDelayerEvent());
		}
	}
	
	public static class JeiLoadDelayerEvent extends Event {}
}

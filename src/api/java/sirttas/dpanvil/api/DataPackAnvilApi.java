package sirttas.dpanvil.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;

public class DataPackAnvilApi {

	public static final String MODID = "dpanvil";
	public static final String TAGS_FOLDER = "dpanvil_tags/";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final ResourceLocation ID_NONE = new ResourceLocation(MODID, "none");
	
	
	private DataPackAnvilApi() {}
}

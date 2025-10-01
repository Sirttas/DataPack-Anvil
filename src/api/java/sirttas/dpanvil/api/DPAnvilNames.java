package sirttas.dpanvil.api;

import net.minecraft.resources.ResourceLocation;

public class DPAnvilNames {

	public static final String TYPE = "type";
	public static final String BLOCK = "block";
	public static final String BLOCKS = "blocks";
	public static final String STATE = "state";
	public static final String TAG = "tag";
	public static final String VALUE = "value";
	public static final String VALUES = "values";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String AMOUNT = "amount";
	public static final String MODIFIER = "modifier";
    public static final String OFFSET = "offset";
	public static final String OPERATION = "operation";
	public static final String GOM_LOADER_TYPE = "gom_loader_type";

    private DPAnvilNames() {}

	public static class ResourceLocations {
		public static final ResourceLocation NONE = create("none");
		public static final ResourceLocation DATA_MANAGER_ROOT = create("data_managers");
		public static final ResourceLocation PARENT = create("parent");
		public static final ResourceLocation REPLACE = create( "replace");
		public static final ResourceLocation NEOFORGE_CONDITIONS = create("neoforge:conditions");

		private ResourceLocations() {}

		public static ResourceLocation create(String name) {
			if (name.contains(":")) {
				return ResourceLocation.parse(name);
			}
			return ResourceLocation.fromNamespaceAndPath(DataPackAnvilApi.MODID, name);
		}
	}

}

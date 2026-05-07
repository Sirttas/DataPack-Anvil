package sirttas.dpanvil.api;

import net.minecraft.resources.Identifier;

public class DPAnvilNames {

	public static final String TYPE = "type";
	public static final String BLOCK = "block";
	public static final String BLOCKS = "blocks";
	public static final String STATE = "state";
	public static final String TAG = "tag";
	public static final String VALUE = "value";
	public static final String VALUES = "values";
	public static final String NAME = "name";
	public static final String MODIFIER = "modifier";
    public static final String OFFSET = "offset";

    private DPAnvilNames() {}

	public static class Identifiers {
		public static final Identifier NONE = create("none");
		public static final Identifier DATA_MANAGER_ROOT = create("data_managers");
		public static final Identifier PARENT = create("parent");
		public static final Identifier REPLACE = create( "replace");
		public static final Identifier NEOFORGE_CONDITIONS = create("neoforge:conditions");

		private Identifiers() {}

		public static Identifier create(String name) {
			if (name.contains(":")) {
				return Identifier.parse(name);
			}
			return Identifier.fromNamespaceAndPath(DataPackAnvilApi.MODID, name);
		}
	}

}

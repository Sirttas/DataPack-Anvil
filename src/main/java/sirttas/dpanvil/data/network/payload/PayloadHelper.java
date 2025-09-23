package sirttas.dpanvil.data.network.payload;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import sirttas.dpanvil.api.DPAnvilNames;

public class PayloadHelper {

    private PayloadHelper() {}

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createType(String name) {
        return new CustomPacketPayload.Type<>(DPAnvilNames.ResourceLocations.create(name));
    }
}

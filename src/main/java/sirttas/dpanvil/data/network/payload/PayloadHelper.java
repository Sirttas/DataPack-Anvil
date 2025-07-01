package sirttas.dpanvil.data.network.payload;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import sirttas.dpanvil.api.DataPackAnvilApi;

public class PayloadHelper {

    private PayloadHelper() {}

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createType(String name) {
        return new CustomPacketPayload.Type<>(DataPackAnvilApi.createRL(name));
    }
}

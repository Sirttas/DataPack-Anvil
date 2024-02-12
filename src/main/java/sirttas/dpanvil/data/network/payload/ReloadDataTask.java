package sirttas.dpanvil.data.network.payload;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;

import java.util.function.Consumer;

public record ReloadDataTask(ServerConfigurationPacketListener listener) implements ICustomConfigurationTask {
    private static final ResourceLocation ID = DataPackAnvilApi.createRL("reload_data_task");
    public static final Type TYPE = new Type(ID);

    @Override
    public void run(Consumer<CustomPacketPayload> sender) {
        sender.accept(new ReloadDataPayload(DataPackAnvil.WRAPPER.ids()));
        listener().finishCurrentTask(type());
    }

    @Override
    public @NotNull Type type() {
        return TYPE;
    }
}

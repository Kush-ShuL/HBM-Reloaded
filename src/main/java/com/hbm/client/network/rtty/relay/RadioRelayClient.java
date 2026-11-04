package com.hbm.client.network.rtty.relay;

import com.hbm.client.network.rtty.RadioTorchBakedModel;
import com.hbm.network.rtty.relay.RadioRelayBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RadioRelayClient {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> RadioRelayBlock.OPEN_SCREEN = relay -> Minecraft.getInstance().setScreen(new RadioRelayScreen(relay)));
    }

    @SubscribeEvent
    public static void models(ModelEvent.ModifyBakingResult event) {
        var sprite = event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.parse("hbm:block/rtty_relay")));
        event.getModels().replaceAll((id, model) -> {
            if (!id.id().equals(ResourceLocation.parse("hbm:radio_torch_relay")) || !id.getVariant().startsWith("facing=")) return model;
            Direction facing = Direction.byName(id.getVariant().substring(7));
            return new RadioTorchBakedModel(model, "rtty_relay", facing.get3DDataValue(), ignored -> sprite, entity -> false);
        });
    }

    private RadioRelayClient() {}
}

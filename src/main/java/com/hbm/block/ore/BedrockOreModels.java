package com.hbm.block.ore;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import com.hbm.registry.HbmContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class BedrockOreModels {
    private BedrockOreModels() { }
    private static ModelResourceLocation model(String suffix) { return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hbm", "block/ore_bedrock." + suffix)); }
    @SubscribeEvent public static void additional(ModelEvent.RegisterAdditional event) {
        event.register(model("plain"));
        for (int shape = 0; shape < 10; shape++) event.register(model("shape_" + shape));
    }
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event) {
        BakedModel plain = event.getModels().get(model("plain"));
        BakedModel[] shapes = new BakedModel[10];
        for (int shape = 0; shape < 10; shape++) shapes[shape] = java.util.Objects.requireNonNull(event.getModels().get(model("shape_" + shape)));
        event.getModels().replaceAll((location, original) -> location.id().equals(ResourceLocation.fromNamespaceAndPath("hbm", "ore_bedrock"))
                ? new BedrockOreModel(original, plain, shapes) : original);
    }
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tint) -> level != null && pos != null && tint == 0 && level.getBlockEntity(pos) instanceof BedrockOreEntity ore
                ? ore.color : 0xFFFFFF, HbmContent.block("ore_bedrock"));
    }
    @SubscribeEvent public static void reload(RegisterClientReloadListenersEvent event) { event.registerReloadListener(OreResourceColors.INSTANCE); }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { BedrockOreEntity.CLIENT_STACK_COLOR = OreResourceColors::color; }
}

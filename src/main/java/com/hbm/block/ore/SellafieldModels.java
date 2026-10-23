package com.hbm.block.ore;

import java.util.HashMap;
import java.util.Map;
import com.hbm.registry.HbmOres;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class SellafieldModels {
    private SellafieldModels() { }
    private static final String[] IDS = {"ore_sellafield_diamond", "ore_sellafield_emerald", "ore_sellafield_uranium_scorched", "ore_sellafield_schrabidium", "ore_sellafield_radgem"};
    private static ModelResourceLocation host(String id, int index) {
        return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hbm", "block/" + id + ".host_" + index));
    }
    @SubscribeEvent public static void registerModels(ModelEvent.RegisterAdditional event) {
        for (String id : IDS) for (int index = 0; index < 4; index++) event.register(host(id, index));
    }
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event) {
        Map<String, BakedModel[]> models = new HashMap<>();
        for (String id : IDS) {
            BakedModel[] hosts = new BakedModel[4];
            for (int index = 0; index < 4; index++) hosts[index] = java.util.Objects.requireNonNull(event.getModels().get(host(id, index)), "Missing original Sellafield host model");
            models.put(id, hosts);
        }
        event.getModels().replaceAll((location, model) -> location.id().getNamespace().equals("hbm") && models.containsKey(location.id().getPath())
                ? new SellafieldModel(model, models.get(location.id().getPath())) : model);
    }
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Block event) {
        Block[] blocks = HbmOres.allItems().stream().map(holder -> holder.get()).filter(item -> item instanceof LegacyOreItem ore
                && ore.spec().originalClass().equals("BlockSellafieldOre")).map(item -> ((LegacyOreItem) item).getBlock()).toArray(Block[]::new);
        event.register((state, level, pos, tint) -> {
            if (tint != 0) return 0xFFFFFF;
            return java.awt.Color.HSBtoRGB(0F, 0F, 1F - state.getValue(LegacyOreBlock.LEGACY_META) / 15F);
        }, blocks);
    }
}

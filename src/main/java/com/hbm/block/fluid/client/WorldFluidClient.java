package com.hbm.block.fluid.client;

import java.util.HashMap;
import java.util.Map;
import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.block.fluid.LegacyFluidBlock;
import com.hbm.block.fluid.LegacyFluidWorld;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.SellafieldModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class WorldFluidClient {
    private WorldFluidClient() { }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        for (var spec : HbmWorldFluids.specifications()) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override public ResourceLocation getStillTexture() { return ResourceLocation.fromNamespaceAndPath("hbm", "block/" + spec.still()); }
                @Override public ResourceLocation getFlowingTexture() { return ResourceLocation.fromNamespaceAndPath("hbm", "block/" + spec.flowing()); }
                @Override public boolean renderFluid(net.minecraft.world.level.material.FluidState fluid, net.minecraft.world.level.BlockAndTintGetter level,
                        net.minecraft.core.BlockPos pos, com.mojang.blaze3d.vertex.VertexConsumer vertices, net.minecraft.world.level.block.state.BlockState state) {
                    var renderer = new LegacyFluidRenderer(vertices, pos, spec);
                    renderer.renderWorldBlock(new LegacyFluidWorld(level), pos.getX(), pos.getY(), pos.getZ(), state.getBlock(), renderer);
                    return true;
                }
            }, HbmWorldFluids.type(spec.fluid()));
        }
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> HbmWorldFluids.specifications().forEach(spec ->
                ItemBlockRenderTypes.setRenderLayer(HbmWorldFluids.fluid(spec.fluid()), spec.renderPass() == 0 ? RenderType.cutout() : RenderType.translucent())));
    }
    private static ModelResourceLocation host(int index) { return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("hbm", "block/sellafield_slaked.host_" + index)); }
    @SubscribeEvent public static void additionalModels(ModelEvent.RegisterAdditional event) {
        if (HbmWorldFluids.specifications().isEmpty()) return;
        for (int i = 0; i < 4; i++) event.register(host(i));
    }
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event) {
        if (HbmWorldFluids.specifications().isEmpty()) return;
        BakedModel[] hosts = new BakedModel[4];
        for (int i = 0; i < hosts.length; i++) hosts[i] = java.util.Objects.requireNonNull(event.getModels().get(host(i)));
        event.getModels().replaceAll((location, original) -> location.id().equals(ResourceLocation.fromNamespaceAndPath("hbm", "sellafield_slaked"))
                ? new SellafieldModel(original, hosts) : original);
    }
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Block event) {
        if (HbmWorldFluids.specifications().isEmpty()) return;
        event.register((state, level, pos, tint) -> tint == 0 ? java.awt.Color.HSBtoRGB(0, 0, 1F - state.getValue(LegacyOreBlock.LEGACY_META) / 15F) : 0xFFFFFF,
                HbmWorldFluids.block("sellafield_slaked"));
    }
}

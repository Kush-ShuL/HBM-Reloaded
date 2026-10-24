package com.hbm.client.ore;

import com.hbm.block.ore.BedrockOreBlock;
import com.hbm.block.ore.BedrockOreEntity;
import com.hbm.item.settings.client.LegacyInfoSystem;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/** Actual BlockBedrockOreTE.printHook, not a Jade provider or timed information message. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class BedrockOreOverlay {
    // The complete original held Item ILookOverlay family, including ItemStructureTool subclasses.
    // Original held tools take precedence even when that tool's own printHook elects to draw nothing.
    private static final Set<String> HELD_LOOK_OVERLAY=Set.of("hbm:conveyor_wand_regular","hbm:conveyor_wand_express","hbm:conveyor_wand_double","hbm:conveyor_wand_triple","hbm:structure_custommachine","hbm:structure_pattern","hbm:structure_randomized","hbm:structure_randomly","hbm:structure_single","hbm:structure_solid");
    @SubscribeEvent public static void render(RenderGuiLayerEvent.Pre event) {
        if(!VanillaGuiLayers.CROSSHAIR.equals(event.getName()))return;
        var client=Minecraft.getInstance();
        if(client.options.hideGui||client.player==null||client.level==null||!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled()
                ||!(client.hitResult instanceof BlockHitResult hit)||hit.getType()!=HitResult.Type.BLOCK)return;
        var held=client.player.getMainHandItem();
        if(!held.isEmpty()&&HELD_LOOK_OVERLAY.contains(BuiltInRegistries.ITEM.getKey(held.getItem()).toString()))return;
        if(!(client.level.getBlockState(hit.getBlockPos()).getBlock() instanceof BedrockOreBlock)
                ||!(client.level.getBlockEntity(hit.getBlockPos()) instanceof BedrockOreEntity ore))return;
        LegacyInfoSystem.printGeneric(event.getGuiGraphics(),Component.translatable("tile.ore_bedrock.name"),0xffff00,0x404000,ore.overlayLines());
    }
    private BedrockOreOverlay(){}
}

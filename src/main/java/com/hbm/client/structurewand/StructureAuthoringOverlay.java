package com.hbm.client.structurewand;

import com.hbm.item.settings.client.LegacyInfoSystem;
import com.hbm.item.structurewand.StructureAuthoringBlock;
import com.hbm.item.structurewand.StructureAuthoringEntity;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class StructureAuthoringOverlay {
    @SubscribeEvent public static void render(RenderGuiLayerEvent.Pre event){
        if(!VanillaGuiLayers.CROSSHAIR.equals(event.getName()))return;var client=Minecraft.getInstance();
        if(client.level==null||client.player==null||client.options.hideGui||!(client.hitResult instanceof BlockHitResult hit))return;
        if(!(client.level.getBlockState(hit.getBlockPos()).getBlock() instanceof StructureAuthoringBlock block)||block.loading||!(client.level.getBlockEntity(hit.getBlockPos()) instanceof StructureAuthoringEntity tile))return;
        var lines=new ArrayList<Component>();lines.add(Component.translatable("hud.hbm.structure_wand.name").withStyle(ChatFormatting.GRAY).append(Component.literal(tile.name).withStyle(ChatFormatting.RESET)));
        if(!tile.blacklist.isEmpty()){lines.add(Component.translatable("tooltip.hbm.structure_wand.blacklist").withStyle(ChatFormatting.GRAY));for(var key:tile.blacklist)lines.add(Component.literal("- ").append(key.actual().getName()).withStyle(ChatFormatting.RED));}
        LegacyInfoSystem.printGeneric(event.getGuiGraphics(),Component.translatable("tile.wand_structure.save.name"),0xffff00,0x404000,lines);
    }
    private StructureAuthoringOverlay(){}
}

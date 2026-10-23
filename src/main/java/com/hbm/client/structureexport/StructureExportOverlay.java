package com.hbm.client.structureexport;

import com.hbm.item.settings.client.LegacyInfoSystem;
import com.hbm.item.structureexport.StructureExportItem;
import com.hbm.item.structureexport.StructureSourceState;
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
public final class StructureExportOverlay {
    @SubscribeEvent public static void render(RenderGuiLayerEvent.Pre event){
        if(!VanillaGuiLayers.CROSSHAIR.equals(event.getName()))return;
        var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||mc.player==null||!(mc.hitResult instanceof BlockHitResult hit))return;
        var stack=mc.player.getMainHandItem();if(!(stack.getItem() instanceof StructureExportItem item))return;
        var lines=new ArrayList<Component>();var anchor=StructureExportItem.anchor(stack);var pos=hit.getBlockPos();
        if(anchor==null)lines.add(Component.translatable("hud.hbm.structure_export.no_anchor").withStyle(ChatFormatting.RED));
        else{
            lines.add(Component.translatable("hud.hbm.structure_export.position",pos.getX()-anchor.getX(),pos.getY()-anchor.getY(),pos.getZ()-anchor.getZ()).withStyle(ChatFormatting.YELLOW));
            var tag=StructureExportItem.data(stack);if(item.dualUse()&&tag.contains("x"))lines.add(Component.translatable("hud.hbm.structure_export.selection",Math.abs(pos.getX()-tag.getInt("x"))+1,Math.abs(pos.getY()-tag.getInt("y"))+1,Math.abs(pos.getZ()-tag.getInt("z"))+1).withStyle(ChatFormatting.GOLD));
        }
        if(mc.player.isShiftKeyDown())try{var sample=StructureSourceState.capture(mc.level.getBlockState(pos));lines.add(Component.translatable("hud.hbm.structure_export.block",sample.name(),sample.metadata()));}
        catch(IllegalArgumentException undefined){lines.add(Component.translatable("message.hbm.structure_export.unrepresentable",undefined.getMessage()).withStyle(ChatFormatting.RED));}
        LegacyInfoSystem.printGeneric(event.getGuiGraphics(),stack.getHoverName(),0xffff00,0x404000,lines);
    }
    private StructureExportOverlay(){}
}

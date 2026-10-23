package com.hbm.client.fluidtank;
import com.hbm.api.machine.IRepairable;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
/** Original IRepairable.addGenericOverlay/ILookOverlay coordinates and title shadow colors. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class RepairableOverlay {
    @SubscribeEvent public static void render(RenderGuiLayerEvent.Pre event){
        if(!event.getName().equals(VanillaGuiLayers.CROSSHAIR))return;var client=Minecraft.getInstance();if(client.options.hideGui||client.player==null||client.level==null||!(client.hitResult instanceof BlockHitResult hit)||!(client.level.getBlockState(hit.getBlockPos()).getBlock() instanceof DummyableBlock block))return;var materials=IRepairable.getRepairMaterials(client.level,hit.getBlockPos(),block,client.player);if(materials==null)return;
        var g=event.getGuiGraphics();var font=LegacyFonts.get();int x=g.guiWidth()/2+8,y=g.guiHeight()/2;var title=block.getName();g.drawString(font,title,x+1,y-9,0x404000,false);g.drawString(font,title,x,y-10,0xffff00,false);g.drawString(font,Component.translatable("gui.hbm.repair.materials").withStyle(ChatFormatting.GOLD),x,y,0xffffff,true);y+=10;
        for(var material:materials){var examples=material.examples();Component line;if(examples.isEmpty())line=Component.translatable("gui.hbm.repair.error").withStyle(ChatFormatting.RED);else{var example=examples.get((int)(client.level.getGameTime()/20%examples.size()));line=Component.translatable("gui.hbm.repair.ingredient",example.getHoverName(),example.getCount());}g.drawString(font,line,x,y,0xffffff,true);y+=10;}
    }
    private RepairableOverlay(){}
}

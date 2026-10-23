package com.hbm.client.rtgblast;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.rtg.RtgContent;
import com.hbm.machine.rtgblast.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Original176x166 processing GUI and both original outboard information panels. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RtgBlastScreen extends LegacyContainerScreen<RtgBlastMenu> {
    private static final ResourceLocation UTILITY=ResourceLocation.parse("hbm:textures/gui/gui_utility.png");
    public RtgBlastScreen(RtgBlastMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_rtg_difurnace.png"),176,166);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RtgBlastContent.MENU.get(),RtgBlastScreen::new);}
    @Override protected void renderLabels(GuiGraphics graphics,int mouseX,int mouseY){graphics.drawString(font,title,88-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,72,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){
        super.renderBg(graphics,partial,mouseX,mouseY);if(menu.heat()>=15)drawLegacyRegion(graphics,58,36,176,31,18,16);
        drawLegacyRegion(graphics,101,35,176,14,menu.scaledProgress(24)+1,17);
        drawTexturedModalRect(graphics,UTILITY,leftPos-15,topPos+36,8,0,16,16,256,256);
        drawTexturedModalRect(graphics,UTILITY,leftPos-15,topPos+52,24,0,16,16,256,256);
    }
    private static List<Component> description(){return java.util.stream.IntStream.range(0,4).mapToObj(i->(Component)Component.translatable("gui.hbm.rtg_blast.description."+i)).toList();}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        super.render(graphics,mouseX,mouseY,partial);
        if(legacyInfoHovered(mouseX,mouseY,-15,52,16,16))graphics.renderComponentTooltip(font,description(),leftPos-8,topPos+52);
        if(legacyInfoHovered(mouseX,mouseY,58,36,18,16))graphics.renderTooltip(font,Component.translatable("desc.gui.rtg.heat",menu.heat()),mouseX,mouseY);
        if(legacyInfoHovered(mouseX,mouseY,-15,36,16,16)){
            var lines=new ArrayList<Component>();lines.add(Component.translatable("desc.gui.rtg.pellets"));
            for(var pellet:RtgContent.PELLETS)lines.add(Component.translatable("desc.gui.rtg.pelletHeat",new net.minecraft.world.item.ItemStack(pellet.get()).getHoverName(),pellet.get().heat()));
            graphics.renderComponentTooltip(font,lines,leftPos-8,topPos+52);
        }
        if(menu.getCarried().isEmpty()&&hoveredSlot!=null&&hoveredSlot.index>=0&&hoveredSlot.index<2){int side=menu.side(hoveredSlot.index);String[] names={"down","up","north","south","west","east"};
            var direction=side>=0&&side<6?Component.translatable("gui.hbm.side."+names[side]):Component.translatable("gui.hbm.rtg_blast.side_unknown");
            graphics.renderTooltip(font,Component.translatable("gui.hbm.rtg_blast.accepts",direction).withStyle(ChatFormatting.YELLOW),mouseX,mouseY-(hoveredSlot.hasItem()?15:0));
        }
    }
}

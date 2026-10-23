package com.hbm.client.reactor.rbmk.channel;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.reactor.rbmk.channel.*;
import com.hbm.machine.reactor.rbmk.fuel.RbmkFuelRodItem;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Exact original176x186 GUI/UV/slot/warning positions, with translated original warning text. */
public final class RbmkChannelScreen extends LegacyContainerScreen<RbmkChannelMenu> {
    private static final ResourceLocation UTILITY=ResourceLocation.parse("hbm:textures/gui/gui_utility.png");
    public RbmkChannelScreen(RbmkChannelMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_rbmk_element.png"),176,186);}
    private RbmkFuelChannelEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof RbmkFuelChannelEntity channel?channel:null;}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var held=menu.getSlot(0).getItem();if(held.getItem() instanceof RbmkFuelRodItem){drawLegacyRegion(g,34,21,176,0,18,67);int d=(int)((1-RbmkFuelRodItem.getEnrichment(held))*67);drawLegacyRegion(g,34,21,194,0,18,d);int xenon=(int)(RbmkFuelRodItem.getPoisonLevel(held)*58);drawLegacyRegion(g,126,82-xenon,212,58-xenon,14,xenon);}var machine=machine();if(machine!=null){if(!machine.coldEnoughForAutoloader())g.blit(UTILITY,leftPos-16,topPos+20,8,16,16,16);if(!machine.coldEnoughForManual())g.blit(UTILITY,leftPos-16,topPos+36,24,16,16,16);}}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var machine=machine();if(machine==null)return;if(!machine.coldEnoughForAutoloader()&&legacyInfoHovered(x,y,-16,20,16,16))g.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.rbmk.fuel.autoloader1"),Component.translatable("gui.hbm.rbmk.fuel.autoloader2")),leftPos-8,topPos+36);if(!machine.coldEnoughForManual()&&legacyInfoHovered(x,y,-16,36,16,16))g.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.rbmk.fuel.manual1"),Component.translatable("gui.hbm.rbmk.fuel.manual2")),leftPos-8,topPos+52);}
}

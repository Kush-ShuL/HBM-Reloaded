package com.hbm.client.conveyorgrabber;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.conveyorgrabber.*;
import com.hbm.pneumatic.PatternMatcher;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Original176x185 source GUI, exact whitelist arrow/hover bounds and actual nine-pattern tooltips. */
public final class ConveyorGrabberScreen extends LegacyContainerScreen<ConveyorGrabberMenu> {
    public ConveyorGrabberScreen(ConveyorGrabberMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/storage/gui_crane_grabber.png"),176,185);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){var name=menu.grabber.getDisplayName();graphics.drawString(font,name,88-font.width(name)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,91,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);drawLegacyRegion(graphics,108,menu.grabber.isWhitelist?33:47,176,0,3,6);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean result=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,97,30,14,26)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));PacketDistributor.sendToServer(new ConveyorGrabberControl(menu.grabber.getBlockPos()));return true;}return result;}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);if(menu.getCarried().isEmpty())for(int i=0;i<9;i++){var slot=menu.slots.get(i);var mode=menu.grabber.matcher.modes[i];if(legacyInfoHovered(x,y,slot.x,slot.y,16,16)&&mode!=null)graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.pneumatic.change_mode").withStyle(ChatFormatting.RED),PatternMatcher.label(mode)),x,y-30);}}
}

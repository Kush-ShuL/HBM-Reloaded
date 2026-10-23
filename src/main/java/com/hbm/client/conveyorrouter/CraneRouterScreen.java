package com.hbm.client.conveyorrouter;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.conveyorrouter.*;
import com.hbm.pneumatic.PatternMatcher;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Source 256x201 screen, two source background rectangles, six controls and the actual matcher tooltips. */
public final class CraneRouterScreen extends LegacyContainerScreen<CraneRouterMenu> {
    public CraneRouterScreen(CraneRouterMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/storage/gui_crane_router.png"),256,201);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){var name=menu.router.getDisplayName();graphics.drawString(font,name,128-font.width(name)/2,5,4210752,false);graphics.drawString(font,playerInventoryTitle,47,107,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){
        graphics.setColor(1,1,1,1);drawLegacyRegion(graphics,0,0,0,0,256,93);drawLegacyRegion(graphics,39,93,39,93,176,108);
        for(int group=0;group<2;group++)for(int row=0;row<3;row++)drawLegacyRegion(graphics,7+group*222,16+row*26,238,93+menu.router.modes[group*3+row]*18,18,18);
        if(GLFW.glfwGetKey(minecraft.getWindow().getWindow(),GLFW.GLFW_KEY_LEFT_ALT)==GLFW.GLFW_PRESS)for(int i=0;i<menu.slots.size();i++){var slot=menu.slots.get(i);graphics.drawString(font,Integer.toString(i),leftPos+slot.x+2,topPos+slot.y,0xffffff,true);graphics.drawString(font,Integer.toString(slot.getContainerSlot()),leftPos+slot.x+2,topPos+slot.y+8,0xff8080,true);}
    }
    @Override public boolean mouseClicked(double x,double y,int button){
        boolean handled=super.mouseClicked(x,y,button);
        for(int group=0;group<2;group++)for(int row=0;row<3;row++)if(legacyInfoHovered(x,y,7+group*222,16+row*26,18,18)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));PacketDistributor.sendToServer(new CraneRouterControl(menu.router.getBlockPos(),group*3+row));return true;}
        return handled;
    }
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){
        super.render(graphics,x,y,partial);
        for(int group=0;group<2;group++)for(int row=0;row<3;row++)if(legacyInfoHovered(x,y,7+group*222,16+row*26,18,18)){
            int mode=menu.router.modes[group*3+row];var lines=new ArrayList<Component>();lines.add(Component.translatable("gui.hbm.crane_router.mode."+mode));if(mode!=0)lines.add(Component.translatable("gui.hbm.crane_router.mode."+mode+".desc"));graphics.renderComponentTooltip(font,lines,x,y);
        }
        if(menu.getCarried().isEmpty())for(int i=0;i<30;i++){var slot=menu.slots.get(i);var mode=menu.router.patterns[i/5].modes[i%5];if(legacyInfoHovered(x,y,slot.x,slot.y,16,16)&&mode!=null)graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.pneumatic.change_mode").withStyle(ChatFormatting.RED),PatternMatcher.label(mode)),x,y-30);}
    }
}

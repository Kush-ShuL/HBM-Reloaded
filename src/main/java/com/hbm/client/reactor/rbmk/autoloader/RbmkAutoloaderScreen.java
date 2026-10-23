package com.hbm.client.reactor.rbmk.autoloader;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.reactor.rbmk.autoloader.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Source176x182screen, exact two12px control areas and original18-grid positions. */
public final class RbmkAutoloaderScreen extends LegacyContainerScreen<RbmkAutoloaderMenu> {
    public RbmkAutoloaderScreen(RbmkAutoloaderMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/machine/gui_autoloader.png"),176,182);}
    private RbmkAutoloaderEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof RbmkAutoloaderEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,0xffffff,false);g.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);var machine=machine();if(machine!=null){String text=machine.cycle+"%";g.drawString(font,text,imageWidth/2-font.width(text)/2,23,0x00ff00,false);}}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);for(int i=0;i<2;i++)if(legacyInfoHovered(x,y,74+i*16,36,12,12)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new RbmkAutoloaderControl(menu.containerId,i==1));return true;}return handled;}
}

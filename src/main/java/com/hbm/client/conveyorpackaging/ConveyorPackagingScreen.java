package com.hbm.client.conveyorpackaging;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.conveyorpackaging.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Exact two source176x185 screens, actual slots/title and Boxer's four-mode texture button. */
public final class ConveyorPackagingScreen extends LegacyContainerScreen<ConveyorPackagingMenu> {
    public ConveyorPackagingScreen(ConveyorPackagingMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/storage/gui_crane_"+(menu.unboxer?"unboxer":"boxer")+".png"),176,185);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);if(!menu.unboxer&&menu.machine instanceof ConveyorPackagingEntity crane)graphics.blit(ResourceLocation.parse("hbm:textures/gui/storage/gui_crane_boxer.png"),leftPos+151,topPos+34,176,crane.mode*18,18,18);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){Component name=menu.machine instanceof ConveyorPackagingEntity crane?crane.getDisplayName():title;graphics.drawString(font,name,88-font.width(name)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,91,4210752,false);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(!menu.unboxer&&leftPos+151<=x&&leftPos+169>x&&topPos+34<y&&topPos+52>=y){net.minecraft.client.resources.sounds.SimpleSoundInstance sound=net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,1F);minecraft.getSoundManager().play(sound);PacketDistributor.sendToServer(new ConveyorPackagingControl(menu.position));return true;}return handled;}
}

package com.hbm.client.siren;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.siren.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** X5846 exact176x166 background, labels and slot coordinates, current chosen Minecraft font. */
public final class SirenScreen extends LegacyContainerScreen<SirenMenu> {
    public SirenScreen(SirenMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/gui_siren.png"),176,166);}
    @Override protected void renderLabels(GuiGraphics graphics,int mouseX,int mouseY){graphics.drawString(font,title,(imageWidth-font.width(title))/2,6,0x404040,false);graphics.drawString(font,playerInventoryTitle,8,imageHeight-96+2,0x404040,false);var track=menu.machine.track();if(track!=SirenTrack.NULL){graphics.drawString(font,track.title(),46,28,track.color,false);graphics.drawString(font,Component.translatable("gui.hbm.siren.type",track.typeName()),46,40,track.color,false);graphics.drawString(font,Component.translatable("gui.hbm.siren.volume",track.volume),46,52,track.color,false);}}
}

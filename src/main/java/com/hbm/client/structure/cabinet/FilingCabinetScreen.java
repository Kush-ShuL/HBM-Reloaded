package com.hbm.client.structure.cabinet;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.world.structure.cabinet.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
/** Unchanged source GUIFileCabinet 176x170 pixels and all title/player-slot coordinates. */
public final class FilingCabinetScreen extends LegacyContainerScreen<FilingCabinetMenu>{
 public FilingCabinetScreen(FilingCabinetMenu menu,Inventory inventory,Component title){super(menu,inventory,title,FilingCabinetContent.id("textures/gui/storage/gui_file_cabinet.png"),176,170);}
 @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,176/2-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,76,4210752,false);}
}

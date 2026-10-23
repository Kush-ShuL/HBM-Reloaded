package com.hbm.client.storage;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.storage.StorageCrateKind;
import com.hbm.machine.storage.StorageCrateMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Unchanged source backgrounds, coordinates and font settings; safe retains its own ordinary click behavior. */
public final class StorageCrateScreen extends LegacyContainerScreen<StorageCrateMenu> {
    public StorageCrateScreen(StorageCrateMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse(menu.kind.texture()),menu.kind.width,menu.kind.height);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){
        if(menu.kind==StorageCrateKind.TUNGSTEN&&menu.container instanceof com.hbm.machine.storage.StorageCrateBlockEntity crate&&crate.getBlockState().getValue(com.hbm.machine.storage.StorageCrateBlock.LEGACY_META)!=0){
            graphics.setColor(1,1,1,1);drawTexturedModalRect(graphics,ResourceLocation.parse("hbm:textures/gui/storage/gui_crate_tungsten_hot.png"),leftPos,topPos,0,0,imageWidth,imageHeight,256,256);
        }else super.renderBg(graphics,partial,x,y);
    }
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){var kind=menu.kind;graphics.drawString(font,title,kind.width/2-font.width(title)/2,6,kind.titleColor,false);graphics.drawString(font,playerInventoryTitle,kind.playerX,kind.height-96+(kind==StorageCrateKind.DESH?3:2),kind.titleColor,false);}
    @Override public boolean mouseClicked(double x,double y,int button){if(menu.kind==StorageCrateKind.SAFE)return super.mouseClicked(x,y,button);boolean touch=minecraft.options.touchscreen().get();minecraft.options.touchscreen().set(false);try{return super.mouseClicked(x,y,button);}finally{minecraft.options.touchscreen().set(touch);}}
}

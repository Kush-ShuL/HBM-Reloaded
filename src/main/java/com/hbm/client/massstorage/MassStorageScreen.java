package com.hbm.client.massstorage;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.massstorage.*;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Unchanged original176x221 background, labels,88px gauge and two source14px control hitboxes. */
public final class MassStorageScreen extends LegacyContainerScreen<MassStorageMenu> {
    public MassStorageScreen(MassStorageMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/storage/gui_mass_storage.png"),176,221);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,127,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var storage=menu.storage;int gauge=storage.getStockpile()*88/storage.getCapacity();drawLegacyRegion(graphics,97,105-gauge,176,88-gauge,16,gauge);if(storage.output)drawLegacyRegion(graphics,80,72,192,0,14,14);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var storage=menu.storage;
        if(legacyInfoHovered(x,y,96,16,18,90)){String percent=((int)(storage.getStockpile()*1000D/storage.getCapacity())/10D)+"%";graphics.renderComponentTooltip(font,List.of(Component.literal(String.format(Locale.US,"%,d / %,d",storage.getStockpile(),storage.getCapacity())),Component.literal(percent)),x,y);}
        if(legacyInfoHovered(x,y,62,72,14,14))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.mass_storage.provide_one"),Component.translatable("gui.hbm.mass_storage.provide_stack")),x,y);
        if(legacyInfoHovered(x,y,80,72,14,14))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.mass_storage.toggle_output")),x,y);
    }
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,62,72,14,14)){send(false);return true;}if(legacyInfoHovered(x,y,80,72,14,14)){send(true);return true;}return handled;}
    private void send(boolean toggle){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));boolean leftShift=GLFW.glfwGetKey(minecraft.getWindow().getWindow(),GLFW.GLFW_KEY_LEFT_SHIFT)==GLFW.GLFW_PRESS;PacketDistributor.sendToServer(new MassStoragePackets.Control(menu.storage.getBlockPos(),toggle,leftShift));}
}

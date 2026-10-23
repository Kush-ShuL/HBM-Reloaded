package com.hbm.client.holotape;

import com.hbm.client.gui.LegacyScreen;
import com.hbm.item.holotape.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import org.lwjgl.glfw.GLFW;

/** Original300×150 translucent green narrative panel; it contains no source image/music decoding branch. */
public final class HolotapeScreen extends LegacyScreen {
    private final InteractionHand hand;private HolotapeImageType image;
    public HolotapeScreen(InteractionHand hand){super(Component.translatable("item.holotape_image.name"));this.hand=hand;}
    @Override protected void init(){super.init();minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ResourceLocation.parse("hbm:block.bobble")),1));if(minecraft.player!=null&&minecraft.player.getItemInHand(hand).getItem() instanceof HolotapeImageItem)image=HolotapeImageItem.type(minecraft.player.getItemInHand(hand));else onClose();}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){if(image==null)return;renderBackground(graphics,mouseX,mouseY,partial);double left=(width-300)/2D,top=(height-150)/2D;graphics.pose().pushPose();graphics.pose().translate(left,top,0);graphics.fill(0,0,300,150,0xcc003300);graphics.pose().popPose();int y=(int)top+30;for(var line:font.getSplitter().splitLines(image.text().getString(),275,Style.EMPTY)){String text=line.getString();graphics.drawString(font,text,(int)(left+150-font.width(text)/2),y,0x009900,true);y+=10;}}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==GLFW.GLFW_KEY_ESCAPE||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return super.keyPressed(key,scan,modifiers);}
    @Override public boolean isPauseScreen(){return false;}
}

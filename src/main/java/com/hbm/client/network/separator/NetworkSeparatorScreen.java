package com.hbm.client.network.separator;
import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.energy.ConnectionPriority;
import com.hbm.network.separator.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
/** Original GUIDiode geometry, old widget pixels and close-only authoritative update. No invented container. */
public final class NetworkSeparatorScreen extends LegacyScreen {
 private static final ResourceLocation WIDGETS=ResourceLocation.parse("hbm:textures/gui/network/source_widgets_1_7_10.png");
 private final NetworkSeparatorEntity tile;private EditBox throughput;private int priority,fieldX,buttonX;private boolean sent;
 public NetworkSeparatorScreen(NetworkSeparatorEntity tile){super(Component.empty());this.tile=tile;this.priority=tile.priority.ordinal();}
 @Override protected void init(){font=LegacyFonts.get();fieldX=width/2-150;buttonX=width/2+20;String previous=throughput==null?tile.configuredLimit.toString():throughput.getValue();throughput=new EditBox(font,fieldX+4,106,82,8,Component.translatable("gui.hbm.network_separator.throughput"));throughput.setBordered(false);throughput.setMaxLength(11);throughput.setValue(previous);throughput.setTextColor(0xffe0e0e0);throughput.setFocused(false);addWidget(throughput);}
 @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){if(tile.isRemoved()){onClose();return;}renderBackground(graphics,mouseX,mouseY,partial);graphics.drawString(font,Component.translatable("gui.hbm.network_separator.throughput"),fieldX,80,0xa0a0a0,true);graphics.drawString(font,Component.translatable("gui.hbm.network_separator.max"),fieldX,90,0xa0a0a0,true);graphics.fill(fieldX-1,99,fieldX+91,121,0xffa0a0a0);graphics.fill(fieldX,100,fieldX+90,120,0xff000000);throughput.render(graphics,mouseX,mouseY,partial);graphics.drawString(font,Component.translatable("gui.hbm.network_separator.priority"),buttonX,80,0xa0a0a0,true);boolean over=buttonAt(mouseX,mouseY);int v=over?86:66;graphics.blit(WIDGETS,buttonX,100,0F,v,45,20,256,256);graphics.blit(WIDGETS,buttonX+45,100,155F,v,45,20,256,256);var title=NetworkSeparatorReadout.priority(ConnectionPriority.values()[priority]);graphics.drawString(font,title,buttonX+45-font.width(title)/2,106,over?16777120:14737632,true);}
 private boolean buttonAt(double x,double y){return x>=buttonX&&x<buttonX+90&&y>=100&&y<120;}
 @Override public boolean mouseClicked(double x,double y,int button){boolean field=x>=fieldX&&x<fieldX+90&&y>=100&&y<120;throughput.setFocused(field);if(field){setFocused(throughput);return throughput.mouseClicked(x,Math.max(106,Math.min(113,y)),button);}setFocused(null);if(button==0&&buttonAt(x,y)){priority=(priority+1)%ConnectionPriority.values().length;minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));return true;}return false;}
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(throughput.keyPressed(key,scan,modifiers))return true;if(key==GLFW.GLFW_KEY_ESCAPE||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return false;}
 @Override public boolean charTyped(char character,int modifiers){return throughput.charTyped(character,modifiers);}
 @Override public void removed(){super.removed();if(sent||throughput==null)return;sent=true;var data=new CompoundTag();data.putByte("priority",(byte)priority);try{data.putLong("limit",Long.parseLong(throughput.getValue()));}catch(NumberFormatException ignored){}PacketDistributor.sendToServer(new NetworkSeparatorControl(tile.getBlockPos(),data));}
 @Override public boolean isPauseScreen(){return false;}
}

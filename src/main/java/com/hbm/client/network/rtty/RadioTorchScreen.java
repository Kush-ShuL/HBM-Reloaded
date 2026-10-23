package com.hbm.client.network.rtty;
import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.network.rtty.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
/** GUIScreenRadioTorch: actual 256x204 atlas, collapsed42px strip, seventeen green fields and three source hitboxes. */
public final class RadioTorchScreen extends LegacyScreen {
 private final RadioTorchEntity radio;private final boolean sender;private final ResourceLocation texture;private int left,top;private EditBox frequency;private final EditBox[] remap=new EditBox[16];
 public RadioTorchScreen(RadioTorchEntity radio){super(Component.translatable(radio instanceof RadioTorchSenderEntity?"container.rttySender":"container.rttyReceiver"));this.radio=radio;sender=radio instanceof RadioTorchSenderEntity;texture=ResourceLocation.parse("hbm:textures/gui/machine/gui_rtty_"+(sender?"sender":"receiver")+".png");}
 @Override protected void init(){font=LegacyFonts.get();left=(width-256)/2;top=(height-204)/2;frequency=field(left+29,top+21,15,radio.channel,"gui.hbm.rtty.frequency");for(int i=0;i<16;i++)remap[i]=field(left+11+130*(i/8)+(sender?18:0),top+57+18*(i%8),32,radio.mapping[i],"gui.hbm.rtty.mapping");}
 private EditBox field(int x,int y,int max,String value,String name){var field=new EditBox(font,x,y,82,14,Component.translatable(name));field.setBordered(false);field.setTextColor(0x00ff00);field.setTextColorUneditable(0x00ff00);field.setMaxLength(max);field.setValue(value==null?"":value);field.setFocused(false);addWidget(field);return field;}
 private boolean hovered(double x,double y,int ox){return left+ox<=x&&x<left+ox+18&&top+17<y&&y<=top+35;}
 private void renderField(EditBox field,GuiGraphics graphics,int mx,int my,float partial){if(field.isFocused())field.setFocused(true);field.render(graphics,mx,my,partial);}
 @Override public void render(GuiGraphics graphics,int mx,int my,float partial){
  renderBackground(graphics,mx,my,partial);
  if(radio.customMap){graphics.blit(texture,left,top,0F,0F,256,204,256,256);graphics.blit(texture,left+137,top+17,0F,204F,18,18,256,256);if(radio.polling)graphics.blit(texture,left+173,top+17,0F,222F,18,18,256,256);for(var field:remap)renderField(field,graphics,mx,my,partial);}else{graphics.blit(texture,left,top,0F,0F,256,35,256,256);graphics.blit(texture,left,top+35,0F,197F,256,7,256,256);if(radio.polling)graphics.blit(texture,left+173,top+17,0F,222F,18,18,256,256);}
  renderField(frequency,graphics,mx,my,partial);graphics.drawString(font,title,left+128-font.width(title)/2,top+6,4210752,false);
  if(hovered(mx,my,137))graphics.renderTooltip(font,Component.translatable(radio.customMap?"gui.hbm.rtty.custom":"gui.hbm.rtty.passthrough"),mx,my);if(hovered(mx,my,173))graphics.renderTooltip(font,Component.translatable(radio.polling?"gui.hbm.rtty.polling":"gui.hbm.rtty.state_change"),mx,my);if(hovered(mx,my,209))graphics.renderTooltip(font,Component.translatable("gui.hbm.radio.save_settings"),mx,my);
 }
 @Override public boolean mouseClicked(double x,double y,int button){boolean clicked=frequency.mouseClicked(x,y,button);if(radio.customMap)for(var field:remap)clicked=field.mouseClicked(x,y,button)||clicked;setFocused(frequency.isFocused()?frequency:null);if(radio.customMap)for(var field:remap)if(field.isFocused())setFocused(field);var data=new CompoundTag();if(hovered(x,y,137))data.putBoolean("m",!radio.customMap);if(hovered(x,y,173))data.putBoolean("p",!radio.polling);if(hovered(x,y,209)){data.putString("c",frequency.getValue());for(int i=0;i<16;i++)data.putString("m"+i,remap[i].getValue());}if(!data.isEmpty()){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));PacketDistributor.sendToServer(new RadioTorchControl(radio.getBlockPos(),data));return true;}return clicked;}
 @Override public boolean charTyped(char c,int modifiers){if(frequency.charTyped(c,modifiers))return true;if(radio.customMap)for(var field:remap)if(field.charTyped(c,modifiers))return true;return false;}
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(frequency.keyPressed(key,scan,modifiers))return true;if(radio.customMap)for(var field:remap)if(field.keyPressed(key,scan,modifiers))return true;boolean focused=frequency.isFocused();if(radio.customMap)for(var field:remap)focused|=field.isFocused();if(key==GLFW.GLFW_KEY_ESCAPE||!focused&&minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return false;}
 @Override public boolean isPauseScreen(){return false;}
}

package com.hbm.client.network.rtty.logic;
import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.network.rtty.logic.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
/** Original GUI, including unsaved local condition edits but server-saved mapping highlights. */
public final class RadioLogicScreen extends LegacyScreen {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/machine/gui_rtty_logic_receiver.png");private final RadioLogicEntity logic;private EditBox frequency;private final EditBox[] map=new EditBox[16];private final int[] conditions=new int[16];private int left,top;
 public RadioLogicScreen(RadioLogicEntity logic){super(Component.translatable("container.rttyLogic"));this.logic=logic;}
 @Override protected void init(){font=LegacyFonts.get();left=(width-256)/2;top=(height-204)/2;frequency=field(left+29,top+21,82,logic.channel,"gui.hbm.rtty.frequency");for(int i=0;i<16;i++){map[i]=field(left+29+130*(i/8),top+57+18*(i%8),46,logic.mapping[i],"gui.hbm.rtty.mapping");conditions[i]=logic.conditions[i];}}
 private EditBox field(int x,int y,int w,String text,String name){var field=new EditBox(font,x,y,w,14,Component.translatable(name));field.setTextColor(0x00ff00);field.setTextColorUneditable(0x00ff00);field.setBordered(false);field.setMaxLength(15);field.setValue(text==null?"":text);field.setFocused(false);addWidget(field);return field;}
 private boolean button(double x,double y,int ox){return left+ox<=x&&x<left+ox+18&&top+17<y&&y<=top+35;}
 private int conditionAt(double x,double y){for(int i=0;i<16;i++){int cx=left+7+130*(i/8),cy=top+53+18*(i%8);if(x>=cx&&x<cx+18&&y>=cy&&y<cy+18)return i;}return -1;}
 private void renderField(EditBox field,GuiGraphics graphics,int x,int y,float partial){if(field.isFocused())field.setFocused(true);field.render(graphics,x,y,partial);}
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){
  renderBackground(graphics,x,y,partial);graphics.blit(TEXTURE,left,top,0F,0F,256,204,256,256);if(logic.descending)graphics.blit(TEXTURE,left+137,top+17,0F,204F,18,18,256,256);if(logic.polling)graphics.blit(TEXTURE,left+173,top+17,0F,222F,18,18,256,256);
  for(int i=0;i<16;i++){int cx=left+7+130*(i/8),cy=top+53+18*(i%8);if(logic.mapping[i].isEmpty()){if(conditions[i]!=0)graphics.blit(TEXTURE,cx,cy,18F+conditions[i]*18,222F,18,18,256,256);}else{graphics.blit(TEXTURE,cx,cy,18F+conditions[i]*18,204F,18,18,256,256);graphics.blit(TEXTURE,left+85+130*(i/8),top+57+18*(i%8),198F,204F,14,10,256,256);}}
  for(var field:map)renderField(field,graphics,x,y,partial);renderField(frequency,graphics,x,y,partial);graphics.drawString(font,title,left+128-font.width(title)/2,top+6,4210752,false);
  if(button(x,y,137))graphics.renderTooltip(font,Component.translatable(logic.descending?"gui.hbm.rtty.descending":"gui.hbm.rtty.ascending"),x,y);if(button(x,y,173))graphics.renderTooltip(font,Component.translatable(logic.polling?"gui.hbm.rtty.polling":"gui.hbm.rtty.state_change"),x,y);if(button(x,y,209))graphics.renderTooltip(font,Component.translatable("gui.hbm.radio.save_settings"),x,y);int condition=conditionAt(x,y);if(condition>=0)graphics.renderTooltip(font,Component.translatable("desc.gui.rttyLogic.cond"+conditions[condition]),x,y);
 }
 @Override public boolean mouseClicked(double x,double y,int button){boolean field=frequency.mouseClicked(x,y,button);for(var text:map)field=text.mouseClicked(x,y,button)||field;setFocused(frequency.isFocused()?frequency:null);for(var text:map)if(text.isFocused())setFocused(text);var data=new CompoundTag();if(button(x,y,137))data.putBoolean("d",!logic.descending);if(button(x,y,173))data.putBoolean("p",!logic.polling);if(button(x,y,209)){data.putString("c",frequency.getValue());for(int i=0;i<16;i++){data.putString("m"+i,map[i].getValue());data.putInt("c"+i,conditions[i]);}}boolean handled=!data.isEmpty();if(handled){click();PacketDistributor.sendToServer(new RadioLogicControl(logic.getBlockPos(),data));}int condition=conditionAt(x,y);if(condition>=0){click();conditions[condition]=(conditions[condition]+1)%10;handled=true;}return field||handled;}
 @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){long window=minecraft.getWindow().getWindow();if(GLFW.glfwGetMouseButton(window,GLFW.GLFW_MOUSE_BUTTON_LEFT)==GLFW.GLFW_PRESS||GLFW.glfwGetMouseButton(window,GLFW.GLFW_MOUSE_BUTTON_RIGHT)==GLFW.GLFW_PRESS)return false;int at=conditionAt(x,y);if(at<0||vertical==0)return false;conditions[at]=(conditions[at]+(vertical>0?1:9))%10;return true;}
 private void click(){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));}
 @Override public boolean charTyped(char c,int modifiers){if(frequency.charTyped(c,modifiers))return true;for(var field:map)if(field.charTyped(c,modifiers))return true;return false;}
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(frequency.keyPressed(key,scan,modifiers))return true;for(var field:map)if(field.keyPressed(key,scan,modifiers))return true;boolean focused=frequency.isFocused();for(var field:map)focused|=field.isFocused();if(key==GLFW.GLFW_KEY_ESCAPE||!focused&&minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return false;}
 @Override public boolean isPauseScreen(){return false;}
}

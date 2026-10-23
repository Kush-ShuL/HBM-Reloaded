package com.hbm.client.network.rtty.ror;
import api.hbm.redstoneoverradio.IRORInfo;
import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.network.rtty.ror.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
/** The two actual original layouts, using real attached IRORInfo for the original help lists. */
public final class RadioRorScreen extends LegacyScreen {
 private final RadioRorEntity radio;private final boolean reader;private final ResourceLocation texture;private int left,top;private EditBox[] fields;
 public RadioRorScreen(RadioRorEntity radio){super(Component.translatable(radio instanceof RadioReaderEntity?"container.rttyReader":"container.rttyController"));this.radio=radio;reader=radio instanceof RadioReaderEntity;texture=ResourceLocation.parse("hbm:textures/gui/machine/gui_rtty_"+(reader?"reader":"controller")+".png");}
 @Override protected void init(){font=LegacyFonts.get();left=(width-256)/2;top=(height-(reader?204:42))/2;fields=new EditBox[reader?16:1];if(radio instanceof RadioReaderEntity input){for(int i=0;i<8;i++){fields[i]=field(left+29,top+57+18*i,64,15,input.channels[i],"gui.hbm.rtty.frequency");fields[i+8]=field(left+123,top+57+18*i,118,25,input.names[i],"gui.hbm.rtty.value_name");}}else fields[0]=field(left+29,top+21,82,15,((RadioControllerEntity)radio).channel,"gui.hbm.rtty.frequency");}
 private EditBox field(int x,int y,int width,int max,String value,String key){var field=new EditBox(font,x,y,width,14,Component.translatable(key));field.setTextColor(0x00ff00);field.setTextColorUneditable(0x00ff00);field.setBordered(false);field.setMaxLength(max);field.setValue(value==null?"":value);field.setFocused(false);addWidget(field);return field;}
 private boolean hovered(double x,double y,int ox){return left+ox<=x&&x<left+ox+18&&top+17<y&&y<=top+35;}
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){renderBackground(graphics,x,y,partial);graphics.blit(texture,left,top,0F,0F,256,reader?204:42,256,256);if(radio.polling)graphics.blit(texture,left+173,top+17,0F,reader?204F:42F,18,18,256,256);for(var field:fields){if(field.isFocused())field.setFocused(true);field.render(graphics,x,y,partial);}graphics.drawString(font,title,left+128-font.width(title)/2,top+6,4210752,false);if(hovered(x,y,173))graphics.renderTooltip(font,Component.translatable(radio.polling?"gui.hbm.rtty.polling":"gui.hbm.rtty.state_change"),x,y);if(hovered(x,y,209))graphics.renderTooltip(font,Component.translatable("gui.hbm.radio.save_settings"),x,y);if(hovered(x,y,reader?29:137)&&radio.support() instanceof IRORInfo provider){var lines=new ArrayList<Component>();lines.add(Component.translatable(reader?"gui.hbm.rtty.readable_values":"gui.hbm.rtty.usable_functions"));String prefix=reader?IRORInfo.PREFIX_VALUE:IRORInfo.PREFIX_FUNCTION;String[] info=provider.getFunctionInfo();if(info!=null)for(String line:info)if(line.startsWith(prefix))lines.add(Component.literal(line.substring(4)).withStyle(reader?ChatFormatting.LIGHT_PURPLE:ChatFormatting.AQUA));graphics.renderComponentTooltip(font,lines,x,y);}}
 @Override public boolean mouseClicked(double x,double y,int button){boolean clicked=false;for(var field:fields)clicked=field.mouseClicked(x,y,button)||clicked;setFocused(null);for(var field:fields)if(field.isFocused())setFocused(field);var data=new CompoundTag();if(hovered(x,y,173))data.putBoolean("p",!radio.polling);if(hovered(x,y,209)){if(reader){for(int i=0;i<8;i++){data.putString("c"+i,fields[i].getValue());data.putString("n"+i,fields[i+8].getValue());}}else data.putString("c",fields[0].getValue());}if(!data.isEmpty()){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));PacketDistributor.sendToServer(new RadioRorControl(radio.getBlockPos(),data));return true;}return clicked;}
 @Override public boolean charTyped(char c,int modifiers){for(var field:fields)if(field.charTyped(c,modifiers))return true;return false;}
 @Override public boolean keyPressed(int key,int scan,int modifiers){for(var field:fields)if(field.keyPressed(key,scan,modifiers))return true;boolean focused=false;for(var field:fields)focused|=field.isFocused();if(key==GLFW.GLFW_KEY_ESCAPE||!focused&&minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return false;}
 @Override public boolean isPauseScreen(){return false;}
}

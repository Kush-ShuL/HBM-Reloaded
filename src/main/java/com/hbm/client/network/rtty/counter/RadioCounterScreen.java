package com.hbm.client.network.rtty.counter;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.network.rtty.counter.*;
import com.hbm.pneumatic.PatternMatcher;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Source GUICounterTorch218x238, exact3fields/3ghosts/player inventory and18px controls. */
public final class RadioCounterScreen extends LegacyContainerScreen<RadioCounterMenu> {
 private final RadioCounterEntity counter;private final EditBox[] frequency=new EditBox[3];
 public RadioCounterScreen(RadioCounterMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/machine/gui_rtty_counter.png"),218,238);counter=menu.counter;}
 @Override protected void init(){super.init();for(int i=0;i<3;i++){var field=new EditBox(font,leftPos+29,topPos+21+44*i,86,14,Component.translatable("gui.hbm.rtty.frequency"));field.setTextColor(0x00ff00);field.setTextColorUneditable(0x00ff00);field.setBordered(false);field.setMaxLength(10);field.setValue(counter.channel[i]==null?"":counter.channel[i]);field.setFocused(false);frequency[i]=addWidget(field);}}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int mx,int my){super.renderBg(graphics,partial,mx,my);if(counter.polling)drawLegacyRegion(graphics,193,8,218,0,18,18);for(var field:frequency){if(field.isFocused())field.setFocused(true);field.render(graphics,mx,my,partial);}}
 @Override protected void renderLabels(GuiGraphics graphics,int mx,int my){graphics.drawString(font,title,92-font.width(title)/2,6,4210752,false);graphics.drawString(font,Component.translatable("container.inventory"),16,144,4210752,false);}
 @Override public void render(GuiGraphics graphics,int mx,int my,float partial){super.render(graphics,mx,my,partial);if(legacyInfoHovered(mx,my,193,8,18,18))graphics.renderTooltip(font,Component.translatable(counter.polling?"gui.hbm.rtty.polling":"gui.hbm.rtty.state_change"),mx,my);if(legacyInfoHovered(mx,my,193,30,18,18))graphics.renderTooltip(font,Component.translatable("gui.hbm.radio.save_settings"),mx,my);if(menu.getCarried().isEmpty())for(int i=0;i<3;i++){var slot=menu.getSlot(i);if(isHovering(slot.x,slot.y,16,16,mx,my)&&counter.matcher.modes[i]!=null)graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.rtty.change_match").withStyle(net.minecraft.ChatFormatting.RED),PatternMatcher.label(counter.matcher.modes[i])),mx,my-30);}}
 @Override public boolean mouseClicked(double x,double y,int button){boolean clicked=super.mouseClicked(x,y,button);for(var field:frequency)clicked=field.mouseClicked(x,y,button)||clicked;setFocused(null);for(var field:frequency)if(field.isFocused())setFocused(field);var data=new CompoundTag();if(legacyInfoHovered(x,y,193,8,18,18))data.putBoolean("polling",true);if(legacyInfoHovered(x,y,193,30,18,18))for(int i=0;i<3;i++)data.putString("c"+i,frequency[i].getValue());if(!data.isEmpty()){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));PacketDistributor.sendToServer(new RadioCounterControl(counter.getBlockPos(),data));return true;}return clicked;}
 @Override public boolean charTyped(char c,int modifiers){for(var field:frequency)if(field.charTyped(c,modifiers))return true;return super.charTyped(c,modifiers);}
 @Override public boolean keyPressed(int key,int scan,int modifiers){for(var field:frequency)if(field.keyPressed(key,scan,modifiers))return true;boolean focused=false;for(var field:frequency)focused|=field.isFocused();if(focused&&key!=org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE&&minecraft.options.keyInventory.matches(key,scan))return false;return super.keyPressed(key,scan,modifiers);}
 @Override public boolean isPauseScreen(){return false;}
}

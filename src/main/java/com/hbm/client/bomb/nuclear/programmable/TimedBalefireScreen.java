package com.hbm.client.bomb.nuclear.programmable;
import com.hbm.bomb.nuclear.programmable.*;
import com.hbm.client.gui.LegacyContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/** Original 176x222 FSTBMB schematic, source timer editor and three exact status sprites. */
public final class TimedBalefireScreen extends LegacyContainerScreen<TimedBalefireMenu> {
 private EditBox timer;
 public TimedBalefireScreen(TimedBalefireMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/weapon/fstbmbschematic.png"),176,222);}
 @Override protected void init(){super.init();timer=new EditBox(font,leftPos+94,topPos+40,29,12,Component.translatable("gui.hbm.fstbmb.timer"));timer.setTextColor(0xff0000);timer.setTextColorUneditable(0x800000);timer.setBordered(false);timer.setMaxLength(3);timer.setValue(""+(menu.timer()/20));timer.setResponder(value->{try{double parsed=Double.parseDouble(value);if(Double.isFinite(parsed))send(Mth.clamp((int)parsed,1,999));}catch(NumberFormatException ignored){}});addWidget(timer);}
 private void send(int value){if(minecraft!=null&&minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,value);}
 @Override public boolean mouseClicked(double x,double y,int button){boolean used=super.mouseClicked(x,y,button);timer.mouseClicked(x,y,button);if(!menu.started()&&legacyInfoHovered(x,y,142,35,18,18)){send(0);return true;}return used;}
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(timer.keyPressed(key,scan,modifiers)||timer.canConsumeInput()&&key!=256)return true;return super.keyPressed(key,scan,modifiers);}
 @Override public boolean charTyped(char character,int modifiers){return timer.charTyped(character,modifiers)||super.charTyped(character,modifiers);}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,128,4210752,false);if(TimedBalefireBlockEntity.battery(menu.bomb)>0){String text=TimedBalefireBlockEntity.clock(menu.timer());g.pose().pushPose();g.pose().scale(.75F,.75F,.75F);g.drawString(font,text,(int)((69-font.width(text)/2)* (1/.75D)),(int)(95.5*(1/.75D)),0xff0000,false);g.pose().popPose();}}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);if(TimedBalefireBlockEntity.hasEgg(menu.bomb))drawLegacyRegion(g,19,90,176,0,30,16);int battery=TimedBalefireBlockEntity.battery(menu.bomb);if(battery==1)drawLegacyRegion(g,88,93,176,16,18,10);else if(battery==2)drawLegacyRegion(g,88,93,194,16,18,10);if(menu.started())drawLegacyRegion(g,142,35,176,26,18,18);timer.render(g,x,y,partial);}
}

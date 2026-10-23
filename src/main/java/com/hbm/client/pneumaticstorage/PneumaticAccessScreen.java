package com.hbm.client.pneumaticstorage;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.pneumaticstorage.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.network.PacketDistributor;

/** Actual48-slot native cache view. Client only selects hashes; server alone owns every quantity and transfer. */
public final class PneumaticAccessScreen extends LegacyContainerScreen<PneumaticAccessMenu> {
 private EditBox search;private boolean draggingScroll;private static int sorting;private static boolean startFocused,detailed;
 public PneumaticAccessScreen(PneumaticAccessMenu menu,Inventory inv,Component title){super(menu,inv,title,PneumaticStorageContent.id("textures/gui/storage/gui_pneumatic_access.png"),210,251);}
 @Override protected void init(){super.init();search=new EditBox(font,leftPos+79,topPos+127,86,12,Component.translatable("gui.hbm.pneumatic_storage.search"));search.setTextColor(0xFFFFFF);search.setTextColorUneditable(0xA0A0A0);search.setBordered(false);search.setMaxLength(50);search.setFocused(startFocused);search.setValue("");search.setResponder(value->updateSearch());menu.sort=sorting;updateSearch();}
 private int scrollBounds(){return Math.max(1,(int)Math.ceil(menu.listingSize/8D-6));}
 private void scroll(int row){menu.listingStart=Math.clamp(row,0,scrollBounds());menu.rebuild();}
 private void updateSearch(){if(search==null)return;menu.search=search.getValue();menu.listingStart=0;menu.detailedMatch=detailed?stack->{String term=menu.search.toLowerCase(Locale.US);var flag=minecraft.options.advancedItemTooltips?TooltipFlag.ADVANCED:TooltipFlag.NORMAL;return stack.getTooltipLines(Item.TooltipContext.of(minecraft.level),minecraft.player,flag).stream().anyMatch(line->line.getString().toLowerCase(Locale.US).contains(term));}:null;menu.rebuild();}
 private void click(){minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(),1,1);}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,122-font.width(title)/2,5,4210752,false);g.drawString(font,playerInventoryTitle,42,157,4210752,false);g.pose().pushPose();g.pose().translate(0,0,200);g.pose().scale(.5F,.5F,1);for(int i=0;i<48;i++){var entry=menu.visible[i];if(entry==null)continue;var slot=menu.slots.get(i);String label=BatteryItem.shortNumber(entry.amount);g.drawString(font,label,(slot.x+16)*2-font.width(label),(slot.y+16)*2-font.lineHeight,0xFFFFFFFF,true);}g.pose().popPose();}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){g.setColor(1,1,1,1);drawTexturedModalRect(g,leftPos+34,topPos,0,0,176,251);drawTexturedModalRect(g,leftPos,topPos,176,15,32,122);drawLegacyRegion(g,7,7+sorting*18,208,0,18,18);if(startFocused)drawLegacyRegion(g,7,79,208,18,18,18);if(detailed)drawLegacyRegion(g,7,97,208,18,18,18);int scrollY=17+(int)(Math.min(1D,(double)menu.listingStart/scrollBounds())*91);drawLegacyRegion(g,188,scrollY,draggingScroll?188:176,0,12,15);g.pose().pushPose();g.pose().translate(0,2,0);search.render(g,x,y,partial);g.pose().popPose();}
 @Override public boolean mouseClicked(double x,double y,int button){if(legacyInfoHovered(x,y,7,7,18,108)){int selected=(int)(y-topPos-7)/18;if(selected>=0&&selected<6){click();if(selected<4){sorting=selected;menu.sort=sorting;scroll(0);}else if(selected==4)startFocused=!startFocused;else{detailed=!detailed;updateSearch();}search.mouseClicked(x,y,button);return true;}}if(button==0&&legacyInfoHovered(x,y,187,16,14,108)){draggingScroll=true;dragScroll(y);return true;}boolean result=super.mouseClicked(x,y,button);return search.mouseClicked(x,y,button)||result;}
 private void dragScroll(double y){int distance=(int)Math.clamp(y-topPos-24,0,92);scroll((int)Math.round(scrollBounds()*((double)distance/92)));}
 @Override public boolean mouseDragged(double x,double y,int button,double dx,double dy){if(draggingScroll&&button==0){dragScroll(y);return true;}return super.mouseDragged(x,y,button,dx,dy);}
 @Override public boolean mouseReleased(double x,double y,int button){if(button==0)draggingScroll=false;return super.mouseReleased(x,y,button);}
 @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical){if(legacyInfoHovered(x,y,0,0,210,251)&&vertical!=0){scroll(menu.listingStart-(vertical>0?1:-1));return true;}return super.mouseScrolled(x,y,horizontal,vertical);}
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(search.keyPressed(key,scan,modifiers))return true;if(search.isFocused()&&key!=org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE&&key!=org.lwjgl.glfw.GLFW.GLFW_KEY_TAB)return true;return super.keyPressed(key,scan,modifiers);}
 @Override public boolean charTyped(char character,int modifiers){return search.charTyped(character,modifiers)||super.charTyped(character,modifiers);}
 @Override protected void slotClicked(Slot slot,int index,int button,ClickType type){if(index>=0&&index<48){int click=type==ClickType.PICKUP&&button==0?0:type==ClickType.PICKUP&&button==1?1:type==ClickType.QUICK_MOVE&&button==0?2:-1;if(click>=0){var entry=menu.visible[index];PacketDistributor.sendToServer(new PneumaticStoragePackets.Click(menu.containerId,entry==null?0:entry.hash,click));}return;}super.slotClicked(slot,index,button,type);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);for(int i=0;i<4;i++)if(legacyInfoHovered(x,y,7,7+i*18,18,18))g.renderTooltip(font,Component.translatable("gui.hbm.pneumatic_storage.sorting",Component.translatable("gui.hbm.pneumatic_storage.sort."+i).withStyle(ChatFormatting.YELLOW)),x,y);if(legacyInfoHovered(x,y,7,79,18,18))g.renderTooltip(font,Component.translatable("gui.hbm.pneumatic_storage.focus",state(startFocused)),x,y);if(legacyInfoHovered(x,y,7,97,18,18))g.renderTooltip(font,Component.translatable("gui.hbm.pneumatic_storage.detailed",state(detailed)),x,y);}
 private static Component state(boolean enabled){return Component.translatable(enabled?"gui.hbm.pneumatic.on":"gui.hbm.pneumatic.off").withStyle(enabled?ChatFormatting.GREEN:ChatFormatting.RED);}
}

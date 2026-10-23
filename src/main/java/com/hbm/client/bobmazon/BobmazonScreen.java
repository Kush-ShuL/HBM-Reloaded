package com.hbm.client.bobmazon;

import com.hbm.bobmazon.*;
import com.hbm.client.gui.LegacyScreen;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/** Exact217x229 catalog, original three products/page and source button/star/item/half-scale text placement. */
public final class BobmazonScreen extends LegacyScreen {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/gui_bobmazon.png");
 private final Player player;private final InteractionHand hand;private final List<BobmazonOffers.Offer> offers;private int currentPage,left,top;
 public BobmazonScreen(Player player,InteractionHand hand){super(Component.translatable("item.bobmazon.name"));this.player=player;this.hand=hand;var item=player.getItemInHand(hand).getItem();offers=BobmazonOffers.offers(player.registryAccess(),item instanceof CatalogItem catalog&&catalog.hidden);}
 private int lastPage(){return Math.max(0,(offers.size()-1)/3);}
 @Override protected void init(){left=(width-217)/2;top=(height-229)/2;}
 @Override public void tick(){currentPage=Math.max(0,Math.min(currentPage,lastPage()));if(player.getItemInHand(hand).getItem() instanceof CatalogItem catalog&&catalog.hidden&&player.getDisplayName().getString().equals("SolsticeUnlimitd"))onClose();}
 @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
  renderBackground(graphics,mouseX,mouseY,partial);graphics.blit(TEXTURE,left,top,0,0,217,229);
  for(int index=currentPage*3;index<Math.min(currentPage*3+3,offers.size());index++){
   var offer=offers.get(index);int x=left+34,y=top+35+(index-currentPage*3)*54;drawButton(graphics,x,y,0,inside(mouseX,mouseY,x,y));
   if(offer.available())graphics.renderItem(offer.product(),x+1,y+1);else fallbackIcon(graphics,offer,x+1,y+1);
   drawRequirement(graphics,offer,x,y+18);
  }
  if(currentPage!=0){drawButton(graphics,left+7,top+107,1,inside(mouseX,mouseY,left+7,top+107));if(inside(mouseX,mouseY,left+7,top+107))graphics.renderTooltip(font,Component.translatable("gui.hbm.bobmazon.previous"),mouseX,mouseY);}
  if(currentPage!=lastPage()){drawButton(graphics,left+192,top+107,2,inside(mouseX,mouseY,left+192,top+107));if(inside(mouseX,mouseY,left+192,top+107))graphics.renderTooltip(font,Component.translatable("gui.hbm.bobmazon.next"),mouseX,mouseY);}
  var label=Component.literal((currentPage+1)+"/"+(lastPage()+1));graphics.drawString(font,label,left+217/2-font.width(label)/2,top+205,4210752,false);
 }
 private static boolean inside(double mouseX,double mouseY,int x,int y){return x<=mouseX&&mouseX<x+18&&y<mouseY&&mouseY<=y+18;}
 private void drawButton(GuiGraphics graphics,int x,int y,int type,boolean hover){graphics.blit(TEXTURE,x,y,hover?235:217,type==1?18:type==2?36:0,18,18);}
 private void drawRequirement(GuiGraphics graphics,BobmazonOffers.Offer offer,int x,int y){
  graphics.blit(TEXTURE,x+19,y-4,217,62,39,8);graphics.blit(TEXTURE,x+19,y-4,217,54,offer.rating(),8);
  Component name=offer.available()?offer.product().getHoverName():Component.literal(offer.source().getAsJsonObject("sourceDisplay").get("name_"+language()).getAsString());
  int count=offer.available()?offer.product().getCount():offer.source().getAsJsonObject("sourceDisplay").get("count").getAsInt();if(count>1)name=name.copy().append(" x"+count);
  graphics.pose().pushPose();graphics.pose().scale(.5F,.5F,1);graphics.drawString(font,name,(x+20)*2,(y-12)*2,4210752,false);graphics.pose().popPose();
  graphics.drawString(font,Component.translatable(offer.cost()==1?"gui.hbm.bobmazon.price_one":"gui.hbm.bobmazon.price_many",offer.cost()),x+62,y-3,4210752,false);
  graphics.pose().pushPose();graphics.pose().scale(.5F,.5F,1);if(!offer.author().isEmpty())graphics.drawString(font,"- "+offer.author(),(x+20)*2,(y+18)*2,0x222222,false);graphics.drawString(font,offer.comment().equals("No Ratings")?Component.translatable("gui.hbm.bobmazon.no_ratings"):Component.literal(offer.comment()),(x+20)*2,(y+8)*2,0x222222,false);graphics.pose().popPose();
  graphics.renderItem(BobmazonOffers.requirementIcon(player.registryAccess(),offer.requirement()),x+1,y+1);
 }
 private static String language(){return Minecraft.getInstance().getLanguageManager().getSelected().startsWith("zh")?"zh_cn":"en_us";}
 private void fallbackIcon(GuiGraphics graphics,BobmazonOffers.Offer offer,int x,int y){var display=offer.source().getAsJsonObject("sourceDisplay");if(display.has("preview"))graphics.blit(ResourceLocation.parse(display.get("preview").getAsString()),x,y,0,0,16,16,16,16);}
 @Override public boolean mouseClicked(double x,double y,int button){
  for(int index=currentPage*3;index<Math.min(currentPage*3+3,offers.size());index++)if(inside(x,y,left+34,top+35+(index-currentPage*3)*54)){click();PacketDistributor.sendToServer(new BobmazonPurchase(hand,index));return true;}
  if(currentPage!=0&&inside(x,y,left+7,top+107)){click();currentPage--;return true;}if(currentPage!=lastPage()&&inside(x,y,left+192,top+107)){click();currentPage++;return true;}return super.mouseClicked(x,y,button);
 }
 private void click(){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));}
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==256||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return super.keyPressed(key,scan,modifiers);}
 @Override public boolean isPauseScreen(){return false;}
}

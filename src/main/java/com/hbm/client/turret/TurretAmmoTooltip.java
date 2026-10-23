package com.hbm.client.turret;
import java.util.List;
import net.minecraft.client.gui.*;
import net.minecraft.world.item.ItemStack;
/** Source GuiInfoContainer.drawStackText two rows, including the cycling red selection frame and zero-count label. */
public final class TurretAmmoTooltip {
 public static void draw(GuiGraphics graphics,Font font,List<ItemStack> ammo,int selected,int mouseX,int mouseY,int screenWidth,int screenHeight){
  String name=ammo.get(selected).getHoverName().getString();int width=Math.max(ammo.size()*18,font.width(name)),height=28,x=mouseX+12,y=mouseY-12;if(x+width>screenWidth)x-=28+width;if(y+height+6>screenHeight)y=screenHeight-height-6;
  graphics.pose().pushPose();graphics.pose().translate(0,0,400);int bg=0xf0100010,top=0x505000ff,bottom=0x5028007f;
  graphics.fill(x-3,y-4,x+width+3,y-3,bg);graphics.fill(x-3,y+height+3,x+width+3,y+height+4,bg);graphics.fill(x-3,y-3,x+width+3,y+height+3,bg);graphics.fill(x-4,y-3,x-3,y+height+3,bg);graphics.fill(x+width+3,y-3,x+width+4,y+height+3,bg);
  graphics.fillGradient(x-3,y-2,x-2,y+height+2,top,bottom);graphics.fillGradient(x+width+2,y-2,x+width+3,y+height+2,top,bottom);graphics.fill(x-3,y-3,x+width+3,y-2,top);graphics.fill(x-3,y+height+2,x+width+3,y+height+3,bottom);
  for(int i=0;i<ammo.size();i++){int ix=x+i*18;if(i==selected){graphics.fill(ix-1,y-1,ix+17,y+17,0xffff0000);graphics.fill(ix,y,ix+16,y+16,0xffb0b0b0);}graphics.renderItem(ammo.get(i),ix,y);graphics.renderItemDecorations(font,ammo.get(i),ix,y,i==selected?"0":null);}graphics.drawString(font,name,x,y+20,-1,true);graphics.pose().popPose();
 }
 private TurretAmmoTooltip(){}
}

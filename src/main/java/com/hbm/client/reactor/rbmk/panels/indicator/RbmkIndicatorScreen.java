package com.hbm.client.reactor.rbmk.panels.indicator;
import com.hbm.client.reactor.rbmk.panels.RbmkRadioPanelScreen;
import com.hbm.machine.reactor.rbmk.panels.indicator.RbmkIndicatorEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
/** Original258-high GUI composed from two regions of its256-high texture; thirty actual green fields. */
public final class RbmkIndicatorScreen extends RbmkRadioPanelScreen<RbmkIndicatorEntity>{
    private final EditBox[] color=new EditBox[6],label=new EditBox[6],rtty=new EditBox[6],min=new EditBox[6],max=new EditBox[6];private final boolean[] active=new boolean[6],polling=new boolean[6];
    public RbmkIndicatorScreen(RbmkIndicatorEntity panel){super(panel,"container.rbmkIndicator","hbm:textures/gui/machine/gui_rbmk_indicator.png",256,258);}
    @Override protected void init(){super.init();for(int i=0;i<6;i++){var indicator=panel.indicators[i];String hex=Integer.toHexString(indicator.color);while(hex.length()<6)hex="0"+hex;color[i]=field(31,41+i*36,64,6,hex);label[i]=field(179,41+i*36,64,15,indicator.label);rtty[i]=field(31,59+i*36,64,15,indicator.rtty);min[i]=field(125,59+i*36,44,32,Long.toString(indicator.min));max[i]=field(199,59+i*36,44,32,Long.toString(indicator.max));active[i]=indicator.active;polling[i]=indicator.polling;}}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){renderBackground(graphics,mouseX,mouseY,partial);region(graphics,0,0,0,0,256,143);region(graphics,0,143,0,35,256,115);options(graphics);graphics.drawString(font,getTitle(),left+imageWidth/2-font.width(getTitle())/2,top+6,4210752,false);for(var widget:renderables)widget.render(graphics,mouseX,mouseY,partial);}
    @Override protected void options(GuiGraphics graphics){for(int i=0;i<6;i++){region(graphics,102,148+i*36,34+8*i,150,8,8);if(active[i])region(graphics,111,36+i*36,18,150,16,16);if(polling[i])region(graphics,128,35+i*36,0,150,18,18);}}
    @Override public boolean mouseClicked(double x,double y,int button){for(int i=0;i<6;i++){if(hover(x,y,111,36+i*36,16,16)){active[i]=!active[i];sound(.5F+(active[i]?.25F:0));return true;}if(hover(x,y,128,35+i*36,18,18)){polling[i]=!polling[i];sound(.5F+(polling[i]?.25F:0));return true;}}if(hover(x,y,209,8,18,18)){sound(1);var tag=new CompoundTag();byte a=0,p=0;for(int i=0;i<6;i++){if(active[i])a|=1<<i;if(polling[i])p|=1<<i;try{tag.putInt("color"+i,Integer.parseInt(color[i].getValue(),16));}catch(Exception ignored){}tag.putString("label"+i,label[i].getValue());tag.putString("rtty"+i,rtty[i].getValue());try{tag.putInt("min"+i,Integer.parseInt(min[i].getValue()));}catch(Exception ignored){}try{tag.putInt("max"+i,Integer.parseInt(max[i].getValue()));}catch(Exception ignored){}}tag.putByte("active",a);tag.putByte("polling",p);send(tag);return true;}return super.mouseClicked(x,y,button);}
}

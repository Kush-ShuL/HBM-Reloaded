package com.hbm.client.reactor.rbmk.panels.lever;
import com.hbm.client.reactor.rbmk.panels.RbmkRadioPanelScreen;
import com.hbm.machine.reactor.rbmk.panels.lever.RbmkLeverEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
public final class RbmkLeverScreen extends RbmkRadioPanelScreen<RbmkLeverEntity>{
    private final EditBox[] label=new EditBox[2],rtty=new EditBox[2],on=new EditBox[2],off=new EditBox[2];private final boolean[] active=new boolean[2],polling=new boolean[2];
    public RbmkLeverScreen(RbmkLeverEntity panel){super(panel,"container.rbmkLever","hbm:textures/gui/machine/gui_rbmk_lever.png",256,150);}
    @Override protected void init(){super.init();for(int i=0;i<2;i++){var lever=panel.levers[i];rtty[i]=field(31,59+i*54,64,15,lever.rtty);label[i]=field(179,59+i*54,64,15,lever.label);on[i]=field(49,77+i*54,73,32,lever.commandOn);off[i]=field(170,77+i*54,73,32,lever.commandOff);active[i]=lever.active;polling[i]=lever.polling;}}
    @Override protected void options(GuiGraphics graphics){for(int i=0;i<2;i++){if(active[i])region(graphics,111,54+i*54,18,150,16,16);if(polling[i])region(graphics,128,53+i*54,0,150,18,18);}}
    @Override public boolean mouseClicked(double x,double y,int button){for(int i=0;i<2;i++){if(hover(x,y,111,54+i*54,16,16)){active[i]=!active[i];sound(.5F+(active[i]?.25F:0));return true;}if(hover(x,y,128,53+i*54,18,18)){polling[i]=!polling[i];sound(.5F+(polling[i]?.25F:0));return true;}}if(hover(x,y,209,17,18,18)){sound(1);var tag=new CompoundTag();byte a=0,p=0;for(int i=0;i<2;i++){if(active[i])a|=1<<i;if(polling[i])p|=1<<i;tag.putString("label"+i,label[i].getValue());tag.putString("rtty"+i,rtty[i].getValue());tag.putString("cmdOn"+i,on[i].getValue());tag.putString("cmdOff"+i,off[i].getValue());}tag.putByte("active",a);tag.putByte("polling",p);send(tag);return true;}return super.mouseClicked(x,y,button);}
}

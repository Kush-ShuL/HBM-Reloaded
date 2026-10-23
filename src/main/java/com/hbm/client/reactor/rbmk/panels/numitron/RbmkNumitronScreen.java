package com.hbm.client.reactor.rbmk.panels.numitron;
import com.hbm.client.reactor.rbmk.panels.RbmkRadioPanelScreen;
import com.hbm.machine.reactor.rbmk.panels.numitron.RbmkNumitronEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
/** The source class was named GUIScreenRBMKDisplay; it really configures these two Numitron units. */
public final class RbmkNumitronScreen extends RbmkRadioPanelScreen<RbmkNumitronEntity>{
    private final EditBox[] label=new EditBox[2],rtty=new EditBox[2];private final boolean[] active=new boolean[2],polling=new boolean[2],shorten=new boolean[2],leading=new boolean[2];
    public RbmkNumitronScreen(RbmkNumitronEntity panel){super(panel,"container.rbmkNumitron","hbm:textures/gui/machine/gui_rbmk_numitron.png",256,150);}
    @Override protected void init(){super.init();for(int i=0;i<2;i++){var display=panel.displays[i];label[i]=field(31,77+i*54,77,30,display.label);rtty[i]=field(31,59+i*54,77,15,display.rtty);active[i]=display.active;polling[i]=display.polling;shorten[i]=display.shorten_number;leading[i]=display.leading_zeroes;}}
    @Override protected void options(GuiGraphics graphics){for(int i=0;i<2;i++){if(active[i])region(graphics,124,54+i*54,18,150,16,16);if(polling[i])region(graphics,159,53+i*54,0,150,18,18);if(shorten[i])region(graphics,195,53+i*54,34,150,18,18);if(leading[i])region(graphics,231,53+i*54,52,150,18,18);}}
    @Override public boolean mouseClicked(double x,double y,int button){for(int i=0;i<2;i++){if(hover(x,y,124,54+i*54,16,16)){active[i]=!active[i];sound(.5F+(active[i]?.25F:0));return true;}if(hover(x,y,159,53+i*54,18,18)){polling[i]=!polling[i];sound(.5F+(polling[i]?.25F:0));return true;}if(hover(x,y,195,53+i*54,18,18)){shorten[i]=!shorten[i];sound(.5F+(shorten[i]?.25F:0));return true;}if(hover(x,y,231,53+i*54,18,18)){leading[i]=!leading[i];sound(.5F+(leading[i]?.25F:0));return true;}}if(hover(x,y,209,17,18,18)){sound(1);var tag=new CompoundTag();byte a=0,p=0,s=0,l=0;for(int i=0;i<2;i++){if(active[i])a|=1<<i;if(polling[i])p|=1<<i;if(shorten[i])s|=1<<i;if(leading[i])l|=1<<i;tag.putString("label"+i,label[i].getValue());tag.putString("rtty"+i,rtty[i].getValue());}tag.putByte("active",a);tag.putByte("polling",p);tag.putByte("shorten_number",s);tag.putByte("leading_zeroes",l);send(tag);return true;}return super.mouseClicked(x,y,button);}
}

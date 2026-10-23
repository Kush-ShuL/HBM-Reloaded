package com.hbm.client.reactor.rbmk.panels.keypad;
import com.hbm.client.reactor.rbmk.panels.RbmkRadioPanelScreen;
import com.hbm.machine.reactor.rbmk.panels.keypad.RbmkKeypadEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
public final class RbmkKeypadScreen extends RbmkRadioPanelScreen<RbmkKeypadEntity>{
    private final EditBox[] color=new EditBox[4],label=new EditBox[4],rtty=new EditBox[4],command=new EditBox[4];private final boolean[] active=new boolean[4],polling=new boolean[4];
    public RbmkKeypadScreen(RbmkKeypadEntity panel){super(panel,"container.rbmkKeyPad","hbm:textures/gui/machine/gui_rbmk_keypad.png",256,204);}
    @Override protected void init(){super.init();for(int i=0;i<4;i++){var key=panel.keys[i];String hex=Integer.toHexString(key.color);while(hex.length()<6)hex="0"+hex;color[i]=field(31,59+i*36,64,6,hex);label[i]=field(179,59+i*36,64,15,key.label);rtty[i]=field(31,77+i*36,64,15,key.rtty);command[i]=field(125,77+i*36,118,32,key.command);active[i]=key.active;polling[i]=key.polling;}}
    @Override protected void options(GuiGraphics graphics){for(int i=0;i<4;i++){if(active[i])region(graphics,111,54+i*36,18,204,16,16);if(polling[i])region(graphics,128,53+i*36,0,204,18,18);}}
    @Override public boolean mouseClicked(double x,double y,int button){for(int i=0;i<4;i++){if(hover(x,y,111,54+i*36,16,16)){active[i]=!active[i];sound(.5F+(active[i]?.25F:0));return true;}if(hover(x,y,128,53+i*36,18,18)){polling[i]=!polling[i];sound(.5F+(polling[i]?.25F:0));return true;}}if(hover(x,y,209,17,18,18)){sound(1);var tag=new CompoundTag();byte mask=0,poll=0;for(int i=0;i<4;i++){if(active[i])mask|=1<<i;if(polling[i])poll|=1<<i;try{tag.putInt("color"+i,Integer.parseInt(color[i].getValue(),16));}catch(Exception ignored){}tag.putString("label"+i,label[i].getValue());tag.putString("rtty"+i,rtty[i].getValue());tag.putString("cmd"+i,command[i].getValue());}tag.putByte("active",mask);tag.putByte("polling",poll);send(tag);return true;}return super.mouseClicked(x,y,button);}
}

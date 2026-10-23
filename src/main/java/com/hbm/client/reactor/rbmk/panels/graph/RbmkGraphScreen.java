package com.hbm.client.reactor.rbmk.panels.graph;
import com.hbm.client.reactor.rbmk.panels.RbmkRadioPanelScreen;
import com.hbm.machine.reactor.rbmk.panels.graph.RbmkGraphEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
/** Original256x150graph GUI, optional long-valued bounds and source control payload. */
public final class RbmkGraphScreen extends RbmkRadioPanelScreen<RbmkGraphEntity>{
    private final EditBox[] label=new EditBox[2],rtty=new EditBox[2],min=new EditBox[2],max=new EditBox[2];private final boolean[] active=new boolean[2],polling=new boolean[2];
    public RbmkGraphScreen(RbmkGraphEntity panel){super(panel,"container.rbmkGraph","hbm:textures/gui/machine/gui_rbmk_graph.png",256,150);}
    @Override protected void init(){super.init();for(int i=0;i<2;i++){var graph=panel.graphs[i];label[i]=field(31,77+i*54,64,30,graph.label);rtty[i]=field(31,59+i*54,64,15,graph.rtty);min[i]=field(179,59+i*54,64,15,graph.minBound?Long.toString(graph.min):"");max[i]=field(179,77+i*54,64,15,graph.maxBound?Long.toString(graph.max):"");active[i]=graph.active;polling[i]=graph.polling;}}
    @Override protected void options(GuiGraphics graphics){for(int i=0;i<2;i++){if(active[i])region(graphics,111,54+i*54,18,150,16,16);if(polling[i])region(graphics,128,53+i*54,0,150,18,18);}}
    @Override public boolean mouseClicked(double x,double y,int button){for(int i=0;i<2;i++){if(hover(x,y,111,54+i*54,16,16)){active[i]=!active[i];sound(.5F+(active[i]?.25F:0));return true;}if(hover(x,y,128,53+i*54,18,18)){polling[i]=!polling[i];sound(.5F+(polling[i]?.25F:0));return true;}}if(hover(x,y,209,17,18,18)){sound(1);var tag=new CompoundTag();byte a=0,p=0;for(int i=0;i<2;i++){if(active[i])a|=1<<i;if(polling[i])p|=1<<i;tag.putString("label"+i,label[i].getValue());tag.putString("rtty"+i,rtty[i].getValue());if(!min[i].getValue().isEmpty())try{tag.putLong("min"+i,Long.parseLong(min[i].getValue()));}catch(Exception ignored){}if(!max[i].getValue().isEmpty())try{tag.putLong("max"+i,Long.parseLong(max[i].getValue()));}catch(Exception ignored){}}tag.putByte("active",a);tag.putByte("polling",p);send(tag);return true;}return super.mouseClicked(x,y,button);}
}

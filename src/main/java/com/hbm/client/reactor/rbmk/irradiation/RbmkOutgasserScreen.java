package com.hbm.client.reactor.rbmk.irradiation;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.reactor.rbmk.irradiation.*;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Exact source176x186 irradiation GUI, fluxarrow13x6/gas10x42 and nativegas information. */
public final class RbmkOutgasserScreen extends LegacyContainerScreen<RbmkOutgasserMenu> {
    public RbmkOutgasserScreen(RbmkOutgasserMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_rbmk_outgasser.png"),176,186);}
    private RbmkOutgasserEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof RbmkOutgasserEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var machine=machine();if(machine==null)return;int progress=(int)(machine.progress*13/RbmkOutgasserEntity.DURATION);drawLegacyRegion(g,82,50,176,0,progress,6);int gas=machine.gas.getFill()*42/machine.gas.getMaxFill();drawLegacyRegion(g,115,66-gas,188,42-gas,10,gas);}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var machine=machine();if(machine!=null&&legacyInfoHovered(x,y,112,21,16,48)){var tank=machine.gas;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+" / "+tank.getMaxFill()+" mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}}
}

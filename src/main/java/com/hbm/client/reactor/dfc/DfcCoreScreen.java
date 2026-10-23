package com.hbm.client.reactor.dfc;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.reactor.dfc.*;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x166 core GUI, both52px field/heat strips and two actual128000mB fluid tanks. */
public final class DfcCoreScreen extends LegacyContainerScreen<DfcCoreMenu> {
    public DfcCoreScreen(DfcCoreMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/dfc/gui_core.png"),176,166);}
    private DfcCoreEntity core(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position)instanceof DfcCoreEntity core?core:null;}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,72,4210752,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var core=core();if(core==null)return;int field=core.field*52/100,heat=core.heat*52/100;drawLegacyRegion(g,8,69-field,176,52-field,16,field);drawLegacyRegion(g,152,69-heat,192,52-heat,16,heat);tank(g,core.tanks[0],26);tank(g,core.tanks[1],134);}
    private void tank(GuiGraphics g,FluidTank tank,int x){int fill=tank.getFill()*52/tank.getMaxFill();if(fill<=0)return;int rgb=tank.getTankType().getTint();g.flush();RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();g.setColor((rgb>>16&255)/255F,(rgb>>8&255)/255F,(rgb&255)/255F,1);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+69-fill,0,16-fill,16,fill,16,16);g.flush();g.setColor(1,1,1,1);RenderSystem.disableBlend();}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var core=core();if(core==null)return;for(int i=0;i<2;i++)if(legacyInfoHovered(x,y,26+i*108,17,16,52)){var tank=core.tanks[i];var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+" / "+tank.getMaxFill()+" mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}if(legacyInfoHovered(x,y,8,17,16,52))g.renderTooltip(font,Component.translatable("gui.hbm.dfc.field",core.field),x,y);if(legacyInfoHovered(x,y,152,17,16,52))g.renderTooltip(font,Component.translatable("gui.hbm.dfc.heat",core.heat),x,y);}
}

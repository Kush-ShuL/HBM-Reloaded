package com.hbm.client.reactor.rbmk.thermal;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.reactor.rbmk.thermal.*;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Source176x186, native fluid14x58 gauges with original repeated UVs and10x10 foreground marks. */
public final class RbmkHeaterScreen extends LegacyContainerScreen<RbmkHeaterMenu>{
    public RbmkHeaterScreen(RbmkHeaterMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_rbmk_heater.png"),176,186);}
    private RbmkHeaterEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof RbmkHeaterEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var machine=machine();if(machine==null)return;tank(graphics,machine.feed,68);tank(graphics,machine.steam,126);drawLegacyRegion(graphics,72,72,176,0,10,10);drawLegacyRegion(graphics,130,72,186,0,10,10);}
    private void tank(GuiGraphics graphics,FluidTank tank,int x){int height=tank.getFill()*58/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();graphics.flush();RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);graphics.blit(tank.getTankType().getTexture(),leftPos+x,topPos+82-height,0,16-height,14,height,16,16);graphics.flush();graphics.setColor(1,1,1,1);RenderSystem.disableBlend();}
    private void info(GuiGraphics graphics,FluidTank tank,int px,int x,int y){if(!legacyInfoHovered(x,y,px,24,16,58))return;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+" / "+tank.getMaxFill()+" mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var machine=machine();if(machine!=null){info(graphics,machine.feed,68,x,y);info(graphics,machine.steam,126,x,y);}}
}

package com.hbm.client.turret.fritz;

import com.hbm.client.turret.SentryScreen;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.turret.fritz.*;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Source176x222 shared controls plus the original7x52 bottom-anchored fluid strip at134,63. */
public final class FritzTurretScreen extends SentryScreen<FritzTurretMenu> {
    public FritzTurretScreen(FritzTurretMenu menu,Inventory inventory,Component title){super(menu,inventory,title,FritzTurretContent.id("textures/gui/weapon/gui_turret_fritz.png"));}
    private FritzTurretEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof FritzTurretEntity turret?turret:null;}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){
        super.renderBg(graphics,partial,mouseX,mouseY);var turret=machine();if(turret==null||turret.tank.getMaxFill()==0)return;
        var tank=turret.tank;int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;
        int tint=tank.getTankType().getTint();graphics.flush();RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();
        graphics.setColor((tint>>16&255)/255F,(tint>>8&255)/255F,(tint&255)/255F,1);
        graphics.blit(tank.getTankType().getTexture(),leftPos+134,topPos+115-height,0,16-height,7,height,16,16);
        graphics.flush();graphics.setColor(1,1,1,1);RenderSystem.disableBlend();
    }
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        super.render(graphics,mouseX,mouseY,partial);var turret=machine();if(turret==null||!legacyInfoHovered(mouseX,mouseY,134,63,7,52))return;
        var tank=turret.tank;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));
        if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED));}
        FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,mouseX,mouseY);
    }
}

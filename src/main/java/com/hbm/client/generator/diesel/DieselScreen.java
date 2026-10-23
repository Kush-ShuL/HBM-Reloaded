package com.hbm.client.generator.diesel;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.generator.diesel.*;
import com.mojang.blaze3d.systems.RenderSystem;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Supplied176x203 GUI pixels and exact narrower x89 switch hitbox, with localized source information. */
public final class DieselScreen extends LegacyContainerScreen<DieselMenu> {
    private static final ResourceLocation UTILITY=ResourceLocation.parse("hbm:textures/gui/gui_utility.png");
    public DieselScreen(DieselMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/generators/gui_diesel.png"),176,203);}
    private DieselBlockEntity machine(){return minecraft.level.getBlockEntity(menu.position) instanceof DieselBlockEntity engine?engine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,playerInventoryTitle,8,109,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var engine=machine();if(engine==null)return;if(engine.getPowerExact().signum()>0&&engine.powerCapacity.signum()>0){int height=engine.getPowerExact().multiply(BigInteger.valueOf(52)).divide(engine.powerCapacity).intValue();drawLegacyRegion(graphics,141,69-height,176,52-height,16,height);}if(engine.isOn)drawLegacyRegion(graphics,79,61,192,16,35,14);if(engine.wasOn)drawLegacyRegion(graphics,89,42,192,0,16,16);graphics.blit(UTILITY,leftPos-8,topPos+36,8,0,16,16,256,256);if(!engine.hasAcceptableFuel())graphics.blit(UTILITY,leftPos-8,topPos+68,8,16,16,16,256,256);tank(graphics,engine.tank);}
    private void tank(GuiGraphics graphics,FluidTank tank){if(tank.getMaxFill()==0)return;int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();graphics.flush();RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);graphics.blit(tank.getTankType().getTexture(),leftPos+35,topPos+69-height,0,16-height,16,height,16,16);graphics.flush();graphics.setColor(1,1,1,1);RenderSystem.disableBlend();}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var engine=machine();if(engine==null)return;if(legacyInfoHovered(x,y,141,17,16,52))graphics.renderTooltip(font,Component.literal(engine.getPowerExact()+" / "+engine.powerCapacity+" HE"),x,y);if(legacyInfoHovered(x,y,35,17,16,52)){var lines=new ArrayList<Component>();lines.add(engine.tank.getTankType().displayName());lines.add(Component.literal(engine.tank.getFill()+" / "+engine.tank.getMaxFill()+" mB"));FluidInfo.append(engine.tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}if(legacyInfoHovered(x,y,-8,36,16,16))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.diesel.consumption"),Component.literal("  1 mB/t"),Component.literal("  20 mB/s"),Component.translatable("gui.hbm.diesel.constant")),x,y);if(!engine.hasAcceptableFuel()&&legacyInfoHovered(x,y,-8,68,16,16))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.diesel.unsupported.1"),Component.translatable("gui.hbm.diesel.unsupported.2")),x,y);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,89,61,16,14)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new DieselControl(menu.position));return true;}return handled;}
}

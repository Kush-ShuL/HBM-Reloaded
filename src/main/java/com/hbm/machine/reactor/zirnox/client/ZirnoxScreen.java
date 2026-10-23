package com.hbm.machine.reactor.zirnox.client;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.machine.reactor.zirnox.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Original 203x256 GUI, source slot lights, smooth instrument geometry and control hitboxes. */
public final class ZirnoxScreen extends LegacyContainerScreen<ZirnoxMenu> {
    private static final ResourceLocation UTILITY=ZirnoxRenderer.id("textures/gui/gui_utility.png");
    public ZirnoxScreen(ZirnoxMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ZirnoxRenderer.id("textures/gui/reactors/gui_zirnox.png"),203,256);}
    private ZirnoxBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof ZirnoxBlockEntity be?be:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,160,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var reactor=machine();if(reactor==null)return;ZirnoxGauges.linear(graphics,leftPos+162,topPos+114,(double)reactor.steam.getFill()/reactor.steam.getMaxFill());ZirnoxGauges.linear(graphics,leftPos+144,topPos+114,(double)reactor.carbonDioxide.getFill()/reactor.carbonDioxide.getMaxFill());ZirnoxGauges.linear(graphics,leftPos+180,topPos+114,(double)reactor.water.getFill()/reactor.water.getMaxFill());ZirnoxGauges.radial(graphics,leftPos+169,topPos+42,(double)reactor.heat/100000);ZirnoxGauges.radial(graphics,leftPos+187,topPos+42,(double)reactor.pressure/100000);graphics.flush();if(reactor.isOn){for(int cx=0;cx<4;cx++)for(int cy=0;cy<4;cy++)drawLegacyRegion(graphics,7+36*cx,15+36*cy,238,238,18,18);for(int cx=0;cx<3;cx++)for(int cy=0;cy<3;cy++)drawLegacyRegion(graphics,25+36*cx,33+36*cy,238,238,18,18);drawLegacyRegion(graphics,142,15,220,238,18,18);}icon(graphics,36,8,0);icon(graphics,52,24,0);if(reactor.water.getFill()<=0)icon(graphics,68,8,16);if(reactor.carbonDioxide.getFill()<=4000)icon(graphics,84,8,16);}
    private void icon(GuiGraphics graphics,int y,int u,int v){graphics.blit(UTILITY,leftPos-16,topPos+y,u,v,16,16);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var reactor=machine();if(reactor==null)return;tank(graphics,reactor.steam,x,y,160);tank(graphics,reactor.carbonDioxide,x,y,142);tank(graphics,reactor.water,x,y,178);if(legacyInfoHovered(x,y,160,33,18,17))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.zirnox.temperature"),Component.literal("   "+Math.round(reactor.heat*.00001*780+20)+"°C")),x,y);if(legacyInfoHovered(x,y,178,33,18,17))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.zirnox.pressure"),Component.literal("   "+Math.round(reactor.pressure*.00001*30)+" bar")),x,y);info(graphics,x,y,36,"coolant");info(graphics,x,y,52,"pressure");if(reactor.water.getFill()<=0)info(graphics,x,y,68,"warning1");if(reactor.carbonDioxide.getFill()<4000)info(graphics,x,y,84,"warning2");}
    private void tank(GuiGraphics graphics,FluidTank tank,int x,int y,int relativeX){if(legacyInfoHovered(x,y,relativeX,108,18,12))graphics.renderComponentTooltip(font,List.of(tank.getTankType().displayName(),Component.literal(tank.getFill()+" / "+tank.getMaxFill()+" mB")),x,y);}
    private void info(GuiGraphics graphics,int x,int y,int atY,String key){if(legacyInfoHovered(x,y,-16,atY,16,16))graphics.renderComponentTooltip(font,Arrays.stream(Component.translatable("desc.gui.zirnox."+key).getString().split("\\$")).<Component>map(Component::literal).toList(),x,y);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean result=super.mouseClicked(x,y,button);boolean control=legacyInfoHovered(x,y,144,35,14,14),vent=legacyInfoHovered(x,y,151,51,36,36);if(control||vent){PacketDistributor.sendToServer(new ZirnoxControl(menu.containerId,control,vent));minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ZirnoxRenderer.id("block.rbmk_az5_cover")),.5F));return true;}return result;}
}

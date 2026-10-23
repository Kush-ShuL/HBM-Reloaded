package com.hbm.client.lambda;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.lambda.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/** Original176x226 foreground, two actual private-fluid bars and all source controls/indicators. */
public final class LambdaScreen extends LegacyContainerScreen<LambdaMenu> {
    public LambdaScreen(LambdaMenu menu,Inventory inventory,Component title){super(menu,inventory,title,LambdaContent.id("textures/gui/machine/gui_launchpad_lambda.png"),176,226);}
    private LambdaEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof LambdaEntity m?m:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){
        graphics.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);var m=machine();if(m==null)return;
        if(m.erected&&m.countdown>0){String secs=Integer.toString(m.countdown/20),cents=Integer.toString((m.countdown%20)*5);if(secs.length()==1)secs="0"+secs;if(cents.length()==1)cents+="0";graphics.drawString(font,secs+":"+cents,40,103,0xFF0000,false);}
        else label(graphics,Component.translatable(!m.hasRocketLoaded()?"desc.gui.soyuz.idle":!m.erected?"desc.gui.soyuz.loading":"desc.gui.soyuz.ready"),!m.hasRocketLoaded()?0xFF0000:!m.erected?0xFF8000:0x00FF00);
    }
    private void label(GuiGraphics graphics,Component label,int color){float scale=Math.min(1,22F/Math.max(font.width(label),1));graphics.pose().pushPose();graphics.pose().scale(scale,scale,1);graphics.drawString(font,label,(int)(53/scale-font.width(label)/2F),(int)(107/scale-font.lineHeight/2F),color,false);graphics.pose().popPose();}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){
        super.renderBg(graphics,partial,x,y);var m=machine();if(m==null)return;int power=(int)(m.power*52/LambdaEntity.MAX_POWER);if(power>0)drawLegacyRegion(graphics,89,78-power,194,52-power,16,power);
        drawLegacyRegion(graphics,34+(m.autolaunch?18:0),43,210+(m.autolaunch?18:0),26,18,18);drawLegacyRegion(graphics,112,13,m.hasJetFuel()?210:216,0,6,8);drawLegacyRegion(graphics,130,13,m.hasOxidizer()?210:216,0,6,8);drawLegacyRegion(graphics,94,13,m.power>=LambdaEntity.CONSUMPTION?210:216,0,6,8);
        if(m.countdown>0)drawLegacyRegion(graphics,43,70,210,44,18,18);drawLegacyRegion(graphics,34,16,m.hasRocketLoaded()?228:210,8,18,18);var sat=m.slots.get(1);drawLegacyRegion(graphics,52,16,sat.isEmpty()||LambdaEntity.needsOrbiter(sat)?210:228,8,18,18);tank(graphics,m.tanks[0],107);tank(graphics,m.tanks[1],125);
    }
    private void tank(GuiGraphics graphics,FluidTank tank,int offset){int height=Mth.clamp(tank.getFill()*52/Math.max(tank.getMaxFill(),1),0,52);if(height==0)return;graphics.flush();var matrix=graphics.pose().last().pose();int color=tank.getTankType().getTint(),x=leftPos+offset,y=topPos+78;RenderSystem.setShader(GameRenderer::getPositionTexColorShader);RenderSystem.setShaderTexture(0,tank.getTankType().getTexture());RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();var out=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);out.addVertex(matrix,x,y,0).setUv(0,1).setColor(color>>16&255,color>>8&255,color&255,255);out.addVertex(matrix,x+16,y,0).setUv(1,1).setColor(color>>16&255,color>>8&255,color&255,255);out.addVertex(matrix,x+16,y-height,0).setUv(1,1-height/16F).setColor(color>>16&255,color>>8&255,color&255,255);out.addVertex(matrix,x,y-height,0).setUv(0,1-height/16F).setColor(color>>16&255,color>>8&255,color&255,255);BufferUploader.drawWithShader(out.buildOrThrow());RenderSystem.disableBlend();}
    private List<Component> description(String key){return Arrays.stream(Component.translatable(key).getString().split("\\$")).map(Component::literal).map(value->(Component)value).toList();}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var m=machine();if(m==null)return;for(int i=0;i<2;i++)if(legacyInfoHovered(x,y,107+i*18,26,16,52)){var tank=m.tanks[i];var text=new ArrayList<Component>();text.add(tank.getTankType().displayName());text.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));FluidInfo.append(tank.getTankType(),text,hasShiftDown());graphics.renderComponentTooltip(font,text,x,y);}if(legacyInfoHovered(x,y,89,26,16,52))graphics.renderTooltip(font,Component.literal(com.hbm.item.battery.BatteryItem.shortNumber(m.power)+"/"+com.hbm.item.battery.BatteryItem.shortNumber(LambdaEntity.MAX_POWER)+"HE"),x,y);for(int i=0;i<2;i++)if(legacyInfoHovered(x,y,34+i*18,43,18,18))graphics.renderComponentTooltip(font,description(i==0?"desc.gui.lambda.manual":"desc.gui.lambda.auto"),x,y);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);var m=machine();int action=legacyInfoHovered(x,y,34,43,18,18)?0:legacyInfoHovered(x,y,52,43,18,18)?1:m!=null&&m.erected&&legacyInfoHovered(x,y,43,70,18,18)?2:-1;if(action>=0){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));minecraft.gameMode.handleInventoryButtonClick(menu.containerId,action);return true;}return handled;}
}

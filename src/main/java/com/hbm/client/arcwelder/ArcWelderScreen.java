package com.hbm.client.arcwelder;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.arcwelder.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Original176x204 GUI and every original hitbox/slot/bar, with native language-selected font. */
public final class ArcWelderScreen extends LegacyContainerScreen<ArcWelderMenu> {
    public ArcWelderScreen(ArcWelderMenu menu,Inventory player,Component title){super(menu,player,title,ArcWelderContent.id("textures/gui/processing/gui_arc_welder.png"),176,204);}
    private ArcWelderBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof ArcWelderBlockEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,imageWidth/2-font.width(title)/2-18,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,110,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){
        super.renderBg(graphics,partial,x,y);var machine=machine();if(machine==null)return;
        int power=(int)(machine.power*52/Math.max(machine.maxPower,1));if(power>0)drawLegacyRegion(graphics,152,70-power,176,52-power,16,power);
        if(!machine.display.isEmpty()&&machine.progress>0){int progress=machine.progress*33/Math.max(machine.processTime,1);drawLegacyRegion(graphics,72,37,192,0,progress,14);}
        if(machine.power>=machine.consumption)drawLegacyRegion(graphics,156,4,176,52,9,12);
        graphics.blit(ArcWelderContent.id("textures/gui/gui_utility.png"),leftPos+78,topPos+67,0,32,8,8,256,256);
        horizontalFluid(graphics,machine.tank);
    }
    /** Original orientation1 fills x, with backwards repeating U(1..1-width/16), not a vertical level. */
    private void horizontalFluid(GuiGraphics graphics,FluidTank tank){
        int width=tank.getFill()*34/Math.max(tank.getMaxFill(),1);if(width<=0)return;graphics.flush();int color=tank.getTankType().getTint();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);RenderSystem.setShaderTexture(0,tank.getTankType().getTexture());RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();
        var vertices=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);var pose=graphics.pose().last().pose();int x=leftPos+35,y=topPos+63;float endU=1-width/16F;
        vertices.addVertex(pose,x,y+16,0).setUv(1,1).setColor(color>>16&255,color>>8&255,color&255,255);
        vertices.addVertex(pose,x+width,y+16,0).setUv(endU,1).setColor(color>>16&255,color>>8&255,color&255,255);
        vertices.addVertex(pose,x+width,y,0).setUv(endU,0).setColor(color>>16&255,color>>8&255,color&255,255);
        vertices.addVertex(pose,x,y,0).setUv(1,0).setColor(color>>16&255,color>>8&255,color&255,255);
        BufferUploader.drawWithShader(vertices.buildOrThrow());RenderSystem.disableBlend();
    }
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){
        super.render(graphics,x,y,partial);var machine=machine();if(machine==null)return;
        if(legacyInfoHovered(x,y,35,63,34,16)){var lines=new ArrayList<Component>();lines.add(machine.tank.getTankType().displayName());lines.add(Component.literal(machine.tank.getFill()+"/"+machine.tank.getMaxFill()+"mB"));if(machine.tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",machine.tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(ChatFormatting.DARK_RED));}FluidInfo.append(machine.tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
        if(legacyInfoHovered(x,y,152,18,16,52))graphics.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,d HE",machine.getPower(),machine.getMaxPower())),x,y);
        if(legacyInfoHovered(x,y,78,67,8,8))graphics.renderComponentTooltip(font,List.of(Component.translatable("upgrade.gui.title"),Component.translatable("upgrade.gui.speed",3),Component.translatable("upgrade.gui.power",3),Component.translatable("upgrade.gui.overdrive",3)),x,y);

    }
}

package com.hbm.client.wiaj;

import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.client.wiaj.actors.*;
import com.hbm.client.wiaj.cannery.CanneryBase;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

/** Source camera, actor order,24px controls and scene rewind/forward; no simulation in the player level. */
public final class GuiWorldInAJar extends LegacyScreen {
    private static final ResourceLocation UTILITY=ResourceLocation.parse("hbm:textures/gui/gui_utility.png");
    private final JarScript script;private final ItemStack icon;private final CanneryBase[] seeAlso;private final ActorFancyPanel titlePanel;private final ActorFancyPanel[] seeAlsoTitles;private final Screen previous;
    public GuiWorldInAJar(CanneryBase cannery,Screen previous){this(cannery.createScript(),cannery.getName(),cannery.getIcon(),previous,cannery.seeAlso());}
    public GuiWorldInAJar(JarScript script,String title,ItemStack icon,Screen previous,CanneryBase... seeAlso){super(Component.translatable(title));this.script=script;this.icon=icon;this.previous=previous;this.seeAlso=seeAlso;var font=LegacyFonts.get();titlePanel=new ActorFancyPanel(font,40,27,new Object[][]{{JarSymbols.text(title)}},0).setColors(CanneryBase.colorGold).setOrientation(ActorFancyPanel.Orientation.LEFT);seeAlsoTitles=new ActorFancyPanel[seeAlso.length];for(int i=0;i<seeAlso.length;i++)seeAlsoTitles[i]=new ActorFancyPanel(font,40,27+36*(i+1),new Object[][]{{JarSymbols.text(seeAlso[i].getName())}},0).setColors(CanneryBase.colorGrey).setOrientation(ActorFancyPanel.Orientation.LEFT);}
    @Override public boolean isPauseScreen(){return false;}@Override public void onClose(){minecraft.setScreen(previous);}
    private boolean over(double x,double y,int left,int top,int w,int h){return left<=x&&left+w>x&&top<y&&top+h>=y;}
    private void blit(GuiGraphics graphics,int x,int y,int u,int v,int w,int h){graphics.blit(UTILITY,x,y,u,v,w,h,256,256);}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){renderBackground(graphics,mouseX,mouseY,partial);script.run();miniature(graphics);for(var actor:script.actors.values()){graphics.pose().pushPose();actor.drawForegroundComponent(graphics,width,height,script.ticksElapsed,script.interp);graphics.pose().popPose();}if(hasAltDown())JarStackText.draw(graphics,Collections.singletonList(new Object[]{(mouseX-width/2)+" / "+(mouseY-height/2)}),mouseX,mouseY,width,height);int play=script.isPaused()?64:40;blit(graphics,width/2-12,height-36,play,over(mouseX,mouseY,width/2-12,height-36,24,24)?24:48,24,24);blit(graphics,width/2-48,height-36,88,script.sceneNumber==0?72:over(mouseX,mouseY,width/2-48,height-36,24,24)?24:48,24,24);blit(graphics,width/2+24,height-36,112,script.sceneNumber>=script.scenes.size()?72:over(mouseX,mouseY,width/2+24,height-36,24,24)?24:48,24,24);blit(graphics,15,15,136,48,24,24);graphics.renderItem(icon,19,19);graphics.renderItemDecorations(LegacyFonts.get(),icon,19,19);if(over(mouseX,mouseY,15,15,24,24))titlePanel.drawForegroundComponent(graphics,0,0,script.ticksElapsed,script.interp);for(int i=0;i<seeAlso.length;i++){blit(graphics,15,15+36*(i+1),136,72,24,24);graphics.renderItem(seeAlso[i].getIcon(),19,19+36*(i+1));graphics.renderItemDecorations(LegacyFonts.get(),seeAlso[i].getIcon(),19,19+36*(i+1));if(over(mouseX,mouseY,15,15+36*(i+1),24,24))seeAlsoTitles[i].drawForegroundComponent(graphics,0,0,script.ticksElapsed,script.interp);}}
    private void miniature(GuiGraphics graphics){graphics.flush();com.mojang.blaze3d.platform.Lighting.setupFor3DItems();var pose=graphics.pose();pose.pushPose();pose.translate(width/2D,height/2D,400);pose.scale(-10,-10,-10);pose.scale(1,1,.5F);pose.scale((float)script.zoom(),(float)script.zoom(),(float)script.zoom());pose.mulPose(Axis.XP.rotationDegrees((float)script.pitch()));pose.mulPose(Axis.YP.rotationDegrees((float)script.yaw()));var world=script.world;pose.translate(world.sizeX/-2D,world.sizeY/-2D,world.sizeZ/-2D);pose.translate(script.offsetX(),script.offsetY(),script.offsetZ());var buffers=minecraft.renderBuffers().bufferSource();var context=new JarRenderContext(pose,buffers);var renderer=minecraft.getBlockRenderer();for(int x=0;x<world.sizeX;x++)for(int y=0;y<world.sizeY;y++)for(int z=0;z<world.sizeZ;z++){var pos=new BlockPos(x,y,z);var state=world.getBlockState(pos);if(state.getBlock() instanceof com.hbm.block.pipe.NativePipeBlock pipe){JarMachineActors.pipeGeometry(context,world,pos,pipe);continue;}if(state.getBlock() instanceof com.hbm.machine.foundry.FoundryBlock foundry){JarMachineActors.foundryGeometry(context,world,pos,foundry);continue;}if(state.getRenderShape()!=RenderShape.MODEL)continue;pose.pushPose();pose.translate(x,y,z);var base=renderer.getBlockModel(state);var localDetector=world.localDetectorMetadata(pos);net.minecraft.client.resources.model.BakedModel model=localDetector==null?base:new JarDetectorModel(base,localDetector);var data=model.getModelData(world,pos,state,ModelData.EMPTY);for(var type:model.getRenderTypes(state,RandomSource.create(0),data))renderer.getModelRenderer().tesselateBlock(world,model,state,pos,pose,buffers.getBuffer(RenderTypeHelper.getMovingBlockRenderType(type)),true,RandomSource.create(),state.getSeed(pos),OverlayTexture.NO_OVERLAY,data,type);pose.popPose();}buffers.endBatch();for(var actor:script.actors.values()){pose.pushPose();actor.drawBackgroundComponent(context,world,script.ticksElapsed,script.interp);pose.popPose();}buffers.endBatch();pose.popPose();
        // Source foreground actors disabled depth; native GUI RenderTypes restore it on each flush.
        // Keep the completed miniature's color, but give all foreground panels/items their own depth layer.
        graphics.flush();
        com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
        com.mojang.blaze3d.systems.RenderSystem.clear(org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT,net.minecraft.client.Minecraft.ON_OSX);
        com.mojang.blaze3d.platform.Lighting.setupForFlatItems();
    }
    private void click(){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));}
    @Override public boolean mouseClicked(double x,double y,int button){if(over(x,y,width/2-12,height-36,24,24)){click();if(script.isPaused())script.unpause();else script.pause();return true;}if(over(x,y,width/2-48,height-36,24,24)){if(script.sceneNumber>0){script.rewindOne();click();}return true;}if(over(x,y,width/2+24,height-36,24,24)){if(script.sceneNumber<script.scenes.size()){script.forwardOne();click();}return true;}for(int i=0;i<seeAlso.length;i++)if(over(x,y,15,15+36*(i+1),24,24)){click();minecraft.setScreen(new GuiWorldInAJar(seeAlso[i],previous));return true;}return super.mouseClicked(x,y,button);}
}

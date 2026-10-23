package com.hbm.client.missile.launchpad.rusted;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.missile.rusted.RustedMissileRenderer;
import com.hbm.missile.launchpad.rusted.*;
import com.hbm.registry.HbmContent;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Matrix3f;
import org.joml.Vector3f;
/** Original176x236 GUI, original UV digits and source live missile preview, with normal legacy font/no blur. */
public final class RustedLaunchPadScreen extends LegacyContainerScreen<RustedLaunchPadMenu> {
    public RustedLaunchPadScreen(RustedLaunchPadMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/weapon/gui_launch_pad_rusted.png"),176,236);}
    private RustedLaunchPadBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof RustedLaunchPadBlockEntity pad?pad:null;}
    @Override public void renderBackground(GuiGraphics graphics,int x,int y,float partial){graphics.fillGradient(0,0,width,height,0xc0101010,0xd0101010);renderBg(graphics,partial,x,y);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,4,4210752,false);graphics.drawString(font,playerInventoryTitle,8,142,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){
        super.renderBg(graphics,partial,x,y);boolean codes=menu.getSlot(1).getItem().is(HbmContent.item("launch_code")),key=menu.getSlot(2).getItem().is(HbmContent.item("launch_key"));var pad=machine();
        if(codes)drawLegacyRegion(graphics,121,32,192,0,6,8);if(key)drawLegacyRegion(graphics,139,32,192,0,6,8);
        if(pad!=null&&pad.missileLoaded){if(codes&&key){Random random=new Random(menu.position().getX()*131_071+menu.position().getZ());int number=random.nextInt(100_000_000);for(int i=0;i<8;i++){int magnitude=(int)Math.pow(10,i),digit=number%(magnitude*10)/magnitude;drawLegacyRegion(graphics,109+6*i,85,192+6*digit,8,6,8);}}preview(graphics);}
    }
    private void preview(GuiGraphics graphics){graphics.flush();var poses=graphics.pose();poses.pushPose();poses.translate(leftPos+70,topPos+120,100);poses.mulPose(Axis.YP.rotationDegrees(90));poses.scale(.875F,.875F,.875F);poses.scale(-8,-8,-8);var lights=new Matrix3f(poses.last().normal()).rotateY((float)Math.toRadians(75));RenderSystem.setShaderLights(lights.transform(new Vector3f(.2F,1,-.7F).normalize()).normalize(),lights.transform(new Vector3f(-.2F,1,.7F).normalize()).normalize());RustedMissileRenderer.model(poses,graphics.bufferSource(),LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY,true);graphics.flush();poses.popPose();Lighting.setupFor3DItems();}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);if(legacyInfoHovered(x,y,26,36,16,16)){List<Component> lines=new ArrayList<>();lines.add(Component.translatable("gui.hbm.launch_pad_rusted.release").withStyle(ChatFormatting.YELLOW));for(int i=0;i<4;i++)lines.add(Component.translatable("gui.hbm.launch_pad_rusted.release."+i));graphics.renderComponentTooltip(font,lines,x,y);}}
    @Override public boolean mouseClicked(double x,double y,int button){boolean result=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,26,36,16,16)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));if(minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,0);return true;}return result;}
}

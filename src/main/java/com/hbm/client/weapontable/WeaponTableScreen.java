package com.hbm.client.weapontable;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.machine.weapontable.WeaponTableMenu;
import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.joml.Vector3f;

/** Original176x240 GUI, config arrow/hit area and camera-drag-controlled raw source renderModTable. */
public final class WeaponTableScreen extends LegacyContainerScreen<WeaponTableMenu> implements GunWeaponTableScreen {
    private double yaw=20,pitch=-10;
    private boolean draggingPreview;
    private final WeaponTablePreview preview=new WeaponTablePreview();
    public WeaponTableScreen(WeaponTableMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/machine/gui_weapon_modifier.png"),176,240);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){var title=Component.translatable("container.weaponsTable");graphics.drawString(font,title,(imageWidth-font.width(title))/2,6,0xffffff,false);graphics.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){
        super.renderBg(graphics,partial,mouseX,mouseY);var gun=menu.weapon();if(menu.configCount()==0)return;
        drawLegacyRegion(graphics,35,112,176+6*menu.index,0,6,8);
        var actual=IClientItemExtensions.of(gun).getCustomRenderer();if(!(actual instanceof GunFirstPersonRenderer renderer))return;
        graphics.flush();var poses=graphics.pose();poses.pushPose();GunRenderOwner.push(minecraft.player,gun);
        try{
            poses.translate(leftPos+88,topPos+57,100);poses.mulPose(Axis.YP.rotationDegrees((float)yaw));poses.mulPose(Axis.XP.rotationDegrees((float)pitch));
            // Source RenderHelper.enableStandardItemLighting was called under an180degree X matrix.
            RenderSystem.setShaderLights(new Vector3f(.2F,-1,.7F).normalize(),new Vector3f(-.2F,-1,-.7F).normalize());RenderSystem.enableDepthTest();
            preview.draw(renderer,gun,menu.index,poses,graphics.bufferSource(),240,OverlayTexture.NO_OVERLAY,partial);graphics.flush();
        }finally{GunRenderOwner.pop();poses.popPose();Lighting.setupFor3DItems();}
    }
    @Override public boolean mouseClicked(double x,double y,int button){
        draggingPreview=false;
        if(button==0&&menu.configCount()>0&&legacyInfoHovered(x,y,8,18,160,79)){
            draggingPreview=true;setDragging(true);return true;
        }
        boolean handled=super.mouseClicked(x,y,button);
        if(legacyInfoHovered(x,y,26,111,7,10)&&menu.configCount()>1){int next=(menu.index+1)%menu.configCount();menu.index=next;minecraft.gameMode.handleInventoryButtonClick(menu.containerId,next);return true;}
        return handled;
    }
    @Override public boolean mouseDragged(double x,double y,int button,double deltaX,double deltaY){
        if(button==0&&draggingPreview){
            yaw+=deltaX*360D/160D;
            pitch=Math.max(-90D,Math.min(90D,pitch-deltaY*180D/79D));
            return true;
        }
        return super.mouseDragged(x,y,button,deltaX,deltaY);
    }
    @Override public boolean mouseReleased(double x,double y,int button){
        if(button==0&&draggingPreview){draggingPreview=false;setDragging(false);return true;}
        return super.mouseReleased(x,y,button);
    }
}

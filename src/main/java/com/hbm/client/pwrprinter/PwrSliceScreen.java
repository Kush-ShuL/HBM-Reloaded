package com.hbm.client.pwrprinter;

import com.hbm.item.pwrprinter.PwrPrintSnapshot;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

/** Exact source fixed isometric matrix/swizzle, one real layer per frame, actual full-resolution transparent PNG export. */
public final class PwrSliceScreen extends Screen {
    private final PwrPrintSnapshot snapshot;private final String directory=DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss").format(LocalDateTime.now());private int index,failures;
    public PwrSliceScreen(PwrPrintSnapshot snapshot){super(Component.translatable("item.hbm.pwr_printer"));this.snapshot=snapshot;}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        graphics.fill(0,0,width,height,0xffff00ff);graphics.flush();
        if(index>=snapshot.sizeY()){if(minecraft.player!=null)minecraft.player.displayClientMessage(failures>0?Component.translatable("message.hbm.pwr_printer.failed",failures):Component.translatable("message.hbm.pwr_printer.saved",".minecraft/printer/"+directory),false);onClose();return;}
        var poses=graphics.pose();poses.pushPose();
        poses.translate(width/2D,height/2D-36,400);poses.scale(-24,-24,-24);poses.scale(1,1,.5F);poses.mulPose(Axis.XP.rotationDegrees(-30));poses.mulPose(Axis.YP.rotationDegrees(225));
        var direction=snapshot.direction();if(direction==Direction.WEST||direction==Direction.EAST)poses.translate(snapshot.sizeX()/-2D,-snapshot.sizeY()/2D,snapshot.sizeZ()/-2D);else poses.translate(snapshot.sizeZ()/-2D,-snapshot.sizeY()/2D,snapshot.sizeX()/-2D);
        RenderSystem.enableDepthTest();Lighting.setupFor3DItems();var buffers=graphics.bufferSource();var vertex=buffers.getBuffer(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));var blocks=minecraft.getBlockRenderer();
        for(var cell:snapshot.cells()){
            if(cell.position().getY()!=snapshot.min().getY()+index)continue;int x=cell.position().getX()-snapshot.min().getX(),z=cell.position().getZ()-snapshot.min().getZ(),dx=x,dz=z;
            if(direction==Direction.WEST){dx=snapshot.sizeZ()-1-z;dz=x;}else if(direction==Direction.SOUTH){dx=snapshot.sizeX()-1-x;dz=snapshot.sizeZ()-1-z;}else if(direction==Direction.EAST){dx=z;dz=snapshot.sizeX()-1-x;}
            // Source print renders original components with zero metadata; controller0 has the south front.
            var state=cell.original().defaultBlockState();poses.pushPose();poses.translate(dx,0,dz);blocks.getModelRenderer().renderModel(poses.last(),vertex,state,blocks.getBlockModel(state),1,1,1,LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY);poses.popPose();
        }
        buffers.endBatch();poses.popPose();
        try(var image=Screenshot.takeScreenshot(minecraft.getMainRenderTarget())){
            for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++)if(image.getPixelRGBA(x,y)==0xffff00ff)image.setPixelRGBA(x,y,0);
            Path target=minecraft.gameDirectory.toPath().resolve("printer").resolve(directory);Files.createDirectories(target);image.writeToFile(target.resolve("slice_"+index+".png"));
        }catch(Exception error){failures++;LogUtils.getLogger().warn("Failed to save source PWR construction slice",error);}
        index++;
    }
}

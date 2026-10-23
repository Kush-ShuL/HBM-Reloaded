package com.hbm.block.pipe.client;

import com.hbm.HbmReloaded;
import com.hbm.block.pipe.NativePipeBlock;
import com.hbm.block.pipe.NativePipeBlockEntity;
import com.hbm.block.pipe.PipeKind;
import com.hbm.block.pipe.SpecialPipeBlock;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

final class SpecialPipeRenderer {
    static final ResourceLocation ANCHOR_MODEL = HbmReloaded.id("models/network/pipe_anchor.obj");
    private SpecialPipeRenderer() {}
    static void render(NativePipeBlockEntity pipe, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        PipeKind kind = pipe.kind(); var state = pipe.getBlockState();
        if (kind.anchor()) { anchor(pipe, poses, buffers, light, overlay); return; }
        if (kind.box()) {
            int meta = ((SpecialPipeBlock) state.getBlock()).metadata(state), mask = ((SpecialPipeBlock) state.getBlock()).mask(pipe.getLevel(), pipe.getBlockPos());
            box(poses, buffers, light, overlay, kind, meta, mask, !kind.exhaust() && meta % 3 == 2 ? lighten(pipe.getFluidType().getColor()) : 0xFFFFFF); return;
        }
        var output = buffers.getBuffer(RenderType.cutout());
        if (kind == PipeKind.COUNTER) {
            cube(poses, output, light, overlay, face -> PipeCubeRenderer.sprite("fluid_counter_valve_" + (state.getValue(NativePipeBlock.OPEN) ? "on" : "off")), 0xFFFFFF); return;
        }
        if (kind == PipeKind.GAUGE) {
            cube(poses, output, light, overlay, face -> PipeCubeRenderer.sprite("deco_steel"), 0xFFFFFF);
            cube(poses, output, light, overlay, face -> PipeCubeRenderer.sprite(face == state.getValue(SpecialPipeBlock.FACING) ? "pipe_gauge" : "fluid_duct_paintable_overlay"), 0xFFFFFF); return;
        }
        if (kind.paintable()) {
            BlockState paint = pipe.paint();
            if (paint != null) {
                cube(poses, output, light, overlay, face -> paintedSprite(paint, face), 0xFFFFFF);
                cube(poses, output, light, overlay, state.getValue(SpecialPipeBlock.HIDE_OVERLAY) ? face -> paintedSprite(paint, face) : face -> PipeCubeRenderer.sprite("fluid_duct_paintable_overlay"), 0xFFFFFF);
            } else {
                String base = kind.exhaust() ? "fluid_duct_paintable_block_exhaust" : "fluid_duct_paintable";
                cube(poses, output, light, overlay, face -> PipeCubeRenderer.sprite(base), 0xFFFFFF);
                cube(poses, output, light, overlay, face -> PipeCubeRenderer.sprite(kind.exhaust() ? base : "fluid_duct_paintable_color"), kind.exhaust() ? 0xFFFFFF : pipe.getFluidType().getColor());
            }
        }
    }
    private static TextureAtlasSprite paintedSprite(BlockState state, Direction side) {
        var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        var quads = model.getQuads(state, side, RandomSource.create(0));
        if (quads.isEmpty()) for (var quad : model.getQuads(state, null, RandomSource.create(0))) if (quad.getDirection() == side) return quad.getSprite();
        return quads.isEmpty() ? model.getParticleIcon() : quads.getFirst().getSprite();
    }
    private static void cube(PoseStack poses, com.mojang.blaze3d.vertex.VertexConsumer out, int light, int overlay, Function<Direction, TextureAtlasSprite> sprites, int color) {
        PipeCubeRenderer.cube(poses, out, light, overlay, 0, 0, 0, 1, 1, 1, sprites, color, null, true);
    }
    static void box(PoseStack poses, MultiBufferSource buffers, int light, int overlay, PipeKind kind, int meta, int mask, int color) {
        float low = (2 + meta / 3) / 16F, high = 1-low, jLow = low - 1/16F, jHigh = high + 1/16F;
        int[] rotation = new int[6];
        boolean straightX = mask != 0 && (mask & 15) == 0, straightZ = mask != 0 && (mask & 60) == 0, straightY = mask != 0 && (mask & 51) == 0;
        if (straightX) { rotation[1]=1; rotation[0]=1; rotation[2]=2; rotation[3]=1; }
        if (straightZ) { rotation[4]=1; rotation[5]=2; }
        int count = Integer.bitCount(mask);
        if (!straightX && !straightY && !straightZ && count == 2) {
            if ((mask & 12) != 0 && (mask & 48) != 0) { rotation[0]=1; rotation[1]=1; }
            if ((mask & 12) == 0) { rotation[4]=1; rotation[5]=2; rotation[2]=2; rotation[3]=1; }
        }
        var out = buffers.getBuffer(RenderType.cutout());
        Function<Direction, TextureAtlasSprite> sprites = side -> PipeCubeRenderer.sprite(boxTexture(kind, meta, mask, side));
        if (straightX) { emitBox(poses,out,light,overlay,0,low,low,1,high,high,sprites,color,rotation); return; }
        if (straightZ) { emitBox(poses,out,light,overlay,low,low,0,high,high,1,sprites,color,rotation); return; }
        if (straightY) { emitBox(poses,out,light,overlay,low,0,low,high,1,high,sprites,color,rotation); return; }
        float centerLow = count == 2 ? low : jLow, centerHigh = count == 2 ? high : jHigh;
        emitBox(poses,out,light,overlay,centerLow,centerLow,centerLow,centerHigh,centerHigh,centerHigh,sprites,color,rotation);
        if ((mask&4)!=0) emitBox(poses,out,light,overlay,low,0,low,high,centerLow,high,sprites,color,rotation);
        if ((mask&8)!=0) emitBox(poses,out,light,overlay,low,centerHigh,low,high,1,high,sprites,color,rotation);
        if ((mask&16)!=0) emitBox(poses,out,light,overlay,0,low,low,centerLow,high,high,sprites,color,rotation);
        if ((mask&32)!=0) emitBox(poses,out,light,overlay,centerHigh,low,low,1,high,high,sprites,color,rotation);
        if ((mask&1)!=0) emitBox(poses,out,light,overlay,low,low,0,high,high,centerLow,sprites,color,rotation);
        if ((mask&2)!=0) emitBox(poses,out,light,overlay,low,low,centerHigh,high,high,1,sprites,color,rotation);
    }
    private static void emitBox(PoseStack poses, com.mojang.blaze3d.vertex.VertexConsumer out, int light, int overlay, float x0,float y0,float z0,float x1,float y1,float z1,Function<Direction,TextureAtlasSprite> sprites,int color,int[] rotation) {
        PipeCubeRenderer.cube(poses,out,light,overlay,x0,y0,z0,x1,y1,z1,sprites,color,rotation,true);
    }
    /** FluidDuctBox.getIcon's original side/connection decision tree, including four identical-side horizontal branches. */
    static String boxTexture(PipeKind kind, int meta, int mask, Direction side) {
        String prefix = "boxduct_" + (kind.exhaust() ? "exhaust" : new String[]{"silver","copper","white"}[meta % 3]) + "_";
        if (mask != 0 && (mask&15)==0) return prefix + (side.getAxis()==Direction.Axis.X ? "end":"straight");
        if (mask != 0 && (mask&60)==0) return prefix + (side.getAxis()==Direction.Axis.Z ? "end":"straight");
        if (mask != 0 && (mask&51)==0) return prefix + (side.getAxis()==Direction.Axis.Y ? "end":"straight");
        if (Integer.bitCount(mask)==2) {
            if ((mask&NativePipeBlock.bit(side))!=0) return prefix+"end";
            if ((mask&NativePipeBlock.bit(side.getOpposite()))!=0) return prefix+"straight";
            boolean nY=(mask&4)!=0,pY=(mask&8)!=0,pZ=(mask&2)!=0,nZ=(mask&1)!=0,pX=(mask&32)!=0,nX=(mask&16)!=0;
            if(nY&&pZ) return prefix+(side==Direction.WEST?"curve_br":"curve_bl");
            if(nY&&nZ) return prefix+(side==Direction.EAST?"curve_br":"curve_bl");
            if(nY&&pX) return prefix+(side==Direction.SOUTH?"curve_br":"curve_bl");
            if(nY&&nX) return prefix+(side==Direction.NORTH?"curve_br":"curve_bl");
            if(pY&&pZ) return prefix+(side==Direction.WEST?"curve_tr":"curve_tl");
            if(pY&&nZ) return prefix+(side==Direction.EAST?"curve_tr":"curve_tl");
            if(pY&&pX) return prefix+(side==Direction.SOUTH?"curve_tr":"curve_tl");
            if(pY&&nX) return prefix+(side==Direction.NORTH?"curve_tr":"curve_tl");
            if(pX&&nZ) return prefix+"curve_tr"; if(pX&&pZ) return prefix+"curve_br";
            if(nX&&nZ) return prefix+"curve_tl"; if(nX&&pZ) return prefix+"curve_bl";
        }
        return prefix+"junction_"+meta/3;
    }
    static int lighten(int rgb) { int r=rgb>>16&255,g=rgb>>8&255,b=rgb&255; return (int)(r+(255-r)*.25)<<16 | (int)(g+(255-g)*.25)<<8 | (int)(b+(255-b)*.25); }
    private static void anchor(NativePipeBlockEntity pipe, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        poses.pushPose(); poses.translate(.5,.5,.5);
        poses.pushPose();
        switch (pipe.getBlockState().getValue(SpecialPipeBlock.FACING)) {
            case DOWN -> poses.mulPose(Axis.XP.rotationDegrees(180)); case UP -> {}
            case NORTH -> { poses.mulPose(Axis.XP.rotationDegrees(90)); poses.mulPose(Axis.ZP.rotationDegrees(180)); }
            case SOUTH -> poses.mulPose(Axis.XP.rotationDegrees(90));
            case WEST -> { poses.mulPose(Axis.XP.rotationDegrees(90)); poses.mulPose(Axis.ZP.rotationDegrees(90)); }
            case EAST -> { poses.mulPose(Axis.XP.rotationDegrees(90)); poses.mulPose(Axis.ZP.rotationDegrees(270)); }
        }
        poses.translate(0,-.5,0); anchorPart(poses,buffers,light,overlay,pipe.kind(),0xFFFFFF,"Anchor"); poses.popPose();
        for (var target : pipe.connected()) if (pipe.getLevel().getBlockEntity(target) instanceof NativePipeBlockEntity other && other.kind().anchor()) {
            Vec3 first=pipe.connectionPoint(),second=other.connectionPoint();
            if (!dominant(first,second)) continue;
            Vec3 delta=second.subtract(first); double horizontal=Math.sqrt(delta.x*delta.x+delta.z*delta.z),length=delta.length();
            poses.pushPose(); poses.mulPose(Axis.YP.rotation((float)Math.atan2(delta.x,delta.z))); poses.mulPose(Axis.XP.rotation((float)(Math.PI/2-Math.atan2(delta.y,horizontal))));
            poses.pushPose(); poses.scale(1,(float)length,1); poses.translate(0,-.5,0);
            anchorPart(poses,buffers,light,overlay,pipe.kind(),pipe.kind().exhaust()?0xFFFFFF:lighten(pipe.getFluidType().getColor()),"Pipe"); poses.popPose();
            poses.translate(0,length/2-1.5,0); anchorPart(poses,buffers,light,overlay,pipe.kind(),0xFFFFFF,"Ring"); poses.popPose();
        }
        poses.popPose();
    }
    private static boolean dominant(Vec3 a,Vec3 b) { return a.x<b.x || a.x==b.x&&(a.y<b.y || a.y==b.y&&a.z<b.z); }
    static void anchorPart(PoseStack poses,MultiBufferSource buffers,int light,int overlay,PipeKind kind,int color,String part) {
        var model=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ANCHOR_MODEL);
        var texture=HbmReloaded.id("textures/models/network/pipe_anchor"+(kind.exhaust()?"_exhaust":"")+".png");
        LegacyObjRenderer.renderOnly(model,poses,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,0xFF000000|color,LegacyObjRenderer.Convention.HFR,part);
    }
}

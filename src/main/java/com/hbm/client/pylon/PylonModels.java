package com.hbm.client.pylon;

import com.hbm.HbmReloaded;
import com.hbm.block.pylon.PylonBlockEntity;
import com.hbm.block.pylon.PylonKind;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.model.WavefrontMesh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.EnumMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

/** One authored mesh/transform definition for both original rendering and finite culling bounds. */
final class PylonModels {
    private record CachedBounds(WavefrontMesh mesh,AABB bounds) {}
    private static final EnumMap<PylonKind,EnumMap<Direction,CachedBounds>> BOUNDS=new EnumMap<>(PylonKind.class);
    private PylonModels() {}
    private static String modelName(PylonKind kind) {
        if(kind.medium()) return "pylon_medium";
        return switch(kind) { case CONNECTOR->"connector";case SUPER_CONNECTOR->"connector_super";case LARGE->"pylon_large";case SUBSTATION->"substation";default->"pylon"; };
    }
    static WavefrontMesh mesh(PylonKind kind) { return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),HbmReloaded.id("models/network/"+modelName(kind)+".obj")); }
    static ResourceLocation texture(PylonKind kind) {
        String name=kind.medium()?(kind.steel()?"pylon_medium_steel":"pylon_medium"):(kind==PylonKind.STEEL?"pylon_steel":modelName(kind));
        return HbmReloaded.id("textures/models/network/"+name+".png");
    }
    static boolean selected(PylonKind kind,WavefrontMesh.Face face) {
        if(kind.medium()) return face.legacyPart().equalsIgnoreCase("Pylon")||kind.transformer()&&face.legacyPart().equalsIgnoreCase("Transformer");
        if(kind==PylonKind.WOOD||kind==PylonKind.STEEL) return face.legacyPart().equalsIgnoreCase(kind==PylonKind.WOOD?"Pylon":"Pylon_steel");
        return true;
    }
    static void worldTransform(PoseStack poses,PylonKind kind,Direction facing) {
        if(kind.connector()) {
            poses.translate(.5,.5,.5);
            switch(facing) {
                case DOWN->poses.mulPose(Axis.XP.rotationDegrees(180));
                case NORTH->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(180));}
                case SOUTH->poses.mulPose(Axis.XP.rotationDegrees(90));
                case WEST->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(90));}
                case EAST->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(270));}
                default->{}
            }
            poses.translate(0,-.5,0);
        } else {
            poses.translate(.5,0,.5);
            if(kind.medium()) poses.mulPose(Axis.YP.rotationDegrees(switch(facing) { case NORTH->180;case WEST->270;case EAST->90;default->0; }));
            else if(kind==PylonKind.LARGE) poses.mulPose(Axis.YP.rotationDegrees(switch(facing) { case NORTH->90;case WEST->135;case EAST->45;default->0; }));
            else if(kind==PylonKind.SUBSTATION&&facing.getAxis()==Direction.Axis.Z) poses.mulPose(Axis.YP.rotationDegrees(90));
        }
    }
    static void draw(PylonKind kind,PoseStack poses,MultiBufferSource buffers,int light,int overlay,boolean item) {
        ResourceLocation texture=texture(kind);
        boolean noCull=!item&&(kind==PylonKind.WOOD||kind==PylonKind.STEEL||kind==PylonKind.LARGE);
        var consumer=buffers.getBuffer(noCull?RenderType.entityCutoutNoCull(texture):RenderType.entityCutout(texture));
        LegacyObjRenderer.render(mesh(kind),poses,consumer,light,overlay,0xFFFFFFFF,kind==PylonKind.SUBSTATION?LegacyObjRenderer.Convention.HFR:LegacyObjRenderer.Convention.HFR_FLAT,face->selected(kind,face));
    }
    static AABB worldBounds(PylonBlockEntity pylon) {
        PylonKind kind=pylon.kind();Direction facing=pylon.facing();WavefrontMesh mesh=mesh(kind);
        var variants=BOUNDS.computeIfAbsent(kind,ignored->new EnumMap<>(Direction.class));var cached=variants.get(facing);
        if(cached==null||cached.mesh()!=mesh) {
            PoseStack poses=new PoseStack();worldTransform(poses,kind,facing);
            double minX=0,minY=0,minZ=0,maxX=1,maxY=1,maxZ=1;
            for(var face:mesh.faces()) if(selected(kind,face)) for(var corner:face.corners()) {
                var position=mesh.positions().get(corner.position());
                var transformed=new Vector3f(position.x(),position.y(),position.z()).mulPosition(poses.last().pose());
                minX=Math.min(minX,transformed.x);minY=Math.min(minY,transformed.y);minZ=Math.min(minZ,transformed.z);
                maxX=Math.max(maxX,transformed.x);maxY=Math.max(maxY,transformed.y);maxZ=Math.max(maxZ,transformed.z);
            }
            cached=new CachedBounds(mesh,new AABB(minX,minY,minZ,maxX,maxY,maxZ).inflate(.001));variants.put(facing,cached);
        }
        return cached.bounds().move(pylon.getBlockPos());
    }
}

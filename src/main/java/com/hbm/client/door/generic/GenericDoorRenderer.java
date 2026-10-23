package com.hbm.client.door.generic;
import com.hbm.client.model.*;
import com.hbm.door.generic.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Exact original RenderDoorGeneric part transforms and RenderCargoDoor BOT/TOP buses. */
public final class GenericDoorRenderer implements BlockEntityRenderer<GenericDoorBlockEntity> {
    public GenericDoorRenderer(BlockEntityRendererProvider.Context context){}
    public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}
    public static WavefrontMesh mesh(GenericDoorDeclaration declaration){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id(declaration.siloHatch()?"models/doors/"+declaration.id+".obj":"models/pheodoors/cargo_door.obj"));}
    private static ResourceLocation texture(GenericDoorDeclaration declaration){return id(declaration.siloHatch()?"textures/models/doors/"+declaration.id+".png":"textures/models/pheodoors/cargo_door.png");}
    private static void part(GenericDoorDeclaration declaration,String part,PoseStack poses,MultiBufferSource buffers,int light,int overlay,boolean item){var type=declaration.siloHatch()&&!item?RenderType.entityTranslucent(texture(declaration)):RenderType.entityCutout(texture(declaration));LegacyObjRenderer.renderOnly(mesh(declaration),poses,buffers.getBuffer(type),light,overlay,-1,LegacyObjRenderer.Convention.HFR,part);}
    public static void silo(PoseStack poses,MultiBufferSource buffers,int light,int overlay,float ticks,boolean item){silo(GenericDoorDeclaration.SILO_HATCH,poses,buffers,light,overlay,ticks,item);}
    public static void silo(GenericDoorDeclaration type,PoseStack poses,MultiBufferSource buffers,int light,int overlay,float ticks,boolean item){
        part(type,"Frame",poses,buffers,light,overlay,item);poses.pushPose();poses.translate(0,.875,type.hatchPivotZ());
        float angle=item?-120:GenericDoorDeclaration.smooth(GenericDoorDeclaration.norm(ticks,20,100))*-240;
        poses.mulPose(Axis.XP.rotationDegrees(angle));poses.translate(0,-.875+(item?.25:GenericDoorDeclaration.smooth(GenericDoorDeclaration.norm(ticks,0,10))*.25),-type.hatchPivotZ());part(type,"Hatch",poses,buffers,light,overlay,item);poses.popPose();
    }
    public static void cargo(PoseStack poses,MultiBufferSource buffers,int light,int overlay,double bot,double top,boolean item){var type=GenericDoorDeclaration.CARGO_DOOR;part(type,"Frame",poses,buffers,light,overlay,item);poses.pushPose();poses.translate(0,top,0);part(type,"DoorTop",poses,buffers,light,overlay,item);poses.popPose();poses.pushPose();poses.translate(0,bot,0);part(type,"DoorBot",poses,buffers,light,overlay,item);poses.popPose();}
    @Override public void render(GenericDoorBlockEntity door,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(switch(door.getBlockState().getValue(DummyableBlock.LEGACY_META)-10){case 2->90;case 4->180;case 3->270;default->0;}));
        if(door.declaration()==GenericDoorDeclaration.FIRE_DOOR){com.hbm.client.door.fire.FireDoorModel.world(door,poses,buffers,light,overlay);poses.popPose();return;}
        if(door.declaration()==GenericDoorDeclaration.SLIDE_DOOR){com.hbm.client.door.slidingblast.SlidingBlastDoorModel.world(door,poses,buffers,light,overlay);poses.popPose();return;}
        if(door.declaration()==GenericDoorDeclaration.VAULT_DOOR){com.hbm.client.door.vault.VaultDoorModel.world(door,poses,buffers,light,overlay);poses.popPose();return;}
        if(door.declaration().family247()){com.hbm.client.door.family.GenericDoorFamilyModels.world(door,poses,buffers,light,overlay);poses.popPose();return;}
        if(door.declaration()==GenericDoorDeclaration.TRANSITION_SEAL){com.hbm.client.door.transition.TransitionSealModel.world(door,poses,buffers,light,overlay);poses.popPose();return;}
        long ms=System.currentTimeMillis()-door.animStartTime;
        if(door.declaration().siloHatch()){float ticks=Mth.clamp(door.state==GenericDoorBlockEntity.CLOSING||door.state==GenericDoorBlockEntity.CLOSED?3000-ms:ms,0,3000)*.02F;silo(door.declaration(),poses,buffers,light,overlay,ticks,false);}
        else {double bot=door.state==GenericDoorBlockEntity.OPEN?2:0,top=door.state==GenericDoorBlockEntity.OPEN?1:0;if(door.animStartTime!=0){double phase=Mth.clamp(ms/3000D,0,1);if(door.state==GenericDoorBlockEntity.OPENING||door.state==GenericDoorBlockEntity.OPEN){bot=2*phase;top=Mth.clamp((ms-1500)/1500D,0,1);}else if(door.state==GenericDoorBlockEntity.CLOSING||door.state==GenericDoorBlockEntity.CLOSED){bot=2*(1-phase);top=1-Mth.clamp((ms-1500)/1500D,0,1);}}cargo(poses,buffers,light,overlay,bot,top,false);}
        poses.popPose();
    }
    @Override public AABB getRenderBoundingBox(GenericDoorBlockEntity door){if(door.declaration()==GenericDoorDeclaration.TRANSITION_SEAL)return AABB.INFINITE;double radius=door.declaration()==GenericDoorDeclaration.VAULT_DOOR?8:6;return new AABB(door.getBlockPos()).inflate(radius,8,radius);}
    @Override public boolean shouldRenderOffScreen(GenericDoorBlockEntity door){return true;}
    @Override public int getViewDistance(){return 256;}
}

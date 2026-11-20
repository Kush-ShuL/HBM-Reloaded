package com.hbm.client.mass;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.entity.mob.mass.EntityMass;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MassRenderer extends EntityRenderer<EntityMass> {
    private static final ResourceLocation MESH = ResourceLocation.parse("hbm:models/mobs/mass.obj");
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/entity/mass.png");
    private static final String[] PARTS = {"Gear","Beam","Panel","Bar","Angle","Pipe","Drill"};
    private final WeakHashMap<EntityMass, Animation> animations = new WeakHashMap<>();

    public MassRenderer(EntityRendererProvider.Context context) { super(context); }
    @SubscribeEvent public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityMass.TYPE.get(), MassRenderer::new);
    }
    @Override public ResourceLocation getTextureLocation(EntityMass entity) { return TEXTURE; }
    @Override public void render(EntityMass entity,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light) {
        var animation = animations.computeIfAbsent(entity, ignored -> new Animation(entity.getId()));
        animation.tick(entity);
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MESH);
        var out = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        pose.pushPose();
        pose.translate(0,1.5,0);
        for (var part : animation.fragments) {
            pose.pushPose();
            var position = part.previous.lerp(part.pos,partial);
            pose.translate(position.x,position.y,position.z);
            pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partial,part.prevPitch,part.pitch)));
            pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partial,part.prevYaw,part.yaw)));
            pose.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partial,part.prevRoll,part.roll)));
            pose.scale(.75F,.75F,.75F);
            LegacyObjRenderer.renderOnly(mesh,pose,out,light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,part.model);
            pose.popPose();
        }
        pose.popPose();
        super.render(entity,yaw,partial,pose,buffers,light);
    }

    private static final class Animation {
        final RandomSource random;
        final Fragment[] fragments = new Fragment[250];
        int lastTick = -1;
        Animation(int seed) {
            random = RandomSource.create(seed);
            for (int i=0;i<fragments.length;i++) fragments[i] = new Fragment(random);
        }
        void tick(EntityMass mass) {
            if (lastTick == mass.tickCount) return;
            lastTick = mass.tickCount;
            for (var part : fragments) part.advance(random);
            if (random.nextInt(40)==0) for (int i=0;i<5;i++)
                mass.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK,Blocks.REDSTONE_BLOCK.defaultBlockState()),
                        mass.getX()+offset(random),mass.getY()+1.5+offset(random),mass.getZ()+offset(random),0,-.15,0);
        }
    }
    private static final class Fragment {
        final String model;
        Vec3 pos, previous, target;
        float yaw, pitch, roll, prevYaw, prevPitch, prevRoll, yawSpeed, pitchSpeed, rollSpeed;
        Fragment(RandomSource random) {
            model = PARTS[random.nextInt(PARTS.length)];
            double x=offset(random), y=offset(random), z=offset(random);
            if(random.nextInt(3)!=0){
                if(random.nextInt(3)==0)x=1;
                else if(random.nextBoolean())y=1;
                else z=1;
                if(random.nextBoolean()){x=-x;y=-y;z=-z;}
            }
            pos = previous = target = new Vec3(x,y,z);
        }
        void advance(RandomSource r) {
            previous=pos;prevYaw=yaw;prevPitch=pitch;prevRoll=roll;
            if(Math.abs(pos.x-target.x)<.05&&Math.abs(pos.y-target.y)<.05&&Math.abs(pos.z-target.z)<.05){
                if(r.nextInt(3)==0)target=new Vec3(offset(r),target.y,target.z);
                else if(r.nextBoolean())target=new Vec3(target.x,offset(r),target.z);
                else target=new Vec3(target.x,target.y,offset(r));
            }else pos=new Vec3(approach(pos.x,target.x),approach(pos.y,target.y),approach(pos.z,target.z));
            yawSpeed=spin(yawSpeed,r);pitchSpeed=spin(pitchSpeed,r);rollSpeed=spin(rollSpeed,r);
            yaw+=yawSpeed;pitch+=pitchSpeed;roll+=rollSpeed;
        }
        private static float spin(float speed,RandomSource r) {
            return r.nextInt(20)==0?Mth.clamp(speed+(float)r.nextGaussian()*5,-15,15):speed;
        }
        private static double approach(double position,double target){
            return Math.abs(position-target)<.05?position:position+Math.copySign(.05,target-position);
        }
    }
    private static double offset(RandomSource random) { return random.nextDouble()*2.5-1.25; }
}

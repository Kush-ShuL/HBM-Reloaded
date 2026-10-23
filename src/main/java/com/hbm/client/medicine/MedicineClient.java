package com.hbm.client.medicine;

import com.hbm.item.medicine.MedicineVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SmokeParticle;
import com.hbm.item.medicine.MedicineContent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MedicineClient {
    private static SpriteSet smokeSprites;
    private MedicineClient() {}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {MedicineVisuals.clientHandler=MedicineClient::particles;}
    @SubscribeEvent public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(MedicineContent.SMOKE.get(),sprites->{smokeSprites=sprites;return (type,level,x,y,z,vx,vy,vz)->new CigaretteSmoke(level,x,y,z,vx,vy,vz,sprites);});
    }
    private static void particles(MedicineVisuals.Effect effect) {
        var minecraft=Minecraft.getInstance();var level=minecraft.level;if(level==null)return;
        var random=level.random;
        int setting=minecraft.options.particles().get().getId();
        if(effect.kind()==0) {
            int count=10/(setting+1);
            for(int i=0;i<count;i++) {
                double vy=random.nextGaussian()*(1+count/100),vx=random.nextGaussian()*(1+count/150),vz=random.nextGaussian()*(1+count/150);
                if(random.nextBoolean())vy=Math.abs(vy);
                minecraft.particleEngine.add(new SourceExplosionSmoke(level,effect.x(),effect.y(),effect.z(),vx,vy,vz));
            }
        } else if((effect.kind()==1||effect.kind()==2)&&level.getEntity(effect.entity()) instanceof LivingEntity entity) {
            sourceVomit(entity,effect.kind()==1?1:2,effect.kind()==1?25:30);
        } else if(effect.kind()==3&&minecraft.player!=null) {
            minecraft.player.animateHurt(0);minecraft.player.hurtTime=(int)effect.x();minecraft.player.hurtDuration=(int)effect.y();
        }
    }
    /** The source auxiliary vomit renderer, shared by MKU, radiation sickness and lung disease. */
    public static void sourceVomit(LivingEntity entity,int mode,int count){
        var minecraft=Minecraft.getInstance();var level=minecraft.level;if(level==null)return;var random=level.random;int setting=minecraft.options.particles().get().getId();var look=entity.getLookAngle();double x=entity.getX(),y=entity.getEyeY()+(entity instanceof net.minecraft.world.entity.player.Player?1:0),z=entity.getZ();
        for(int i=0;i<count/(setting+1);i++){
            if(mode==2){if(smokeSprites!=null)minecraft.particleEngine.add(new CigaretteSmoke(level,x,y,z,(look.x+random.nextGaussian()*.1)*.05,(look.y+random.nextGaussian()*.1)*.05,(look.z+random.nextGaussian()*.1)*.05,smokeSprites));}
            else {double vx=(look.x+random.nextGaussian()*.2)*.2,vy=(look.y+random.nextGaussian()*.2)*.2,vz=(look.z+random.nextGaussian()*.2)*.2;var block=mode==0?(random.nextBoolean()?Blocks.LIME_TERRACOTTA:Blocks.GREEN_TERRACOTTA):Blocks.REDSTONE_BLOCK;minecraft.particleEngine.add(new BloodDust(level,x,y,z,vx,vy,vz,block.defaultBlockState()));}
        }
    }
    /** EntityBlockDustFX constructor replaced all base random velocity with the requested blood velocity. */
    private static final class BloodDust extends TerrainParticle {
        BloodDust(ClientLevel level,double x,double y,double z,double vx,double vy,double vz,net.minecraft.world.level.block.state.BlockState state) {
            super(level,x,y,z,vx,vy,vz,state);
            xd=vx;yd=vy;zd=vz;lifetime=150+random.nextInt(50);
        }
    }
    private static final class CigaretteSmoke extends SmokeParticle {
        CigaretteSmoke(ClientLevel level,double x,double y,double z,double vx,double vy,double vz,SpriteSet sprites) {
            super(level,x,y,z,vx,vy,vz,.2F,sprites);lifetime=10+random.nextInt(10);
        }
    }
    /** ParticleExSmoke's six independently shaded and positioned quads, exact source fade and drag. */
    private static final class SourceExplosionSmoke extends TextureSheetParticle {
        private static int nextSeed;
        private final int seed=nextSeed++;
        SourceExplosionSmoke(ClientLevel level,double x,double y,double z,double vx,double vy,double vz) {
            super(level,x,y,z);setSprite(Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.fromNamespaceAndPath("hbm","block/particle/particle_base")));
            lifetime=100+random.nextInt(40);xd=vx;yd=vy;zd=vz;
        }
        @Override public ParticleRenderType getRenderType() {return ParticleRenderType.TERRAIN_SHEET;}
        @Override public void tick() {
            xo=x;yo=y;zo=z;alpha=1-(float)age/lifetime;
            if(++age==lifetime)remove();
            xd*=.7599999785423279D;yd*=.7599999785423279D;zd*=.7599999785423279D;move(xd,yd,zd);
        }
        @Override public void render(com.mojang.blaze3d.vertex.VertexConsumer consumer,net.minecraft.client.Camera camera,float partial) {
            var random=new java.util.Random(seed);
            double oldX=x,oldY=y,oldZ=z,oldXo=xo,oldYo=yo,oldZo=zo;
            for(int i=0;i<6;i++) {
                rCol=gCol=bCol=random.nextFloat()*.25F+.25F;quadSize=random.nextFloat()+.5F;
                double dx=(random.nextGaussian()-1)*.75,dy=(random.nextGaussian()-1)*.75,dz=(random.nextGaussian()-1)*.75;
                x=oldX+dx;y=oldY+dy;z=oldZ+dz;xo=oldXo+dx;yo=oldYo+dy;zo=oldZo+dz;
                super.render(consumer,camera,partial);
            }
            x=oldX;y=oldY;z=oldZ;xo=oldXo;yo=oldYo;zo=oldZo;
        }
        @Override protected float getU0() {return sprite.getU1();}
        @Override protected float getU1() {return sprite.getU0();}
    }
}

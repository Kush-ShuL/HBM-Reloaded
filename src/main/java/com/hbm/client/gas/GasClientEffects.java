package com.hbm.client.gas;

import com.hbm.block.gas.GasContent;
import com.hbm.block.gas.GasKind;
import com.hbm.block.gas.GasVisuals;
import com.hbm.exposure.HbmExposure;
import com.hbm.particle.LegacyParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.PlayerCloudParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/** Native equivalents of townAura/smoke and the source's tinted, scale-7.5 cloud. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GasClientEffects {
    private static SpriteSet sprites;
    private GasClientEffects(){}
    @SubscribeEvent public static void particles(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(GasContent.CLOUD.get(),set->{
            sprites=set;
            return (type,level,x,y,z,vx,vy,vz)->new GasCloud(level,x,y,z,1,1,1,set);
        });
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){
        event.enqueueWork(()->{
            ItemBlockRenderTypes.setRenderLayer(GasContent.BLOCKS_BY_KIND.get(GasKind.CHLORINE).get(),RenderType.translucent());
            GasVisuals.install((level,pos,kind,random)->{
                var player=Minecraft.getInstance().player;
                if(player!=null&&sprites!=null&&level instanceof ClientLevel client){
                    var head=BuiltInRegistries.ITEM.getKey(player.getItemBySlot(EquipmentSlot.HEAD).getItem());
                    boolean glasses=head.getNamespace().equals("hbm")&&head.getPath().equals("ashglasses");
                    if(glasses||HbmExposure.seesGas(player)){
                        var cloud=new GasCloud(client,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,
                                kind.red(),kind.green(),kind.blue(),sprites);
                        float jitter=client.random.nextFloat()*.1F;
                        cloud.setColor(kind.red()+jitter,kind.green()+jitter,kind.blue()+jitter);
                        Minecraft.getInstance().particleEngine.add(cloud);
                    }
                }
                boolean dust=kind==GasKind.RADON_DENSE||kind==GasKind.MELTDOWN||kind==GasKind.ASBESTOS&&level.random.nextInt(5)==0;
                if(dust)level.addParticle(LegacyParticles.AURA.get(),pos.getX()+random.nextFloat(),pos.getY()+random.nextFloat(),pos.getZ()+random.nextFloat(),0,0,0);
                if(kind==GasKind.COAL)level.addParticle(ParticleTypes.SMOKE,pos.getX()+random.nextFloat(),pos.getY()+random.nextFloat(),pos.getZ()+random.nextFloat(),0,0,0);
            });
        });
    }
    /** Source EntityCloudFX (bks): its cached scale is multiplied by .1 in EntityFX rendering. */
    private static final class GasCloud extends PlayerCloudParticle{
        private final SpriteSet sourceSprites;
        GasCloud(ClientLevel level,double x,double y,double z,float r,float g,float b,SpriteSet sprites){
            super(level,x,y,z,0,0,0,sprites);
            sourceSprites=sprites;
            quadSize=.75F;setColor(r,g,b);setParticleSpeed(0,0,0);hasPhysics=true;
            // EntityFX starts on texture 0; the first actual cloud update chooses the aging frame.
            setSprite(sprites.get(7,7));
            setBoundingBox(getBoundingBox().move(0,-.1D,0));
        }
        @Override public void tick(){
            xo=x;yo=y;zo=z;
            if(age++>=lifetime)remove();
            setSprite(sourceSprites.get(Math.min(7,age*8/lifetime),7));
            move(xd,yd,zd);
            xd*=.9599999785423279D;yd*=.9599999785423279D;zd*=.9599999785423279D;
            var player=level.getNearestPlayer(x,y,z,2,false);
            if(player!=null){
                double floor=player.getBoundingBox().minY;
                if(y>floor){y+=(floor-y)*.2D;yd+=(player.getDeltaMovement().y-yd)*.2D;setPos(x,y,z);setBoundingBox(getBoundingBox().move(0,-.1D,0));}
            }
            if(onGround){xd*=.699999988079071D;zd*=.699999988079071D;}
        }
        @Override public void move(double dx,double dy,double dz){
            var allowed=net.minecraft.world.entity.Entity.collideBoundingBox(null,new net.minecraft.world.phys.Vec3(dx,dy,dz),getBoundingBox(),level,java.util.List.of());
            setBoundingBox(getBoundingBox().move(allowed));setLocationFromBoundingbox();onGround=dy!=allowed.y&&dy<0;
            if(dx!=allowed.x)xd=0;if(dy!=allowed.y)yd=0;if(dz!=allowed.z)zd=0;
        }
        @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=.1D;}
    }
}

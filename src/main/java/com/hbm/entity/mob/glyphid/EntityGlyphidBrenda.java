package com.hbm.entity.mob.glyphid;
import com.hbm.entity.effect.EntityMist;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidItemContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
/** Actual fire-immune Brenda, pheromone death mist and twelve genuine grunt offspring. */
public final class EntityGlyphidBrenda extends EntityGlyphid {
    public EntityGlyphidBrenda(EntityType<? extends EntityGlyphidBrenda> type,Level level){super(type,level);}
    @Override public ResourceLocation getSkin(){return ResourceLocation.parse("hbm:textures/entity/glyphid_brenda.png");}
    @Override public double sourceScale(){return 2;}
    @Override public GlyphidStats.StatBundle getStats(){return GlyphidStats.getStats().getBrenda();}
    @Override public boolean isArmorBroken(float amount){return random.nextInt(100)<=Math.min(Math.pow(amount*.12,2),100);}
    @Override public void die(DamageSource source){
        super.die(source);if(!level().isClientSide&&getHealth()<=0){var mist=new EntityMist(level());mist.setType(HbmFluids.PHEROMONE);mist.setPos(getX(),getY(),getZ());mist.setArea(14,6);mist.setDuration(80);level().addFreshEntity(mist);
            for(int i=0;i<12;i++){var grunt=new EntityGlyphid(level());grunt.moveTo(getX(),getY()+.5,getZ(),random.nextFloat()*360,0);level().addFreshEntity(grunt);grunt.move(MoverType.SELF,new Vec3(random.nextGaussian(),0,random.nextGaussian()));}
        }
    }
    @Override protected void sourceDrops(boolean recentlyHit,int looting){super.sourceDrops(recentlyHit,looting);if(random.nextInt(3)==0){var gland=sourceItem("glyphid_gland",1);FluidItemContent.setType(gland,HbmFluids.PHEROMONE);spawnAtLocation(gland,1);}}
}

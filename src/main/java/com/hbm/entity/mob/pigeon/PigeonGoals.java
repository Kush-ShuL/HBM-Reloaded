package com.hbm.entity.mob.pigeon;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/** Five actual X5846 custom tasks. Walking/flying, flag masks and order are source-defined. */
final class PigeonGoals {
    static final class StartFlying extends Goal {
        final EntityPigeon bird;StartFlying(EntityPigeon bird){this.bird=bird;}
        @Override public boolean canUse(){return bird.getFlyingState()==bird.STATE_WALKING&&(bird.getLastHurtByMob()!=null||bird.isOnFire()||bird.getRandom().nextInt(600)==0);}
        @Override public void start(){bird.setFlyingState(bird.STATE_FLYING);}
    }
    static final class StopFlying extends Goal {
        final EntityPigeon bird;StopFlying(EntityPigeon bird){this.bird=bird;}
        @Override public boolean canUse(){return bird.getFlyingState()==bird.STATE_FLYING&&bird.getRandom().nextInt(200)==0;}
        @Override public void start(){bird.setFlyingState(bird.STATE_WALKING);}
    }
    static final class Swim extends Goal {
        final EntityPigeon bird;Swim(EntityPigeon bird){this.bird=bird;setFlags(EnumSet.of(Flag.JUMP));bird.getNavigation().setCanFloat(true);}
        @Override public boolean canUse(){return(bird.isInWater()||bird.isInLava())&&bird.getFlyingState()==bird.STATE_WALKING;}
        @Override public void tick(){if(bird.getRandom().nextFloat()<.8F)bird.getJumpControl().jump();}
        @Override public boolean requiresUpdateEveryTick(){return true;}
    }
    static final class Wander extends Goal {
        final EntityPigeon bird;Vec3 selected;Wander(EntityPigeon bird){this.bird=bird;setFlags(EnumSet.of(Flag.MOVE));}
        @Override public boolean canUse(){
            if(bird.getFlyingState()!=bird.STATE_WALKING||bird.getNoActionTime()>=100||bird.getRandom().nextInt(120)!=0)return false;
            boolean home=bird.hasRestriction()&&bird.getRestrictCenter().distSqr(BlockPos.containing(bird.position()))+4D<(bird.getRestrictRadius()+10F)*(bird.getRestrictRadius()+10F);
            boolean found=false;float best=-99999F;int bx=0,by=0,bz=0;
            for(int i=0;i<10;i++){
                int x=bird.getRandom().nextInt(20)-10,y=bird.getRandom().nextInt(14)-7,z=bird.getRandom().nextInt(20)-10;
                x+=Mth.floor(bird.getX());y+=Mth.floor(bird.getY());z+=Mth.floor(bird.getZ());var pos=new BlockPos(x,y,z);
                if(home&&!bird.isWithinRestriction(pos))continue;float weight=bird.getWalkTargetValue(pos);if(weight>best){best=weight;bx=x;by=y;bz=z;found=true;}
            }
            selected=found?new Vec3(bx,by,bz):null;return found;
        }
        @Override public boolean canContinueToUse(){return!bird.getNavigation().isDone()&&bird.getFlyingState()==bird.STATE_WALKING;}
        @Override public void start(){bird.getNavigation().moveTo(selected.x,selected.y,selected.z,.2);}
    }
    static final class EatBread extends Goal {
        final EntityPigeon bird;ItemEntity item;EatBread(EntityPigeon bird){this.bird=bird;setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));}
        @Override public boolean canUse(){
            if(bird.isFat()||bird.getFlyingState()!=bird.STATE_WALKING)return false;
            for(var candidate:bird.level().getEntitiesOfClass(ItemEntity.class,bird.getBoundingBox().inflate(10,10,10)))if(candidate.getItem().is(Items.BREAD)){item=candidate;return true;}return false;
        }
        @Override public boolean canContinueToUse(){return item!=null&&item.isAlive()&&canUse();}
        @Override public boolean requiresUpdateEveryTick(){return true;}
        @Override public void tick(){
            bird.getLookControl().setLookAt(item,30,bird.getMaxHeadXRot());
            if(bird.distanceTo(item)>1)bird.getNavigation().moveTo(item,.4);
            else{
                if(bird.getRandom().nextInt(3)==0){
                    var stack=item.getItem();if(stack.getCount()>1){stack.shrink(1);var remaining=new ItemEntity(bird.level(),item.getX(),item.getY(),item.getZ(),stack);remaining.setDeltaMovement(Vec3.ZERO);remaining.setPickUpDelay(0);bird.level().addFreshEntity(remaining);}item.discard();
                }
                // This is intentionally outside the random consumption branch in the source.
                bird.setFat(true);bird.playSound(SoundEvents.GENERIC_EAT,.5F+.5F*bird.getRandom().nextInt(2),(bird.getRandom().nextFloat()-bird.getRandom().nextFloat())*.2F+1);
            }
        }
    }
}

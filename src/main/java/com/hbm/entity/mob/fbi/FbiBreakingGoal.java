package com.hbm.entity.mob.fbi;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
/** Actual EntityAIBreaking scans each source body cell and keeps its cast-before-divide health/zero-health quirks. */
final class FbiBreakingGoal extends Goal {
    private final EntityFBI mob;private LivingEntity target;private BlockPos marked;private int digTick,scanTick;
    FbiBreakingGoal(EntityFBI mob){this.mob=mob;}
    @Override public boolean canUse(){target=mob.getTarget();if(target==null||!mob.getNavigation().isDone()||mob.distanceTo(target)<=1||!target.onGround()&&mob.getSensing().hasLineOfSight(target))return false;var hit=obstacle();if(hit==null)return false;marked=hit.getBlockPos();return true;}
    @Override public boolean canContinueToUse(){return marked!=null&&mob.isAlive()&&new Vec3(marked.getX()-mob.getX(),marked.getY()-mob.getEyeY(),marked.getZ()-mob.getZ()).length()<=4;}
    @Override public boolean requiresUpdateEveryTick(){return true;}
    @Override public void tick(){
        if(mob.tickCount%10==0){var hit=obstacle();if(hit!=null)marked=hit.getBlockPos();}
        if(marked==null||mob.level().getBlockState(marked).isAir()){digTick=0;return;}
        var state=mob.level().getBlockState(marked);digTick++;int health=(int)state.getDestroySpeed(mob.level(),marked)/3;
        if(health<0){marked=null;return;}float strength=(digTick*.05F)/(float)health;
        if(strength>=1){digTick=0;mob.level().destroyBlock(marked,false);marked=null;if(target!=null)mob.getNavigation().moveTo(target,1);}
        else if(digTick%5==0){var sound=state.getSoundType(mob.level(),marked,mob);mob.playSound(sound.getStepSound(),sound.getVolume()+1,sound.getPitch());mob.swing(InteractionHand.MAIN_HAND);mob.level().destroyBlockProgress(mob.getId(),marked,(int)(strength*10));}
    }
    @Override public void stop(){marked=null;digTick=0;}
    private BlockHitResult obstacle(){
        int width=Mth.ceil(mob.getBbWidth()),height=Mth.ceil(mob.getBbHeight()),passes=width*width*height;
        int x=scanTick%width-width/2,y=scanTick/(width*width),z=scanTick%(width*width)/width-width/2;
        var start=new Vec3(mob.getX()+x,mob.getY()+y,mob.getZ()+z);
        float cy=Mth.cos(-mob.getYRot()*.017453292F-(float)Math.PI),sy=Mth.sin(-mob.getYRot()*.017453292F-(float)Math.PI),cp=-Mth.cos(-mob.getXRot()*.017453292F),sp=Mth.sin(-mob.getXRot()*.017453292F);
        var hit=mob.level().clip(new ClipContext(start,start.add(sy*cp*2,sp*2,cy*cp*2),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,mob));
        if(hit.getType()==HitResult.Type.BLOCK&&mob.level().getBlockState(hit.getBlockPos()).getDestroySpeed(mob.level(),hit.getBlockPos())>=0){scanTick=0;return hit;}
        scanTick=(scanTick+1)%passes;return null;
    }
}

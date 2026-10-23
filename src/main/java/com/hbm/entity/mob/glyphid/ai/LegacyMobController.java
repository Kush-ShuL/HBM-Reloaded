package com.hbm.entity.mob.glyphid.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

/** Official1.7.10 EntityCreature.updateEntityActionState/wander plus EntityLiving idle gaze, preserving source branch/RNG order. */
public final class LegacyMobController {
    private final LegacyCreatureMob mob;private final IdleState idleState=new IdleState();
    public LegacyMobController(LegacyCreatureMob mob){this.mob=mob;}
    public void tick(){
        var random=mob.getRandom();if(mob.fleeingTicks>0)--mob.fleeingTicks;boolean ceased=mob.legacyCeasedAttack();
        if(mob.legacyTarget==null){mob.legacyTarget=mob.findLegacyTarget();if(mob.legacyTarget!=null)mob.legacyPath=LegacyMobPath.toEntity(mob,mob.legacyTarget,16,true,false,false,true);}
        else if(mob.legacyTarget.isAlive()){float distance=mob.distanceTo(mob.legacyTarget);if(mob.hasLineOfSight(mob.legacyTarget))mob.attackLegacyTarget(mob.legacyTarget,distance);}else mob.legacyTarget=null;
        // Source attackEntity may set hasAttacked during this same action-state tick (RADBeast ranged attack).
        ceased=mob.legacyCeasedAttack();
        if(mob.legacyTarget instanceof Player player&&player.getAbilities().invulnerable)mob.legacyTarget=null;
        if(ceased||mob.legacyTarget==null||mob.legacyPath!=null&&random.nextInt(20)!=0){if(!ceased&&(mob.legacyPath==null&&random.nextInt(180)==0||random.nextInt(120)==0||mob.fleeingTicks>0)&&mob.legacyIdleAge<100)wander();}
        else mob.legacyPath=LegacyMobPath.toEntity(mob,mob.legacyTarget,16,true,false,false,true);
        int y=Mth.floor(mob.getBoundingBox().minY+.5D);boolean water=mob.isInWater(),lava=mob.isInLava();mob.setXRot(0);
        if(mob.legacyPath!=null&&random.nextInt(100)!=0){
            var point=mob.legacyPath.next(mob);double diameter=mob.getBbWidth()*2F;
            while(point!=null&&point.distanceToSqr(mob.getX(),point.y,mob.getZ())<diameter*diameter){if(++mob.legacyPath.index>=mob.legacyPath.nodes.length){point=null;mob.legacyPath=null;}else point=mob.legacyPath.next(mob);}
            boolean jumping=false;mob.xxa=0;
            if(point!=null){double dx=point.x-mob.getX(),dz=point.z-mob.getZ(),dy=point.y-y;float yaw=(float)(Math.atan2(dz,dx)*180D/(float)Math.PI)-90F,difference=Mth.wrapDegrees(yaw-mob.getYRot());mob.zza=mob.legacyMoveSpeed();difference=Mth.clamp(difference,-30F,30F);mob.setYRot(mob.getYRot()+difference);
                if(ceased&&mob.legacyTarget!=null){float previous=mob.getYRot();mob.setYRot((float)(Math.atan2(mob.legacyTarget.getZ()-mob.getZ(),mob.legacyTarget.getX()-mob.getX())*180D/(float)Math.PI)-90F);difference=(previous-mob.getYRot()+90F)*(float)Math.PI/180F;mob.xxa=-Mth.sin(difference)*mob.zza;mob.zza=Mth.cos(difference)*mob.zza;}
                if(dy>0)jumping=true;
            }
            if(mob.legacyTarget!=null)mob.lookAt(mob.legacyTarget,30,30);
            if(mob.horizontalCollision&&mob.legacyPath==null)jumping=true;
            if(random.nextFloat()<.8F&&(water||lava))jumping=true;
            mob.setJumping(jumping);
        }else{idle();mob.legacyPath=null;}
        // Old non-goal travel used a fixed .1 grounded movement factor and put
        // the source MOVEMENT_SPEED attribute into moveForward itself.
        float forward=mob.zza,strafe=mob.xxa;mob.setSpeed(.1F);mob.zza=forward;mob.xxa=strafe;
    }
    private void wander(){
        if(!mob.legacyMayWander())return;var random=mob.getRandom();boolean selected=false;int x=-1,y=-1,z=-1;float best=-99999F;
        for(int i=0;i<10;i++){int ix=Mth.floor(mob.getX()+random.nextInt(13)-6D),iy=Mth.floor(mob.getY()+random.nextInt(7)-3D),iz=Mth.floor(mob.getZ()+random.nextInt(13)-6D);float weight=mob.legacyPathWeight(ix,iy,iz);if(weight>best){best=weight;x=ix;y=iy;z=iz;selected=true;}}
        if(selected)mob.legacyPath=LegacyMobPath.toPosition(mob,x,y,z,10,true,false,false,true);
    }
    /** Source EntityCreature with its default null target/zero path weight: no Monster parent or activated goal AI. */
    public static final class PassiveState {private LegacyMobPath path;private int age;private final IdleState idle=new IdleState();}
    public static void passiveCreature(net.minecraft.world.entity.PathfinderMob mob,PassiveState state){
        var random=mob.getRandom();
        // target==null short-circuits the source path's nextInt20 branch. Preserve RNG before the age check.
        if((state.path==null&&random.nextInt(180)==0||random.nextInt(120)==0)&&state.age<100){
            int x=0,y=0,z=0;
            for(int i=0;i<10;i++){int ix=Mth.floor(mob.getX()+random.nextInt(13)-6D),iy=Mth.floor(mob.getY()+random.nextInt(7)-3D),iz=Mth.floor(mob.getZ()+random.nextInt(13)-6D);if(i==0){x=ix;y=iy;z=iz;}}
            // Original default EntityCreature path weight is always0: the first candidate wins, all ten still draw RNG.
            state.path=LegacyMobPath.toPosition((Mob)mob,x,y,z,10,true,false,false,true);
        }
        int baseY=Mth.floor(mob.getBoundingBox().minY+.5D);boolean water=mob.isInWater(),lava=mob.isInLava();mob.setXRot(0);
        if(state.path!=null&&random.nextInt(100)!=0){
            var point=state.path.next(mob);double diameter=mob.getBbWidth()*2F;
            while(point!=null&&point.distanceToSqr(mob.getX(),point.y,mob.getZ())<diameter*diameter){if(++state.path.index>=state.path.nodes.length){point=null;state.path=null;}else point=state.path.next(mob);}
            boolean jumping=false;mob.xxa=0;
            if(point!=null){double x=point.x-mob.getX(),z=point.z-mob.getZ(),dy=point.y-baseY;float yaw=(float)(Math.atan2(z,x)*180D/(float)Math.PI)-90F;mob.zza=(float)mob.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);mob.setYRot(mob.getYRot()+Mth.clamp(Mth.wrapDegrees(yaw-mob.getYRot()),-30F,30F));if(dy>0)jumping=true;}
            if(mob.horizontalCollision&&state.path==null)jumping=true;
            if(random.nextFloat()<.8F&&(water||lava))jumping=true;mob.setJumping(jumping);
        }else{state.age++;idle(mob,state.idle,()->state.age=despawn(mob,state.age,()->true));state.path=null;}
        float forward=mob.zza,strafe=mob.xxa;mob.setSpeed(.1F);mob.zza=forward;mob.xxa=strafe;
    }
    private void idle(){mob.legacyIdleAge++;idle(mob,idleState,mob::legacyDespawn);}
    /** The one inherited old EntityLiving idle method, usable without inheriting Monster aggression/peaceful removal. */
    public static final class IdleState {private Entity idleLook;private int lookTime;private float randomYaw;}
    public static void idle(Mob mob,IdleState state,Runnable despawn){
        var random=mob.getRandom();mob.xxa=mob.zza=0;despawn.run();
        if(random.nextFloat()<.02F){Player player=nearestPlayer(mob,8,false);if(player!=null){state.idleLook=player;state.lookTime=10+random.nextInt(20);}else state.randomYaw=(random.nextFloat()-.5F)*20F;}
        if(state.idleLook!=null){mob.lookAt(state.idleLook,10,mob.getMaxHeadXRot());if(state.lookTime--<=0||!state.idleLook.isAlive()||mob.distanceToSqr(state.idleLook)>64)state.idleLook=null;}
        else{if(random.nextFloat()<.05F)state.randomYaw=(random.nextFloat()-.5F)*20F;mob.setYRot(mob.getYRot()+state.randomYaw);mob.setXRot(0);}
        if(mob.isInWater()||mob.isInLava())mob.setJumping(random.nextFloat()<.8F);
    }
    /** Same original despawn conditions and RNG order; caller owns its existing age field. */
    public static int despawn(Mob mob,int age,java.util.function.BooleanSupplier mayDespawn){
        if(mob.isPersistenceRequired())return 0;
        Player player=nearestPlayer(mob,-1,false);if(player==null)return age;double distance=mob.distanceToSqr(player);
        if(mayDespawn.getAsBoolean()&&distance>16384D)mob.discard();
        if(age>600&&mob.getRandom().nextInt(800)==0&&distance>1024D&&mayDespawn.getAsBoolean())mob.discard();else if(distance<1024D)age=0;
        return age;
    }
    // Keep the old descriptor for already-compiled source callers.
    public static Player nearestPlayer(LegacyCreatureMob mob,double range,boolean vulnerable){return nearestPlayer((Mob)mob,range,vulnerable);}
    public static Player nearestPlayer(Mob mob,double range,boolean vulnerable){
        Player best=null;double closest=-1;
        for(Player player:mob.level().players()){
            if(player.isSpectator()||vulnerable&&player.getAbilities().invulnerable)continue;
            double distance=player.distanceToSqr(mob),effective=range;
            if(vulnerable){if(player.isShiftKeyDown())effective*=.8D;if(player.isInvisible()){int worn=0;for(var armor:player.getArmorSlots())if(!armor.isEmpty())worn++;float visible=Math.max(worn/4F,.1F);effective*=.7F*visible;}}
            if((range<0||distance<effective*effective)&&(closest<0||distance<closest)){closest=distance;best=player;}
        }
        return best;
    }
}

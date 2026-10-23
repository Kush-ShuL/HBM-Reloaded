package com.hbm.entity.mob.fbi;

import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.mob.undead.mixin.UndeadSpawnTypeAccess;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.*;

/** The real source agent: ranged AI intentionally invokes an empty attack method, not a fabricated hitscan weapon. */
public final class EntityFBI extends Monster implements RangedAttackMob,LegacyTrackingRange {
    private static final Set<String> MACHINES=Set.of("machine_press","machine_epress","machine_chemical_plant","machine_chemical_factory","machine_crystallizer","machine_industrial_turbine","machine_chungus","machine_purex","crate_iron","crate_steel","machine_diesel","machine_rtg","machine_cyclotron");
    public EntityFBI(EntityType<? extends EntityFBI> type,Level level){super(type,level);setPersistenceRequired();setPathfindingMalus(PathType.WATER,8);if(getNavigation() instanceof net.minecraft.world.entity.ai.navigation.GroundPathNavigation ground){ground.setCanOpenDoors(true);ground.setCanFloat(true);}}
    public static AttributeSupplier.Builder attributes(){return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH,20).add(Attributes.ATTACK_DAMAGE,2).add(Attributes.KNOCKBACK_RESISTANCE,.5).add(Attributes.MOVEMENT_SPEED,.3).add(Attributes.FOLLOW_RANGE,16);}
    @Override protected void registerGoals(){goalSelector.addGoal(0,new FloatGoal(this));goalSelector.addGoal(1,new FbiBreakingGoal(this));goalSelector.addGoal(2,new SourceRanged());goalSelector.addGoal(3,new MeleeAttackGoal(this,1,true){@Override public boolean canUse(){return EntityFBI.this.getTarget() instanceof Player&&super.canUse();}@Override public boolean canContinueToUse(){return EntityFBI.this.getTarget() instanceof Player&&super.canContinueToUse();}});goalSelector.addGoal(5,new MoveTowardsRestrictionGoal(this,1));goalSelector.addGoal(7,new RandomStrollGoal(this,1));goalSelector.addGoal(8,new LookAtPlayerGoal(this,Player.class,8));goalSelector.addGoal(8,new RandomLookAroundGoal(this));targetSelector.addGoal(1,new HurtByTargetGoal(this).setAlertOthers());targetSelector.addGoal(2,new NearestAttackableTargetGoal<>(this,Player.class,0,false,false,null));}
    @Override public int getArmorValue(){return 20;}
    @Override public int legacyTrackingRange(){return 80;}
    @Override public void performRangedAttack(LivingEntity target,float strength){} // Actual source is empty.
    @Override public boolean hurt(DamageSource source,float amount){
        if(source.is(DamageTypeTags.IS_PROJECTILE)&&source.getEntity() instanceof EntityFBI)return false;
        if(getItemBySlot(EquipmentSlot.HEAD).is(Blocks.GLASS.asItem())&&(source.getMsgId().equals("oxygenSuffocation")||source.getMsgId().equals("thermal")))return false;
        return super.hurt(source,amount);
    }
    @Override public boolean canBeAffected(MobEffectInstance effect){if(getItemBySlot(EquipmentSlot.HEAD).isEmpty())setItemSlot(EquipmentSlot.HEAD,new ItemStack(HbmContent.item("gas_mask_m65")));return false;}
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,DifficultyInstance difficulty,MobSpawnType reason,SpawnGroupData group){
        setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(HbmContent.item(random.nextInt(2)==0?"gun_heavy_revolver":"gun_spas12")));
        if(random.nextInt(5)==0){setItemSlot(EquipmentSlot.HEAD,new ItemStack(HbmContent.item("security_helmet")));setItemSlot(EquipmentSlot.CHEST,new ItemStack(HbmContent.item("security_plate")));setItemSlot(EquipmentSlot.LEGS,new ItemStack(HbmContent.item("security_legs")));setItemSlot(EquipmentSlot.FEET,new ItemStack(HbmContent.item("security_boots")));}
        if(level().dimension()!=Level.OVERWORLD){setItemSlot(EquipmentSlot.HEAD,new ItemStack(Blocks.GLASS));setItemSlot(EquipmentSlot.CHEST,new ItemStack(PaaContent.PLATE.get()));setItemSlot(EquipmentSlot.LEGS,new ItemStack(PaaContent.LEGS.get()));setItemSlot(EquipmentSlot.FEET,new ItemStack(PaaContent.BOOTS.get()));}
        var range=getAttribute(Attributes.FOLLOW_RANGE);if(range!=null)range.addPermanentModifier(new AttributeModifier(ResourceLocation.parse("hbm:fbi_spawn_"+UUID.randomUUID()),random.nextGaussian()*.05,AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        ((UndeadSpawnTypeAccess)(Object)this).hbm$sourceSpawnType(reason);setLeftHanded(false);return group;
    }
    @Override protected void customServerAiStep(){super.customServerAiStep();if(getTarget()==null)setTarget(sourceNearestVulnerablePlayer());if(getTarget()!=null)sourcePartialPath(getTarget());}
    /** Official1.7 World.getClosestVulnerablePlayer: source sneak/invisibility/armor visibility range factors. */
    private Player sourceNearestVulnerablePlayer(){
        Player best=null;double distance=-1;
        for(var player:level().players()){
            if(!player.isAlive()||player.getAbilities().invulnerable||player.isSpectator())continue;
            double reach=128;if(player.isCrouching())reach*=.8;
            if(player.isInvisible()){int armor=0;for(var stack:player.getArmorSlots())if(!stack.isEmpty())armor++;float visibility=armor/4F;if(visibility<.1F)visibility=.1F;reach*=.7F*visibility;}
            double next=distanceToSqr(player);if(next<reach*reach&&(distance<0||next<distance)){best=player;distance=next;}
        }
        return best;
    }
    private void sourcePartialPath(LivingEntity target){
        var direction=target.position().subtract(position()).normalize().scale(16);int x=(int)Math.floor(getX()+direction.x),y=(int)Math.floor(getY()+direction.y),z=(int)Math.floor(getZ()+direction.z);boolean solid=false;
        for(int i=y;i>y-10;i--)if(standing(x,i,z)){solid=true;y=i;break;}
        if(!solid)for(int i=y+10;i>y;i--)if(standing(x,i,z)){y=i;break;}
        var path=getNavigation().createPath(x,y,z,0);getNavigation().moveTo(path,1);
    }
    private boolean standing(int x,int y,int z){return !level().getBlockState(new BlockPos(x,y,z)).blocksMotion()&&level().getBlockState(new BlockPos(x,y-1,z)).isRedstoneConductor(level(),new BlockPos(x,y-1,z));}
    @Override public void aiStep(){super.aiStep();if(level().isClientSide||getHealth()<=0)return;
        if(tickCount%FbiConfig.ATTACK_DELAY.get()==0){var vector=new Vec3(FbiConfig.ATTACK_REACH.get(),0,0).yRot((float)Math.PI*2*random.nextFloat());var start=new Vec3(getX(),getY()+.5+random.nextFloat(),getZ());var hit=level().clip(new ClipContext(start,start.add(vector),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));if(hit.getType()==HitResult.Type.BLOCK){var block=level().getBlockState(hit.getBlockPos()).getBlock();boolean destroy=block==Blocks.OAK_DOOR||block==Blocks.IRON_DOOR||block==Blocks.OAK_TRAPDOOR||block==Blocks.CHEST||block==Blocks.TRAPPED_CHEST;if(!destroy)for(var symbol:MACHINES){var id=ResourceLocation.parse("hbm:"+symbol);if(net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(id)&&block==net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(id)){destroy=true;break;}}if(destroy)level().destroyBlock(hit.getBlockPos(),false);}}
        for(var item:level().getEntitiesOfClass(ItemEntity.class,new AABB(getX(),getY(),getZ(),getX(),getY(),getZ()).inflate(1.5)))item.igniteForSeconds(10);
    }
    private final class SourceRanged extends Goal {
        LivingEntity target;int cooldown=-1,seen;SourceRanged(){setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));}
        @Override public boolean canUse(){if(getTarget()==null)return false;target=getTarget();return true;}
        @Override public boolean canContinueToUse(){return canUse()||!getNavigation().isDone();}
        @Override public void stop(){target=null;seen=0;cooldown=-1;}
        @Override public boolean requiresUpdateEveryTick(){return true;}
        @Override public void tick(){if(target==null)return;double distance=distanceToSqr(target.getX(),target.getBoundingBox().minY,target.getZ());boolean visible=getSensing().hasLineOfSight(target);seen=visible?seen+1:0;if(distance<=225&&seen>=20)getNavigation().stop();else getNavigation().moveTo(target,1);getLookControl().setLookAt(target,30,30);if(--cooldown==0){if(distance>225||!visible)return;float ratio=(float)Math.sqrt(distance)/15;performRangedAttack(target,Math.max(.1F,Math.min(1,ratio)));cooldown=(int)Math.floor(ratio*5+20);}else if(cooldown<0)cooldown=(int)Math.floor(Math.sqrt(distance)/15*5+20);}
    }
}

package com.hbm.entity.mob.blockspider;

import com.hbm.entity.mob.undead.mixin.UndeadSpawnTypeAccess;
import com.hbm.world.structure.LegacyStructureStates;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

/** Actual source EntityMob: wander and a target selector, deliberately no melee/retaliation goal. */
public final class EntityBlockSpider extends Monster {
    private static final EntityDataAccessor<BlockState> MATERIAL=SynchedEntityData.defineId(EntityBlockSpider.class,EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Integer> METADATA=SynchedEntityData.defineId(EntityBlockSpider.class,EntityDataSerializers.INT);
    public EntityBlockSpider(EntityType<? extends EntityBlockSpider> type,Level world){super(type,world);setPathfindingMalus(PathType.WATER,8);}
    public EntityBlockSpider(Level world){this(BlockSpiderContent.SPIDER.get(),world);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){super.defineSynchedData(data);data.define(MATERIAL,Blocks.STONE.defaultBlockState());data.define(METADATA,0);}
    public static AttributeSupplier.Builder attributes(){return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH,10).add(Attributes.MOVEMENT_SPEED,1).add(Attributes.FOLLOW_RANGE,16).add(Attributes.ATTACK_DAMAGE,2);}
    @Override protected void registerGoals(){goalSelector.addGoal(1,new SourceWander());targetSelector.addGoal(1,new NearestAttackableTargetGoal<>(this,Player.class,0,true,false,null));}
    public BlockState blockState(){return entityData.get(MATERIAL);}
    public int blockMetadata(){return entityData.get(METADATA);}
    /** Existing canonical old-metadata decoder, never raw modern numeric registry IDs. */
    public void makeBlock(Block block,int metadata){makeBlock(LegacyStructureStates.resolve(BuiltInRegistries.BLOCK.getKey(block).toString(),metadata,0),metadata);}
    public void makeBlock(BlockState state,int metadata){entityData.set(MATERIAL,state);entityData.set(METADATA,metadata);getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.max(1D,state.getBlock().getExplosionResistance()));setHealth(getMaxHealth());}
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,DifficultyInstance difficulty,MobSpawnType reason,SpawnGroupData group){
        // Same source-parent Gaussian as the already ported Undead/Dummy, not modern triangular/left-handed rolls.
        getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath("hbm","block_spider_spawn_"+UUID.randomUUID()),random.nextGaussian()*.05,AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        ((UndeadSpawnTypeAccess)(Object)this).hbm$sourceSpawnType(reason);setLeftHanded(false);return group;
    }
    // MATERIAL/METADATA deliberately remain watcher-only: source did not persist them.
    /** EntityAIWander + RandomPositionGenerator(10,7), retaining exactly 10 attempts and source exclusive bounds. */
    private final class SourceWander extends Goal {
        private Vec3 destination;
        SourceWander(){setFlags(EnumSet.of(Flag.MOVE));}
        @Override public boolean canUse(){
            if(getNoActionTime()>=100||random.nextInt(120)!=0)return false;
            boolean requireHome=hasRestriction()&&getRestrictCenter().distSqr(BlockPos.containing(position()))+4D<(getRestrictRadius()+10F)*(getRestrictRadius()+10F);
            boolean selected=false;float best=-99999F;int bestX=0,bestY=0,bestZ=0;
            for(int i=0;i<10;i++){
                int x=random.nextInt(20)-10,y=random.nextInt(14)-7,z=random.nextInt(20)-10;
                x+=net.minecraft.util.Mth.floor(getX());y+=net.minecraft.util.Mth.floor(getY());z+=net.minecraft.util.Mth.floor(getZ());
                var pos=new BlockPos(x,y,z);if(requireHome&&!isWithinRestriction(pos))continue;
                float weight=getWalkTargetValue(pos);if(weight>best){best=weight;bestX=x;bestY=y;bestZ=z;selected=true;}
            }
            destination=selected?new Vec3(bestX,bestY,bestZ):null;return selected;
        }
        @Override public boolean canContinueToUse(){return!getNavigation().isDone();}
        @Override public void start(){getNavigation().moveTo(destination.x,destination.y,destination.z,.5D);}
    }
}

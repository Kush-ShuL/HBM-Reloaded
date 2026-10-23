package com.hbm.entity.mob.duck;

import com.hbm.registry.HbmSounds;
import java.util.EnumSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Original EntityDuck extends Chicken, with the actual old goals and three duck sounds. */
public class SourceDuck extends Chicken {
    public SourceDuck(EntityType<? extends SourceDuck> type,Level level){super(type,level);}
    @Override protected void registerGoals(){
        goalSelector.addGoal(0,new FloatGoal(this));
        goalSelector.addGoal(1,new SourcePanic(this));
        goalSelector.addGoal(2,new BreedGoal(this,1));
        goalSelector.addGoal(3,new TemptGoal(this,1,stack->stack.is(Items.WHEAT_SEEDS),false));
        goalSelector.addGoal(4,new FollowParentGoal(this,1.1));
        goalSelector.addGoal(5,new RandomStrollGoal(this,1));
        goalSelector.addGoal(6,new LookAtPlayerGoal(this,Player.class,6));
        goalSelector.addGoal(7,new RandomLookAroundGoal(this));
    }
    @Override public boolean isFood(ItemStack stack){return stack.is(Items.WHEAT_SEEDS)||stack.is(Items.PUMPKIN_SEEDS)||stack.is(Items.MELON_SEEDS);}
    @Override public EntityDimensions getDefaultDimensions(Pose pose){return getType().getDimensions().scale(isBaby()?.5F:1F);}
    @Override public SourceDuck getBreedOffspring(ServerLevel level,AgeableMob other){return DuckContent.DUCK.get().create(level);}
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.remove("EggLayTime");}
    @Override public void readAdditionalSaveData(CompoundTag tag){var source=tag.copy();source.remove("EggLayTime");super.readAdditionalSaveData(source);}
    @Override protected SoundEvent getAmbientSound(){return HbmSounds.get("entity.ducc");}
    @Override protected SoundEvent getHurtSound(DamageSource source){return HbmSounds.get("entity.ducc");}
    @Override protected SoundEvent getDeathSound(){return HbmSounds.get("entity.ducc");}
    @Override public void die(DamageSource source){
        if(level() instanceof ServerLevel server)server.getServer().getPlayerList().broadcastSystemMessage(getCombatTracker().getDeathMessage(),false);
        super.die(source);
    }
    @Override protected void dropFromLootTable(DamageSource source,boolean recentlyHit){
        if(isBaby())return;
        int looting=0;
        if(source.getEntity() instanceof LivingEntity killer){
            var enchantment=level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
            looting=EnchantmentHelper.getItemEnchantmentLevel(enchantment,killer.getMainHandItem());
        }
        int feathers=random.nextInt(3)+random.nextInt(looting+1);
        for(int i=0;i<feathers;i++)spawnAtLocation(Items.FEATHER);
        spawnAtLocation(isOnFire()?Items.COOKED_CHICKEN:Items.CHICKEN);
    }
    /** Old EntityAIPanic has no freezing trigger or modern water-seeking branch. */
    private static final class SourcePanic extends Goal {
        private final SourceDuck duck;private Vec3 destination;
        SourcePanic(SourceDuck duck){this.duck=duck;setFlags(EnumSet.of(Flag.MOVE));}
        @Override public boolean canUse(){if(duck.getLastHurtByMob()==null&&!duck.isOnFire())return false;destination=DefaultRandomPos.getPos(duck,5,4);return destination!=null;}
        @Override public void start(){duck.getNavigation().moveTo(destination.x,destination.y,destination.z,1.4);}
        @Override public boolean canContinueToUse(){return !duck.getNavigation().isDone();}
    }
}

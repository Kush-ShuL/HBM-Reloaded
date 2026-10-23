package com.hbm.entity.mob.glyphid;

import com.hbm.entity.mob.glyphid.ai.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.*;

/** Source EntityParasiteMaggot, including its actual inherited old mob AI; its bite does not invent a parasite disease. */
public final class ParasiteMaggot extends LegacyCreatureMob {
    public ParasiteMaggot(EntityType<? extends ParasiteMaggot> type,Level level){super(type,level);}
    public ParasiteMaggot(Level level){this(MaggotContent.MAGGOT.get(),level);}
    public static AttributeSupplier.Builder attributes(){return createMonsterAttributes().add(Attributes.MAX_HEALTH,8).add(Attributes.MOVEMENT_SPEED,1).add(Attributes.ATTACK_DAMAGE,2).add(Attributes.FOLLOW_RANGE,16);}
    @Override protected MovementEmission getMovementEmission(){return MovementEmission.NONE;}
    @Override public Entity findLegacyTarget(){return LegacyMobController.nearestPlayer(this,16,true);}
    @Override public void tick(){yBodyRot=getYRot();super.tick();}
    @Override public boolean checkSpawnRules(LevelAccessor level,MobSpawnType reason){return level.getDifficulty()!=Difficulty.PEACEFUL&&legacyPathWeight(net.minecraft.util.Mth.floor(getX()),net.minecraft.util.Mth.floor(getBoundingBox().minY),net.minecraft.util.Mth.floor(getZ()))>=0;}
    // Actual source isValidLightLevel always true; the inherited path-weight
    // check above still exists. It has no registered natural-biome spawn entry.
}

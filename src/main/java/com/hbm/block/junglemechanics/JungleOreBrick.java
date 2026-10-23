package com.hbm.block.junglemechanics;
import com.hbm.block.ore.*;
import com.hbm.exposure.ExposureEffects;
import com.hbm.item.medicine.MedicineEffects;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** The actual two BlockOre identities, reusing unique original drops/fortune/oil-neighbor handling. */
public final class JungleOreBrick extends LegacyOreBlock {
 private final boolean mystic;
 public JungleOreBrick(boolean mystic){super(new OreSpec(mystic?"brick_jungle_mystic":"brick_jungle_ooze","BlockOre","rock","stone",15,216,5,true,true,null,null,0,1,0,"ORE",0,List.of(),"COMMON",true,0,false,false));this.mystic=mystic;}
 @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity){if(entity instanceof LivingEntity living)living.addEffect(new MobEffectInstance(mystic?MedicineEffects.TAINT:ExposureEffects.RADIATION,15*20,mystic?2:9));}
}

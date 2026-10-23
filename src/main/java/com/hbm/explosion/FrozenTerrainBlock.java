package com.hbm.explosion;

import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.OreSpec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Original frozen WasteEarth/WasteLog/BlockOre registrations, without invented grass growth. */
public final class FrozenTerrainBlock extends LegacyOreBlock {
    public FrozenTerrainBlock(String id) {
        super(specification(id),specification(id).properties().sound(SoundType.GLASS)
                .mapColor(id.equals("frozen_log")||id.equals("frozen_planks")?MapColor.WOOD:MapColor.DIRT));
    }
    private static OreSpec specification(String id) {
        boolean log=id.equals("frozen_log"),grass=id.equals("frozen_grass"),wood=log||id.equals("frozen_planks");
        return new OreSpec(id,log?"WasteLog":grass?"WasteEarth":"BlockOre",wood?"wood":"ground","glass",.5F,1.5F,0,false,true,
                "minecraft:snowball",null,0,log?2:1,log?3:0,grass||log?"NONE":"ORE",log?1:0,List.of(),"COMMON",false,0,false,false);
    }
    @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity) {
        if((spec.id().equals("frozen_grass")||spec.id().equals("frozen_dirt"))&&entity instanceof LivingEntity living)
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,2*60*20,2));
    }
}

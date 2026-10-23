package com.hbm.block.sourcestones;

import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.OreSpec;
import com.mojang.serialization.MapCodec;
import java.util.List;

/** Actual BlockBiomeStone/BlockEnumMulti: desert0, woodland1; no biome generator is implicitly enabled. */
public final class BiomeStoneBlock extends LegacyOreBlock {
    public BiomeStoneBlock(){super(new OreSpec("stone_biome","BlockBiomeStone","rock","stone",5,6,0,true,true,null,null,0,1,0,"NONE",0,List.of("desert","woodland"),"COMMON",false,0,false,false));}
    @Override protected MapCodec<? extends LegacyOreBlock> codec(){return MapCodec.unit(this);}
}

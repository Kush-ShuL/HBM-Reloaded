package com.hbm.worldgen.redroom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** HbmWorldGen466: one4-sided hidden stone, not a vein or a prebuilt room. */
public final class StrangeStoneFeature extends Feature<NoneFeatureConfiguration> {
    public StrangeStoneFeature(){super(NoneFeatureConfiguration.CODEC);}
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context){
        if(!com.hbm.config.WorldgenControls.structures()||!com.hbm.config.WorldgenControls.feature("red_rooms"))return false;var control=com.hbm.config.WorldgenControls.rule("red_rooms","stone_strange");if(!control.enabled())return false;
        var level=context.level();if(!RedRooms.ENABLED.get()||!level.getLevel().dimension().equals(Level.OVERWORLD))return false;
        var random=context.random();int chance=control.chance(RedRooms.CHANCE.get());if(chance==0||random.nextInt(chance)!=0)return false;
        int x=(context.origin().getX()&~15)+random.nextInt(16)+8;
        int base=RedRooms.DEEP.get()?level.getMinBuildHeight():0;int y=control.height(random,6+base,18+base,true);
        int z=(context.origin().getZ()&~15)+random.nextInt(16)+8;
        var pos=new BlockPos(x,y,z);if(!level.ensureCanWrite(pos))return false;
        if(!level.getBlockState(pos).is(RedRooms.DEEP.get()&&y<0?Blocks.DEEPSLATE:Blocks.STONE))return false;
        return level.setBlock(pos,RedRooms.STONE.get().defaultBlockState(),3);
    }
}

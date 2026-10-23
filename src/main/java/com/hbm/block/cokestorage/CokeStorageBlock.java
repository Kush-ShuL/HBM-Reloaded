package com.hbm.block.cokestorage;

import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.OreSpec;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** X5846 BlockCoke's actual enum/drop/raw16 behavior and original iron-material flammability. */
public final class CokeStorageBlock extends LegacyOreBlock {
    public static final List<String> NAMES=List.of("coal","lignite","petroleum");
    private static final OreSpec SOURCE=new OreSpec("block_coke","BlockCoke","iron","metal",5,6,0,true,true,
        null,null,0,1,0,"NONE",0,NAMES,"COMMON",false,0,false,false);
    public CokeStorageBlock(){super(SOURCE,Properties.of().mapColor(MapColor.METAL).strength(5,6)
        .sound(SoundType.METAL).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends LegacyOreBlock> codec(){return MapCodec.unit(this);}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){
        ItemStack result=new ItemStack(this);result.set(HbmHazards.LEGACY_METADATA.get(),state.getValue(LEGACY_META));return result;
    }
    @Override public int getFlammability(BlockState state,BlockGetter level,BlockPos pos,Direction face){return 5;}
    @Override public int getFireSpreadSpeed(BlockState state,BlockGetter level,BlockPos pos,Direction face){return 10;}
}

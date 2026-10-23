package com.hbm.world.structure.templates.materials;

import com.hbm.block.ore.*;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.material.MapColor;

/** Real BlockDeadPlant: five crossed variants, source soil set, no drop and no collision. */
public final class DeadPlantBlock extends LegacyOreBlock {
    public static final List<String> VARIANTS=List.of("generic","grass","flower","bigflower","fern");
    private static OreSpec source(){return new OreSpec("plant_dead","BlockDeadPlant","plants","grass",0,0,0,false,false,null,null,0,1,0,"NONE",0,VARIANTS,"COMMON",false,0,false,false);}
    public DeadPlantBlock(){super(source(),Properties.of().mapColor(MapColor.PLANT).strength(0).sound(SoundType.GRASS).noCollission().noOcclusion().isValidSpawn((a,b,c,d)->false));}
    private static boolean soil(BlockState state){if(state.is(Blocks.GRASS_BLOCK)||state.is(Blocks.DIRT)||state.is(Blocks.COARSE_DIRT)||state.is(Blocks.PODZOL))return true;String id=BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();return id.equals("hbm:waste_earth")||id.equals("hbm:dirt_oily")||id.equals("hbm:dirt_dead");}
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){return soil(level.getBlockState(pos.below()));}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block block,BlockPos from,boolean moving){if(!canSurvive(state,level,pos))level.setBlock(pos,Blocks.AIR.defaultBlockState(),2);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of();}
}

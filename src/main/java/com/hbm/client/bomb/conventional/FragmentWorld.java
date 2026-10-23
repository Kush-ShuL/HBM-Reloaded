package com.hbm.client.bomb.conventional;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

/** The source WorldInAJar block snapshot, plains colors, no tiles, and shared packed light value. */
final class FragmentWorld implements BlockAndTintGetter {
    private final ClientLevel actual;private final BlockState[][][] cells;final int size;int light;
    FragmentWorld(ClientLevel actual,int size){this.actual=actual;this.size=size;cells=new BlockState[size][size][size];}
    void put(int x,int y,int z,BlockState state){if(x>=0&&y>=0&&z>=0&&x<size&&y<size&&z<size)cells[x][y][z]=state;}
    @Override public BlockState getBlockState(BlockPos pos){int x=pos.getX(),y=pos.getY(),z=pos.getZ();return x<0||y<0||z<0||x>=size||y>=size||z>=size||cells[x][y][z]==null?Blocks.AIR.defaultBlockState():cells[x][y][z];}
    @Override public BlockEntity getBlockEntity(BlockPos pos){return null;}
    @Override public FluidState getFluidState(BlockPos pos){return getBlockState(pos).getFluidState();}
    @Override public int getHeight(){return size;}
    @Override public int getMinBuildHeight(){return 0;}
    @Override public float getShade(Direction side,boolean shade){return actual.getShade(side,shade);}
    @Override public LevelLightEngine getLightEngine(){return actual.getLightEngine();}
    @Override public int getBrightness(LightLayer layer,BlockPos pos){return layer==LightLayer.SKY?light>>20&15:light>>4&15;}
    @Override public int getRawBrightness(BlockPos pos,int darkness){return Math.max((light>>20&15)-darkness,light>>4&15);}
    @Override public int getBlockTint(BlockPos pos,ColorResolver resolver){return resolver.getColor(actual.registryAccess().registryOrThrow(Registries.BIOME).get(Biomes.PLAINS),pos.getX(),pos.getZ());}
    @Override public int getLightEmission(BlockPos pos){return Math.max(light>>4&15,getBlockState(pos).getLightEmission(this,pos));}
}

package com.hbm.client.wiaj;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.Direction;

/** Isolated source miniature. No Level writes, loaded chunks, ticking machines or server entities. */
public final class WorldInAJar implements BlockAndTintGetter {
    public final int sizeX,sizeY,sizeZ;public int lightlevel=15;
    private BlockState[][][] blocks;private Object[][][] tiles;private final java.util.Map<BlockPos,Integer> localDetectors=new java.util.HashMap<>();
    public WorldInAJar(int x,int y,int z){sizeX=x;sizeY=y;sizeZ=z;nuke();}
    public void nuke(){blocks=new BlockState[sizeX][sizeY][sizeZ];tiles=new Object[sizeX][sizeY][sizeZ];localDetectors.clear();}
    private boolean inside(int x,int y,int z){return x>=0&&x<sizeX&&y>=0&&y<sizeY&&z>=0&&z<sizeZ;}
    public void setBlock(int x,int y,int z,JarSymbols.SourceBlock block,int meta){if(!inside(x,y,z))return;var pos=new BlockPos(x,y,z);if(block.id().equals("hbm:machine_detector")&&!net.minecraft.core.registries.BuiltInRegistries.BLOCK.containsKey(net.minecraft.resources.ResourceLocation.parse("hbm:machine_detector"))){localDetectors.put(pos,meta);blocks[x][y][z]=Blocks.IRON_BLOCK.defaultBlockState();}else{localDetectors.remove(pos);blocks[x][y][z]=block.state(meta);}}
    public void setBlock(int x,int y,int z,BlockState state){if(inside(x,y,z)){blocks[x][y][z]=state;localDetectors.remove(new BlockPos(x,y,z));}}
    public Integer localDetectorMetadata(BlockPos pos){return localDetectors.get(pos);}
    public void setTileEntity(int x,int y,int z,Object tile){if(inside(x,y,z))tiles[x][y][z]=tile;}
    public Object getTileEntity(int x,int y,int z){return inside(x,y,z)?tiles[x][y][z]:null;}
    @Override public BlockEntity getBlockEntity(BlockPos pos){Object tile=getTileEntity(pos.getX(),pos.getY(),pos.getZ());return tile instanceof JarTiles.Pipe pipe?pipe.renderTile(pos,getBlockState(pos)):tile instanceof JarTiles.Foundry foundry?foundry.renderTile(pos,getBlockState(pos)):tile instanceof BlockEntity be?be:null;}
    @Override public BlockState getBlockState(BlockPos pos){return inside(pos.getX(),pos.getY(),pos.getZ())&&blocks[pos.getX()][pos.getY()][pos.getZ()]!=null?blocks[pos.getX()][pos.getY()][pos.getZ()]:Blocks.AIR.defaultBlockState();}
    @Override public FluidState getFluidState(BlockPos pos){return getBlockState(pos).getFluidState();}
    @Override public int getHeight(){return sizeY;}@Override public int getMinBuildHeight(){return 0;}
    @Override public float getShade(Direction direction,boolean shade){if(!shade)return 1;return switch(direction){case DOWN->.5F;case UP->1F;case NORTH,SOUTH->.8F;case WEST,EAST->.6F;};}
    @Override public LevelLightEngine getLightEngine(){return Minecraft.getInstance().level.getLightEngine();}
    @Override public int getBrightness(LightLayer layer,BlockPos pos){return lightlevel;}@Override public int getRawBrightness(BlockPos pos,int darkness){return lightlevel;}
    @Override public int getBlockTint(BlockPos pos,ColorResolver resolver){return resolver.getColor(Minecraft.getInstance().level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).getOrThrow(net.minecraft.world.level.biome.Biomes.PLAINS),pos.getX(),pos.getZ());}
}

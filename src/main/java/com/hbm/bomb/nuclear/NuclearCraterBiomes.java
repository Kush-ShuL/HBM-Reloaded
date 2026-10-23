package com.hbm.bomb.nuclear;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;

/** Exact original vertical columns plus the native1.18+ quart compatibility view. */
public final class NuclearCraterBiomes {
 public static final ResourceKey<Biome> CRATER=key("crater"),INNER=key("crater_inner"),OUTER=key("crater_outer");
 private static ResourceKey<Biome> key(String name){return ResourceKey.create(Registries.BIOME,ResourceLocation.fromNamespaceAndPath("hbm",name));}
 public static Holder<Biome> change(ServerLevel level,double distance,int scale,Holder<Biome> previous){
  if(!NuclearConfig.CRATER_BIOMES.get())return null;ResourceKey<Biome> result=null;
  if(scale>=150&&distance<15)result=INNER;
  else if(scale>=100&&distance<55&&!previous.is(INNER))result=CRATER;
  else if(scale>=25&&!previous.is(INNER)&&!previous.is(CRATER))result=OUTER;
  return result==null?null:level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(result);
 }
 public static void column(LevelChunk chunk,int x,int z,Holder<Biome> biome){
  if(!(chunk.getLevel() instanceof ServerLevel level))throw new IllegalArgumentException("Crater columns require a server chunk");
  com.hbm.bomb.nuclear.biome.ColumnBiomes.column(level,chunk,x,z,biome);
 }
 public static void sync(ServerLevel level,LevelChunk chunk){com.hbm.bomb.nuclear.biome.ColumnBiomes.sync(level,chunk);}
 private NuclearCraterBiomes(){}
}

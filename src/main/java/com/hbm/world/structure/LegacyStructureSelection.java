package com.hbm.world.structure;

import java.util.*;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.util.RandomSource;

/** Same candidate seed, two grid offset draws, registration order and weighted draw as NBTStructure.GenStructure. */
public final class LegacyStructureSelection {
    public record Selection(NaturalStructures.Spawn spawn,RandomSource random){}
    public static Selection choose(long seed,ChunkPos chunk,Holder<Biome> biome,int spacing,int separation,int salt){
        var key=biome.unwrapKey();if(key.isEmpty())return null;
        var profile=NaturalStructures.BIOMES.get(key.get().location().toString());if(profile==null)return null;
        int cellX=Math.floorDiv(chunk.x,spacing),cellZ=Math.floorDiv(chunk.z,spacing);
        var random=RandomSource.create((long)cellX*341873128712L+(long)cellZ*132897987541L+seed+salt);
        random.nextInt(spacing-separation);random.nextInt(spacing-separation); // Already consumed while choosing the source candidate within its grid cell.
        long total=0;for(var spawn:NaturalStructures.SPAWNS)if(valid(spawn.condition(),profile))total+=NaturalStructures.weight(spawn);
        if(total<=0||total>Integer.MAX_VALUE)return null;int draw=random.nextInt((int)total);
        for(var spawn:NaturalStructures.SPAWNS)if(valid(spawn.condition(),profile)){draw-=NaturalStructures.weight(spawn);if(draw<0)return new Selection(spawn,random);}
        return null;
    }
    private static boolean valid(String condition,NaturalStructures.BiomeProfile p){
        // Conditions below retain the original expressions, including registered SPARSE
        // membership and the literal plains/ocean checks rather than tag approximations.
        return switch(condition){
            case "biome.heightVariation <= 0.05F && !isWaterBiome(biome)" -> p.variation()<=.05F&&!p.water();
            case "!isWaterBiome(biome)" -> !p.water();
            case "!isWaterBiome(biome) && BiomeDictionary.isBiomeOfType(biome, Type.SANDY)" -> !p.water()&&p.has("SANDY");
            case "BiomeDictionary.isBiomeOfType(biome, Type.BEACH)" -> p.has("BEACH");
            case "BiomeDictionary.isBiomeOfType(biome, Type.OCEAN)","OCEAN" -> p.has("OCEAN");
            case "BiomeDictionary.isBiomeOfType(biome, Type.OCEAN) && biome.rootHeight >= -1.5F" -> p.has("OCEAN")&&p.rootHeight()>=-1.5F;
            case "BiomeDictionary.isBiomeOfType(biome, Type.OCEAN) || BiomeDictionary.isBiomeOfType(biome, Type.BEACH)" -> p.has("OCEAN")||p.has("BEACH");
            case "BiomeDictionary.isBiomeOfType(biome, Type.PLAINS)" -> p.has("PLAINS");
            case "BiomeDictionary.isBiomeOfType(biome, Type.PLAINS) || BiomeDictionary.isBiomeOfType(biome, Type.SWAMP)" -> p.has("PLAINS")||p.has("SWAMP");
            case "biome.heightVariation <= 0.3F && !isWaterBiome(biome)" -> p.variation()<=.3F&&!p.water();
            case "isFlatBiome(biome)" -> p.variation()<=.2F&&!p.water()&&p.has("SPARSE");
            case "biome.heightVariation <= 0.2F && !isWaterBiome(biome)" -> p.variation()<=.2F&&!p.water();
            case "BiomeDictionary.isBiomeOfType(biome, Type.SANDY)" -> p.has("SANDY");
            case "!isWaterBiome(biome) && biome.canSpawnLightningBolt()" -> !p.water()&&p.lightning();
            case "biome.heightVariation <= 0.3F && !isWaterBiome(biome) && !BiomeDictionary.isBiomeOfType(biome, Type.SANDY)" -> p.variation()<=.3F&&!p.water()&&!p.has("SANDY");
            case "BiomeDictionary.isBiomeOfType(biome, Type.SNOWY)" -> p.has("SNOWY");
            case "biome == BiomeGenBase.ocean || biome == BiomeGenBase.deepOcean" -> p.exactOcean();
            case "biome.rootHeight >= 0" -> p.rootHeight()>=0;
            case "EXACT_PLAINS" -> p.exactPlains();
            default -> throw new IllegalStateException("Unported source biome expression: "+condition);
        };
    }
    private LegacyStructureSelection(){}
}

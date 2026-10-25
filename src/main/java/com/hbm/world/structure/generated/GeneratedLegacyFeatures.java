package com.hbm.world.structure.generated;
import com.hbm.world.structure.NaturalStructures;
import com.hbm.world.structure.generated.algorithm.legacy.*;
import com.hbm.world.structure.generated.compat.World;
import com.hbm.world.structure.loot.LegacyStructureLoot;
import java.util.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Independent original HbmWorldGen sequence, planned against actual decoration terrain before placement. */
public final class GeneratedLegacyFeatures {
 private static final Object SOURCE_STATE=new Object();
 private static final Map<net.minecraft.core.RegistryAccess,Random> WORLD_RANDOMS=new WeakHashMap<>();
 private static boolean initialized;
 public static final ModConfigSpec CONFIG;
 private static final ModConfigSpec.IntValue ANTENNA,ATOM,LIBRARY,DUD,SPACESHIP,BARREL,JUNGLE,ARCTIC,PYRAMID,BROADCASTER,MINES,VAULT,CHLORINE,RAD,CAPSULE;
 static {var b=new ModConfigSpec.Builder();b.comment("每个区块生成概率的分母；0 关闭对应结构或地点。").push("chanceDenominators");ANTENNA=b.defineInRange(java.util.List.of("antenna"),250,0,Integer.MAX_VALUE);ATOM=b.defineInRange(java.util.List.of("desertAtom"),500,0,Integer.MAX_VALUE);LIBRARY=b.defineInRange(java.util.List.of("libraryDungeon"),64,0,Integer.MAX_VALUE);DUD=b.defineInRange(java.util.List.of("unexplodedBomb"),500,0,Integer.MAX_VALUE);SPACESHIP=b.defineInRange(java.util.List.of("crashedSpaceship"),1000,0,Integer.MAX_VALUE);BARREL=b.defineInRange(java.util.List.of("wasteBarrel"),5000,0,Integer.MAX_VALUE);ARCTIC=b.defineInRange(java.util.List.of("arcticVault"),500,0,Integer.MAX_VALUE);JUNGLE=b.defineInRange(java.util.List.of("jungleDungeon"),2000,0,Integer.MAX_VALUE);PYRAMID=b.defineInRange(java.util.List.of("ancientTomb"),4000,0,Integer.MAX_VALUE);BROADCASTER=b.defineInRange(java.util.List.of("radioTransmitter"),5000,0,Integer.MAX_VALUE);MINES=b.defineInRange(java.util.List.of("landmines"),64,0,Integer.MAX_VALUE);VAULT=b.defineInRange(java.util.List.of("surfaceVault"),2500,0,Integer.MAX_VALUE);CHLORINE=b.defineInRange(java.util.List.of("chlorineGeyser"),3000,0,Integer.MAX_VALUE);RAD=b.defineInRange(java.util.List.of("radiationHotspot"),5000,0,Integer.MAX_VALUE);CAPSULE=b.defineInRange(java.util.List.of("soyuzCapsule"),100,0,Integer.MAX_VALUE);b.pop();CONFIG=b.build();}
 public static int radiationHotspotFrequency(){return RAD.get();}
 public static void registerConfig(ModContainer mod){mod.registerConfig(ModConfig.Type.COMMON,CONFIG,"hbm-generated-structures-common.toml");}
 /** The decoration coordinator holds this same lock across the complete plan and its physical placement. */
 public static Object sourceGenerationLock(){return SOURCE_STATE;}
 public static List<GeneratedPlanPiece> plan(Structure.GenerationContext context,RandomSource sourceRandom,WorldGenLevel terrain){Objects.requireNonNull(terrain,"Independent source planning requires actual decoration terrain");if(!NaturalStructures.oldBuildingsEnabled())return List.of();synchronized(SOURCE_STATE){if(!initialized){CellularDungeonFactory.init();initialized=true;}var selected=new LinkedHashSet<String>();var generatorRandom=new SourceRandom(sourceRandom);var worldRandom=WORLD_RANDOMS.computeIfAbsent(context.registryAccess(),ignored->new Random());try(var world=World.plan(context,worldRandom,terrain)){
   int i=context.chunkPos().getMinBlockX(),j=context.chunkPos().getMinBlockZ();var biome=world.getBiomeGenForCoords(i,j);
   // Source order and conditional short-circuiting are significant: each selected
   // body, including its inventory draws, executes before the next chance draw.
   SourceGlyphidHive.generate(world,generatorRandom,i,j,selected); // HbmWorldGen191..201, before Antenna.
   if(biome.temperature()>=.4F&&biome.rainfall()<=.6F&&hit(generatorRandom,ANTENNA)){int x=i+generatorRandom.nextInt(16),z=j+generatorRandom.nextInt(16),y=world.getHeightValue(x,z);require(world,"Antenna",selected);new Antenna().generate(world,generatorRandom,x,y,z);}
   if(!biome.lightning()&&biome.temperature()>=1.5F&&hit(generatorRandom,ATOM)){int x=i+generatorRandom.nextInt(16),z=j+generatorRandom.nextInt(16),y=world.getHeightValue(x,z);require(world,"DesertAtom001",selected);new DesertAtom001().generate(world,generatorRandom,x,y,z);}
   if(hit(generatorRandom,LIBRARY)){int x=i+generatorRandom.nextInt(16),y=generatorRandom.nextInt(256),z=j+generatorRandom.nextInt(16);require(world,"LibraryDungeon",selected);new LibraryDungeon().generate(world,generatorRandom,x,y,z);}
   if(hit(generatorRandom,DUD)){int x=i+8+generatorRandom.nextInt(16),z=j+8+generatorRandom.nextInt(16),y=world.getHeightValue(x,z);require(world,"Dud",selected);new Dud().generate(world,generatorRandom,x,y,z);}
   if(hit(generatorRandom,SPACESHIP)){int x=i+generatorRandom.nextInt(16),z=j+generatorRandom.nextInt(16),y=world.getHeightValue(x,z);require(world,"Spaceship",selected);new Spaceship().generate(world,generatorRandom,x,y,z);}
   if(BARREL.get()>0&&biome.temperature()>=1.5F&&!biome.lightning()&&generatorRandom.nextInt(BARREL.get())==0){int x=i+generatorRandom.nextInt(16),z=j+generatorRandom.nextInt(16),y=world.getHeightValue(x,z);require(world,"Barrel",selected);new Barrel().generate(world,generatorRandom,x,y,z);}
   SourceNaturalPoints.beforeJungle(world,generatorRandom,i,j,BROADCASTER.get(),MINES.get(),com.hbm.config.GeneralModes.flag("1.05_enableLandmineSpawn"),selected);
   SourceChlorineGeyser.natural(world,generatorRandom,i,j,CHLORINE.get(),selected);
   SourceBeachCapsule.generate(world,generatorRandom,i,j,CAPSULE.get(),selected);
   SourceSurfaceSafe.generate(world,generatorRandom,i,j,VAULT.get(),com.hbm.config.GeneralModes.flag("1.15_enableVaultSpawn"),selected);
   // Source lantern/capsule/vegetation still need their genuine remaining modules; no fake shell or random burn.
   if((biome.biome().equals("minecraft:jungle")||biome.biome().equals("minecraft:sparse_jungle"))&&hit(generatorRandom,JUNGLE)){int x=i+generatorRandom.nextInt(16),z=j+generatorRandom.nextInt(16);require(world,"JungleDungeon",selected);CellularDungeonFactory.jungle.generate(world,x,20,z,world.rand);CellularDungeonFactory.jungle.generate(world,x,24,z,world.rand);CellularDungeonFactory.jungle.generate(world,x,28,z,world.rand);int y=world.getHeightValue(x,z);for(int f=0;f<3;f++)world.setBlock(x,y+f,z,com.hbm.world.structure.generated.compat.ModBlocks.deco_titanium);world.setBlock(x,y+3,z,com.hbm.world.structure.generated.compat.Blocks.redstone_block);}
   if(hit(generatorRandom,ARCTIC)){int x=i+generatorRandom.nextInt(16),z=j+generatorRandom.nextInt(16),y=16+generatorRandom.nextInt(32);require(world,"ArcticVault",selected);new ArcticVault().trySpawn(world,x,y,z);}
   if(PYRAMID.get()>0&&biome.temperature()>=2F&&!biome.lightning()&&generatorRandom.nextInt(PYRAMID.get())==0){int x=i+generatorRandom.nextInt(16),z=j+generatorRandom.nextInt(16),y=world.getHeightValue(x,z);require(world,"AncientTomb",selected);new AncientTomb().build(world,generatorRandom,x,y,z);}
   SourceBlueprintChest.generate(world,generatorRandom,i,j,selected);
   world.finishDeferred();if(world.plannedWrites().isEmpty())return List.of();var dependencies=GeneratedDependencies.forNames(selected);return GeneratedPlanPiece.split(world,dependencies);
  }}}
 private static boolean hit(Random random,ModConfigSpec.IntValue denominator){int n=denominator.get();return n>0&&random.nextInt(n)==0;}
 private static void require(World world,String name,Set<String> selected){String key="old:"+name;var dependencies=GeneratedDependencies.forNames(List.of(key));dependencies.requireRegistryAndHooks();for(var pool:dependencies.pools()){if(pool.equals("DUNGEON_CHEST"))LegacyVanillaDungeon.requireReady();else if(!LegacyStructureLoot.ready(world.lookup(),pool))throw new IllegalStateException("Incomplete actual source loot pool "+pool);}selected.add(key);}
 private GeneratedLegacyFeatures(){}
}

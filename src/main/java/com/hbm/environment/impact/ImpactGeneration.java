package com.hbm.environment.impact;

import com.google.gson.*;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.satellite.resources.ImpactTerrainContent;
import com.mojang.serialization.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Exact old terrain/decoration/village event policies, applied to actual native generation instead of globally mutating registries. */
public final class ImpactGeneration {
    public enum Decoration { TREE,BIG_SHROOM,GRASS,REED,FLOWERS,DEAD_BUSH,CACTUS,PUMPKIN,LILYPAD }
    private record DecorationPass(WorldGenLevel level,EnumMap<Decoration,Boolean> allowed){}
    private static final ThreadLocal<DecorationPass> PASS=new ThreadLocal<>();
    private static final Map<PlacedFeature,EnumSet<Decoration>> KINDS=Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<ServerLevel,Set<Holder<Biome>>> BURNT_SURFACES=Collections.synchronizedMap(new WeakHashMap<>());
    private static final Random VILLAGE_RANDOM=new Random(); // original independent, unseeded ModEventHandlerImpact.rand
    private static final DeferredRegister<StructureProcessorType<?>> TYPES=DeferredRegister.create(Registries.STRUCTURE_PROCESSOR,"hbm");
    private static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<VillageProcessor>> VILLAGE=TYPES.register("impact_village",()->()->MapCodec.unit(VillageProcessor.INSTANCE));
    public static void register(IEventBus bus){TYPES.register(bus);}
    public static Object begin(WorldGenLevel level){var previous=PASS.get();PASS.set(new DecorationPass(level,new EnumMap<>(Decoration.class)));return previous;}
    public static void end(Object previous){if(previous==null)PASS.remove();else PASS.set((DecorationPass)previous);}
    public static boolean allowFeature(WorldGenLevel level,PlacedFeature feature){
        var state=ImpactState.generation(level.getLevel());if(!state.impact())return true;
        var kinds=KINDS.computeIfAbsent(feature,ImpactGeneration::kinds);if(kinds.isEmpty())return true;
        var pass=PASS.get();for(var kind:kinds){boolean allowed=pass!=null&&pass.level()==level?pass.allowed().computeIfAbsent(kind,k->allow(level,state,k)):allow(level,state,kind);if(!allowed)return false;}return true;
    }
    private static boolean allow(WorldGenLevel world,ImpactState state,Decoration kind){
        if(state.active())return false;
        return switch(kind){case TREE,BIG_SHROOM,CACTUS->world.getLevel().random.nextInt(9)==0;default->true;};
    }
    private static EnumSet<Decoration> kinds(PlacedFeature placed){var result=EnumSet.noneOf(Decoration.class);var seen=Collections.newSetFromMap(new IdentityHashMap<ConfiguredFeature<?,?>,Boolean>());inspect(placed.feature().value(),result,seen);return result;}
    private static void inspect(ConfiguredFeature<?,?> configured,EnumSet<Decoration> result,Set<ConfiguredFeature<?,?>> seen){
        if(!seen.add(configured))return;var feature=configured.feature();
        if(feature==Feature.TREE)result.add(Decoration.TREE);
        if(feature==Feature.HUGE_BROWN_MUSHROOM||feature==Feature.HUGE_RED_MUSHROOM)result.add(Decoration.BIG_SHROOM);
        if(feature==Feature.FLOWER)result.add(Decoration.FLOWERS);
        if(configured.config() instanceof SimpleBlockConfiguration simple)provider(simple.toPlace(),result);
        if(configured.config() instanceof BlockPileConfiguration pile)provider(pile.stateProvider,result);
        if(configured.config() instanceof RandomPatchConfiguration patch)inspect(patch.feature().value().feature().value(),result,seen);
        configured.config().getFeatures().forEach(child->inspect(child,result,seen));
    }
    // Read the real provider's codec data, never call getState with the world RNG just to classify it.
    private static void provider(BlockStateProvider provider,EnumSet<Decoration> result){collectStates(BlockStateProvider.CODEC.encodeStart(JsonOps.INSTANCE,provider).getOrThrow(),result);}
    private static void collectStates(JsonElement json,EnumSet<Decoration> result){
        if(json.isJsonArray()){for(var element:json.getAsJsonArray())collectStates(element,result);return;}if(!json.isJsonObject())return;var object=json.getAsJsonObject();
        if(object.has("Name")){var id=ResourceLocation.parse(object.get("Name").getAsString());if(BuiltInRegistries.BLOCK.containsKey(id)){var block=BuiltInRegistries.BLOCK.get(id);if(block instanceof FlowerBlock||block instanceof TallFlowerBlock)result.add(Decoration.FLOWERS);
            else if(block instanceof TallGrassBlock||block==Blocks.TALL_GRASS||block==Blocks.LARGE_FERN)result.add(Decoration.GRASS);
            else if(block instanceof SugarCaneBlock)result.add(Decoration.REED);else if(block instanceof DeadBushBlock)result.add(Decoration.DEAD_BUSH);else if(block instanceof CactusBlock)result.add(Decoration.CACTUS);else if(block instanceof PumpkinBlock)result.add(Decoration.PUMPKIN);else if(block instanceof WaterlilyBlock)result.add(Decoration.LILYPAD);}}
        for(var value:object.entrySet())collectStates(value.getValue(),result);
    }
    /** WorldProvider-era topBlock was mutated once, then did not revert: keep that session quirk per real level/biome, without touching shared registry objects. */
    public static void afterSurface(WorldGenRegion region,ChunkAccess chunk){
        var world=region.getLevel();var state=ImpactState.generation(world);Set<Holder<Biome>> marked;
        synchronized(BURNT_SURFACES){marked=BURNT_SURFACES.computeIfAbsent(world,k->ConcurrentHashMap.newKeySet());}
        if(state.impact()&&state.active()||!marked.isEmpty()){
            var cursor=new BlockPos.MutableBlockPos();int X=chunk.getPos().getMinBlockX(),Z=chunk.getPos().getMinBlockZ();
            for(int x=0;x<16;x++)for(int z=0;z<16;z++)for(int y=chunk.getMinBuildHeight();y<chunk.getMaxBuildHeight();y++){
                cursor.set(X+x,y,Z+z);if(!chunk.getBlockState(cursor).is(Blocks.GRASS_BLOCK))continue;var biome=region.getBiome(cursor);
                if(state.impact()&&state.active())marked.add(biome);if(marked.contains(biome))chunk.setBlockState(cursor,ImpactTerrainContent.DIRT.get().defaultBlockState(),false);
            }
        }
    }
    /** Populate.Post's section walk: original x/y/z order and exact dust >.25 / any fire threshold. */
    public static void afterDecoration(WorldGenLevel region,ChunkAccess chunk){
        var data=ImpactState.generation(region.getLevel());if(!data.impact()||!(data.dust()>.25F||data.fire()>0))return;var pos=new BlockPos.MutableBlockPos();
        for(int section=0;section<chunk.getSectionsCount();section++){
            var storage=chunk.getSection(section);if(storage.hasOnlyAir())continue;int Y=chunk.getSectionYFromSectionIndex(section)<<4;
            for(int x=0;x<16;x++)for(int y=0;y<16;y++)for(int z=0;z<16;z++){
                var state=storage.getBlockState(x,y,z);BlockState replacement=null;
                if(state.is(Blocks.GRASS_BLOCK))replacement=ImpactTerrainContent.DIRT.get().defaultBlockState();
                else if(state.is(BlockTags.LOGS)||state.is(BlockTags.LEAVES)||LegacyMaterials.of(state).name().equals("leaves")||LegacyMaterials.of(state).name().equals("plants")||state.getBlock() instanceof BushBlock)replacement=Blocks.AIR.defaultBlockState();
                if(replacement!=null)chunk.setBlockState(pos.set(chunk.getPos().getMinBlockX()+x,Y+y,chunk.getPos().getMinBlockZ()+z),replacement,false);
            }
        }
    }
    private static final Set<Block> OLD_STONE_SLABS=Set.of(Blocks.SMOOTH_STONE_SLAB,Blocks.SANDSTONE_SLAB,Blocks.COBBLESTONE_SLAB,Blocks.BRICK_SLAB,Blocks.STONE_BRICK_SLAB,Blocks.NETHER_BRICK_SLAB,Blocks.QUARTZ_SLAB);
    private static BlockState village(BlockState state){
        var block=state.getBlock();String material=LegacyMaterials.of(state).name();
        if(material.equals("wood")||material.equals("glass")||block instanceof LadderBlock||block instanceof CropBlock||block==Blocks.CHEST||block instanceof DoorBlock||material.equals("cloth")||material.equals("water")||OLD_STONE_SLABS.contains(block))return Blocks.AIR.defaultBlockState();
        if(block==Blocks.COBBLESTONE||block==Blocks.STONE_BRICKS||block==Blocks.MOSSY_STONE_BRICKS||block==Blocks.CRACKED_STONE_BRICKS||block==Blocks.CHISELED_STONE_BRICKS){synchronized(VILLAGE_RANDOM){if(VILLAGE_RANDOM.nextInt(3)==1)return Blocks.GRAVEL.defaultBlockState();}}
        else if(block==Blocks.SANDSTONE||block==Blocks.CHISELED_SANDSTONE||block==Blocks.CUT_SANDSTONE){synchronized(VILLAGE_RANDOM){if(VILLAGE_RANDOM.nextInt(3)==1)return Blocks.SAND.defaultBlockState();}}
        else if(block==Blocks.FARMLAND)return Blocks.DIRT.defaultBlockState();return state;
    }
    public static final class VillageProcessor extends StructureProcessor {
        public static final VillageProcessor INSTANCE=new VillageProcessor();private VillageProcessor(){}
        @Override public StructureTemplate.StructureBlockInfo processBlock(LevelReader region,BlockPos origin,BlockPos pivot,StructureTemplate.StructureBlockInfo original,StructureTemplate.StructureBlockInfo current,StructurePlaceSettings settings){
            if(!(region instanceof ServerLevelAccessor access)||!ImpactState.generation(access.getLevel()).impact())return current;var state=village(current.state());return state==current.state()?current:new StructureTemplate.StructureBlockInfo(current.pos(),state,null);
        }
        @Override protected StructureProcessorType<?> getType(){return VILLAGE.get();}
    }
    public static void forget(Level world){if(world instanceof ServerLevel server)BURNT_SURFACES.remove(server);}
    public static void clear(){BURNT_SURFACES.clear();KINDS.clear();PASS.remove();}
    private ImpactGeneration(){}
}

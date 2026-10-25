package com.hbm.world.structure;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.*;

/** Source weighted scheduler, real persistent vanilla structures and original private NBT templates. */
public final class NaturalStructures {
    @FunctionalInterface public interface Generator {
        Optional<Structure.GenerationStub> generate(String sourceName,Structure.GenerationContext context,net.minecraft.util.RandomSource random);
    }
    private static final Map<String,Generator> GENERATORS=new HashMap<>();
    public record Spawn(String name,String condition,int weight,String weightKey,boolean ruinToggle,boolean oceanToggle,String template,int heightOffset,boolean conform,int minHeight,int maxHeight,boolean available){}
    public record BiomeProfile(String biome,float rootHeight,float variation,List<String> types,boolean lightning,boolean exactPlains,boolean exactOcean,float temperature,float rainfall,boolean mesa){public boolean has(String type){return types.contains(type);}public boolean water(){return has("OCEAN")||has("RIVER");}}
    private record Definitions(List<Spawn> spawns,List<BiomeProfile> biomeProfiles){}
    private static final Definitions DEFINITIONS=definitions();
    static final List<Spawn> SPAWNS=List.copyOf(DEFINITIONS.spawns());
    static final Map<String,BiomeProfile> BIOMES=new HashMap<>();
    private static final DeferredRegister<StructureType<?>> TYPES=DeferredRegister.create(Registries.STRUCTURE_TYPE,"hbm");
    private static final DeferredRegister<StructurePieceType> PIECES=DeferredRegister.create(Registries.STRUCTURE_PIECE,"hbm");
    static final DeferredHolder<StructureType<?>,StructureType<LegacyNaturalStructure>> TYPE=TYPES.register("legacy_natural_structures",()->()->LegacyNaturalStructure.CODEC);
    static final DeferredHolder<StructureType<?>,StructureType<LegacyGeneratedStructure>> OLD_TYPE=TYPES.register("legacy_generated_structures",()->()->LegacyGeneratedStructure.CODEC);
    static final DeferredHolder<StructurePieceType,StructurePieceType> PIECE=PIECES.register("legacy_natural_piece",()->LegacyStructurePiece::new);
    private static final ModConfigSpec CONFIG;
    private static final ModConfigSpec.BooleanValue RUINS,OCEAN;
    private static final ModConfigSpec.EnumValue<GenerationPolicy> OLD_BUILDINGS,WEIGHTED_STRUCTURES;
    private static final ModConfigSpec.DoubleValue LOOT_AMOUNT;
    private static final Map<String,ModConfigSpec.IntValue> WEIGHTS=new HashMap<>();
    static{
        for(var profile:DEFINITIONS.biomeProfiles())BIOMES.put(profile.biome(),profile);
        var builder=new ModConfigSpec.Builder();
        builder.comment("两组原版结构分别控制；DISABLED=关闭，FORCED=即使世界关闭结构生成也生成，RESPECT_WORLD=遵循世界的生成结构选项。仅影响新的生成过程。");
        OLD_BUILDINGS=builder.comment("旧式独立建筑的生成策略。").translation("config.hbm.structures.old_buildings.policy").defineEnum("oldBuildingsPolicy",GenerationPolicy.RESPECT_WORLD);
        WEIGHTED_STRUCTURES=builder.comment("加权结构调度器的生成策略，与旧式独立建筑互不影响。").translation("config.hbm.structures.weighted.policy").defineEnum("weightedStructuresPolicy",GenerationPolicy.RESPECT_WORLD);
        RUINS=builder.define("enableRuins",true);OCEAN=builder.define("enableOceanStructures",true);
        LOOT_AMOUNT=builder.comment("自然结构容器的战利品数量倍率，不影响战利品堆。").defineInRange("lootAmountFactor",1D,-Double.MAX_VALUE,Double.MAX_VALUE);
        builder.push("weights");for(var spawn:SPAWNS)WEIGHTS.put(spawn.name(),builder.defineInRange(spawn.weightKey(),spawn.weight(),0,1000000));builder.pop();CONFIG=builder.build();
    }
    private static Definitions definitions(){try(var input=NaturalStructures.class.getResourceAsStream("/hbm/natural-structures.json")){if(input==null)throw new IllegalStateException("Run port_natural_structures.py");return new Gson().fromJson(new InputStreamReader(input,StandardCharsets.UTF_8),Definitions.class);}catch(java.io.IOException error){throw new IllegalStateException(error);}}
    public static void register(IEventBus bus,ModContainer container){TYPES.register(bus);PIECES.register(bus);StructureChestPairs.register(bus);container.registerConfig(ModConfig.Type.COMMON,CONFIG,"hbm-natural-structures-common.toml");}
    /** This existing user master switch covers both families; their source policies remain independent. */
    public static boolean enabled(){return com.hbm.config.WorldgenControls.structures();}
    public enum GenerationPolicy {
        DISABLED,FORCED,RESPECT_WORLD;
        boolean allows(boolean worldFlag){return this==FORCED||this==RESPECT_WORLD&&worldFlag;}
    }
    public static boolean oldBuildingsEnabled(boolean worldFlag){return enabled()&&OLD_BUILDINGS.get().allows(worldFlag);}
    public static boolean weightedStructuresEnabled(boolean worldFlag){return enabled()&&WEIGHTED_STRUCTURES.get().allows(worldFlag);}
    public static boolean oldBuildingsEnabled(){return oldBuildingsEnabled(worldStructuresFlag());}
    public static boolean weightedStructuresEnabled(){return weightedStructuresEnabled(worldStructuresFlag());}
    public static boolean forcesStructures(){return enabled()&&(OLD_BUILDINGS.get()==GenerationPolicy.FORCED||WEIGHTED_STRUCTURES.get()==GenerationPolicy.FORCED);}
    /** Native callers pass their own manager flag; pure native prediction has no level in GenerationContext. */
    private static boolean worldStructuresFlag(){var server=net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();return server==null||server.getWorldData().worldGenOptions().generateStructures();}
    public static boolean allows(Structure structure,boolean worldFlag){
        if(structure instanceof LegacyNaturalStructure)return weightedStructuresEnabled(worldFlag);
        if(structure instanceof LegacyGeneratedStructure)return oldBuildingsEnabled(worldFlag);
        return worldFlag;
    }
    public static double lootAmountFactor(){return LOOT_AMOUNT.get();}
    public static BiomeProfile profile(net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biome){return biome.unwrapKey().map(key->BIOMES.get(key.location().toString())).orElse(null);}
    public static void registerGenerator(String sourceName,Generator generator){if(GENERATORS.putIfAbsent(sourceName,Objects.requireNonNull(generator))!=null)throw new IllegalStateException("Duplicate natural generator "+sourceName);}
    static Generator generator(String sourceName){return GENERATORS.get(sourceName);}
    static int weight(Spawn spawn){return spawn.ruinToggle()&&!RUINS.get()||spawn.oceanToggle()&&!OCEAN.get()?0:WEIGHTS.get(spawn.name()).get();}
    static Spawn spawn(String name){return SPAWNS.stream().filter(row->row.name().equals(name)).findFirst().orElseThrow(()->new IllegalStateException("Unknown source structure "+name));}
    private NaturalStructures(){}
}

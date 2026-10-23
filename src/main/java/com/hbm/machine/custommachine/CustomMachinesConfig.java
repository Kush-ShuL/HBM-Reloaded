package com.hbm.machine.custommachine;

import com.google.gson.*;
import com.hbm.world.structure.LegacyStructureStates;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLPaths;

/** Original data-driven machine definitions, distinct from the same-named processing recipe file. */
public final class CustomMachinesConfig {
    public static final Map<String,MachineConfiguration> customMachines=new HashMap<>();
    public static final List<MachineConfiguration> niceList=new ArrayList<>();
    private static JsonObject local;
    public static void initialize(){Path path=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmCustomMachines.json");try{Files.createDirectories(path.getParent());if(!Files.exists(path)){try(var input=CustomMachinesConfig.class.getResourceAsStream("/hbm/custom-machine114-default-machines.json")){if(input==null)throw new IllegalStateException("Missing original custom-machine definition");Files.copy(input,path);}}try(var reader=Files.newBufferedReader(path)){local=JsonParser.parseReader(reader).getAsJsonObject();replace(local);}}catch(Exception error){throw new IllegalStateException("Cannot load actual hbmConfig/hbmCustomMachines.json",error);}}
    public static void replace(JsonObject root){customMachines.clear();niceList.clear();for(var element:root.getAsJsonArray("machines")){var machine=new MachineConfiguration(element.getAsJsonObject());customMachines.put(machine.unlocalizedName,machine);niceList.add(machine);}}
    public static void restoreLocal(){if(local!=null)replace(local);}
    public static JsonObject snapshot(){var root=new JsonObject();var array=new JsonArray();for(var config:niceList)array.add(config.json.deepCopy());root.add("machines",array);return root;}
    public static MachineConfiguration byMetadata(int metadata){int index=metadata-100;return index>=0&&index<customMachines.size()?niceList.get(index):null;}
    public static final class MachineConfiguration {
        public final JsonObject json;
        public final String recipeKey,unlocalizedName,localizedName;
        public final Map<String,String> localization=new HashMap<>();
        public final int fluidInCount,fluidInCap,itemInCount,fluidOutCount,fluidOutCap,itemOutCount,maxPollutionCap,maxHeat;
        public final boolean generatorMode,fluxMode;
        public final double recipeSpeedMult,recipeConsumptionMult;
        public final BigInteger maxPower;
        public final List<ComponentDefinition> components=new ArrayList<>();
        public MachineConfiguration(JsonObject json){this.json=json.deepCopy();recipeKey=json.get("recipeKey").getAsString();unlocalizedName=json.get("unlocalizedName").getAsString();localizedName=json.get("localizedName").getAsString();if(json.has("localization"))json.getAsJsonObject("localization").entrySet().forEach(e->localization.put(e.getKey(),e.getValue().getAsString()));fluidInCount=integer(json,"fluidInCount");fluidInCap=integer(json,"fluidInCap");itemInCount=integer(json,"itemInCount");fluidOutCount=integer(json,"fluidOutCount");fluidOutCap=integer(json,"fluidOutCap");itemOutCount=integer(json,"itemOutCount");generatorMode=json.get("generatorMode").getAsBoolean();maxPollutionCap=json.has("maxPollutionCap")?integer(json,"maxPollutionCap"):0;fluxMode=json.has("fluxMode")&&json.get("fluxMode").getAsBoolean();recipeSpeedMult=json.get("recipeSpeedMult").getAsDouble();recipeConsumptionMult=json.get("recipeConsumptionMult").getAsDouble();maxPower=json.get("maxPower").getAsBigDecimal().toBigInteger();maxHeat=json.has("maxHeat")?integer(json,"maxHeat"):0;for(var component:json.getAsJsonArray("components"))components.add(new ComponentDefinition(component.getAsJsonObject()));}
        public String translation(String locale){var direct=localization.get(locale);if(direct!=null)return direct;for(var entry:localization.entrySet())if(entry.getKey().equalsIgnoreCase(locale))return entry.getValue();return null;}
        public String displayName(String locale){var translated=translation(locale);return translated==null?localizedName:translated;}
    }
    public static final class ComponentDefinition {
        public final String sourceBlock;
        public final int x,y,z;
        public final JsonArray metas;
        public final Set<Integer> allowedMetas=new HashSet<>();
        private final Map<Integer,BlockState> states=new HashMap<>();
        private final JsonObject properties;
        public ComponentDefinition(JsonObject json){sourceBlock=json.get("block").getAsString();properties=json.has("properties")?json.getAsJsonObject("properties").deepCopy():null;x=integer(json,"x");y=integer(json,"y");z=integer(json,"z");metas=json.getAsJsonArray("metas").deepCopy();for(var meta:metas)allowedMetas.add(meta.getAsInt());}
        public BlockPos at(BlockPos core,int metadata){var dir=ForgeDirection.getOrientation(metadata);var rot=dir.getRotation(ForgeDirection.UP);int rx=-dir.offsetX*x+rot.offsetX*x,rz=-dir.offsetZ*z+rot.offsetZ*z;if(dir==ForgeDirection.EAST||dir==ForgeDirection.WEST){rx=dir.offsetZ*z-rot.offsetZ*z;rz=dir.offsetX*x-rot.offsetX*x;}return core.offset(rx,y,rz);}
        public BlockState state(int metadata){return states.computeIfAbsent(metadata,m->{String name=CustomMachineRecipes.alias(sourceBlock);var id=ResourceLocation.parse(name);if(!BuiltInRegistries.BLOCK.containsKey(id))throw new IllegalArgumentException("Actual custom-machine component is not admitted: "+sourceBlock);var block=BuiltInRegistries.BLOCK.get(id);if(properties!=null)return CustomMachineStates.properties(block.defaultBlockState(),properties);if(block instanceof CMComponentBlock||block instanceof CMAnchorBlock||block instanceof CustomMachineBlock)return block.defaultBlockState().setValue(CMComponentBlock.META,m&15);return LegacyStructureStates.resolve(sourceBlock,m,0);});}
        public boolean matches(BlockState actual){for(int metadata:allowedMetas)if(actual.equals(state(metadata)))return true;return false;}
        public int firstMetadata(){return allowedMetas.iterator().next();}
    }
    private static int integer(JsonObject json,String key){return json.get(key).getAsInt();}
    private CustomMachinesConfig(){}
}

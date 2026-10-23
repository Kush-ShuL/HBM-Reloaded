package com.hbm.bomb.nuclear;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.registry.HbmContent;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.fml.loading.FMLPaths;

/** Source FalloutConfigJSON's ordered predicates, independent outcome RNG, and weighted substitutions. */
public final class FalloutRules {
    public record Choice(String block,int metadata,int weight){}
    public static final List<Entry> entries=new ArrayList<>();
    public static Random random=new Random();
    private FalloutRules(){}
    static Choice choice(String symbol,int metadata,int weight){return new Choice(symbol,metadata,weight);}
    static Entry entry(){return new Entry();}
    public static void initialize(){
        entries.clear();defaults();
        var directory=FMLPaths.CONFIGDIR.get().resolve("hbmConfig");var config=directory.resolve("hbmFallout.json");var template=directory.resolve("_hbmFallout.json");
        try{
            Files.createDirectories(directory);
            if(!Files.exists(config)){JsonObject object=new JsonObject();JsonArray array=new JsonArray();for(Entry entry:entries)array.add(entry.write());object.add("entries",array);Files.writeString(template,new GsonBuilder().setPrettyPrinting().create().toJson(object)+"\n");}
            else{var root=JsonParser.parseString(Files.readString(config)).getAsJsonObject();var custom=new ArrayList<Entry>();for(var element:root.getAsJsonArray("entries"))custom.add(Entry.read(element.getAsJsonObject()));entries.clear();entries.addAll(custom);}
        }catch(Exception error){com.hbm.HbmReloaded.LOGGER.error("Cannot read original fallout configuration; retaining defaults",error);}
    }
    private static void defaults(){
        double woodRange=65D;
        entries.add(entry().block("minecraft:log").primary(choice("waste_log",0,1)).max(woodRange));
        entries.add(entry().block("minecraft:log2").primary(choice("waste_log",0,1)).max(woodRange));
        entries.add(entry().block("minecraft:red_mushroom_block").metadata(10).primary(choice("waste_log",0,1)).max(woodRange));
        entries.add(entry().block("minecraft:brown_mushroom_block").metadata(10).primary(choice("waste_log",0,1)).max(woodRange));
        entries.add(entry().block("minecraft:red_mushroom_block").primary(choice("minecraft:air",0,1)).max(woodRange));
        entries.add(entry().block("minecraft:brown_mushroom_block").primary(choice("minecraft:air",0,1)).max(woodRange));
        entries.add(entry().block("minecraft:snow_layer").primary(choice("minecraft:air",0,1)).max(woodRange));
        entries.add(entry().block("minecraft:planks").primary(choice("waste_planks",0,1)).max(woodRange));
        for(String material:List.of("wood","leaves","plants","vine"))entries.add(entry().material(material).primary(choice("minecraft:air",0,1)).max(woodRange));
        entries.add(entry().block("waste_leaves").primary(choice("minecraft:air",0,1)).max(woodRange));
        entries.add(entry().block("minecraft:leaves").primary(choice("waste_leaves",0,1)).min(woodRange-5D));
        entries.add(entry().block("minecraft:leaves2").primary(choice("waste_leaves",0,1)).min(woodRange-5D));
        entries.add(entry().block("glyphid_base").primary(choice("glyphid_base",2,1)));
        entries.add(entry().block("glyphid_spawner").primary(choice("glyphid_spawner",2,1)));
        entries.add(entry().block("minecraft:mossy_cobblestone").primary(choice("minecraft:coal_ore",0,1)));
        entries.add(entry().block("ore_nether_uranium").primary(choice("ore_nether_schrabidium",0,1),choice("ore_nether_uranium_scorched",0,99)));
        for(int i=1;i<=10;i++){
            int metadata=10-i;
            entries.add(entry().primary(choice("ore_sellafield_diamond",metadata,3),choice("ore_sellafield_emerald",metadata,2)).chance(.5).max(i*5).solid(true).block("minecraft:coal_ore"));
            entries.add(entry().primary(choice("ore_sellafield_diamond",metadata,1)).chance(.2).max(i*5).solid(true).block("ore_lignite"));
            entries.add(entry().primary(choice("ore_sellafield_emerald",metadata,1)).max(i*5).solid(true).block("ore_beryllium"));
            if(metadata>4){entries.add(entry().primary(choice("ore_sellafield_schrabidium",metadata,1),choice("ore_sellafield_uranium_scorched",metadata,9)).max(i*5).solid(true).block("ore_uranium"));entries.add(entry().primary(choice("ore_sellafield_schrabidium",metadata,1),choice("ore_sellafield_uranium_scorched",metadata,9)).max(i*5).solid(true).block("ore_gneiss_uranium"));}
            entries.add(entry().primary(choice("ore_sellafield_radgem",metadata,1)).max(i*5).solid(true).block("minecraft:diamond_ore"));
            for(String block:List.of("minecraft:bedrock","ore_bedrock","ore_bedrock_oil","sellafield_bedrock"))entries.add(entry().primary(choice("sellafield_bedrock",metadata,1)).max(i*5).solid(true).block(block));
            for(String material:List.of("iron","rock","sand","ground"))entries.add(entry().primary(choice("sellafield_slaked",metadata,1)).max(i*5).solid(true).material(material));
            if(i<=9)entries.add(entry().primary(choice("sellafield_slaked",metadata,1)).max(i*5).solid(true).material("grass"));
            // Native deepslate replaces the source's optional Et Futurum host; it keeps the same rule position.
            entries.add(entry().primary(choice("sellafield_slaked",metadata,1)).max(i*5).solid(true).block("minecraft:deepslate"));
        }
        entries.add(entry().block("minecraft:mycelium").primary(choice("waste_mycelium",0,1)));
        entries.add(entry().block("minecraft:sand").metadata(0).primary(choice("waste_trinitite",0,1)).chance(.05));
        entries.add(entry().block("minecraft:sand").metadata(1).primary(choice("waste_trinitite_red",0,1)).chance(.05));
        entries.add(entry().block("minecraft:clay").primary(choice("minecraft:hardened_clay",0,1)));
    }
    public static final class Entry {
        private String matchesBlock,matchesMaterial;
        private int matchesMeta=-1;
        private boolean opaque,solid;
        private List<Choice> primary,secondary;
        private double chance=1D,min=0D,max=100D,falloff=.9D;
        public Entry block(String value){matchesBlock=value;return this;}
        public Entry metadata(int value){matchesMeta=value;return this;}
        public Entry material(String value){matchesMaterial=value;return this;}
        public Entry opaque(boolean value){opaque=value;return this;}
        public Entry primary(Choice... value){primary=List.of(value);return this;}
        public Entry secondary(Choice... value){secondary=List.of(value);return this;}
        public Entry chance(double value){chance=value;return this;}
        public Entry min(double value){min=value;return this;}
        public Entry max(double value){max=value;return this;}
        public Entry falloff(double value){falloff=value;return this;}
        public Entry solid(boolean value){solid=value;return this;}
        public boolean isSolid(){return solid;}
        public boolean evaluate(Level world,BlockPos pos,BlockState original,int metadata,double distance){
            if(distance>max||distance<min)return false;
            if(matchesBlock!=null&&!matches(original,matchesBlock,matchesMeta))return false;
            if(matchesMaterial!=null&&!LegacyMaterials.of(original).name().equals(matchesMaterial))return false;
            if(matchesMeta!=-1&&metadata!=matchesMeta)return false;
            if(opaque&&!original.isSolidRender(world,pos))return false;
            if(distance>max*falloff&&Math.abs(world.random.nextGaussian())<Math.pow((distance-max*falloff)/(max-max*falloff),2D)*3D)return false;
            Choice selected=choose((chance==1D||random.nextDouble()<chance)?primary:secondary);if(selected==null)return false;
            boolean slaked=matches(original,"sellafield_slaked",-1),bedrock=matches(original,"sellafield_bedrock",-1);
            if(selected.block.equals("sellafield_slaked")&&slaked&&selected.metadata<=metadata)return false;
            if(selected.block.equals("sellafield_bedrock")&&bedrock&&selected.metadata<=metadata)return false;
            if(bedrock&&!selected.block.equals("sellafield_bedrock"))return false;
            if(pos.getY()==world.getMinBuildHeight()&&!selected.block.equals("sellafield_bedrock"))return false;
            world.setBlock(pos,state(selected.block,selected.metadata),3);return true;
        }
        private Choice choose(List<Choice> choices){
            if(choices==null)return null;int weight=0;for(var choice:choices)weight+=choice.weight;int selected=random.nextInt(weight);
            // The original subtract-then-<= comparison gives the first entry its exact old endpoint weighting.
            for(var choice:choices){selected-=choice.weight;if(selected<=0)return choice;}return choices.getFirst();
        }
        JsonObject write(){
            var result=new JsonObject();if(matchesBlock!=null)result.addProperty("matchesBlock",qualified(matchesBlock));if(matchesMeta!=-1)result.addProperty("matchesMeta",matchesMeta);if(opaque)result.addProperty("mustBeOpaque",true);if(matchesMaterial!=null)result.addProperty("matchesMaterial",matchesMaterial);if(solid)result.addProperty("restrictDepth",true);
            if(primary!=null)result.add("primarySubstitution",writeChoices(primary));if(secondary!=null)result.add("secondarySubstitutions",writeChoices(secondary));
            if(chance!=1)result.addProperty("chance",chance);if(min!=0)result.addProperty("minimumDistancePercent",min);if(max!=100)result.addProperty("maximumDistancePercent",max);if(falloff!=.9)result.addProperty("falloffStartFactor",falloff);return result;
        }
        static Entry read(JsonObject object){
            var entry=new Entry();if(object.has("matchesBlock"))entry.block(object.get("matchesBlock").getAsString());if(object.has("matchesMeta"))entry.metadata(object.get("matchesMeta").getAsInt());if(object.has("mustBeOpaque"))entry.opaque(object.get("mustBeOpaque").getAsBoolean());if(object.has("matchesMaterial"))entry.material(object.get("matchesMaterial").getAsString());if(object.has("restrictDepth"))entry.solid(object.get("restrictDepth").getAsBoolean());
            if(object.has("primarySubstitution"))entry.primary=readChoices(object.getAsJsonArray("primarySubstitution"));if(object.has("secondarySubstitutions"))entry.secondary=readChoices(object.getAsJsonArray("secondarySubstitutions"));
            if(object.has("chance"))entry.chance(object.get("chance").getAsDouble());if(object.has("minimumDistancePercent"))entry.min(object.get("minimumDistancePercent").getAsDouble());if(object.has("maximumDistancePercent"))entry.max(object.get("maximumDistancePercent").getAsDouble());if(object.has("falloffStartFactor"))entry.falloff(object.get("falloffStartFactor").getAsDouble());return entry;
        }
    }
    private static JsonArray writeChoices(List<Choice> choices){var array=new JsonArray();for(var choice:choices){var value=new JsonArray();value.add(qualified(choice.block));value.add(choice.metadata);value.add(choice.weight);array.add(value);}return array;}
    private static List<Choice> readChoices(JsonArray array){var result=new ArrayList<Choice>();for(JsonElement value:array){var entry=value.getAsJsonArray();result.add(choice(entry.get(0).getAsString(),entry.get(1).getAsInt(),entry.get(2).getAsInt()));}return result;}
    private static String qualified(String name){String key=name.indexOf(':')>=0?name:"hbm:"+name;return key.startsWith("hbm:tile.")?"hbm:"+key.substring(9):key;}
    public static boolean matches(BlockState state,String name,int requestedMetadata){
        String key=qualified(name);String actual=BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();if(actual.equals(key))return true;
        if(state.getBlock() instanceof LegacyOreBlock ore&&qualified(ore.spec().id()).equals(key))return true;
        return switch(key){
            case "minecraft:log","minecraft:log2"->state.is(BlockTags.LOGS);
            case "minecraft:planks"->state.is(BlockTags.PLANKS);
            case "minecraft:leaves","minecraft:leaves2"->state.is(BlockTags.LEAVES);
            case "minecraft:red_mushroom_block"->state.is(Blocks.RED_MUSHROOM_BLOCK)||(requestedMetadata==10&&state.is(Blocks.MUSHROOM_STEM));
            case "minecraft:brown_mushroom_block"->state.is(Blocks.BROWN_MUSHROOM_BLOCK)||(requestedMetadata==10&&state.is(Blocks.MUSHROOM_STEM));
            case "minecraft:grass"->state.is(Blocks.GRASS_BLOCK);
            case "minecraft:snow_layer"->state.is(Blocks.SNOW);
            case "minecraft:sand"->state.is(Blocks.SAND)||state.is(Blocks.RED_SAND);
            case "minecraft:coal_ore"->state.is(Blocks.DEEPSLATE_COAL_ORE);
            case "minecraft:diamond_ore"->state.is(Blocks.DEEPSLATE_DIAMOND_ORE);
            default->false;
        };
    }
    public static int metadata(BlockState state){if(state.is(Blocks.RED_SAND))return 1;if(state.is(Blocks.MUSHROOM_STEM))return 10;return com.hbm.explosion.LegacyExplosionMath.metadata(state);}
    public static BlockState state(String name,int metadata){
        String key=qualified(name);Block block;
        if(key.equals("minecraft:hardened_clay"))block=Blocks.TERRACOTTA;
        else if(key.equals("minecraft:grass"))block=Blocks.GRASS_BLOCK;
        else if(key.equals("minecraft:sand")&&metadata==1)block=Blocks.RED_SAND;
        else{
            var id=ResourceLocation.parse(key);
            if(BuiltInRegistries.BLOCK.containsKey(id))block=BuiltInRegistries.BLOCK.get(id);
            else if(id.getNamespace().equals("hbm"))block=HbmContent.block(id.getPath());
            else throw new IllegalStateException("Unknown original fallout substitution "+key);
        }
        BlockState state=block.defaultBlockState();for(Property<?> property:state.getProperties())if(property.getName().equals("legacy_meta")||property.getName().equals("level")){state=setInteger(state,property,metadata);break;}return state;
    }
    @SuppressWarnings({"rawtypes","unchecked"})private static BlockState setInteger(BlockState state,Property property,int value){return property.getPossibleValues().contains(value)?state.setValue(property,value):state;}
}

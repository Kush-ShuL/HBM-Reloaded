package com.hbm.machine.custommachine;
import com.google.gson.JsonObject;
import com.hbm.world.structure.LegacyStructureStates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
/** Exact source metadata or explicit modern native properties. No guessed state or foreign-block substitution. */
public final class CustomMachineStates {
    public static BlockState properties(BlockState state,JsonObject json){for(var entry:json.entrySet()){var property=state.getBlock().getStateDefinition().getProperty(entry.getKey());if(property==null)throw new IllegalArgumentException("Unknown actual native component property "+entry.getKey());state=property(state,property,entry.getValue().getAsString());}return state;}
    private static <T extends Comparable<T>> BlockState property(BlockState state,Property<T> property,String value){return state.setValue(property,property.getValue(value).orElseThrow(()->new IllegalArgumentException("Invalid actual component property "+property.getName()+"="+value)));}
    public static int metadata(BlockState state){if(state.hasProperty(CMComponentBlock.META))return state.getValue(CMComponentBlock.META);String id=BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();for(int i=0;i<16;i++)try{if(LegacyStructureStates.resolve(id,i,0).equals(state))return i;}catch(IllegalArgumentException|IllegalStateException ignored){}return 0;}
    public static JsonObject exportProperties(BlockState state){var json=new JsonObject();for(var property:state.getProperties())write(json,state,property);return json;}
    private static <T extends Comparable<T>> void write(JsonObject json,BlockState state,Property<T> property){json.addProperty(property.getName(),property.getName(state.getValue(property)));}
    private CustomMachineStates(){}
}

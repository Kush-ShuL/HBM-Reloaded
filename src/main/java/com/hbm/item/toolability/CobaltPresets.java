package com.hbm.item.toolability;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** The real source Cobalt configuration: NONE/RECURSION<=1 with NONE/SILK/LUCK<=0, up to99 presets. */
public final class CobaltPresets {
    public static final class Preset {
        // Old boolean/constructors remain source-compatible; area is the actual source ability index0..4.
        public boolean recursion;public int area,recursionLevel,harvest,harvestLevel;
        public Preset(){}
        public Preset(boolean recursion,int level,int harvest){this(recursion?1:0,level,harvest,0);}
        public Preset(boolean recursion,int level,int harvest,int harvestLevel){this(recursion?1:0,level,harvest,harvestLevel);}
        public Preset(int area,int level,int harvest,int harvestLevel){this.area=area;recursion=area==1;recursionLevel=level;this.harvest=harvest;this.harvestLevel=harvestLevel;}
        public int area(){return area==0&&recursion?1:area;}
        public boolean isNone(){return area()==0&&harvest==0;}
        public void restrict(){restrict(AvailableAbilities.COBALT);}
        public void restrict(AvailableAbilities available){area=area();if(!available.allowsArea(area))area=0;recursion=area==1;recursionLevel=area==0?0:Math.clamp(recursionLevel,0,available.maximumAreaLevel(area));if(area==4||!available.allowsHarvest(harvest))harvest=0;harvestLevel=harvest==0?0:Math.clamp(harvestLevel,0,available.maximumHarvestLevel(harvest));}
        public Component message(){
            if(isNone())return Component.translatable("gui.hbm.tool_ability.deactivated").withStyle(ChatFormatting.GOLD);
            var body=Component.empty();if(area()!=0)body.append(Component.translatable(AvailableAbilities.areaKey(area()))).append(AvailableAbilities.areaExtension(area(),recursionLevel));
            if(area()!=0&&harvest!=0)body.append(" + ");if(harvest!=0)body.append(Component.translatable(AvailableAbilities.harvestKey(harvest))).append(harvest==2?" ("+AvailableAbilities.luckPower(harvestLevel)+")":"");
            return Component.translatable("gui.hbm.tool_ability.enabled",body).withStyle(ChatFormatting.YELLOW);
        }
        CompoundTag save(){var tag=new CompoundTag();tag.putString("area",AvailableAbilities.areaKey(area()));tag.putInt("areaLevel",recursionLevel);tag.putString("harvest",AvailableAbilities.harvestKey(harvest));tag.putInt("harvestLevel",harvestLevel);return tag;}
        static Preset read(CompoundTag tag,AvailableAbilities available){
            int area=0,harvest=0;for(int i=1;i<=4;i++)if(AvailableAbilities.areaKey(i).equals(tag.getString("area")))area=i;
            for(int i:new int[]{1,2,3,4,5,6,7})if(AvailableAbilities.harvestKey(i).equals(tag.getString("harvest")))harvest=i;
            var preset=new Preset(area,tag.getInt("areaLevel"),harvest,tag.getInt("harvestLevel"));preset.restrict(available);return preset;
        }
    }
    public final List<Preset> presets=new ArrayList<>();public int current;
    public final AvailableAbilities available;
    public CobaltPresets(){this(AvailableAbilities.COBALT);}
    public CobaltPresets(AvailableAbilities available){this.available=available;reset();}
    public void reset(){current=0;presets.clear();presets.add(new Preset());for(int area=1;area<=4;area++)if(available.allowsArea(area))presets.add(new Preset(area,available.maximumAreaLevel(area),0,0));for(int harvest:new int[]{1,2,3,4,5,6,7})if(available.allowsHarvest(harvest))presets.add(new Preset(0,0,harvest,available.maximumHarvestLevel(harvest)));}
    public Preset active(){return presets.get(current);}
    public CompoundTag save(){var data=new CompoundTag();data.putInt("ability",current);var list=new ListTag();for(var preset:presets)list.add(preset.save());data.put("abilityPresets",list);return data;}
    public static CobaltPresets read(CompoundTag tag){return read(tag,AvailableAbilities.COBALT);}
    public static CobaltPresets read(CompoundTag tag,AvailableAbilities available){
        var config=new CobaltPresets(available);if(!tag.contains("ability")||!tag.contains("abilityPresets"))return config;
        var list=tag.getList("abilityPresets",Tag.TAG_COMPOUND);config.presets.clear();for(int i=0;i<Math.min(99,list.size());i++)config.presets.add(Preset.read(list.getCompound(i),available));
        // Source empty externally supplied lists crash getActivePreset. Keep actual corrupt data repair explicit.
        if(config.presets.isEmpty())config.presets.add(new Preset());config.current=Math.clamp(tag.getInt("ability"),0,config.presets.size()-1);return config;
    }
    public static CobaltPresets read(ItemStack stack){return read(stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag(),AvailableAbilities.of(stack));}
    public void apply(ItemStack stack){for(var preset:presets)preset.restrict(AvailableAbilities.of(stack));var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();data.merge(save());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}
}

package com.hbm.pneumatic;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.bedrockprocessing.*;
import com.hbm.registry.HbmOreDictionary;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
/** True ModulePatternMatcher: exact deliberately ignores tags, wildcard, processed bedrock grade, and original ordered ore names. */
public final class PatternMatcher {
 public static final String MODE_EXACT="exact",MODE_WILDCARD="wildcard",MODE_BEDROCK="bedrock";public final String[] modes;
 public PatternMatcher(){this(1);}public PatternMatcher(int count){modes=new String[count];}
 private static int meta(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
 public void initPatternSmart(ItemStack stack,int index){if(stack.isEmpty()){modes[index]=null;return;}var names=HbmOreDictionary.names(stack);for(String prefix:List.of("ingot","block","dust","nugget","plate"))for(String name:names)if(name.startsWith(prefix)){modes[index]=name;return;}initPatternStandard(stack,index);}
 public void initPatternStandard(ItemStack stack,int index){modes[index]=stack.isEmpty()?null:stack.getItem() instanceof ProcessedBedrockItem?MODE_BEDROCK:SourcePatternItems.hasSubtypes(stack)?MODE_EXACT:MODE_WILDCARD;}
 public void nextMode(ItemStack pattern,int index){if(pattern.isEmpty()){modes[index]=null;return;}String mode=modes[index];if(mode==null)modes[index]=MODE_EXACT;else if(mode.equals(MODE_EXACT))modes[index]=pattern.getItem() instanceof ProcessedBedrockItem?MODE_BEDROCK:MODE_WILDCARD;else if(mode.equals(MODE_BEDROCK))modes[index]=MODE_WILDCARD;else{var names=HbmOreDictionary.names(pattern);if(mode.equals(MODE_WILDCARD))modes[index]=names.isEmpty()?MODE_EXACT:names.getFirst();else if(names.size()<2||mode.equals(names.getLast()))modes[index]=MODE_EXACT;else for(int j=0;j<names.size()-1;j++)if(mode.equals(names.get(j))){modes[index]=names.get(j+1);return;}}}
 public boolean isValidForFilter(ItemStack filter,int index,ItemStack input){String mode=modes[index];if(mode==null)modes[index]=mode=MODE_EXACT;return switch(mode){case MODE_EXACT->SourcePatternItems.sameExact(input,filter);case MODE_WILDCARD->SourcePatternItems.sameItem(input,filter);case MODE_BEDROCK->input.getItem()==filter.getItem()&&input.getItem() instanceof ProcessedBedrockItem&&BedrockProcessingData.grade(meta(input))==BedrockProcessingData.grade(meta(filter));default->HbmOreDictionary.names(input).contains(mode);};}
 public void read(CompoundTag tag){for(int i=0;i<modes.length;i++)modes[i]=tag.contains("mode"+i)?tag.getString("mode"+i):null;}
 public void write(CompoundTag tag){for(int i=0;i<modes.length;i++)if(modes[i]!=null)tag.putString("mode"+i,modes[i]);}
 public static Component label(String mode){return (switch(mode){case MODE_EXACT->Component.translatable("gui.hbm.pneumatic.match.exact");case MODE_WILDCARD->Component.translatable("gui.hbm.pneumatic.match.wildcard");case MODE_BEDROCK->Component.translatable("gui.hbm.pneumatic.match.bedrock");default->Component.translatable("gui.hbm.pneumatic.match.dictionary",mode);}).withStyle(ChatFormatting.YELLOW);}
}

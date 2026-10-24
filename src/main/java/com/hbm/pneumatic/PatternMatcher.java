package com.hbm.pneumatic;
import com.hbm.item.bedrockprocessing.*;
import com.hbm.registry.HbmItemTags;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
/** Exact, wildcard, processed-bedrock grade and source-priority native item-tag filters. */
public final class PatternMatcher {
 public static final String MODE_EXACT="exact",MODE_WILDCARD="wildcard",MODE_BEDROCK="bedrock";public final String[] modes;
 public PatternMatcher(){this(1);}public PatternMatcher(int count){modes=new String[count];}
 private static boolean shape(String tag,String shape){String path=HbmItemTags.key(tag).location().getPath();return path.startsWith(shape+"/")||path.equals(shape)||shape.equals("storage_blocks")&&(path.equals("glass_blocks")||path.startsWith("glass_blocks/"))||path.startsWith("material_groups/")&&(path.endsWith("/"+shape)||path.contains("/"+shape+"/"));}
 public void initPatternSmart(ItemStack stack,int index){if(stack.isEmpty()){modes[index]=null;return;}var tags=HbmItemTags.tags(stack);for(String category:List.of("ingots","storage_blocks","dusts","nuggets","plates"))for(String tag:tags)if(shape(tag,category)){modes[index]=tag;return;}initPatternStandard(stack,index);}
 public void initPatternStandard(ItemStack stack,int index){modes[index]=stack.isEmpty()?null:stack.getItem() instanceof ProcessedBedrockItem?MODE_BEDROCK:SourcePatternItems.hasSubtypes(stack)?MODE_EXACT:MODE_WILDCARD;}
 public void nextMode(ItemStack pattern,int index){if(pattern.isEmpty()){modes[index]=null;return;}String mode=modes[index];if(mode==null)modes[index]=MODE_EXACT;else if(mode.equals(MODE_EXACT))modes[index]=pattern.getItem() instanceof ProcessedBedrockItem?MODE_BEDROCK:MODE_WILDCARD;else if(mode.equals(MODE_BEDROCK))modes[index]=MODE_WILDCARD;else{var tags=HbmItemTags.tags(pattern);if(mode.equals(MODE_WILDCARD))modes[index]=tags.isEmpty()?MODE_EXACT:tags.getFirst();else if(tags.size()<2||mode.equals(tags.getLast()))modes[index]=MODE_EXACT;else for(int j=0;j<tags.size()-1;j++)if(mode.equals(tags.get(j))){modes[index]=tags.get(j+1);return;}}}
 public boolean isValidForFilter(ItemStack filter,int index,ItemStack input){String mode=modes[index];if(mode==null)modes[index]=mode=MODE_EXACT;return switch(mode){case MODE_EXACT->SourcePatternItems.sameExact(input,filter);case MODE_WILDCARD->SourcePatternItems.sameItem(input,filter);case MODE_BEDROCK->input.getItem() instanceof ProcessedBedrockItem inputOre&&filter.getItem() instanceof ProcessedBedrockItem filterOre&&inputOre.grade()==filterOre.grade();default->input.is(HbmItemTags.key(mode));};}
 public void read(CompoundTag tag){for(int i=0;i<modes.length;i++)modes[i]=tag.contains("mode"+i)?tag.getString("mode"+i):null;}
 public void write(CompoundTag tag){for(int i=0;i<modes.length;i++)if(modes[i]!=null)tag.putString("mode"+i,modes[i]);}
 public static Component label(String mode){return(switch(mode){case MODE_EXACT->Component.translatable("gui.hbm.pneumatic.match.exact");case MODE_WILDCARD->Component.translatable("gui.hbm.pneumatic.match.wildcard");case MODE_BEDROCK->Component.translatable("gui.hbm.pneumatic.match.bedrock");default->Component.translatable("gui.hbm.pneumatic.match.tag",mode);}).withStyle(ChatFormatting.YELLOW);}
}

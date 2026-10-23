package com.hbm.weapon.artillery;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
/** Original ItemAmmoArty twelve real types and nested cargo, retaining old metadata independent of item durability. */
public final class ArtilleryAmmoItem extends Item {
    public ArtilleryAmmoItem(){super(new Properties());}
    public static int metadata(ItemStack stack){return ArtyShellTypes.safeType(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));}
    public static ItemStack make(int type,int count){var stack=new ItemStack(ArtilleryAmmoContent.AMMO.get(),count);if(type!=0)stack.set(HbmHazards.LEGACY_METADATA.get(),type);return stack;}
    public static boolean hasCargo(ItemStack stack){return metadata(stack)==ArtyShellTypes.CARGO&&stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).contains("cargo");}
    public static ItemStack cargo(ItemStack stack,HolderLookup.Provider lookup){return hasCargo(stack)?ItemStack.parseOptional(lookup,stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getCompound("cargo")):ItemStack.EMPTY;}
    public static void cargo(ItemStack shell,ItemStack contents,HolderLookup.Provider lookup){var tag=shell.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.put("cargo",contents.save(lookup));shell.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    @Override public String getDescriptionId(ItemStack stack){return "item.hbm."+ArtyShellTypes.NAMES[metadata(stack)];}
    private static void line(List<Component> tooltip,String key,ChatFormatting color,Object... args){tooltip.add(Component.translatable("item.hbm.artillery."+key,args).withStyle(color));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){switch(metadata(stack)){
        case ArtyShellTypes.NORMAL->{line(tooltip,"strength",ChatFormatting.YELLOW,10);line(tooltip,"damage",ChatFormatting.YELLOW,"3x");line(tooltip,"no_blocks",ChatFormatting.BLUE);}
        case ArtyShellTypes.CLASSIC->{line(tooltip,"strength",ChatFormatting.YELLOW,15);line(tooltip,"damage",ChatFormatting.YELLOW,"5x");line(tooltip,"no_blocks",ChatFormatting.BLUE);}
        case ArtyShellTypes.EXPLOSIVE->{line(tooltip,"strength",ChatFormatting.YELLOW,15);line(tooltip,"damage",ChatFormatting.YELLOW,"3x");line(tooltip,"blocks",ChatFormatting.RED);}
        case ArtyShellTypes.PHOSPHORUS->{line(tooltip,"strength",ChatFormatting.YELLOW,10);line(tooltip,"damage",ChatFormatting.YELLOW,"3x");line(tooltip,"phosphorus",ChatFormatting.RED);line(tooltip,"no_blocks",ChatFormatting.BLUE);}
        case ArtyShellTypes.PHOSPHORUS_MULTI->line(tooltip,"splits",ChatFormatting.RED,10);
        case ArtyShellTypes.MINI_NUKE->{line(tooltip,"strength",ChatFormatting.YELLOW,20);line(tooltip,"nuclear",ChatFormatting.RED);line(tooltip,"blocks",ChatFormatting.RED);}
        case ArtyShellTypes.MINI_NUKE_MULTI->line(tooltip,"splits",ChatFormatting.RED,5);
        case ArtyShellTypes.NUKE->{line(tooltip,"skull",ChatFormatting.RED);line(tooltip,"unicode0",ChatFormatting.RED);line(tooltip,"unicode1",ChatFormatting.RED);}
        case ArtyShellTypes.CARGO->{var contents=context.registries()==null?ItemStack.EMPTY:cargo(stack,context.registries());if(contents.isEmpty())line(tooltip,"empty",ChatFormatting.RED);else tooltip.add(contents.getHoverName().copy().withStyle(ChatFormatting.YELLOW));}
        default->{} // Source gas shells deliberately have no additional lore.
    }}
    public static List<ItemStack> creative(){return java.util.stream.IntStream.of(0,1,2,5,7,3,6,4,8,9,10,11).mapToObj(meta->make(meta,1)).toList();}
}

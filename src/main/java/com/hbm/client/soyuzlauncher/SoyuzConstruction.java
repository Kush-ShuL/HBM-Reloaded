package com.hbm.client.soyuzlauncher;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherContent;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.world.item.ItemStack;
/** The one actual ConstructionHandler soysauce record; a display of the real world build, never an executable recipe. */
public final class SoyuzConstruction {
 public static LegacyRecipeDisplays.Category category(){return new LegacyRecipeDisplays.Category("soyuz_construction","Construction","universal",SoyuzLauncherContent.id("textures/gui/nei/gui_nei.png"),166,65,List.of(new ItemStack(SoyuzLauncherContent.STRUCTURE_ITEM.get())));}
 public static LegacyRecipeDisplays.Display display(){var inputs=new ArrayList<LegacyRecipeDisplays.Slot>();for(var row:new String[][]{{"struct_launcher","30"},{"struct_launcher","384"},{"struct_scaffold","63"},{"struct_scaffold","384"},{"concrete_smooth","38"},{"concrete_smooth","320"}})inputs.add(new LegacyRecipeDisplays.Slot(List.of(label(new ItemStack(HbmContent.block(row[0]),Integer.parseInt(row[1]))))));return new LegacyRecipeDisplays.Display(SoyuzLauncherContent.id("construction/soyuz_source_72"),"soyuz_construction",List.copyOf(inputs),List.of(new LegacyRecipeDisplays.Slot(List.of(new ItemStack(SoyuzLauncherContent.ITEM.get())))),null,null,List.of());}
 private static ItemStack label(ItemStack stack){if(stack.getCount()>64)stack.set(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(List.of(net.minecraft.network.chat.Component.literal((stack.getCount()/64)+"x64"+(stack.getCount()%64>0?" + "+(stack.getCount()%64):"")).withStyle(net.minecraft.ChatFormatting.RED))));return stack;}
 private SoyuzConstruction(){}
}

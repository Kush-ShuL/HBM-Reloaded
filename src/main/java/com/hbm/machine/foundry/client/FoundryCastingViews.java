package com.hbm.machine.foundry.client;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.machine.anvil.AnvilContent;
import com.hbm.machine.foundry.*;
import com.hbm.material.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Original CrucibleRecipes.registerMoldsForNEI ordering, amounts, mold IDs and basin selection. */
final class FoundryCastingViews {
 record Casting(ItemStack liquid,ItemStack mold,ItemStack basin,ItemStack output){}
 static List<Casting> recipes(){var recipes=new ArrayList<Casting>();for(var material:HbmMaterials.ordered()){if(material.smeltingBehavior()!=NtmMaterial.SmeltingBehavior.SMELTABLE)continue;for(var mold:AnvilContent.MOLD.get().molds()){var output=mold.output(material);if(output.isEmpty())continue;ItemStack form=new ItemStack(AnvilContent.MOLD.get());form.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),mold.id());recipes.add(new Casting(ScrapsItem.create(new MaterialStack(material,mold.requiredUnits()),true),form,new ItemStack(FoundryContent.block(mold.size()==0?FoundryContent.Kind.MOLD:FoundryContent.Kind.BASIN)),output));}}return recipes;}
 static void draw(GuiGraphics graphics,int x,int y){graphics.blit(ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei_foundry.png"),x,y,5,11,166,65,256,256);}
 private FoundryCastingViews(){}
}

package com.hbm.client.reactor.icf.pellet;
import com.hbm.fluid.HbmFluids;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.reactor.icf.pellet.*;
import com.hbm.material.HbmMaterials;
import com.hbm.registry.*;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Read-only displays of the actual press combinations; processing remains the original per-tick operation. */
final class IcfPelletViews {
 record Row(List<ItemStack> first,List<ItemStack> second,boolean muon,ItemStack output){}
 static void draw(GuiGraphics g,Row row,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);for(int[]p:new int[][]{{20,23},{47,23},{47,5},{101,23},{74,23}})g.blit(texture,x+p[0],y+p[1],5,87,18,18,256,256);g.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.translatable(row.muon?"gui.hbm.icf.muon_charge":"gui.hbm.icf.no_muon"),x+4,y+54,0x404040,false);}
 static String subtype(ItemStack stack){return IcfPelletItem.getType(stack,true).name()+"/"+IcfPelletItem.getType(stack,false).name()+"/"+IcfPelletItem.isMuon(stack);}
 static List<Row> rows(){var inputs=new EnumMap<IcfPelletItem.Fuel,List<ItemStack>>(IcfPelletItem.Fuel.class);for(var fuel:IcfPelletItem.Fuel.values())inputs.put(fuel,new ArrayList<>());for(var name:List.of("HYDROGEN","DEUTERIUM","TRITIUM","HELIUM3","HELIUM4","OXYGEN","CHLORINE")){var type=HbmFluids.fromName(name);inputs.get(IcfPelletItem.fluidFuel(type)).add(FluidIconItem.make(type,1000));}
  var candidates=ItemStackLinkedSet.createTypeAndComponentsSet();for(var material:HbmMaterials.ordered())if(IcfPelletItem.materialFuel(material)!=null)for(var shape:HbmMaterials.shapes())if(shape.q(1)==HbmMaterials.shape("INGOT").q(1))HbmItemTags.items(HbmItemTags.material(shape.symbol(),material.names().getFirst())).forEach(candidates::add);
  for(var entry:HbmMaterials.stackDistributions())if(BuiltInRegistries.ITEM.containsKey(entry.item())){var stack=new ItemStack(BuiltInRegistries.ITEM.get(entry.item()));if(stack.isDamageableItem())stack.setDamageValue(entry.metadata());else stack.set(HbmHazards.LEGACY_METADATA.get(),entry.metadata());candidates.add(stack);}
  for(var stack:candidates){var mats=HbmMaterials.getMaterialsFromItem(stack);if(mats.size()!=1||mats.getFirst().amount!=HbmMaterials.shape("INGOT").q(1))continue;var fuel=IcfPelletItem.materialFuel(mats.getFirst().material);if(fuel!=null)inputs.get(fuel).add(stack);}
  var rows=new ArrayList<Row>();for(var first:IcfPelletItem.Fuel.values())for(var second:IcfPelletItem.Fuel.values())if(first!=second&&!inputs.get(first).isEmpty()&&!inputs.get(second).isEmpty())for(boolean muon:new boolean[]{false,true})rows.add(new Row(List.copyOf(inputs.get(first)),List.copyOf(inputs.get(second)),muon,IcfPelletItem.setup(first,second,muon)));return rows;
 }
 private IcfPelletViews(){}
}

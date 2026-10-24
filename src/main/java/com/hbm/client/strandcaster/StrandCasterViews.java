package com.hbm.client.strandcaster;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.machine.anvil.MoldContent;
import com.hbm.material.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
/** The same real Mold output table; water consumption and spent steam follow the actual caster equation. */
final class StrandCasterViews {
 record Casting(ItemStack molten,ItemStack mold,ItemStack output,int water){}
 static List<Casting> recipes(){var result=new ArrayList<Casting>();for(var material:HbmMaterials.ordered()){if(material.smeltingBehavior()!=NtmMaterial.SmeltingBehavior.SMELTABLE)continue;for(var mold:MoldContent.molds()){var output=mold.output(material);if(output.isEmpty())continue;var stack=MoldContent.stack(mold);result.add(new Casting(ScrapsItem.create(new MaterialStack(material,mold.requiredUnits()),true),stack,output,mold.requiredUnits()*5));}}return result;}
 static void draw(Casting recipe,GuiGraphics graphics,int x,int y){graphics.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.translatable("gui.hbm.strand_caster.water",recipe.water),x+3,y+48,0x3f79c4,false);graphics.drawString(com.hbm.client.gui.font.LegacyFonts.get(),Component.literal("→"),x+81,y+22,0x404040,false);}
}

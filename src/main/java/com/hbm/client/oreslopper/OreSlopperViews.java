package com.hbm.client.oreslopper;
import com.hbm.fluid.*;import com.hbm.item.fluid.FluidIconItem;import com.hbm.item.bedrockprocessing.ProcessedBedrockItem;import com.hbm.machine.oreslopper.*;import java.util.*;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.core.registries.BuiltInRegistries;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
/** Exact original NEIUniversal 2-input/7-output positions, including its source fixed one-item examples. */
final class OreSlopperViews {
 static final ResourceLocation TEXTURE=OreSlopperContent.id("textures/gui/nei/gui_nei.png");static final int[][] INPUT={{48,24},{30,24}},OUTPUT={{102,6},{120,6},{102,24},{120,24},{102,42},{120,42},{138,24}};
 record Display(){}static List<ItemStack> inputs(){return List.of(FluidIconItem.make(new FluidStack(HbmFluids.WATER,1000)),new ItemStack(BuiltInRegistries.ITEM.get(OreSlopperContent.id("bedrock_ore_base"))));}static List<ItemStack> outputs(){var stacks=new ArrayList<ItemStack>();for(var type:com.hbm.worldgen.bedrockresource.BedrockOreType.values())stacks.add(ProcessedBedrockItem.make("BASE",type.name(),1));stacks.add(FluidIconItem.make(new FluidStack(HbmFluids.SLOP,1000)));return stacks;}
 static void draw(GuiGraphics g,int x,int y){for(var p:INPUT)g.blit(TEXTURE,x+p[0]-1,y+p[1]-1,5,87,18,18,256,256);for(var p:OUTPUT)g.blit(TEXTURE,x+p[0]-1,y+p[1]-1,5,87,18,18,256,256);g.blit(TEXTURE,x+74,y+14,59,87,18,36,256,256);}
 private OreSlopperViews(){}
}

package com.hbm.client.reactor.icf;
import com.hbm.api.tool.ToolableBlock.ToolType;
import com.hbm.machine.reactor.icf.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Two actual BlockToolConversion source recipes, sharing the real callback requirements. */
final class IcfTooling{
 record View(int metadata,ToolType tool){ItemStack input(){return IcfReactorContent.COMPONENT.get().stack(metadata,1);}ItemStack output(){return IcfReactorContent.COMPONENT.get().stack(metadata+1,1);}List<List<ItemStack>> inputs(){var lists=new ArrayList<List<ItemStack>>();for(var need:IcfReactorContent.COMPONENT.get().requirements(IcfReactorContent.COMPONENT.get().defaultBlockState().setValue(IcfMaterialBlock.META,metadata),tool))lists.add(java.util.Arrays.stream(need.ingredient().getItems()).map(stack->stack.copyWithCount(need.count())).toList());lists.add(List.of(input()));return lists;}List<ItemStack> tools(){return tool.stacksForDisplay.stream().map(ItemStack::copy).toList();}}
 static List<View> recipes(){return List.of(new View(1,ToolType.TORCH),new View(3,ToolType.BOLT));}static void draw(View recipe,GuiGraphics g,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);for(int i=0;i<recipe.inputs().size();i++)g.blit(texture,x+47-i*18,y+23,5,87,18,18,256,256);g.blit(texture,x+101,y+23,5,87,18,18,256,256);g.blit(texture,x+74,y+14,59,87,18,36,256,256);}private IcfTooling(){}
}

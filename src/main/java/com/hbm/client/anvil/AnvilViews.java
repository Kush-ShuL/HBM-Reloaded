package com.hbm.client.anvil;
import com.hbm.machine.anvil.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeManager;
final class AnvilViews{
    record View(ResourceLocation id,List<List<ItemStack>> inputs,List<AnvilConstructionRecipe.Output> outputs,int lower,int upper,boolean smithing,String kind){boolean tierValid(int tier){return tier>=lower&&(upper==-1||tier<=upper);}}
    record Layout(int ix,int iy,int inColumns,int ox,int oy,int outColumns,int anvilX,String shape){}
    static List<View> recipes(RecipeManager manager){var result=new ArrayList<View>();for(var holder:manager.getAllRecipesFor(AnvilContent.CONSTRUCTION_TYPE.get())){var r=holder.value();result.add(new View(holder.id(),r.inputs().stream().map(AnvilIngredient::examples).toList(),r.outputs(),r.lower(),r.upper(),false,"construction"));}for(var holder:manager.getAllRecipesFor(AnvilContent.SMITHING_TYPE.get())){var r=holder.value();var inputs=new ArrayList<List<ItemStack>>();for(var ingredient:List.of(r.left(),r.right())){var examples=ingredient.examples();if(r.kind().equals("hot"))examples=examples.stream().map(stack->HotSmithingItem.heatUp(stack.copy(),1)).toList();inputs.add(examples);}result.add(new View(holder.id(),inputs,List.of(new AnvilConstructionRecipe.Output(r.output(),1)),r.tier(),-1,true,r.kind()));}return result;}
    static Layout layout(View view){int in=view.inputs().size(),out=view.outputs().size();if(in==1&&out==1)return new Layout(48,24,1,102,24,1,75,"SMITHING");if(in==1&&out>1)return new Layout(12,24,1,48,6,6,30,"RECYCLING");if(in>1&&out==1)return new Layout(12,6,6,138,24,1,120,"CONSTRUCTION");return new Layout(3,6,4,93,6,4,75,"NONE");}
    static String chance(float chance){return ((int)(chance*1000))/10D+"%";}
    static List<Component> conditions(View view){return switch(view.kind()){case "hot"->List.of(Component.translatable("gui.hbm.anvil.condition.hot"));case "mold"->List.of(Component.translatable("gui.hbm.anvil.condition.mold"));case "rename"->List.of(Component.translatable("gui.hbm.anvil.condition.rename"));default->List.of();};}
    static void draw(GuiGraphics graphics,View view,int x,int y){var texture=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei_anvil.png");graphics.blit(texture,x,y,5,11,166,65,256,256);var p=layout(view);switch(p.shape()){case "NONE"->{blit(graphics,texture,x+2,y+5,5,87,72,54);blit(graphics,texture,x+92,y+5,5,87,72,54);blit(graphics,texture,x+74,y+14,131,96,18,36);}case "SMITHING"->{blit(graphics,texture,x+47,y+23,113,105,18,18);blit(graphics,texture,x+101,y+23,113,105,18,18);blit(graphics,texture,x+74,y+14,149,96,18,36);}case "CONSTRUCTION"->{blit(graphics,texture,x+11,y+5,5,87,108,54);blit(graphics,texture,x+137,y+23,113,105,18,18);blit(graphics,texture,x+119,y+14,167,96,18,36);}case "RECYCLING"->{blit(graphics,texture,x+11,y+23,113,105,18,18);blit(graphics,texture,x+47,y+5,5,87,108,54);blit(graphics,texture,x+29,y+14,185,96,18,36);}}}
    private static void blit(GuiGraphics g,ResourceLocation texture,int x,int y,int u,int v,int w,int h){g.blit(texture,x,y,u,v,w,h,256,256);}
    private AnvilViews(){}
}

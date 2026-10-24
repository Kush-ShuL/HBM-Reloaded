package com.hbm.client.woodprocessing;
import com.hbm.machine.woodprocessing.WoodProcessingContent;
import com.hbm.registry.HbmItemTags;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** The four exact TileEntitySawmill.getRecipes/ntmSawmill displays, including source generic oak-plank preview. */
final class SawmillViews {
    record View(String tag,List<ItemStack> inputs,ItemStack result,float dustChance){}
    static List<View> views(){return List.of(view("minecraft:logs",new ItemStack(Items.OAK_PLANKS,6),.5F),view("minecraft:planks",new ItemStack(Items.STICK,6),.1F),view("c:rods/wooden",new ItemStack(WoodProcessingContent.item("powder_sawdust")),0),view("minecraft:saplings",new ItemStack(Items.STICK),.1F));}
    private static View view(String tag,ItemStack result,float chance){return new View(tag,HbmItemTags.items(tag).toList(),result,chance);}
    static Component chance(View view){return Component.literal(Math.round(view.dustChance*100)+"%").withStyle(ChatFormatting.RED);}
    static ItemStack dust(){return new ItemStack(WoodProcessingContent.item("powder_sawdust"));}
    static void draw(GuiGraphics g,View view,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");g.blit(texture,x+47,y+23,5,87,18,18,256,256);g.blit(texture,x+101,y+23,5,87,18,18,256,256);if(view.dustChance>0)g.blit(texture,x+119,y+23,5,87,18,18,256,256);g.blit(texture,x+74,y+14,59,87,18,36,256,256);}
}

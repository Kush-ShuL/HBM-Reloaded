package com.hbm.compat.recipeviewer.client;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays.Category;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays.Display;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.GuiGraphics;

/** Draw the source backgrounds, slot plates, machine panel and original progress sprites. */
public final class LegacyDisplayDrawing {
    private LegacyDisplayDrawing() {}
    /** Old NEI painted a blueprint-book shortcut into its shared atlas. */
    public static void recipeBackground(GuiGraphics graphics, ResourceLocation texture, int x, int y) {
        graphics.blit(texture,x,y,5,11,166,65,256,256);
        if(texture.equals(ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png")))
            graphics.fill(x+147,y+1,x+165,y+19,0xFFC6C6C6);
    }
    public static List<ItemStack> catalysts(Category category) {
        var result=new ArrayList<ItemStack>();
        List<String> ids=category.layout().equals("shredder")?List.of("hbm:blades_steel","hbm:blades_titanium","hbm:blades_desh"):
                category.layout().equals("alloy")?List.of("minecraft:coal","minecraft:coal_block","minecraft:lava_bucket","minecraft:blaze_rod","minecraft:blaze_powder","hbm:lignite","hbm:powder_lignite","hbm:briquette_coal","hbm:coke_coal","hbm:solid_fuel","hbm:powder_coal"):List.of();
        for(String id:ids) if(BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(id))) result.add(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id))));
        return result;
    }
    public static void background(GuiGraphics graphics,Category category,Display recipe,int x,int y) {
        var texture=category.texture(); recipeBackground(graphics,texture,x,y);
        if(category.layout().equals("generic")||category.layout().equals("universal")) {
            for(var point:LegacyRecipeDisplays.inputCoordinates(category,recipe.inputs().size())) graphics.blit(texture,x+point[0]-1,y+point[1]-1,5,87,18,18,256,256);
            for(var point:LegacyRecipeDisplays.outputCoordinates(category,recipe)) graphics.blit(texture,x+point[0]-1,y+point[1]-1,5,87,18,18,256,256);
            graphics.blit(texture,x+74+LegacyRecipeDisplays.assemblyOffset(category,recipe),y+14,59,87,18,36,256,256);
        }
        if(category.layout().equals("shredder")) {
            int width=(int)(System.currentTimeMillis()/50%48)*24/48;
            if(width>0) graphics.blit(texture,x+80,y+23,100,118,width,16,256,256);
            int height=(int)(System.currentTimeMillis()/50%480)*52/480;
            if(height>0) graphics.blit(texture,x+3,y+6+52-height,36,86+52-height,16,height,256,256);
        }
        if(category.layout().equals("alloy")) {
            int width=(int)(System.currentTimeMillis()/50%48)*24/48;
            if(width>0) graphics.blit(texture,x+96,y+24,176,14,width,16,256,256);
            int fire=(int)(System.currentTimeMillis()/50%48)*14/48;
            if(fire>0) graphics.blit(texture,x+57,y+26+14-fire,176,14-fire,14,fire,256,256);
            int heat=(int)(System.currentTimeMillis()/50%480)*52/480;
            if(heat>0) graphics.blit(texture,x+39,y+7+52-heat,201,52-heat,16,heat,256,256);
        }
    }
    public static void extras(GuiGraphics graphics,Category category,Display recipe,int x,int y) {
        var font=LegacyFonts.get();
        if(category.layout().equals("generic")||category.id().equals("soldering")) {
            int right=x+(category.id().equals("soldering")?160:164);
            int timeY=y+(category.id().equals("soldering")?43:45),powerY=y+(category.id().equals("soldering")?55:57);
            if(recipe.duration()!=null) { String label=shortNumber(recipe.duration())+" ticks"; graphics.drawString(font,label,right-font.width(label),timeY,0x404040,false); }
            if(recipe.power()!=null) { String label=shortNumber(recipe.power())+(category.id().equals("soldering")?" HE/t":"HE/t"); graphics.drawString(font,label,right-font.width(label),powerY,0x404040,false); }
        }
        for(int index=0;index<recipe.notes().size();index++) graphics.drawString(font,recipe.notes().get(index),x+8,y+52+index*10,0x404040,false);
    }
    private static String shortNumber(long value) {
        String[] suffix={"","k","M","G","T","P","E"}; int power=0; double number=value;
        while(Math.abs(number)>=1000&&power<6) { number/=1000; power++; }
        if(power==0) return Long.toString(value);
        number=number<=-100?Math.round(number*10)/10D:Math.round(number*100)/100D;
        return number+suffix[power];
    }
}

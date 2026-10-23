package com.hbm.client.generator.steamengine;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.generator.steamengine.SteamEngineBlockEntity;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Engine accepts only normal steam; no invented compressed-steam or other Coolable recipes. */
final class SteamEngineViews {
    record View(ItemStack input,ItemStack output,String energy){}
    static List<View> recipes(){var trait=HbmFluids.STEAM.getTrait(FluidTraits.Coolable.class);return List.of(new View(FluidIconItem.make(HbmFluids.STEAM,trait.amountReq()),FluidIconItem.make(HbmFluids.SPENTSTEAM,trait.amountProduced()),SteamEngineBlockEntity.energy(1,trait).toString()));}
    static void draw(GuiGraphics graphics,int x,int y,String energy){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");graphics.blit(texture,x,y,5,11,166,65,256,256);graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);graphics.drawString(Minecraft.getInstance().font,Component.translatable("gui.hbm.steam_engine.energy",energy),x+4,y+53,0x404040,false);}
}

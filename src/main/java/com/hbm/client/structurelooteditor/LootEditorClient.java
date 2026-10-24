package com.hbm.client.structurelooteditor;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.client.structuretandem.TandemModel;
import com.hbm.item.structurelooteditor.*;
import com.hbm.world.structure.loot.StructureLootContent;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LootEditorClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){
        var side=event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/wand_loot")));
        var top=event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/wand_loot_top")));
        event.getModels().replaceAll((id,model)->id.id().equals(LootEditorContent.BLOCK.getId())?TandemModel.sourceFaces(model,id.getVariant().equals("inventory"),new TextureAtlasSprite[]{side,top},(state,face)->face.getAxis()==net.minecraft.core.Direction.Axis.Y?1:0,(state,face)->{
            int rotation=state==null?0:state.getValue(LootEditorBlock.ROTATION);if(face==net.minecraft.core.Direction.UP)return rotation;if(face==net.minecraft.core.Direction.DOWN)return rotation==1?2:rotation==2?1:rotation;return 0;
        }):model);
    }
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
    public static final class Overlay {
        @SubscribeEvent public static void draw(RenderGuiEvent.Post event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;
            var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockEntity(hit.getBlockPos()) instanceof LootEditorEntity tile))return;
            var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;
            var title=LootEditorContent.BLOCK.get().getName();graphics.drawString(font,title,x+1,y-9,0x404000,false);graphics.drawString(font,title,x,y-10,0xffff00,false);
            var lines=new ArrayList<Component>();lines.add(Component.translatable("hud.hbm.wand_loot.replacement",BuiltInRegistries.BLOCK.getKey(tile.replacement.getBlock()).toString()));lines.add(Component.translatable("hud.hbm.wand_loot.state",tile.replacement.toString()));lines.add(Component.translatable("hud.hbm.wand_loot.pool",tile.pool));
            if(!tile.replacement.is(StructureLootContent.PILE.get())){lines.add(Component.translatable("hud.hbm.wand_loot.minimum",tile.minimum));lines.add(Component.translatable("hud.hbm.wand_loot.maximum",tile.maximum));}
            if(tile.lockCode!=0){lines.add(Component.translatable("hud.hbm.wand_loot.locked"));lines.add(Component.translatable("hud.hbm.wand_loot.lock_chance",Float.toString(tile.lockMod)));}
            for(var line:lines){graphics.drawString(font,line,x,y,0xffffff,true);y+=10;}
        }
    }
    private LootEditorClient(){}
}

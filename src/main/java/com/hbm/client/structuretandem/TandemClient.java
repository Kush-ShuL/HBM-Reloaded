package com.hbm.client.structuretandem;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.item.structuretandem.*;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TandemClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){TandemPackets.CLIENT_OPEN=packet->Minecraft.getInstance().setScreen(new TandemScreen(packet));}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){
        var sprites=new net.minecraft.client.renderer.texture.TextureAtlasSprite[4];
        String[] names={"wand_tandem","wand_tandem_top","wand_tandem_side","wand_tandem_back"};
        for(int i=0;i<4;i++)sprites[i]=event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/"+names[i])));
        event.getModels().replaceAll((id,model)->id.id().equals(TandemContent.BLOCK.getId())?new TandemModel(model,id.getVariant().equals("inventory"),sprites):model);
    }
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
    public static final class Overlay {
        @SubscribeEvent public static void draw(RenderGuiEvent.Post event){
            var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockEntity(hit.getBlockPos()) instanceof TandemEntity tile))return;
            var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;
            Component name=TandemContent.BLOCK.get().getName();graphics.drawString(font,name,x+1,y-9,0x404000,false);graphics.drawString(font,name,x,y-10,0xffff00,false);
            var lines=List.of(Component.translatable("hud.hbm.wand_tandem.pool",tile.pool),Component.translatable("hud.hbm.wand_tandem.target",tile.target),Component.translatable("hud.hbm.wand_tandem.replacement",net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(tile.replacement.getBlock()).toString()),Component.translatable("hud.hbm.wand_tandem.state",tile.replacement.toString()),Component.translatable("hud.hbm.wand_tandem.joint",Component.translatable(tile.rollable?"gui.hbm.wand_tandem.rollable":"gui.hbm.wand_tandem.aligned")));
            for(var line:lines){graphics.drawString(font,line,x,y,0xffffff,true);y+=10;}
        }
    }
    private TandemClient(){}
}

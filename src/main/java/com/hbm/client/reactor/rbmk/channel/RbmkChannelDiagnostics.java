package com.hbm.client.reactor.rbmk.channel;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.reactor.rbmk.channel.*;
import java.util.TreeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Original additional DODD fuel/flux fields, after the common real heat/water/steam bars. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class RbmkChannelDiagnostics {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event) {if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;
        var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof RbmkFuelChannelBlock block)||!(block.core(mc.level,hit.getBlockPos()) instanceof RbmkFuelChannelEntity channel))return;
        var data=channel.diagnosticData();var keys=new TreeSet<>(data.getAllKeys());keys.removeAll(java.util.Set.of("heat","reasimWater","reasimSteam","x","y","z","items","id","muffled"));
        var g=event.getGuiGraphics();var font=LegacyFonts.get();int x=g.guiWidth()/2+8,y=g.guiHeight()/2+44;
        for(var key:keys){String value=data.get(key).toString();if(!value.isEmpty()){char last=value.charAt(value.length()-1);if(last=='d'||last=='s'||last=='b')value=value.substring(0,value.length()-1);}g.drawString(font,Component.translatable("tile.rbmk.dodd."+key).append(": "+value),x,y,0xFFFFFF,true);y+=10;}
    }
    private RbmkChannelDiagnostics(){}
}

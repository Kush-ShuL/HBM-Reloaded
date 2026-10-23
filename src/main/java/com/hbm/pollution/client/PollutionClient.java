package com.hbm.pollution.client;

import com.hbm.item.settings.client.LegacyInfoSystem;
import com.hbm.pollution.PollutionSync;
import com.hbm.pollution.PollutionType;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

/** Original PermaSync pollution view and .05-per-tick soot smoothing, gray fog blend and range formula. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class PollutionClient {
    private static final float[] LEVELS=new float[PollutionType.values().length];
    private static ClientLevel world;
    private static float renderSoot;
    private static boolean enabled;
    private static double threshold=35,divisor=120;
    public static void receive(PollutionSync packet){
        var level=Minecraft.getInstance().level;if(level==null||!level.dimension().location().equals(packet.dimension()))return;
        if(world!=level){reset();world=level;}
        LEVELS[0]=packet.soot();LEVELS[1]=packet.poison();LEVELS[2]=packet.heavyMetal();LEVELS[3]=packet.fallout();enabled=packet.sootFog();threshold=packet.threshold();divisor=packet.divisor();
        if(packet.detector()){
            String[] keys={"soot","poison","heavymetal"};
            for(int i=0;i<keys.length;i++){float shown=((int)(LEVELS[i]*100))/100F;LegacyInfoSystem.push(Component.translatable("pollution."+keys[i]).append(": "+shown).withStyle(ChatFormatting.YELLOW),4000,100+i);}
        }
    }
    public static float get(PollutionType type){return LEVELS[type.ordinal()];}
    @SubscribeEvent public static void tick(ClientTickEvent.Pre event){
        var minecraft=Minecraft.getInstance();if(world!=minecraft.level){reset();world=minecraft.level;}
        if(world==null||minecraft.isPaused()||!enabled)return;
        float target=LEVELS[PollutionType.SOOT.ordinal()],step=.05F;
        if(Math.abs(renderSoot-target)<step)renderSoot=target;else if(renderSoot<target)renderSoot+=step;else if(renderSoot>target)renderSoot-=step;
    }
    @SubscribeEvent(priority=EventPriority.LOW) public static void thickenFog(ViewportEvent.RenderFog event){
        float soot=(float)(renderSoot-threshold);if(world==null||!enabled||soot<=0)return;
        float far=(float)(Minecraft.getInstance().options.renderDistance().get()*16);
        event.setNearPlaneDistance(0);event.setFarPlaneDistance(far/(1+soot*5F/(float)divisor));event.setFogShape(FogShape.SPHERE);event.setCanceled(true);
    }
    @SubscribeEvent(priority=EventPriority.LOW) public static void tintFog(ViewportEvent.ComputeFogColor event){
        float soot=(float)(renderSoot-threshold);if(world==null||!enabled||soot<=0)return;
        float blend=Math.min(soot/(float)divisor,1F),remaining=1-blend;
        event.setRed(event.getRed()*remaining+.15F*blend);event.setGreen(event.getGreen()*remaining+.15F*blend);event.setBlue(event.getBlue()*remaining+.15F*blend);
    }
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event){reset();world=null;}
    private static void reset(){java.util.Arrays.fill(LEVELS,0);renderSoot=0;enabled=false;threshold=35;divisor=120;}
    private PollutionClient(){}
}

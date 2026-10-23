package com.hbm.geiger;

import com.hbm.exposure.HbmExposure;
import com.hbm.radiation.HbmRadiation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/** Original ContaminationUtil.printGeigerData: actual chunk, radBuf, body dose and resistance. */
public final class GeigerReadings {
    private GeigerReadings() {}
    public static ChatFormatting rateColor(double value) {
        return value==0?ChatFormatting.GREEN:value<1?ChatFormatting.YELLOW:value<10?ChatFormatting.GOLD:value<100?ChatFormatting.RED:value<1000?ChatFormatting.DARK_RED:ChatFormatting.DARK_GRAY;
    }
    public static ChatFormatting doseColor(double value) {
        return value<200?ChatFormatting.GREEN:value<400?ChatFormatting.YELLOW:value<600?ChatFormatting.GOLD:value<800?ChatFormatting.RED:value<1000?ChatFormatting.DARK_RED:ChatFormatting.DARK_GRAY;
    }
    public static void print(Player player) {
        if(!(player.level() instanceof ServerLevel server)) return;
        BlockPos pos=player.blockPosition();
        double body=((int)(HbmExposure.radiation(player)*10))/10D;
        double chunk=((int)(HbmRadiation.getRadiation(server,pos.getX(),pos.getY(),pos.getZ())*10))/10D;
        double environment=((int)(HbmExposure.data(player).radBuffer()*10D))/10D;
        double resistance=((int)(10000D-HbmExposure.radiationModifier(player)*10000D))/100D;
        double coefficient=((int)(HbmExposure.radiationResistance(player)*100D))/100D;
        player.displayClientMessage(Component.literal("===== ☢ ").append(Component.translatable("geiger.title")).append(" ☢ =====").withStyle(ChatFormatting.GOLD),false);
        line(player,"geiger.chunkRad",chunk+" RAD/s",rateColor(chunk));
        line(player,"geiger.envRad",environment+" RAD/s",rateColor(environment));
        line(player,"geiger.playerRad",body+" RAD",doseColor(body));
        line(player,"geiger.playerRes",resistance+"% ("+coefficient+")",coefficient>0?ChatFormatting.GREEN:ChatFormatting.WHITE);
    }
    private static void line(Player player,String key,String value,ChatFormatting color) {
        player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.YELLOW).append(Component.literal(" "+value).withStyle(color)),false);
    }
}

package com.hbm.bomb.conventional;

import com.hbm.radiation.HbmRadiation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

/** Source XFactoryCatapult.incrementRad, shared with the nuclear mine's original 21-cell burst. */
public final class ConventionalRadiation {
    private ConventionalRadiation(){}
    public static void incrementRad(Level level,double x,double y,double z,float multiplier){if(level instanceof net.minecraft.server.level.ServerLevel server)for(int i=-2;i<=2;i++)for(int j=-2;j<=2;j++)if(Math.abs(i)+Math.abs(j)<4)HbmRadiation.incrementRad(server,Mth.floor(x+i*16),Mth.floor(y),Mth.floor(z+j*16),50F/(Math.abs(i)+Math.abs(j)+1)*multiplier);}
}

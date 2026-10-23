package com.hbm.client.turret;
import com.hbm.client.bomb.nuclear.LegacyExplosionEffects;
import com.hbm.client.weapon.legendary.SpentCasingParticle;
import com.hbm.machine.turret.TurretEffects;
import com.hbm.weapon.sedna.gun.legendary.CasingEffects;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
/** Source CasingEjector float matrix arithmetic followed by the existing true ParticleSpentCasing implementation. */
public final class TurretClientEffects {
    private static final Random RANDOM=new Random();
    public static void receive(TurretEffects.Packet packet){var client=Minecraft.getInstance();if(client.level==null||packet.data()==null)return;var source=packet.data();if(source.getString("type").equals("smoke")){LegacyExplosionEffects.particle("largeexplode",packet.x(),packet.y(),packet.z(),source.contains("size")?source.getFloat("size"):1,0,0);return;}
        float variance=source.contains("variance")?source.getFloat("variance"):.01F;float pitch=source.getFloat("pitch"),yaw=source.getFloat("yaw"),p=pitch+(float)RANDOM.nextGaussian()*variance,y=yaw+(float)RANDOM.nextGaussian()*variance;
        float vx=(float)((source.contains("motion_x")?source.getDouble("motion_x"):.2)+RANDOM.nextGaussian()*variance),vy=(float)((source.contains("motion_y")?source.getDouble("motion_y"):.2)+RANDOM.nextGaussian()*variance),vz=(float)(source.getDouble("motion_z")+RANDOM.nextGaussian()*variance);
        float c=(float)Math.cos(p),s=(float)Math.sin(p),ny=c*vy+-s*vz,nz=s*vy+c*vz;vy=ny;vz=nz;c=(float)Math.cos(-y);s=(float)Math.sin(-y);float nx=c*vx+s*vz;nz=-s*vx+c*vz;vx=nx;vz=nz;
        var data=new CompoundTag();data.putString("name",source.getString("name"));data.putDouble("mX",vx);data.putDouble("mY",vy);data.putDouble("mZ",vz);data.putFloat("pitch",(float)Math.toDegrees(pitch));data.putFloat("yaw",(float)Math.toDegrees(yaw));data.putFloat("mPitch",(float)(client.level.random.nextGaussian()*5F));data.putFloat("mYaw",(float)(client.level.random.nextGaussian()*10F));SpentCasingParticle.receive(new CasingEffects.Packet(packet.x(),packet.y(),packet.z(),data));
    }
    private TurretClientEffects(){}
}

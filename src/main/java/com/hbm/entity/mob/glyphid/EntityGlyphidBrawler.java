package com.hbm.entity.mob.glyphid;
import com.hbm.bomb.nuclear.LegacyVector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
/** Actual Brawler leap, source stationary-lastTarget quirk, reduced small-fall damage and armor curve. */
public final class EntityGlyphidBrawler extends EntityGlyphid {
    public int timer;protected Entity lastTarget;protected double lastX,lastY,lastZ;
    public EntityGlyphidBrawler(EntityType<? extends EntityGlyphidBrawler> type,Level level){super(type,level);}
    @Override public ResourceLocation getSkin(){return ResourceLocation.parse("hbm:textures/entity/glyphid_brawler.png");}
    @Override public double sourceScale(){return 1.25;}
    @Override public GlyphidStats.StatBundle getStats(){return GlyphidStats.getStats().getBrawler();}
    @Override public void tick(){super.tick();var target=legacyTarget;if(target!=null&&isAlive()){lastX=target.getX();lastY=target.getY();lastZ=target.getZ();if(--timer<=0){leap();timer=80+level().random.nextInt(30);}}}
    public void leap(){
        if(level().isClientSide||!(legacyTarget instanceof LivingEntity target)||distanceTo(target)>=20)return;
        double vx=target.getX()-lastX,vy=target.getY()-lastY,vz=target.getZ()-lastZ;if(lastTarget!=target)vx=vy=vz=0;
        var delta=LegacyVector.createVectorHelper(target.getX()-getX()+vx*60,target.getY()+target.getBbHeight()/2-(getY()+1)+vy*60,target.getZ()-getZ()+vz*60);
        if(delta.lengthVector()<3)return;double yaw=-Math.atan2(delta.xCoord,delta.zCoord),x=Math.sqrt(delta.xCoord*delta.xCoord+delta.zCoord*delta.zCoord),y=delta.yCoord,v=1.5,v2=v*v,g=.01;
        double pitch=Math.atan((v2+Math.sqrt(v2*v2-g*(g*x*x+2*y*v2)))/(g*x));
        if(!Double.isNaN(pitch)){var fire=LegacyVector.createVectorHelper(v,0,0);fire.rotateAroundZ((float)(-pitch/3.5));fire.rotateAroundY((float)-(yaw+Math.PI*.5));setThrowableHeading(fire.xCoord,fire.yCoord,fire.zCoord,(float)v,random.nextFloat());}
    }
    public void setThrowableHeading(double x,double y,double z,float velocity,float inaccuracy){float length=(float)Math.sqrt(x*x+y*y+z*z);x/=length;y/=length;z/=length;x+=random.nextGaussian()*.0075D*inaccuracy;y+=random.nextGaussian()*.0075D*inaccuracy;z+=random.nextGaussian()*.0075D*inaccuracy;x*=velocity;y*=velocity;z*=velocity;setDeltaMovement(x,y,z);float horizontal=(float)Math.sqrt(x*x+z*z);yRotO=(float)(Math.atan2(x,z)*180/Math.PI);xRotO=(float)(Math.atan2(y,horizontal)*180/Math.PI);setYRot(yRotO);setXRot(xRotO);}
    @Override public boolean hurt(DamageSource source,float amount){return source.is(DamageTypes.FALL)&&amount<=10?false:super.hurt(source,amount);}
    @Override public boolean isArmorBroken(float amount){return random.nextInt(100)<=Math.min(Math.pow(amount*.25,2),100);}
}

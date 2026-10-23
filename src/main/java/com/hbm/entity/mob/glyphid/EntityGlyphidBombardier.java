package com.hbm.entity.mob.glyphid;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.entity.projectile.EntityAcidBomb;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
/** Original20-tick target sample /60-tick ballistic salvo, without substituting generic arrows or a homing shot. */
public class EntityGlyphidBombardier extends EntityGlyphid {
    protected Entity lastTarget;protected double lastX,lastY,lastZ;
    public EntityGlyphidBombardier(EntityType<? extends EntityGlyphidBombardier> type,Level level){super(type,level);}
    @Override public ResourceLocation getSkin(){return ResourceLocation.parse("hbm:textures/entity/glyphid_bombardier.png");}
    @Override public GlyphidStats.StatBundle getStats(){return GlyphidStats.getStats().getBombardier();}
    @Override public void tick(){
        super.tick();if(level().isClientSide||!(legacyTarget instanceof LivingEntity target))return;
        if(tickCount%20==0){lastTarget=target;lastX=target.getX();lastY=target.getY();lastZ=target.getZ();}
        if(tickCount%60!=1)return;boolean topAttack=distanceTo(target)>20;
        double vx=target.getX()-lastX,vy=target.getY()-lastY,vz=target.getZ()-lastZ;if(lastTarget!=target||Math.sqrt(vx*vx+vy*vy+vz*vz)>30)vx=vy=vz=0;
        int prediction=topAttack?60:20;var delta=LegacyVector.createVectorHelper(target.getX()-getX()+vx*prediction,target.getY()+target.getBbHeight()/2-(getY()+1)+vy*prediction,target.getZ()-getZ()+vz*prediction);
        if(delta.lengthVector()<3)return;double yaw=-Math.atan2(delta.xCoord,delta.zCoord),x=Math.sqrt(delta.xCoord*delta.xCoord+delta.zCoord*delta.zCoord),y=delta.yCoord,v=getV0(),v2=v*v,g=.04;
        double pitch=Math.atan((v2+Math.sqrt(v2*v2-g*(g*x*x+2*y*v2))*(topAttack?1:-1))/(g*x));
        if(!Double.isNaN(pitch)){var fire=LegacyVector.createVectorHelper(v,0,0);fire.rotateAroundZ((float)-pitch);fire.rotateAroundY((float)-(yaw+Math.PI*.5));for(int i=0;i<getBombCount();i++){var bomb=new EntityAcidBomb(level(),getX(),getY()+1,getZ());bomb.setThrower(this);bomb.setThrowableHeading(fire.xCoord,fire.yCoord,fire.zCoord,(float)v,i*getSpreadMult());bomb.damage=getBombDamage();level().addFreshEntity(bomb);}sourceSwing();}
    }
    public float getBombDamage(){return 5;}public int getBombCount(){return 5;}public float getSpreadMult(){return 1;}public double getV0(){return 1;}
}

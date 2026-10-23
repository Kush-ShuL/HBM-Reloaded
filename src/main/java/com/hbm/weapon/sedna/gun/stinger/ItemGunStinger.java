package com.hbm.weapon.sedna.gun.stinger;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.weapon.sedna.gun.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Source ItemGunStinger state machine, including its absolute-world-coordinate
 * selection box, retained locked target, and deliberately unbounded progress. */
public final class ItemGunStinger extends ItemGunBaseNT {
    public static final String KEY_LOCKINGON="lockingon",KEY_LOCKONPROGRESS="lockonprogress";
    public static float prevLockon,lockon;
    public ItemGunStinger(WeaponQuality quality,GunConfig... configs){super(quality,configs);}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean held){
        super.inventoryTick(stack,level,entity,slot,held);
        if(!(entity instanceof Player player))return;
        if(!level.isClientSide&&!held&&getIsLockingOn(stack))setIsLockingOn(stack,false);
        prevLockon=lockon;
        if(!level.isClientSide){
            int previousTarget=getLockonTarget(stack);
            if(held&&getIsLockingOn(stack)&&getIsAiming(stack)&&getConfig(stack,0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack,new GunInventory(player))>0){
                int target=getLockonTarget(player,150D,10D);
                if(target==-1){if(!getIsLockedOn(stack))resetLockon(stack);}
                else{
                    if(!getIsLockedOn(stack)&&target!=previousTarget){resetLockon(stack);setLockonTarget(stack,target);}
                    setLockonProgress(stack,getLockonProgress(stack)+1);
                    if(getLockonProgress(stack)>=60&&!getIsLockedOn(stack)){
                        GunSounds.play(player,"hbm:item.techBleep",1F,1F);setIsLockedOn(stack,true);
                    }
                }
            }else resetLockon(stack);
        }else{
            if(getLockonProgress(stack)>1)lockon+=1F/60F;else lockon=0;
        }
    }
    public static void resetLockon(ItemStack stack){setLockonProgress(stack,0);setIsLockedOn(stack,false);}
    public static int getLockonTarget(Player player,double distance,double threshold){
        if(player==null)return -1;
        double x=player.getX(),y=player.getY()+player.getEyeHeight(),z=player.getZ();
        Vec3 delta=oldLook(player).scale(distance),look=delta.add(x,y,z),left=rotateY(look,-threshold).add(0,10,0),right=rotateY(look,threshold).add(0,-10,0),position=new Vec3(x,y,z);
        Vec3[] points={look,left,right,position};
        double minX=Double.POSITIVE_INFINITY,minY=minX,minZ=minX,maxX=Double.NEGATIVE_INFINITY,maxY=maxX,maxZ=maxX;
        for(var p:points){if(p.x<minX)minX=p.x;if(p.y<minY)minY=p.y;if(p.z<minZ)minZ=p.z;if(p.x>maxX)maxX=p.x;if(p.y>maxY)maxY=p.y;if(p.z>maxZ)maxZ=p.z;}
        Entity closest=null;double closestAngle=360D;
        var direction=LegacyVector.createVectorHelper(delta.x,delta.y,delta.z);
        for(var entity:player.level().getEntities(player,new AABB(minX,minY,minZ,maxX,maxY,maxZ))){
            if(entity.getBbHeight()<.5F||!entity.isPickable())continue;
            var to=LegacyVector.createVectorHelper(entity.getX()-x,entity.getY()+entity.getBbHeight()/2D-y,entity.getZ()-z);
            double dot=to.xCoord*delta.x+to.yCoord*delta.y+to.zCoord*delta.z;
            double bottom=to.lengthVector()*direction.lengthVector();
            double angle=Math.abs(Math.acos(dot/bottom)*180/Math.PI);
            if(angle<closestAngle&&angle<threshold){closestAngle=angle;closest=entity;}
        }
        return closest==null?-1:closest.getId();
    }
    /** Official1.7.10 EntityLivingBase.getLook(1F): preserve float trig and PI subtraction. */
    private static Vec3 oldLook(Player player){
        float cosine=Mth.cos(-player.getYRot()*.017453292F-(float)Math.PI),sine=Mth.sin(-player.getYRot()*.017453292F-(float)Math.PI);
        float horizontal=-Mth.cos(-player.getXRot()*.017453292F),vertical=Mth.sin(-player.getXRot()*.017453292F);
        return new Vec3(sine*horizontal,vertical,cosine*horizontal);
    }
    private static Vec3 rotateY(Vec3 point,double degrees){double angle=degrees/180D*Math.PI,c=Math.cos(angle),s=Math.sin(angle);return new Vec3(point.x*c+point.z*s,point.y,point.z*c-point.x*s);}
    public static boolean getIsLockingOn(ItemStack stack){return getValueBool(stack,KEY_LOCKINGON);}
    public static void setIsLockingOn(ItemStack stack,boolean value){setValueBool(stack,KEY_LOCKINGON,value);}
    public static int getLockonProgress(ItemStack stack){return getValueInt(stack,KEY_LOCKONPROGRESS);}
    public static void setLockonProgress(ItemStack stack,int value){setValueInt(stack,KEY_LOCKONPROGRESS,value);}
}

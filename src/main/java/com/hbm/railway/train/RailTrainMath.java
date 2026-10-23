package com.hbm.railway.train;
import com.hbm.railway.track.RailVector;
import net.minecraft.util.Mth;
/** Source table-trigonometry X rotation, using the sole original-vector representation from256. */
public final class RailTrainMath {
    public static RailVector vector(double x,double y,double z){return RailVector.createVectorHelper(x,y,z);}
    public static void rotateX(RailVector vector,float angle){float c=Mth.cos(angle),s=Mth.sin(angle);double y=vector.yCoord*c+vector.zCoord*s,z=vector.zCoord*c-vector.yCoord*s;vector.yCoord=y;vector.zCoord=z;}
    public static float yaw(RailVector front,RailVector back){return(float)Mth.wrapDegrees(-Math.atan2(front.xCoord-back.xCoord,front.zCoord-back.zCoord)*180D/Math.PI);}
    private RailTrainMath(){}
}

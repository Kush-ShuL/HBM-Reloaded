package com.hbm.client.weapon.legendary;
/** Original Vec3NT.rotateAroundZDeg, used to cumulatively articulate Sexy's actual ammunition belt. */
final class BeltVector {
    double xCoord,yCoord,zCoord;
    BeltVector(double x,double y,double z){xCoord=x;yCoord=y;zCoord=z;}
    BeltVector rotateAroundZDeg(double angle){double radians=angle/180D*Math.PI,c=Math.cos(radians),s=Math.sin(radians),x=xCoord*c+yCoord*s,y=yCoord*c-xCoord*s;xCoord=x;yCoord=y;return this;}
}

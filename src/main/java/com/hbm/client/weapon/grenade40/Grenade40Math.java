package com.hbm.client.weapon.grenade40;
final class Grenade40Math {static double interp(double x,double y,double partial){return x+(y-x)*partial;}}
/** Original Vec3NT clockwise Z rotation, used by the source moving MK108 belt. */
final class BeltVector {double xCoord,yCoord,zCoord;BeltVector(double x,double y,double z){xCoord=x;yCoord=y;zCoord=z;}void rotateAroundZDeg(double angle){double radians=angle/180D*Math.PI,cos=Math.cos(radians),sin=Math.sin(radians);double x=xCoord*cos+yCoord*sin,y=yCoord*cos-xCoord*sin;xCoord=x;yCoord=y;}}

package com.hbm.explosion;

/** Original unit-distance, double-coordinate replay from the final float tip.
 * Kept independent of world access so its exact sample set can be compared with
 * the original per-chunk loop without bootstrapping a second game client. */
final class OriginalRayReplay {
    int originX,originY,originZ,step,x,y,z,tipX,tipY,tipZ;
    double dx,dy,dz,distance;
    void reset(int originX,int originY,int originZ,float x,float y,float z){
        this.originX=originX;this.originY=originY;this.originZ=originZ;step=0;
        // The float-minus-int operations and float length are source semantics.
        double vx=x-originX,vy=y-originY,vz=z-originZ;
        distance=(double)(float)Math.sqrt(vx*vx+vy*vy+vz*vz);
        dx=vx/distance;dy=vy/distance;dz=vz/distance;
        tipX=(int)Math.floor(x);tipY=(int)Math.floor(y);tipZ=(int)Math.floor(z);
    }
    boolean next(){
        if(!(step<distance))return false;
        int i=step++;
        x=(int)Math.floor(originX+dx*i);y=(int)Math.floor(originY+dy*i);z=(int)Math.floor(originZ+dz*i);
        return true;
    }
    boolean isTip(){return x==tipX&&y==tipY&&z==tipZ;}

    /** First original sample outside one monotone empty span. Binary correction
     * also handles large-coordinate float rounding, where one-step padding around
     * an analytic crossing can otherwise jump over real nonair samples. */
    static int skipAir(int next,int limit,int ox,int oy,int oz,double dx,double dy,double dz,
            int chunkX,int chunkZ,int minY,int maxY,boolean floats){
        double exit=Math.min(axisExit(ox,chunkX,dx),axisExit(oz,chunkZ,dz));
        if(dy!=0)exit=Math.min(exit,((dy>0?maxY:minY)-oy)/dy);
        int guess=Double.isFinite(exit)?Math.max(next,Math.min(limit,(int)Math.floor(exit)-1)):next;
        int low=next,high=limit;
        if(guess<limit&&inside(guess,ox,oy,oz,dx,dy,dz,chunkX,chunkZ,minY,maxY,floats)){
            low=guess+1;
            if(low>=limit||!inside(low,ox,oy,oz,dx,dy,dz,chunkX,chunkZ,minY,maxY,floats))return low;
            low++;
        }else high=guess;
        while(low<high){
            int middle=low+(high-low)/2;
            if(inside(middle,ox,oy,oz,dx,dy,dz,chunkX,chunkZ,minY,maxY,floats))low=middle+1;
            else high=middle;
        }
        return low;
    }
    private static boolean inside(int step,int ox,int oy,int oz,double dx,double dy,double dz,
            int cx,int cz,int minY,int maxY,boolean floats){
        double xx=ox+dx*step,yy=oy+dy*step,zz=oz+dz*step;
        int x=(int)Math.floor(floats?(double)(float)xx:xx),y=(int)Math.floor(floats?(double)(float)yy:yy),z=(int)Math.floor(floats?(double)(float)zz:zz);
        return (x>>4)==cx&&(z>>4)==cz&&y>=minY&&y<maxY;
    }
    private static double axisExit(int origin,int section,double direction){
        return direction==0?Double.POSITIVE_INFINITY:((direction>0?(section+1)<<4:section<<4)-origin)/direction;
    }
}

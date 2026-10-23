package com.hbm.worldgen.deposit;

import java.util.Random;

/** The scalar NoiseGeneratorPerlin.func_151601_a path from official Minecraft1.7.10.
 * This is four Simplex octaves with decreasing frequency and increasing amplitude,
 * not the modern PerlinNoise noise used for vanilla ore veins. */
public final class LegacyPerlin2D {
    private final Simplex[] octaves;
    public LegacyPerlin2D(Random random,int count){octaves=new Simplex[count];for(int i=0;i<count;i++)octaves[i]=new Simplex(random);}
    public double sample(double x,double y){double result=0,scale=1;for(var octave:octaves){result+=octave.sample(x*scale,y*scale)/scale;scale/=2;}return result;}
    private static final class Simplex {
        private static final int[][] GRADIENTS={{1,1,0},{-1,1,0},{1,-1,0},{-1,-1,0},{1,0,1},{-1,0,1},{1,0,-1},{-1,0,-1},{0,1,1},{0,-1,1},{0,1,-1},{0,-1,-1}};
        private static final double ROOT_3=Math.sqrt(3);
        private final int[] permutation=new int[512];
        Simplex(Random random){
            // All three original coordinate offsets are consumed even though the
            // scalar sampling method does not use them. Removing these changes
            // the permutation and every later octave / deposited voxel.
            random.nextDouble();random.nextDouble();random.nextDouble();
            for(int i=0;i<256;i++)permutation[i]=i;
            for(int i=0;i<256;i++){int j=random.nextInt(256-i)+i,old=permutation[i];permutation[i]=permutation[j];permutation[j]=old;permutation[i+256]=permutation[i];}
        }
        private static int floor(double value){return value>0?(int)value:(int)value-1;}
        private static double dot(int[] gradient,double x,double y){return gradient[0]*x+gradient[1]*y;}
        double sample(double x,double y){
            double skew=.5*(ROOT_3-1),offset=(x+y)*skew;
            int cellX=floor(x+offset),cellY=floor(y+offset);
            double unskew=(3-ROOT_3)/6,unoffset=(cellX+cellY)*unskew;
            double originX=cellX-unoffset,originY=cellY-unoffset;
            double dx=x-originX,dy=y-originY;int stepX,stepY;
            if(dx>dy){stepX=1;stepY=0;}else{stepX=0;stepY=1;}
            double dx1=dx-stepX+unskew,dy1=dy-stepY+unskew,dx2=dx-1+2*unskew,dy2=dy-1+2*unskew;
            int ix=cellX&255,iy=cellY&255;
            int g0=permutation[ix+permutation[iy]]%12,g1=permutation[ix+stepX+permutation[iy+stepY]]%12,g2=permutation[ix+1+permutation[iy+1]]%12;
            double a=.5-dx*dx-dy*dy,n0;if(a<0)n0=0;else{a*=a;n0=a*a*dot(GRADIENTS[g0],dx,dy);}
            double b=.5-dx1*dx1-dy1*dy1,n1;if(b<0)n1=0;else{b*=b;n1=b*b*dot(GRADIENTS[g1],dx1,dy1);}
            double c=.5-dx2*dx2-dy2*dy2,n2;if(c<0)n2=0;else{c*=c;n2=c*c*dot(GRADIENTS[g2],dx2,dy2);}
            return 70*(n0+n1+n2);
        }
    }
}

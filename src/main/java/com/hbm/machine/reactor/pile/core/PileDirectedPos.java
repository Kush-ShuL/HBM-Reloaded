package com.hbm.machine.reactor.pile.core;
public record PileDirectedPos(int x,int y,int z,PileDirection dir){public int getX(){return x;}public int getY(){return y;}public int getZ(){return z;}public PileDirection getDir(){return dir;}public boolean compare(int x,int y,int z){return this.x==x&&this.y==y&&this.z==z;}}

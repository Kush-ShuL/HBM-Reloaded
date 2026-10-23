package com.hbm.client.pneumatic;
import java.util.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
/** Authored source boxes with the shared verified RenderBlocks UV projector. */
final class PneumaticQuads {
 static void box(List<BakedQuad> out,TextureAtlasSprite sprite,int omitted,int[] rotations,float x0,float y0,float z0,float x1,float y1,float z1){for(var side:Direction.values())if((omitted&(1<<side.get3DDataValue()))==0)face(out,sprite,side,rotations[side.get3DDataValue()],x0,y0,z0,x1,y1,z1);}
 static void face(List<BakedQuad> out,TextureAtlasSprite sprite,Direction side,int rotation,float x0,float y0,float z0,float x1,float y1,float z1){float[][] points=switch(side){case DOWN->new float[][]{{x0,y0,z1},{x1,y0,z1},{x0,y0,z0},{x1,y0,z0}};case UP->new float[][]{{x0,y1,z0},{x1,y1,z0},{x0,y1,z1},{x1,y1,z1}};case NORTH->new float[][]{{x1,y1,z0},{x0,y1,z0},{x1,y0,z0},{x0,y0,z0}};case SOUTH->new float[][]{{x0,y1,z1},{x1,y1,z1},{x0,y0,z1},{x1,y0,z1}};case WEST->new float[][]{{x0,y1,z0},{x0,y1,z1},{x0,y0,z0},{x0,y0,z1}};case EAST->new float[][]{{x1,y1,z1},{x1,y1,z0},{x1,y0,z1},{x1,y0,z0}};};int[] data=new int[32],order={0,2,3,1};int normal=(side.getStepX()*127&255)|((side.getStepY()*127&255)<<8)|((side.getStepZ()*127&255)<<16);for(int i=0;i<4;i++){float[] p=points[order[i]],uv=com.hbm.block.pipe.client.LegacyBoxUvs.project(side,p[0],p[1],p[2],x0,y0,z0,x1,y1,z1,rotation);int j=i*8;data[j]=Float.floatToRawIntBits(p[0]);data[j+1]=Float.floatToRawIntBits(p[1]);data[j+2]=Float.floatToRawIntBits(p[2]);data[j+3]=-1;data[j+4]=Float.floatToRawIntBits(sprite.getU(uv[0]));data[j+5]=Float.floatToRawIntBits(sprite.getV(uv[1]));data[j+7]=normal;}out.add(new BakedQuad(data,-1,side,sprite,true));}
}

package com.hbm.machine.reactor.zirnox.client;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
/** Source GUIElements polygon needles, including its asymmetric linear outline. */
final class ZirnoxGauges {
    static void radial(GuiGraphics graphics,float x,float y,double progress){float angle=(float)Math.toRadians(-Mth.clamp(progress,0,1)*270-45);float[][] points=rotate(new float[][]{{0,5},{1,-2},{-1,-2}},angle);polygon(graphics,x,y,scale(points,1.5F,false),0xff000000);polygon(graphics,x,y,points,0xff7f0000);}
    static void linear(GuiGraphics graphics,float x,float y,double progress){float[][] points={{0,-2},{-.75F,0},{-.75F,5},{.75F,5},{.75F,0}};x+=(float)Mth.clamp(progress,0,1)*14;polygon(graphics,x,y,scale(points,1.5F,true),0xff000000);polygon(graphics,x,y,points,0xff7f0000);}
    private static float[][] rotate(float[][] input,float angle){float c=(float)Math.cos(angle),s=(float)Math.sin(angle);float[][] result=new float[input.length][2];for(int i=0;i<input.length;i++){result[i][0]=input[i][0]*c+input[i][1]*s;result[i][1]=input[i][1]*c-input[i][0]*s;}return result;}
    private static float[][] scale(float[][] input,float factor,boolean linear){float[][] result=new float[input.length][2];for(int i=0;i<input.length;i++){result[i][0]=input[i][0]*factor;result[i][1]=input[i][1]*(linear&&(i==2||i==3)?1:factor);}return result;}
    private static void polygon(GuiGraphics graphics,float x,float y,float[][] vertices,int color){var buffer=graphics.bufferSource().getBuffer(RenderType.gui());var pose=graphics.pose().last();for(int i=1;i<vertices.length-1;i++)for(int index:new int[]{0,i,i+1,i+1})buffer.addVertex(pose,x+vertices[index][0],y+vertices[index][1],0).setColor(color);}
}

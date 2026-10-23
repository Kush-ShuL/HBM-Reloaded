package com.hbm.machine.reactor.client;
import net.minecraft.client.gui.GuiGraphics;
/** Original NumberDisplay's 5x4 single-pixel seven-segment geometry, right-aligned with 3px digit padding. */
public final class ReactorDigits {
    private static final int[] MASKS={0b1110111,0b0100100,0b1011101,0b1101101,0b0101110,0b1101011,0b1111011,0b0100101,0b1111111,0b1101111};
    public static void draw(GuiGraphics graphics,int x,int y,int value,int length,boolean blink){
        if(blink&&(System.currentTimeMillis()%1000>=500))return;String digits=Integer.toString(value);int gap=length-digits.length();
        for(int i=0;i<digits.length();i++){char ch=digits.charAt(i);int mask=ch>='0'&&ch<='9'?MASKS[ch-'0']:0b0001000;int left=x+(i+gap)*9;
            if((mask&1)!=0)segment(graphics,left+1,y,4,1);if((mask&2)!=0)segment(graphics,left,y+1,1,5);if((mask&4)!=0)segment(graphics,left+5,y+1,1,5);
            if((mask&8)!=0)segment(graphics,left+1,y+6,4,1);if((mask&16)!=0)segment(graphics,left,y+7,1,5);if((mask&32)!=0)segment(graphics,left+5,y+7,1,5);if((mask&64)!=0)segment(graphics,left+1,y+12,4,1);
        }
    }
    private static void segment(GuiGraphics graphics,int x,int y,int width,int height){graphics.fill(x,y,x+width,y+height,0xff08ff00);}
}

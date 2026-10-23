package com.hbm.client.reactorcontroller;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.reactor.client.ReactorDigits;
import com.hbm.machine.reactorcontroller.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Exact176x166 GUI, four edit boxes,3-pixel40-point graph and the existing true NumberDisplay geometry. */
public final class ReactorControllerScreen extends LegacyContainerScreen<ReactorControllerMenu> {
    private final EditBox[] fields=new EditBox[4];
    public ReactorControllerScreen(ReactorControllerMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/gui_reactor_control.png"),176,166);}
    private ReactorControllerEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof ReactorControllerEntity machine?machine:null;}
    @Override protected void init(){super.init();var machine=machine();for(int i=0;i<4;i++){var field=new EditBox(font,leftPos+35+30*(i%2),topPos+(i<2?38:49),26,7,Component.translatable("gui.hbm.reactor_remote.field."+i));field.setTextColor(0x08ff00);field.setTextColorUneditable(-1);field.setBordered(false);field.setMaxLength(i<2?3:4);fields[i]=addWidget(field);}fields[0].setValue(Integer.toString(machine==null?0:(int)machine.levelUpper));fields[1].setValue(Integer.toString(machine==null?0:(int)machine.levelLower));fields[2].setValue(Integer.toString(machine==null?0:(int)machine.heatUpper/50));fields[3].setValue(Integer.toString(machine==null?0:(int)machine.heatLower/50));}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,72,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var machine=machine();if(machine!=null){graphics.flush();RenderSystem.setShader(GameRenderer::getPositionColorShader);RenderSystem.lineWidth(3);var vertices=Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINE_STRIP,DefaultVertexFormat.POSITION_COLOR);for(int i=0;i<40;i++)vertices.addVertex(graphics.pose().last().pose(),leftPos+128+i,(float)(topPos+39+Mth.clamp(machine.getTargetLevel(machine.function,i*1250)/100*28,0,28)),0).setColor(0xff08ff00);BufferUploader.drawWithShader(vertices.buildOrThrow());RenderSystem.lineWidth(1);}int[] numbers=machine==null?new int[3]:machine.displayData();ReactorDigits.draw(graphics,leftPos+6,topPos+20,numbers[0],3,false);ReactorDigits.draw(graphics,leftPos+66,topPos+20,numbers[1],4,false);ReactorDigits.draw(graphics,leftPos+126,topPos+20,numbers[2],3,false);for(var field:fields)field.render(graphics,x,y,partial);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);double localX=x-leftPos,localY=y-topPos;if(localX>=33&&localX<91&&localY>59&&localY<=69){double[] values=new double[4];for(int i=0;i<4;i++){String text=fields[i].getValue();int value=text.matches("[0-9]+")?(int)Mth.clamp(Double.parseDouble(text),0,i<2?100:1000):0;fields[i].setValue(Integer.toString(value));values[i]=value*(i<2?1:50);}var tag=new CompoundTag();tag.putDouble("levelUpper",values[0]);tag.putDouble("levelLower",values[1]);tag.putDouble("heatUpper",values[2]);tag.putDouble("heatLower",values[3]);send(tag);return true;}for(int i=0;i<3;i++)if(localX>=7&&localX<29&&localY>37+i*11&&localY<=47+i*11){var tag=new CompoundTag();tag.putInt("function",i);send(tag);return true;}return handled;}
    private void send(CompoundTag tag){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(),1));PacketDistributor.sendToServer(new ReactorControllerPackets.Control(menu.containerId,tag));}
    @Override public boolean keyPressed(int key,int scan,int modifiers){for(var field:fields)if(field.isFocused()&&field.keyPressed(key,scan,modifiers))return true;return super.keyPressed(key,scan,modifiers);}
    @Override public boolean charTyped(char character,int modifiers){for(var field:fields)if(field.isFocused()&&field.charTyped(character,modifiers))return true;return super.charTyped(character,modifiers);}
}

package com.hbm.client.fluidcontrols;
import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.energy.ConnectionPriority;
import com.hbm.network.fluidcontrols.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
/** Source GUIPump standalone geometry, original widget pixels, no blur and one close-only settings packet. */
public final class PumpScreen extends LegacyScreen {
 private static final ResourceLocation WIDGETS=ResourceLocation.parse("hbm:textures/gui/network/source_widgets_1_7_10.png");
 private final PumpEntity pump;private EditBox throughput;private int pressure,priority,left;private boolean sent;
 public PumpScreen(PumpEntity pump){super(Component.empty());this.pump=pump;pressure=pump.tank.getPressure();priority=pump.priority.ordinal();}
 @Override protected void init(){font=LegacyFonts.get();left=width/2-150;String previous=throughput==null?Integer.toString(pump.bufferSize):throughput.getValue();throughput=new EditBox(font,left+4,106,82,8,Component.translatable("gui.hbm.fluid_pump.throughput"));throughput.setBordered(false);throughput.setMaxLength(5);throughput.setValue(previous);throughput.setTextColor(0xffe0e0e0);addWidget(throughput);}
 @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial){if(pump.isRemoved()){onClose();return;}renderBackground(g,mouseX,mouseY,partial);label(g,"throughput",left,80);label(g,"maximum",left,90);g.fill(left-1,99,left+91,121,0xffa0a0a0);g.fill(left,100,left+90,120,0xff000000);throughput.render(g,mouseX,mouseY,partial);label(g,"pressure",left+100,80);label(g,"priority",left+200,80);button(g,left+100,mouseX,mouseY,Component.literal(pressure+" PU"));button(g,left+200,mouseX,mouseY,Component.literal(ConnectionPriority.values()[priority].name()));}
 private void label(GuiGraphics g,String key,int x,int y){g.drawString(font,Component.translatable("gui.hbm.fluid_pump."+key),x,y,0xa0a0a0,true);}
 private boolean over(double x,double y,int at){return x>=at&&x<at+90&&y>=100&&y<120;}
 private void button(GuiGraphics g,int x,int mouseX,int mouseY,Component text){boolean hover=over(mouseX,mouseY,x);int v=hover?86:66;g.blit(WIDGETS,x,100,0F,v,45,20,256,256);g.blit(WIDGETS,x+45,100,155F,v,45,20,256,256);g.drawString(font,text,x+45-font.width(text)/2,106,hover?16777120:14737632,true);}
 @Override public boolean mouseClicked(double x,double y,int button){boolean field=over(x,y,left);throughput.setFocused(field);if(field){setFocused(throughput);return throughput.mouseClicked(x,Math.max(106,Math.min(113,y)),button);}setFocused(null);if(button==0&&(over(x,y,left+100)||over(x,y,left+200))){if(over(x,y,left+100))pressure=(pressure+1)%6;else priority=(priority+1)%ConnectionPriority.values().length;minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));return true;}return false;}
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(throughput.keyPressed(key,scan,modifiers))return true;if(key==GLFW.GLFW_KEY_ESCAPE||minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return false;}
 @Override public boolean charTyped(char character,int modifiers){return throughput.charTyped(character,modifiers);}
 @Override public void removed(){super.removed();if(sent||throughput==null)return;sent=true;var data=new CompoundTag();data.putByte("pressure",(byte)pressure);data.putByte("priority",(byte)priority);try{data.putInt("capacity",Integer.parseInt(throughput.getValue()));}catch(NumberFormatException ignored){}PacketDistributor.sendToServer(new FluidControlsPackets(pump.getBlockPos(),data));}
 @Override public boolean isPauseScreen(){return false;}
}

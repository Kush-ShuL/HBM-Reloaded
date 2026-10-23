package com.hbm.client.reactor.rbmk.control;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.reactor.rbmk.control.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Both source176x186 screens, exact original UV/button/text-field layout; shared current font. */
public final class RbmkControlScreen extends LegacyContainerScreen<RbmkControlMenu> {
    private final EditBox[] fields=new EditBox[4];
    public RbmkControlScreen(RbmkControlMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_rbmk_control.png"),176,186);}
    private RbmkControlEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof RbmkControlEntity control?control:null;}
    private ResourceLocation texture(){var control=machine();return ResourceLocation.parse("hbm:textures/gui/reactors/gui_rbmk_control"+(control!=null&&control.automatic()?"_auto":"")+".png");}
    @Override protected void init(){super.init();var control=machine();if(control==null||!control.automatic())return;double[] values={control.levelUpper,control.levelLower,control.heatUpper,control.heatLower};for(int i=0;i<4;i++){fields[i]=new EditBox(font,leftPos+30,topPos+27+11*i,26,6,Component.empty());fields[i].setTextColor(-1);fields[i].setTextColorUneditable(-1);fields[i].setBordered(false);fields[i].setMaxLength(i<2?3:4);fields[i].setValue(""+(int)values[i]);addRenderableWidget(fields[i]);}}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
    private void region(GuiGraphics g,int x,int y,int u,int v,int w,int h){drawTexturedModalRect(g,texture(),leftPos+x,topPos+y,u,v,w,h,256,256);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){g.setColor(1,1,1,1);region(g,0,0,0,0,imageWidth,imageHeight);var c=machine();if(c==null)return;int height=(int)(56*(1-c.extraction));if(height>0)region(g,c.automatic()?124:75,29,176,56-height,8,height);if(c.automatic())region(g,59,27,184,c.function.ordinal()*19,26,19);else if(c.color!=null)region(g,28,26+c.color.ordinal()*11,184,c.color.ordinal()*10,12,10);if(c.powered())region(g,c.automatic()?136:87,21,c.automatic()?210:196,c.hasPower?16:0,16,16);}
    private void tooltip(GuiGraphics g,int x,int y,int rx,int ry,int w,int h,String...keys){if(!legacyInfoHovered(x,y,rx,ry,w,h))return;g.renderComponentTooltip(font,Arrays.stream(keys).map(Component::translatable).map(c->(Component)c).toList(),x,y);}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var c=machine();if(c==null)return;if(legacyInfoHovered(x,y,c.automatic()?124:71,29,16,56))g.renderTooltip(font,Component.literal((int)(c.extraction*100)+"%"),x,y);if(c.powered()&&legacyInfoHovered(x,y,c.automatic()?136:87,21,16,16))g.renderTooltip(font,Component.literal(c.getPowerExact()+" / 50000HE"),x,y);if(!c.automatic())return;if(legacyInfoHovered(x,y,58,26,28,19))g.renderTooltip(font,Component.translatable("gui.hbm.rbmk.control.function",Component.translatable("gui.hbm.rbmk.control.function."+c.function.ordinal())),x,y);for(int i=0;i<3;i++)tooltip(g,x,y,61,48+i*11,22,10,"gui.hbm.rbmk.control.select."+i);for(int i=0;i<4;i++)tooltip(g,x,y,28,26+i*11,30,10,"gui.hbm.rbmk.control.field."+i,"gui.hbm.rbmk.control.field."+i+".note");tooltip(g,x,y,28,70,30,10,"gui.hbm.rbmk.control.save");}
    private void send(int action,int value,double a,double b,double c,double d){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new RbmkControlPacket(menu.containerId,action,value,a,b,c,d));}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);var c=machine();if(c==null)return handled;if(!c.automatic()){for(int i=0;i<5;i++){if(legacyInfoHovered(x,y,118,26+i*11,30,10)){send(0,0,1-i*.25,0,0,0);return true;}if(legacyInfoHovered(x,y,28,26+i*11,12,10)){send(1,i,0,0,0,0);return true;}}}else{if(legacyInfoHovered(x,y,28,70,30,10)){double[] values=new double[4];for(int i=0;i<4;i++){try{double value=Double.parseDouble(fields[i].getValue());values[i]=Double.isFinite(value)?(int)Mth.clamp(value,0,i<2?100:9999):0;}catch(NumberFormatException error){values[i]=0;}fields[i].setValue(""+(int)values[i]);}send(3,0,values[0],values[1],values[2],values[3]);return true;}for(int i=0;i<3;i++)if(legacyInfoHovered(x,y,61,48+i*11,22,10)){send(2,i,0,0,0,0);return true;}}return handled;}
    @Override public boolean keyPressed(int key,int scan,int mods){for(var field:fields)if(field!=null&&field.isFocused()&&field.keyPressed(key,scan,mods))return true;return super.keyPressed(key,scan,mods);}
    @Override public boolean charTyped(char value,int mods){for(var field:fields)if(field!=null&&field.isFocused()&&field.charTyped(value,mods))return true;return super.charTyped(value,mods);}
}

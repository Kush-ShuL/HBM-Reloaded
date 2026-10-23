package com.hbm.client.reactor.rbmk.panels;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.reactor.rbmk.panels.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.EditBox;
import com.hbm.client.gui.LegacyScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
/** Exact free-standing source config-screen shell and green unbordered fields using Minecraft's actual font. */
public abstract class RbmkRadioPanelScreen<T extends RbmkRadioPanelEntity>extends LegacyScreen{
    protected final T panel;protected final ResourceLocation texture;protected final int imageWidth,imageHeight;protected int left,top;
    protected RbmkRadioPanelScreen(T panel,String title,String texture,int width,int height){super(Component.translatable(title));this.panel=panel;this.texture=ResourceLocation.parse(texture);imageWidth=width;imageHeight=height;}
    @Override protected void init(){font=LegacyFonts.get();left=(width-imageWidth)/2;top=(height-imageHeight)/2;}
    protected EditBox field(int x,int y,int width,int maximum,String value){var field=new EditBox(font,left+x,top+y,width,14,Component.empty());field.setTextColor(0x00ff00);field.setTextColorUneditable(0x00ff00);field.setBordered(false);field.setMaxLength(maximum);field.setValue(value==null?"":value);addRenderableWidget(field);return field;}
    protected boolean hover(double x,double y,int rx,int ry,int width,int height){return left+rx<=x&&left+rx+width>x&&top+ry<y&&top+ry+height>=y;}
    protected void region(GuiGraphics graphics,int x,int y,int u,int v,int width,int height){graphics.blit(texture,left+x,top+y,u,v,width,height,256,256);}
    protected void sound(float pitch){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,pitch));}
    protected void send(CompoundTag tag){PacketDistributor.sendToServer(new RbmkRadioPanelNetwork.Control(panel.getBlockPos(),tag));}
    protected abstract void options(GuiGraphics graphics);
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){renderBackground(graphics,x,y,partial);region(graphics,0,0,0,0,imageWidth,imageHeight);options(graphics);graphics.drawString(font,title,left+imageWidth/2-font.width(title)/2,top+6,4210752,false);super.render(graphics,x,y,partial);}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(super.keyPressed(key,scan,modifiers))return true;if(key==256||minecraft.options.keyInventory.matches(key,scan)&&!(getFocused() instanceof EditBox)){onClose();return true;}return false;}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(!handled)setFocused(null);return handled;}@Override public boolean isPauseScreen(){return false;}
}

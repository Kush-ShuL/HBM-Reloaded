package com.hbm.world.structure.templates.radio.client;
import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.world.structure.templates.radio.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
/** Actual GUIRadioRec: unchanged220x42 region, source text field position/length and the two original18px hitboxes. */
public final class RadioReceiverScreen extends LegacyScreen {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/machine/gui_radio.png");
    private final RadioReceiverBlockEntity radio;private int left,top;private EditBox frequency;
    public RadioReceiverScreen(RadioReceiverBlockEntity radio){super(Component.translatable("container.radiorec"));this.radio=radio;}
    @Override protected void init(){font=LegacyFonts.get();left=(width-220)/2;top=(height-42)/2;frequency=new EditBox(font,left+29,top+21,82,14,Component.translatable("gui.hbm.radio.frequency"));frequency.setBordered(false);frequency.setTextColor(0x00ff00);frequency.setTextColorUneditable(0x00ff00);frequency.setMaxLength(10);frequency.setValue(radio.channel==null?"":radio.channel);frequency.setFocused(false);addWidget(frequency);}
    private boolean hovered(double x,double y,int ox){return left+ox<=x&&x<left+ox+18&&top+17<y&&y<=top+35;}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        renderBackground(graphics,mouseX,mouseY,partial);graphics.blit(TEXTURE,left,top,0F,0F,220,42,256,256);if(radio.isOn)graphics.blit(TEXTURE,left+173,top+17,0F,42F,18,18,256,256);
        // Source GUI never calls updateCursorCounter, so its focused cursor stays at phase0.
        if(frequency.isFocused())frequency.setFocused(true);frequency.render(graphics,mouseX,mouseY,partial);graphics.drawString(font,title,left+110-font.width(title)/2,top+6,4210752,false);
        if(hovered(mouseX,mouseY,137))graphics.renderTooltip(font,Component.translatable("gui.hbm.radio.save_settings"),mouseX,mouseY);if(hovered(mouseX,mouseY,173))graphics.renderTooltip(font,Component.translatable("gui.hbm.radio.toggle"),mouseX,mouseY);
    }
    @Override public boolean mouseClicked(double x,double y,int button){boolean field=frequency.mouseClicked(x,y,button);setFocused(frequency.isFocused()?frequency:null);if(hovered(x,y,137)){click();PacketDistributor.sendToServer(new RadioReceiverControl(radio.getBlockPos(),true,frequency.getValue(),false));return true;}if(hovered(x,y,173)){click();PacketDistributor.sendToServer(new RadioReceiverControl(radio.getBlockPos(),false,"",!radio.isOn));return true;}return field;}
    private void click(){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));}
    @Override public boolean charTyped(char character,int modifiers){return frequency.charTyped(character,modifiers);}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(frequency.keyPressed(key,scan,modifiers))return true;if(key==GLFW.GLFW_KEY_ESCAPE||!frequency.isFocused()&&minecraft.options.keyInventory.matches(key,scan)){onClose();return true;}return false;}
    @Override public boolean isPauseScreen(){return false;}
}

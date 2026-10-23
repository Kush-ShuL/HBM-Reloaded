package com.hbm.client.reactor.rbmk.panels.terminal;
import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.reactor.rbmk.panels.RbmkRadioPanelNetwork;
import com.hbm.machine.reactor.rbmk.panels.terminal.RbmkTerminalEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
/** Source invisible50-characterinput: actual text is rendered on the world terminal, with six half-size help lines. */
public final class RbmkTerminalScreen extends LegacyScreen{
    public static RbmkTerminalEntity lastTerminal;private EditBox line;
    public RbmkTerminalScreen(RbmkTerminalEntity terminal){super(Component.translatable("block.hbm.rbmk_terminal"));lastTerminal=terminal;}
    @Override protected void init(){font=LegacyFonts.get();line=new EditBox(font,0,0,0,0,Component.empty());line.setMaxLength(50);line.setFocused(true);line.setCanLoseFocus(false);addWidget(line);setFocused(line);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){if(lastTerminal.isRemoved()){onClose();return;}graphics.pose().pushPose();graphics.pose().scale(.5F,.5F,1);for(int i=0;i<6;i++)graphics.drawString(font,Component.translatable("gui.hbm.rbmk_terminal.help."+i),2,2+10*i,0xffffff,false);graphics.pose().popPose();}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(key==GLFW.GLFW_KEY_ESCAPE){onClose();return true;}if(key==GLFW.GLFW_KEY_ENTER){var tag=new CompoundTag();tag.putString("cmd",line.getValue());PacketDistributor.sendToServer(new RbmkRadioPanelNetwork.Control(lastTerminal.getBlockPos(),tag));line.setValue("");return true;}if(key==GLFW.GLFW_KEY_HOME||key==GLFW.GLFW_KEY_LEFT||key==GLFW.GLFW_KEY_RIGHT||key==GLFW.GLFW_KEY_END)return true;line.moveCursorToEnd(false);return line.keyPressed(key,scan,modifiers);}
    @Override public boolean charTyped(char typed,int modifiers){line.moveCursorToEnd(false);return line.charTyped(typed,modifiers);}
    public static String workingLine(RbmkTerminalEntity terminal){return terminal==lastTerminal&&Minecraft.getInstance().screen instanceof RbmkTerminalScreen screen?screen.line.getValue():"";}
    @Override public boolean isPauseScreen(){return false;}
}

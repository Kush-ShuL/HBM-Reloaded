package com.hbm.machine.reactor.client;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.reactor.*;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Original176x222 research GUI, slots, seven-segment instruments and mechanical target switch. */
public final class ResearchReactorScreen extends LegacyContainerScreen<ResearchReactorMenu> {
    private EditBox field;private byte timer;
    private static final ResourceLocation UTILITY=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/gui_utility.png");
    public ResearchReactorScreen(ResearchReactorMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/reactors/gui_research_reactor.png"),176,222);}
    private ResearchReactorBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof ResearchReactorBlockEntity be?be:null;}
    @Override protected void init(){super.init();field=new EditBox(font,leftPos+8,topPos+99,33,16,Component.translatable("gui.hbm.reactor.control"));field.setBordered(false);field.setMaxLength(3);var reactor=machine();field.setValue(Integer.toString(reactor==null?0:(int)(reactor.rodLevel*100)));addWidget(field);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,121-font.width(title)/2,6,15066597,false);graphics.drawString(font,playerInventoryTitle,8,128,4210752,false);graphics.drawString(font,Component.translatable("gui.hbm.reactor.flux"),6,13,15066597,false);graphics.drawString(font,Component.translatable("gui.hbm.reactor.heat"),6,51,15066597,false);graphics.drawString(font,Component.translatable("gui.hbm.reactor.control"),6,89,15066597,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var reactor=machine();if(reactor!=null&&reactor.rodLevel<=.5){for(int cx=0;cx<3;cx++)for(int cy=0;cy<3;cy++)drawLegacyRegion(graphics,81+36*cx,26+36*cy,176,0,8,8);for(int cx=0;cx<2;cx++)for(int cy=0;cy<2;cy++)drawLegacyRegion(graphics,99+36*cx,44+36*cy,176,0,8,8);}if(timer>0){drawLegacyRegion(graphics,44,97,176,8,11,20);timer--;}
        ReactorDigits.draw(graphics,leftPos+14,topPos+25,reactor==null?0:reactor.totalFlux,4,false);ReactorDigits.draw(graphics,leftPos+12,topPos+63,reactor==null?20:reactor.temperature(),3,false);ReactorDigits.draw(graphics,leftPos+5,topPos+101,controlValue(),3,field.isFocused());
        graphics.blit(UTILITY,leftPos-14,topPos+23,24,0,16,16);graphics.blit(UTILITY,leftPos-14,topPos+61,8,0,16,16);
    }
    private int controlValue(){try{if(!field.getValue().matches("[0-9]+")){field.setValue("0");return 0;}int value=Math.max(0,Math.min(100,Integer.parseInt(field.getValue())));String text=Integer.toString(value);if(!text.equals(field.getValue()))field.setValue(text);return value;}catch(NumberFormatException ignored){field.setValue("0");return 0;}}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);if(legacyInfoHovered(x,y,-14,23,16,16))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.reactor.submerged.1"),Component.translatable("gui.hbm.reactor.submerged.2"),Component.translatable("gui.hbm.reactor.submerged.3"),Component.translatable("gui.hbm.reactor.submerged.4")),x,y);if(legacyInfoHovered(x,y,-14,61,16,16))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.reactor.plate.1"),Component.translatable("gui.hbm.reactor.plate.2")),x,y);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,44,97,11,20)){PacketDistributor.sendToServer(new ReactorControlPacket(menu.containerId,controlValue()*.01D));timer=15;minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("hbm","block.rbmk_az5_cover")),.5F));return true;}return handled;}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(field.isFocused()&&key!=org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE){field.keyPressed(key,scan,modifiers);return true;}return super.keyPressed(key,scan,modifiers);}
    @Override public boolean charTyped(char character,int modifiers){return field.isFocused()&&field.charTyped(character,modifiers)||super.charTyped(character,modifiers);}
}

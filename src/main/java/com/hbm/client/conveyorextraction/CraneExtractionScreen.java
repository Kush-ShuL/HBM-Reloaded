package com.hbm.client.conveyorextraction;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.conveyorextraction.*;
import com.hbm.pneumatic.PatternMatcher;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Exact source 212x185 extractor and 176x185 inserter, source buttons and modern selected font. */
public final class CraneExtractionScreen extends LegacyContainerScreen<CraneExtractionMenu> {
    public CraneExtractionScreen(CraneExtractionMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/storage/gui_crane_"+(menu.extractor?"ejector":"inserter")+".png"),menu.extractor?212:176,185);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){var name=menu.crane.getDisplayName();graphics.drawString(font,name,imageWidth/2-font.width(name)/2-(menu.extractor?0:18),menu.extractor?6:5,4210752,false);graphics.drawString(font,playerInventoryTitle,menu.extractor?26:8,91,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);if(menu.crane instanceof CraneExtractorEntity crane){if(crane.maxEject)drawLegacyRegion(graphics,187,34,212,0,18,18);drawLegacyRegion(graphics,139,crane.isWhitelist?33:47,212,18,3,6);}else if(((CraneInserterEntity)menu.crane).destroyer)drawLegacyRegion(graphics,151,34,176,0,18,18);}
    private boolean buttonHovered(double x,double y,int left,int top,int width,int height){return leftPos+left<=x&&leftPos+left+width>x&&topPos+top<y&&topPos+top+height>=y;}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);int action=-1;if(menu.extractor){if(buttonHovered(x,y,187,34,18,18))action=1;else if(buttonHovered(x,y,128,30,14,26))action=0;}else if(buttonHovered(x,y,151,34,18,18))action=2;if(action>=0){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));PacketDistributor.sendToServer(new CraneExtractionControl(menu.crane.getBlockPos(),action));return true;}return handled;}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);if(menu.crane instanceof CraneExtractorEntity crane){if(menu.getCarried().isEmpty())for(int i=0;i<9;i++){var slot=menu.slots.get(i);if(legacyInfoHovered(x,y,slot.x,slot.y,16,16)&&crane.matcher.modes[i]!=null)graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.pneumatic.change_mode").withStyle(ChatFormatting.RED),PatternMatcher.label(crane.matcher.modes[i])),x,y-30);}if(buttonHovered(x,y,187,34,18,18))graphics.renderTooltip(font,Component.translatable("gui.hbm.crane_extractor.maximum",status(crane.maxEject)),x,y);}else if(buttonHovered(x,y,151,34,18,18))graphics.renderTooltip(font,Component.translatable("gui.hbm.crane_inserter.destroy_overflow",status(((CraneInserterEntity)menu.crane).destroyer)),x,y);}
    private static Component status(boolean value){return Component.translatable(value?"gui.hbm.crane_extraction.on":"gui.hbm.crane_extraction.off").withStyle(value?ChatFormatting.GREEN:ChatFormatting.RED);}
}

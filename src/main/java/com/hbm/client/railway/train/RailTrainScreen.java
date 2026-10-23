package com.hbm.client.railway.train;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.railway.train.*;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x204/222cargo GUI, positions, gauge and battery indicator; no added controls. */
public final class RailTrainScreen extends LegacyContainerScreen<RailTrainMenu> {
    public RailTrainScreen(RailTrainMenu menu,Inventory inventory,Component title){super(menu,inventory,title,RailTrainContent.id("textures/gui/vehicles/gui_cargo_tram"+(menu.trailer?"_trailer":"")+".png"),176,menu.trailer?222:204);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,(menu.trailer?176:140)/2-font.width(title)/2,6,0xffffff,false);graphics.drawString(font,playerInventoryTitle,8,imageHeight-94,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);if(menu.trailer)return;var train=menu.entity();int power=train==null?0:train.getPower(),height=power*53/1000;drawLegacyRegion(graphics,152,70-height,176,52-height,16,height);if(power>10)drawLegacyRegion(graphics,156,4,176,52,9,12);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);if(!menu.trailer&&x>=leftPos+152&&x<leftPos+168&&y>=topPos+18&&y<topPos+70){var train=menu.entity();graphics.renderComponentTooltip(font,List.of(Component.translatable("tooltip.hbm.source_train.power",train==null?0:train.getPower(),1000)),x,y);}}
}

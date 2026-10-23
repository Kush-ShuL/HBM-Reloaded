package com.hbm.client.network.cargo.request;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.network.cargo.request.*;
import com.hbm.pneumatic.PatternMatcher;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
public final class CargoRequestScreen extends LegacyContainerScreen<CargoRequestMenu> {
 public CargoRequestScreen(CargoRequestMenu menu,Inventory inv,Component title){super(menu,inv,title,ResourceLocation.parse("hbm:textures/gui/storage/gui_drone_"+switch(menu.kind){case DOCK->"dock";case PROVIDER->"provider";case REQUESTER->"requester";}+".png"),176,menu.kind==CargoRequestContent.Kind.DOCK?185:186);}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,imageHeight-94,4210752,false);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);if(menu.kind==CargoRequestContent.Kind.REQUESTER&&menu.getCarried().isEmpty()&&menu.tile instanceof DroneRequesterEntity requester)for(int i=0;i<9;i++){var slot=menu.slots.get(i);var mode=requester.matcher.modes[i];if(mode!=null&&x>=leftPos+slot.x-1&&x<leftPos+slot.x+17&&y>=topPos+slot.y-1&&y<topPos+slot.y+17)g.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.drone.filter_change").withStyle(ChatFormatting.RED),PatternMatcher.label(mode)),x,y-30);}}
}

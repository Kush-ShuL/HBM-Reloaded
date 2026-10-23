package com.hbm.client.bomb.nuclear;
import com.hbm.bomb.nuclear.NuclearAssembly;
import com.hbm.bomb.nuclear.NuclearAssemblyMenu;
import com.hbm.client.gui.LegacyContainerScreen;
import java.util.Arrays;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Generated from each active original GUI background method; source rectangles retain their order. */
public final class NuclearAssemblyScreen extends LegacyContainerScreen<NuclearAssemblyMenu> {
 private static final ResourceLocation UTILITY=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/gui_utility.png");
 public NuclearAssemblyScreen(NuclearAssemblyMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.fromNamespaceAndPath("hbm",menu.assembly().definition().gui().texture()),menu.assembly().definition().gui().width(),menu.assembly().definition().gui().height());}
 @Override protected void renderLabels(GuiGraphics graphics,int x,int y){var spec=menu.assembly().definition();graphics.drawString(font,title,imageWidth/2-font.width(title)/2,spec.gui().titleY(),4210752,false);graphics.drawString(font,playerInventoryTitle,spec.gui().playerX(),imageHeight-94,4210752,false);}
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);String info=menu.assembly().definition().gui().infoKey();if(info!=null&&legacyInfoHovered(x,y,-16,16,16,16)){var lines=Arrays.stream(Component.translatable(info).getString().split("\\$")).map(Component::literal).map(Component::getVisualOrderText).toList();graphics.renderTooltip(font,lines,leftPos-8,topPos+32);}}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){NuclearAssembly assembly=menu.assembly();ResourceLocation current=legacyBackground;
 switch(assembly.definition().kind()){
case "GADGET" -> {
		graphics.setColor(1,1,1,1);
		current=legacyBackground;
		drawTexturedModalRect(graphics,current,leftPos, topPos, 0, 0, imageWidth, imageHeight,256,256);
		
		if(assembly.matches(1,"early_explosive_lenses"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 82, topPos + 19, 176, 0, 24, 24,256,256);
		}
		
		if(assembly.matches(2,"early_explosive_lenses"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 106, topPos + 19, 200, 0, 24, 24,256,256);
		}
		
		if(assembly.matches(3,"early_explosive_lenses"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 82, topPos + 43, 176, 24, 24, 24,256,256);
		}
		
		if(assembly.matches(4,"early_explosive_lenses"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 106, topPos + 43, 200, 24, 24, 24,256,256);
		}

		if(assembly.isReady())
		{
			drawTexturedModalRect(graphics,current,leftPos + 134, topPos + 35, 176, 48, 16, 16,256,256);
		}
		
		drawTexturedModalRect(graphics,UTILITY,leftPos-16,topPos+16,8,0,16,16,256,256);
	}
case "BOY" -> {
		graphics.setColor(1,1,1,1);
		current=legacyBackground;
		drawTexturedModalRect(graphics,current,leftPos, topPos, 0, 0, imageWidth, imageHeight,256,256);

		if(assembly.isReady())
		{
			drawTexturedModalRect(graphics,current,leftPos + 142, topPos + 90, 176, 0, 16, 16,256,256);
		}

		if(!assembly.getItem(0).isEmpty() && assembly.matches(0,"boy_shielding"))
			drawTexturedModalRect(graphics,current,leftPos + 27, topPos + 87, 176, 16, 21, 22,256,256);
		if(!assembly.getItem(1).isEmpty() && assembly.matches(1,"boy_target"))
			drawTexturedModalRect(graphics,current,leftPos + 27, topPos + 89, 176, 38, 21, 18,256,256);
		if(!assembly.getItem(2).isEmpty() && assembly.matches(2,"boy_bullet"))
			drawTexturedModalRect(graphics,current,leftPos + 74, topPos + 94, 176, 57, 19, 8,256,256);
		if(!assembly.getItem(3).isEmpty() && assembly.matches(3,"boy_propellant"))
			drawTexturedModalRect(graphics,current,leftPos + 92, topPos + 95, 176, 66, 12, 6,256,256);
		if(!assembly.getItem(4).isEmpty() && assembly.matches(4,"boy_igniter"))
			drawTexturedModalRect(graphics,current,leftPos + 107, topPos + 91, 176, 75, 16, 14,256,256);
		
		drawTexturedModalRect(graphics,UTILITY,leftPos-16,topPos+16,8,0,16,16,256,256);
	}
case "MAN" -> {
		graphics.setColor(1,1,1,1);
		current=legacyBackground;
		drawTexturedModalRect(graphics,current,leftPos, topPos, 0, 0, imageWidth, imageHeight,256,256);
		
		if(assembly.matches(1,"early_explosive_lenses"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 82, topPos + 19, 176, 0, 24, 24,256,256);
		}
		
		if(assembly.matches(2,"early_explosive_lenses"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 106, topPos + 19, 200, 0, 24, 24,256,256);
		}
		
		if(assembly.matches(3,"early_explosive_lenses"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 82, topPos + 43, 176, 24, 24, 24,256,256);
		}
		
		if(assembly.matches(4,"early_explosive_lenses"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 106, topPos + 43, 200, 24, 24, 24,256,256);
		}

		if(assembly.isReady())
		{
			drawTexturedModalRect(graphics,current,leftPos + 134, topPos + 35, 176, 48, 16, 16,256,256);
		}
		
		drawTexturedModalRect(graphics,UTILITY,leftPos-16,topPos+16,8,0,16,16,256,256);
	}
case "MIKE" -> {
		graphics.setColor(1,1,1,1);
		current=legacyBackground;
		drawTexturedModalRect(graphics,current,leftPos, topPos, 0, 0, imageWidth, imageHeight,256,256);

		if(assembly.isReady() && !assembly.isFilled())
		{
			drawTexturedModalRect(graphics,current,leftPos + 5, topPos + 35, 177, 1, 16, 16,256,256);
		}

		if(assembly.isReady() && assembly.isFilled())
		{
			drawTexturedModalRect(graphics,current,leftPos + 5, topPos + 35, 177, 19, 16, 16,256,256);
		}
		
		if(!assembly.getItem(5).isEmpty() && assembly.matches(5,"mike_core"))
			drawTexturedModalRect(graphics,current,leftPos + 75, topPos + 25, 176, 49, 80, 36,256,256);
		
		if(!assembly.getItem(6).isEmpty() && assembly.matches(6,"mike_deut"))
			drawTexturedModalRect(graphics,current,leftPos + 79, topPos + 30, 180, 88, 58, 26,256,256);
		
		if(!assembly.getItem(7).isEmpty() && assembly.matches(7,"mike_cooling_unit"))
			drawTexturedModalRect(graphics,current,leftPos + 140, topPos + 30, 240, 88, 12, 26,256,256);
		
		for(int i = 0; i < 4; i++) {
			if(!assembly.getItem(i).isEmpty() && assembly.matches(i,"explosive_lenses"))
				switch(i) {
				case 0: drawTexturedModalRect(graphics,current,leftPos + 24, topPos + 20 , 209, 1, 23, 23,256,256); break;
				case 2: drawTexturedModalRect(graphics,current,leftPos + 47, topPos + 20 , 232, 1, 23, 23,256,256); break;
				case 1: drawTexturedModalRect(graphics,current,leftPos + 24, topPos + 43 , 209, 24, 23, 23,256,256); break;
				case 3: drawTexturedModalRect(graphics,current,leftPos + 47, topPos + 43 , 232, 24, 23, 23,256,256); break;
				}
		}
		
		drawTexturedModalRect(graphics,UTILITY,leftPos-16,topPos+16,8,0,16,16,256,256);
	}
case "TSAR" -> {
		graphics.setColor(1,1,1,1);
		current=legacyBackground;
		drawTexturedModalRect(graphics,current,leftPos, topPos, 0, 0, imageWidth, imageHeight,256,256);
		
		current=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/weapon/ivymikeschematic.png");

		if(assembly.isFilled()) drawTexturedModalRect(graphics,current,leftPos + 18, topPos + 50, 176, 18, 16, 16,256,256);
		else if(assembly.isReady()) drawTexturedModalRect(graphics,current,leftPos + 18, topPos + 50, 176, 0, 16, 16,256,256);

		for(int i = 0; i < 4; i++) {
			if(!assembly.getItem(i).isEmpty() && assembly.matches(i,"explosive_lenses")) switch(i) {
				case 0: drawTexturedModalRect(graphics,current,leftPos + 24 + 16, topPos + 20 + 16, 209, 1, 23, 23,256,256); break;
				case 2: drawTexturedModalRect(graphics,current,leftPos + 47 + 16, topPos + 20 + 16, 232, 1, 23, 23,256,256); break;
				case 1: drawTexturedModalRect(graphics,current,leftPos + 24 + 16, topPos + 43 + 16, 209, 24, 23, 23,256,256); break;
				case 3: drawTexturedModalRect(graphics,current,leftPos + 47 + 16, topPos + 43 + 16, 232, 24, 23, 23,256,256); break;
			}
		}
			
		if(!assembly.getItem(5).isEmpty() && assembly.matches(5,"tsar_core"))
			drawTexturedModalRect(graphics,current,leftPos + 75 + 16, topPos + 25 + 16, 176, 220, 80, 36,256,256);
		
		drawTexturedModalRect(graphics,UTILITY,leftPos-16,topPos+16,8,0,16,16,256,256);
	}
case "FLEIJA" -> {
		graphics.setColor(1,1,1,1);
		current=legacyBackground;
		drawTexturedModalRect(graphics,current,leftPos, topPos, 0, 0, imageWidth, imageHeight,256,256);
		
		if(!assembly.getItem(0).isEmpty() && assembly.matches(0,"fleija_igniter"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 7, topPos + 88, 176, 0, 30, 20,256,256);
		}
		
		if(!assembly.getItem(1).isEmpty() && assembly.matches(1,"fleija_igniter"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 139, topPos + 88, 206, 0, 30, 20,256,256);
		}
		
		if(!assembly.getItem(2).isEmpty() && assembly.matches(2,"fleija_propellant"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 57, topPos + 77, 176, 62, 18, 14,256,256);
		}
		
		if(!assembly.getItem(3).isEmpty() && assembly.matches(3,"fleija_propellant"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 57, topPos + 91, 176, 76, 18, 14,256,256);
		}
		
		if(!assembly.getItem(4).isEmpty() && assembly.matches(4,"fleija_propellant"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 57, topPos + 105, 176, 90, 18, 14,256,256);
		}
		
		if(!assembly.getItem(5).isEmpty() && assembly.matches(5,"fleija_core"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 85, topPos + 77, 176, 20, 18, 15,256,256);
		}
		
		if(!assembly.getItem(6).isEmpty() && assembly.matches(6,"fleija_core"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 103, topPos + 77, 194, 20, 18, 15,256,256);
		}
		
		if(!assembly.getItem(7).isEmpty() && assembly.matches(7,"fleija_core"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 85, topPos + 92, 176, 35, 18, 12,256,256);
		}
		
		if(!assembly.getItem(8).isEmpty() && assembly.matches(8,"fleija_core"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 103, topPos + 92, 194, 35, 18, 12,256,256);
		}
		
		if(!assembly.getItem(9).isEmpty() && assembly.matches(9,"fleija_core"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 85, topPos + 104, 176, 47, 18, 15,256,256);
		}
		
		if(!assembly.getItem(10).isEmpty() && assembly.matches(10,"fleija_core"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 103, topPos + 104, 194, 47, 18, 15,256,256);
		}
	}
case "SOLINIUM" -> {
		graphics.setColor(1,1,1,1);
		current=legacyBackground;
		drawTexturedModalRect(graphics,current,leftPos, topPos, 0, 0, imageWidth, imageHeight,256,256);
		
		if(!assembly.getItem(0).isEmpty() && assembly.matches(0,"solinium_igniter"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 24, topPos + 84, 0, 222, 22, 14,256,256);
		}
		
		if(!assembly.getItem(1).isEmpty() && assembly.matches(1,"solinium_propellant"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 46, topPos + 84, 22, 222, 18, 14,256,256);
		}
		
		if(!assembly.getItem(2).isEmpty() && assembly.matches(2,"solinium_propellant"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 76, topPos + 84, 52, 222, 18, 14,256,256);
		}
		
		if(!assembly.getItem(3).isEmpty() && assembly.matches(3,"solinium_igniter"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 94, topPos + 84, 70, 222, 22, 14,256,256);
		}
		
		if(!assembly.getItem(4).isEmpty() && assembly.matches(4,"solinium_core"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 64, topPos + 84, 40, 222, 12, 28,256,256);
		}
		
		if(!assembly.getItem(5).isEmpty() && assembly.matches(5,"solinium_igniter"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 24, topPos + 98, 0, 236, 22, 14,256,256);
		}
		
		if(!assembly.getItem(6).isEmpty() && assembly.matches(6,"solinium_propellant"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 46, topPos + 98, 22, 236, 18, 14,256,256);
		}
		
		if(!assembly.getItem(7).isEmpty() && assembly.matches(7,"solinium_propellant"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 76, topPos + 98, 52, 236, 18, 14,256,256);
		}
		
		if(!assembly.getItem(8).isEmpty() && assembly.matches(8,"solinium_igniter"))
		{
			drawTexturedModalRect(graphics,current,leftPos + 94, topPos + 98, 70, 236, 22, 14,256,256);
		}
		
		if(assembly.isReady()) {
			drawTexturedModalRect(graphics,current,leftPos + 134, topPos + 90, 176, 0, 16, 16,256,256);
		}
	}
case "N2" -> {
		graphics.setColor(1,1,1,1);
		current=legacyBackground;
		drawTexturedModalRect(graphics,current,leftPos, topPos, 0, 0, imageWidth, imageHeight,256,256);
		
		int count = 0;
		
		for(int i = 0; i < 12; i++) {
			if(!assembly.getItem(i).isEmpty() && assembly.matches(i,"n2_charge"))
				count++;
		}
		
		if(count > 0)
		{
			drawTexturedModalRect(graphics,current,leftPos + 35, topPos + 120 - 6 * count, 176, 0, 34, 6 * count,256,256);
		}
	}
case "PROTOTYPE" -> {
		graphics.setColor(1,1,1,1);
		current=legacyBackground;
		drawTexturedModalRect(graphics,current,leftPos, topPos, 0, 0, imageWidth, imageHeight,256,256);
	}
 default -> throw new IllegalStateException(assembly.definition().kind());
 }
 }
}

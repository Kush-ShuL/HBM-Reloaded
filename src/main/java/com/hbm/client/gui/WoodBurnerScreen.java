package com.hbm.client.gui;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.woodburner.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class WoodBurnerScreen extends LegacyContainerScreen<WoodBurnerMenu> {
    public WoodBurnerScreen(WoodBurnerMenu menu,Inventory player,Component title) { super(menu,player,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/generators/gui_wood_burner_alt.png"),176,186); }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(WoodBurnerContent.MENU.get(),WoodBurnerScreen::new); }
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y) { graphics.drawString(font,title,70-font.width(title)/2,6,0xffffff,false); graphics.drawString(font,playerInventoryTitle,8,92,4210752,false); }
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y) {
        super.renderBg(graphics,partial,x,y);
        if(menu.liquidBurn()) { drawLegacyRegion(graphics,16,17,176,52,60,54); drawLegacyRegion(graphics,79,17,176,106,36,54); }
        if(menu.on()) drawLegacyRegion(graphics,53,17,196,0,16,15);
        int p=(int)(menu.power()*34/WoodBurnerBlockEntity.MAX_POWER); drawLegacyRegion(graphics,143,52-p,176,52-p,16,p);
        if(menu.maxBurnTime()>0&&!menu.liquidBurn()) { int h=menu.burnTime()*52/menu.maxBurnTime(); drawLegacyRegion(graphics,17,70-h,192,52-h,4,h); }
        if(menu.liquidBurn()&&menu.tankCapacity()>0) {
            int height=menu.tankFill()*52/menu.tankCapacity(),tint=menu.tankType().getTint(); graphics.setColor((tint>>16&255)/255F,(tint>>8&255)/255F,(tint&255)/255F,1);
            // Original tank repeats its16px texture from V=1-height/16 to1; no scaled texture replacement.
            if(height>0) graphics.blit(menu.tankType().getTexture(),leftPos+80,topPos+70-height,0,16-height,16,height,16,16); graphics.setColor(1,1,1,1);
        }
    }
    @Override public void render(GuiGraphics graphics,int x,int y,float partial) {
        super.render(graphics,x,y,partial);
        if(legacyInfoHovered(x,y,143,18,16,34)) graphics.renderTooltip(font,Component.literal(shortNumber(menu.power())+"/"+shortNumber(WoodBurnerBlockEntity.MAX_POWER)+"HE"),x,y);
        if(menu.getCarried().isEmpty()&&hoveredSlot==menu.getSlot(0)&&!hoveredSlot.hasItem()) graphics.renderComponentTooltip(font,WoodBurnerFuel.description(),x,y);
        if(menu.liquidBurn()&&legacyInfoHovered(x,y,80,18,16,52)) { var tooltip=new ArrayList<Component>(); tooltip.add(menu.tankType().displayName()); tooltip.add(Component.literal(menu.tankFill()+"/"+menu.tankCapacity()+"mB")); if(menu.pressure()!=0) { tooltip.add(Component.translatable("gui.hbm.chemical.pressure",menu.pressure()).withStyle(ChatFormatting.RED)); tooltip.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED)); } FluidInfo.append(menu.tankType(),tooltip,hasShiftDown()); graphics.renderComponentTooltip(font,tooltip,x,y); }
        if(!menu.liquidBurn()&&legacyInfoHovered(x,y,16,17,8,54)) graphics.renderTooltip(font,Component.literal(menu.burnTime()/20+"s"),x,y);
        if(legacyInfoHovered(x,y,53,17,16,15)) graphics.renderTooltip(font,Component.translatable(menu.on()?"gui.hbm.state.on":"gui.hbm.state.off").withStyle(menu.on()?ChatFormatting.GREEN:ChatFormatting.RED),x,y);
    }
    @Override public boolean mouseClicked(double x,double y,int button) {
        boolean handled=super.mouseClicked(x,y,button);
        int control=legacyInfoHovered(x,y,53,17,16,15)?0:legacyInfoHovered(x,y,46,37,30,14)?1:-1;
        if(control>=0&&minecraft!=null&&minecraft.gameMode!=null) { minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1)); minecraft.gameMode.handleInventoryButtonClick(menu.containerId,control); return true; } return handled;
    }
    private static String shortNumber(long value) { if(Math.abs(value)<1000) return Long.toString(value); String[] suffix={"k","M","G","T","P","E"}; double number=value; int i=-1; do { number/=1000; i++; } while(Math.abs(number)>=1000&&i<5); return (number<=-100?Math.round(number*10)/10D:Math.round(number*100)/100D)+suffix[i]; }
}

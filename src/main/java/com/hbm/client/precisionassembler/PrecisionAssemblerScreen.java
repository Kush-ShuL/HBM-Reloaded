package com.hbm.client.precisionassembler;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.recipe.generic.GenericRecipeSelectorScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.precisionassembler.*;
import com.hbm.recipe.generic.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.PacketDistributor;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PrecisionAssemblerScreen extends LegacyContainerScreen<PrecisionAssemblerMenu> {
    public PrecisionAssemblerScreen(PrecisionAssemblerMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/processing/gui_precass.png"),176,256);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(PrecisionAssemblerContent.MENU.get(),PrecisionAssemblerScreen::new);}
    private PrecisionAssemblerBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof PrecisionAssemblerBlockEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,70-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,162,4210752,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);long capacity=menu.capacity();int p=capacity==0?0:(int)(menu.power()*61/capacity);drawLegacyRegion(g,152,79-p,176,61-p,16,p);if(menu.progress()>0)drawLegacyRegion(g,62,126,176,61,(int)Math.ceil(menu.progress()*70),16);var machine=machine();var recipe=machine==null?null:machine.module.getRecipe();if(menu.processing()){drawLegacyRegion(g,51,121,195,0,3,6);drawLegacyRegion(g,56,121,195,0,3,6);}else if(recipe!=null){drawLegacyRegion(g,51,121,192,0,3,6);if(menu.power()>=recipe.power())drawLegacyRegion(g,56,121,192,0,3,6);}if(recipe!=null){g.renderItem(recipe.icon(),leftPos+8,topPos+126);for(int i=0;i<Math.min(recipe.inputItems().size(),9);i++)if(!menu.getSlot(4+i).hasItem()){var examples=recipe.inputItems().get(i).examples();if(examples.isEmpty())continue;var slot=menu.getSlot(4+i);g.renderItem(examples.get((int)(System.currentTimeMillis()/1000%examples.size())),leftPos+slot.x,topPos+slot.y);g.setColor(1,1,1,.5F);drawLegacyRegion(g,slot.x,slot.y,slot.x,slot.y,16,16);g.setColor(1,1,1,1);}}if(machine!=null){tank(g,machine.inputTank(),8);tank(g,machine.outputTank(),80);}}
    private void tank(GuiGraphics g,FluidTank tank,int x){if(tank.getMaxFill()==0)return;int width=tank.getFill()*52/tank.getMaxFill();if(width<=0)return;int tint=tank.getTankType().getTint();g.setColor((tint>>16&255)/255F,(tint>>8&255)/255F,(tint&255)/255F,1);g.pose().pushPose();g.pose().translate(leftPos+x+width,topPos+99,0);g.pose().scale(-1,1,1);g.blit(tank.getTankType().getTexture(),0,0,16-width,0,width,16,16,16);g.pose().popPose();g.setColor(1,1,1,1);}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var machine=machine();if(machine!=null){if(legacyInfoHovered(x,y,7,125,18,18)){var recipe=machine.module.getRecipe();g.renderComponentTooltip(font,recipe==null?List.of(Component.translatable("gui.recipe.setRecipe")):recipe.tooltip(),x,y);}FluidTank[] tanks={machine.inputTank(),machine.outputTank()};for(int i=0;i<2;i++)if(legacyInfoHovered(x,y,i==0?8:80,99,52,16)){var tank=tanks[i];var tooltip=new ArrayList<Component>();tooltip.add(tank.getTankType().displayName());tooltip.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0)tooltip.add(Component.literal(tank.getPressure()+" PU"));FluidInfo.append(tank.getTankType(),tooltip,hasShiftDown());g.renderComponentTooltip(font,tooltip,x,y);}}if(legacyInfoHovered(x,y,152,18,16,61))g.renderTooltip(font,Component.literal(menu.power()+"/"+menu.capacity()+"HE"),x,y);}
    @Override public boolean mouseClicked(double x,double y,int button){if(button==0&&legacyInfoHovered(x,y,7,125,18,18)){var machine=machine();if(machine!=null){GenericRecipeSelectorScreen.open(machine.recipes(),machine.module.recipeName,Blueprints.pool(menu.getSlot(1).getItem()),this,selection->PacketDistributor.sendToServer(new PrecisionAssemblerSelection(menu.containerId,0,selection)));return true;}}return super.mouseClicked(x,y,button);}
}

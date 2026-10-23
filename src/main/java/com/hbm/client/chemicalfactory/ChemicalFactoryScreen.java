package com.hbm.client.chemicalfactory;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.recipe.generic.GenericRecipeSelectorScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.chemicalfactory.*;
import com.hbm.recipe.generic.Blueprints;
import java.math.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
/** Actual248x216 two-rectangle background, four indexed selectors and narrow original tank gauges. */
public final class ChemicalFactoryScreen extends LegacyContainerScreen<ChemicalFactoryMenu> {
 public ChemicalFactoryScreen(ChemicalFactoryMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_chemical_factory.png"),248,216);}
 private ChemicalFactoryEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof ChemicalFactoryEntity factory?factory:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,106-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,26,122,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){g.setColor(1,1,1,1);drawLegacyRegion(g,0,0,0,0,248,116);drawLegacyRegion(g,18,116,18,116,230,100);var m=machine();if(m==null)return;int power=m.maxPower.signum()==0?0:m.power.multiply(BigInteger.valueOf(68)).divide(m.maxPower).intValue();if(power>0)drawLegacyRegion(g,224,86-power,0,184-power,16,power);
  for(int i=0;i<4;i++){var module=m.modules[i];var recipe=module.getRecipe();if(module.progress>0)drawLegacyRegion(g,113,29+i*22,0,216,(int)Math.ceil(22*module.progress),6);if(m.didProcess[i]){drawLegacyRegion(g,113,21+i*22,4,222,4,4);drawLegacyRegion(g,121,21+i*22,4,222,4,4);}else if(recipe!=null){drawLegacyRegion(g,113,21+i*22,0,222,4,4);if(m.power.compareTo(BigInteger.valueOf(recipe.power()))>=0&&m.canCool())drawLegacyRegion(g,121,21+i*22,0,222,4,4);}
   g.renderItem(recipe==null?new ItemStack(Blueprints.FOLDER.get()):recipe.icon(),leftPos+75,topPos+20+i*22);if(recipe!=null){for(int j=0;j<Math.min(recipe.inputItems().size(),3);j++){var slot=menu.getSlot(5+i*7+j);if(!slot.hasItem()){var options=recipe.inputItems().get(j).processingExamples();if(!options.isEmpty())g.renderItem(options.get((int)(System.currentTimeMillis()/1000%options.size())),leftPos+slot.x,topPos+slot.y);}}g.pose().pushPose();g.pose().translate(0,0,300);g.setColor(1,1,1,.5F);for(int j=0;j<Math.min(recipe.inputItems().size(),3);j++){var slot=menu.getSlot(5+i*7+j);if(!slot.hasItem())drawLegacyRegion(g,slot.x,slot.y,slot.x,slot.y,16,16);}g.setColor(1,1,1,1);g.pose().popPose();}
  }
  for(int i=0;i<3;i++)for(int lane=0;lane<4;lane++){tank(g,m.inputTanks[i+lane*3],60+i*5,36+lane*22,3,16);tank(g,m.outputTanks[i+lane*3],189+i*5,36+lane*22,3,16);}tank(g,m.water,224,177,7,52);tank(g,m.lps,233,177,7,52);
 }
 private void tank(GuiGraphics g,FluidTank tank,int x,int bottom,int width,int height){if(tank.getMaxFill()<=0)return;int fill=tank.getFill()*height/tank.getMaxFill();if(fill<=0)return;int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int y=0;y<fill;y+=16){int h=Math.min(16,fill-y);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+bottom-y-h,0,16-h,width,h,16,16);}g.setColor(1,1,1,1);}
 private void tankInfo(GuiGraphics g,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED));}FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var m=machine();if(m==null)return;for(int i=0;i<3;i++)for(int lane=0;lane<4;lane++){if(legacyInfoHovered(x,y,60+i*5,20+lane*22,3,16))tankInfo(g,m.inputTanks[i+lane*3],x,y);if(legacyInfoHovered(x,y,189+i*5,20+lane*22,3,16))tankInfo(g,m.outputTanks[i+lane*3],x,y);}if(legacyInfoHovered(x,y,224,125,7,52))tankInfo(g,m.water,x,y);if(legacyInfoHovered(x,y,233,125,7,52))tankInfo(g,m.lps,x,y);if(legacyInfoHovered(x,y,224,18,16,68))g.renderTooltip(font,Component.literal(m.power+"/"+m.maxPower+"HE"),x,y);for(int i=0;i<4;i++)if(legacyInfoHovered(x,y,74,19+i*22,18,18)){var recipe=m.modules[i].getRecipe();g.renderComponentTooltip(font,recipe==null?List.of(Component.translatable("gui.recipe.setRecipe").withStyle(ChatFormatting.YELLOW)):recipe.tooltip(),x,y);}}
 @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);var m=machine();if(m!=null)for(int i=0;i<4;i++)if(legacyInfoHovered(x,y,74,19+i*22,18,18)){int index=i;minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));GenericRecipeSelectorScreen.open(m.recipes(),m.modules[i].getRecipeName(),Blueprints.pool(menu.getSlot(4+i*7).getItem()),this,selection->PacketDistributor.sendToServer(new ChemicalFactorySelection(menu.containerId,index,selection)));return true;}return handled;}
}

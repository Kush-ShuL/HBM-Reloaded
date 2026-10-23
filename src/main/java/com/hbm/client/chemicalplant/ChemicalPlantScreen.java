package com.hbm.client.chemicalplant;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.recipe.generic.GenericRecipeSelectorScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.chemicalplant.*;
import com.hbm.recipe.generic.*;
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

/** Source176x256 pixels, unchanged slot locations, tank UV repeat and recipe selector. */
public final class ChemicalPlantScreen extends LegacyContainerScreen<ChemicalPlantMenu> {
    public ChemicalPlantScreen(ChemicalPlantMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/processing/gui_chemplant.png"),176,256);}
    private ChemicalPlantBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof ChemicalPlantBlockEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,70-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,162,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){
        super.renderBg(graphics,partial,x,y);
        int bar=menu.capacity()==0?0:(int)(menu.power()*61/menu.capacity());drawLegacyRegion(graphics,152,79-bar,176,61-bar,16,bar);
        if(menu.progress()>0)drawLegacyRegion(graphics,62,126,176,61+(menu.restricted()?16:0),(int)Math.ceil(menu.progress()*70),16);
        var machine=machine();var recipe=machine==null?null:machine.module.getRecipe();
        if(menu.processing()){drawLegacyRegion(graphics,51,121,195,0,3,6);drawLegacyRegion(graphics,56,121,195,0,3,6);}
        else if(recipe!=null){drawLegacyRegion(graphics,51,121,192,0,3,6);if(menu.power()>=recipe.power())drawLegacyRegion(graphics,56,121,192,0,3,6);}
        graphics.renderItem(recipe==null?new ItemStack(Blueprints.FOLDER.get()):recipe.icon(),leftPos+8,topPos+126);
        if(recipe!=null){
            for(int i=0;i<Math.min(recipe.inputItems().size(),3);i++)if(!menu.getSlot(4+i).hasItem()){
                var examples=recipe.inputItems().get(i).examples();if(examples.isEmpty())continue;var slot=menu.getSlot(4+i);
                graphics.renderItem(examples.get((int)(System.currentTimeMillis()/1000%examples.size())),leftPos+slot.x,topPos+slot.y);
            }
            graphics.pose().pushPose();graphics.pose().translate(0,0,300);graphics.setColor(1,1,1,.5F);
            for(int i=0;i<Math.min(recipe.inputItems().size(),3);i++){var slot=menu.getSlot(4+i);if(!slot.hasItem())drawLegacyRegion(graphics,slot.x,slot.y,slot.x,slot.y,16,16);}
            graphics.setColor(1,1,1,1);graphics.pose().popPose();
        }
        if(machine!=null)for(int i=0;i<3;i++){tank(graphics,machine.inputTanks[i],8+i*18);tank(graphics,machine.outputTanks[i],80+i*18);}
    }
    private void tank(GuiGraphics graphics,FluidTank tank,int x){
        if(tank.getMaxFill()==0)return;int height=tank.getFill()*34/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();
        graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);
        graphics.blit(tank.getTankType().getTexture(),leftPos+x,topPos+52-height,0,16-height,16,height,16,16);graphics.setColor(1,1,1,1);
    }
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){
        super.render(graphics,x,y,partial);var machine=machine();
        if(machine!=null){
            if(legacyInfoHovered(x,y,7,125,18,18)){
                var recipe=machine.module.getRecipe();graphics.renderComponentTooltip(font,recipe==null?List.of(Component.translatable("gui.recipe.setRecipe").withStyle(ChatFormatting.YELLOW)):recipe.tooltip(),x,y);
            }
            for(int i=0;i<3;i++){if(legacyInfoHovered(x,y,8+i*18,18,16,34))tankInfo(graphics,machine.inputTanks[i],x,y);if(legacyInfoHovered(x,y,80+i*18,18,16,34))tankInfo(graphics,machine.outputTanks[i],x,y);}
        }
        if(legacyInfoHovered(x,y,152,18,16,61))graphics.renderTooltip(font,Component.literal(shortNumber(menu.power())+"/"+shortNumber(menu.capacity())+"HE"),x,y);
    }
    private void tankInfo(GuiGraphics graphics,FluidTank tank,int x,int y){
        var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));
        if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED));}
        FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);
    }
    @Override public boolean mouseClicked(double x,double y,int button){
        boolean handled=super.mouseClicked(x,y,button);
        if(legacyInfoHovered(x,y,7,125,18,18)){
            var machine=machine();if(machine!=null){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));GenericRecipeSelectorScreen.open(machine.recipes(),machine.module.recipeName,Blueprints.pool(menu.getSlot(1).getItem()),this,selection->PacketDistributor.sendToServer(new ChemicalPlantSelection(menu.containerId,0,selection)));return true;}
        }
        return handled;
    }
    private static String shortNumber(long value){String[] units={"","k","M","G","T","P","E"};int unit=0;double n=value;while(Math.abs(n)>=1000&&unit<6){n/=1000;unit++;}return unit==0?Long.toString(value):(n<=-100?Math.round(n*10)/10D:Math.round(n*100)/100D)+units[unit];}
}

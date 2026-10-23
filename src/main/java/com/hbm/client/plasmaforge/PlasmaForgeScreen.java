package com.hbm.client.plasmaforge;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.recipe.generic.GenericRecipeSelectorScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.battery.BatteryItem;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.plasmaforge.*;
import com.hbm.recipe.generic.Blueprints;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/** Original176×244 background/slots/recipe input ghosts. The two source dial hitboxes now hold linear bars. */
public final class PlasmaForgeScreen extends LegacyContainerScreen<PlasmaForgeMenu> {
    public PlasmaForgeScreen(PlasmaForgeMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_fusion_plasmaforge.png"),176,244);}
    private PlasmaForgeBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof PlasmaForgeBlockEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,70-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,150,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){
        super.renderBg(graphics,partial,x,y);var machine=machine();if(machine==null)return;var recipe=(PlasmaForgeRecipe)machine.module.getRecipe();int power=machine.maxPower<=0?0:(int)Math.clamp((double)machine.power/machine.maxPower*62,0D,62D);drawLegacyRegion(graphics,152,80-power,176,62-power,16,power);
        if(recipe!=null&&machine.module.progress>0)drawLegacyRegion(graphics,62,81,176,62,(int)Math.ceil(70*machine.module.progress),16);
        if(machine.didProcess){drawLegacyRegion(graphics,51,76,195,0,3,6);drawLegacyRegion(graphics,56,76,195,0,3,6);}else if(recipe!=null){drawLegacyRegion(graphics,51,76,192,0,3,6);if(machine.power>=recipe.power())drawLegacyRegion(graphics,56,76,192,0,3,6);}
        double input=recipe==null?0:Math.min((double)machine.plasmaEnergySync/recipe.ignitionTemp(),1.5)/1.5D,booster=machine.maxBooster<=0?0:(double)machine.booster/machine.maxBooster;
        gauge(graphics,25,115,input,0xffa00000);gauge(graphics,61,115,booster,0xffa00000);
        if(recipe!=null){graphics.renderItem(recipe.icon(),leftPos+8,topPos+81);for(int i=0;i<Math.min(recipe.inputItems().size(),machine.module.inputSlots.length);i++){var slot=menu.getSlot(machine.module.inputSlots[i]);if(slot.hasItem())continue;var examples=recipe.inputItems().get(i).processingExamples();if(examples.isEmpty())continue;graphics.renderItem(examples.get((int)(System.currentTimeMillis()/1000%examples.size())),leftPos+slot.x,topPos+slot.y);graphics.pose().pushPose();graphics.pose().translate(0,0,300);graphics.setColor(1,1,1,.5F);drawLegacyRegion(graphics,slot.x,slot.y,slot.x,slot.y,16,16);graphics.setColor(1,1,1,1);graphics.pose().popPose();}}
        tank(graphics,machine.inputTank);
    }
    private void gauge(GuiGraphics graphics,int x,int y,double ratio,int color){int left=leftPos+x,top=topPos+y;graphics.fill(left,top,left+18,top+18,0xffc6c6c6);graphics.fill(left,top+5,left+18,top+13,0xff373737);graphics.fill(left+1,top+6,left+17,top+12,0xff8b8b8b);int fill=(int)Math.clamp(ratio*16,0D,16D);if(fill>0)graphics.fill(left+1,top+6,left+1+fill,top+12,color);}
    private void tank(GuiGraphics graphics,FluidTank tank){if(tank.getMaxFill()<=0)return;int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);graphics.blit(tank.getTankType().getTexture(),leftPos+80,topPos+70-height,0,16-height,16,height,16,16);graphics.setColor(1,1,1,1);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var machine=machine();if(machine==null)return;var recipe=(PlasmaForgeRecipe)machine.module.getRecipe();
        if(legacyInfoHovered(x,y,7,80,18,18))graphics.renderComponentTooltip(font,recipe==null?List.of(Component.translatable("gui.recipe.setRecipe").withStyle(ChatFormatting.YELLOW)):recipe.tooltip(),x,y);
        if(legacyInfoHovered(x,y,152,18,16,62))graphics.renderTooltip(font,Component.literal(BatteryItem.shortNumber(machine.power)+"/"+BatteryItem.shortNumber(machine.maxPower)+"HE"),x,y);
        if(legacyInfoHovered(x,y,80,18,16,52)){var tank=machine.inputTank;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0)lines.add(Component.literal(tank.getPressure()+" PU"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
        if(legacyInfoHovered(x,y,25,115,18,18))graphics.renderTooltip(font,recipe==null?Component.literal("0TU / 0TU"):Component.literal(ChatFormatting.GREEN+"-> "+ChatFormatting.RESET+BatteryItem.shortNumber(machine.plasmaEnergySync)+"TU / "+BatteryItem.shortNumber(recipe.ignitionTemp())+"TU"),x,y);
        if(legacyInfoHovered(x,y,61,115,18,18))graphics.renderTooltip(font,Component.translatable("gui.hbm.plasmaforge.booster_remaining",machine.booster,machine.maxBooster),x,y);
        var slot=menu.getSlot(2);if(!slot.hasItem()&&menu.getCarried().isEmpty()&&legacyInfoHovered(x,y,slot.x,slot.y,16,16))boosterTooltip(graphics,x,y);
    }
    private void boosterTooltip(GuiGraphics graphics,int x,int y){
        var values=new ArrayList<ItemStack>();for(var booster:PlasmaForgeBlockEntity.boosters)values.addAll(booster.ingredient().examples());if(values.isEmpty())return;int selected=(int)(System.currentTimeMillis()/1000%values.size());int rows=values.size()<10?1:values.size()<24?2:3;int[] starts=new int[rows+1];starts[rows]=values.size();for(int i=1;i<rows;i++)starts[i]=rows==2?values.size()/2:(int)Math.ceil(values.size()/(double)rows*i);int max=0;for(int i=0;i<rows;i++)max=Math.max(max,(starts[i+1]-starts[i])*18);String heading=Component.translatable("gui.hbm.plasmaforge.booster").getString(),name=values.get(selected).getHoverName().getString();max=Math.max(max,Math.max(font.width(heading),font.width(name)));int height=20+rows*18,minX=x+12,minY=y-12;if(minX+max>width)minX-=28+max;if(minY+height+6>this.height)minY=this.height-height-6;
        graphics.pose().pushPose();graphics.pose().translate(0,0,400);int bg=0xf0100010,c0=0x505000ff,c1=(c0&0xfefefe)>>1|c0&0xff000000;graphics.fill(minX-3,minY-4,minX+max+3,minY+height+4,bg);graphics.fill(minX-4,minY-3,minX-3,minY+height+3,bg);graphics.fill(minX+max+3,minY-3,minX+max+4,minY+height+3,bg);graphics.fillGradient(minX-3,minY-2,minX-2,minY+height+2,c0,c1);graphics.fillGradient(minX+max+2,minY-2,minX+max+3,minY+height+2,c0,c1);graphics.fill(minX-3,minY-3,minX+max+3,minY-2,c0);graphics.fill(minX-3,minY+height+2,minX+max+3,minY+height+3,c1);graphics.drawString(font,heading,minX,minY,-1,true);minY+=12;
        for(int row=0;row<rows;row++){for(int i=starts[row];i<starts[row+1];i++){int bx=minX+(i-starts[row])*18;if(values.size()>1&&i==selected){graphics.fill(bx-1,minY-1,bx+17,minY+17,0xffff0000);graphics.fill(bx,minY,bx+16,minY+16,0xffb0b0b0);}graphics.renderItem(values.get(i),bx,minY);}minY+=18;}graphics.drawString(font,name,minX,minY,-1,true);graphics.pose().popPose();
    }
    @Override public boolean mouseClicked(double x,double y,int button){if(legacyInfoHovered(x,y,7,80,18,18)){var machine=machine();if(machine!=null){GenericRecipeSelectorScreen.open(machine.recipes(),machine.module.recipeName,Blueprints.pool(menu.getSlot(1).getItem()),this,selection->PacketDistributor.sendToServer(new PlasmaForgeSelection(menu.containerId,0,selection)));return true;}}return super.mouseClicked(x,y,button);}
}

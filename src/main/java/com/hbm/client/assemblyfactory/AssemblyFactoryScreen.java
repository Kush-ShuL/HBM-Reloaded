package com.hbm.client.assemblyfactory;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.recipe.generic.GenericRecipeSelectorScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.assemblyfactory.*;
import com.hbm.recipe.generic.Blueprints;
import java.math.BigInteger;
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

/** Original256x240 two-piece GUI, four12-input selectors and every original gauge/LED coordinate. */
public final class AssemblyFactoryScreen extends LegacyContainerScreen<AssemblyFactoryMenu> {
    public AssemblyFactoryScreen(AssemblyFactoryMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_assembly_factory.png"),256,240);}
    private AssemblyFactoryEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof AssemblyFactoryEntity factory?factory:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,113-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,33,146,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){
        graphics.setColor(1,1,1,1);drawLegacyRegion(graphics,0,0,0,0,256,140);drawLegacyRegion(graphics,25,140,25,140,231,100);
        var machine=machine();if(machine==null)return;
        int power=machine.maxPower.signum()==0?0:machine.power.multiply(BigInteger.valueOf(92)).divide(machine.maxPower).intValue();if(power>0)drawLegacyRegion(graphics,234,110-power,0,232-power,16,power);
        for(int field=0;field<4;field++){
            int x=(field%2)*109,y=(field/2)*56;var module=machine.modules[field];var recipe=module.getRecipe();
            if(module.progress>0)drawLegacyRegion(graphics,45+x,63+y,0,240,(int)Math.ceil(37*module.progress),6);
            if(machine.didProcess[field]){drawLegacyRegion(graphics,45+x,55+y,4,236,4,4);drawLegacyRegion(graphics,53+x,55+y,4,236,4,4);}
            else if(recipe!=null){drawLegacyRegion(graphics,45+x,55+y,0,236,4,4);if(machine.power.compareTo(BigInteger.valueOf(recipe.power()))>=0&&machine.canCool())drawLegacyRegion(graphics,53+x,55+y,0,236,4,4);}
            graphics.renderItem(recipe==null?new ItemStack(Blueprints.FOLDER.get()):recipe.icon(),leftPos+7+x,topPos+54+y);
            if(recipe!=null){
                for(int i=0;i<Math.min(recipe.inputItems().size(),12);i++){var slot=menu.getSlot(5+field*14+i);if(slot.hasItem())continue;var options=recipe.inputItems().get(i).examples();if(!options.isEmpty())graphics.renderItem(options.get((int)(System.currentTimeMillis()/1000%options.size())),leftPos+slot.x,topPos+slot.y);}
                graphics.pose().pushPose();graphics.pose().translate(0,0,300);graphics.setColor(1,1,1,.5F);
                for(int i=0;i<Math.min(recipe.inputItems().size(),12);i++){var slot=menu.getSlot(5+field*14+i);if(!slot.hasItem())drawLegacyRegion(graphics,slot.x,slot.y,slot.x,slot.y,16,16);}
                graphics.setColor(1,1,1,1);graphics.pose().popPose();
            }
            tank(graphics,machine.inputTanks[field],105+x,52+y,5,32);tank(graphics,machine.outputTanks[field],105+x,70+y,5,16);
        }
        tank(graphics,machine.water,232,201,7,52);tank(graphics,machine.lps,241,201,7,52);
    }
    private void tank(GuiGraphics graphics,FluidTank tank,int x,int bottom,int width,int height){
        if(tank.getMaxFill()<=0)return;int fill=tank.getFill()*height/tank.getMaxFill();if(fill<=0)return;int color=tank.getTankType().getTint();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);
        for(int y=0;y<fill;y+=16){int h=Math.min(16,fill-y);graphics.blit(tank.getTankType().getTexture(),leftPos+x,topPos+bottom-y-h,0,16-h,width,h,16,16);}graphics.setColor(1,1,1,1);
    }
    private void tankInfo(GuiGraphics graphics,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+" mB"));if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED));}FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        super.render(graphics,mouseX,mouseY,partial);var machine=machine();if(machine==null)return;
        for(int i=0;i<4;i++){int x=(i%2)*109,y=(i/2)*56;if(legacyInfoHovered(mouseX,mouseY,105+x,20+y,5,32))tankInfo(graphics,machine.inputTanks[i],mouseX,mouseY);if(legacyInfoHovered(mouseX,mouseY,105+x,54+y,5,16))tankInfo(graphics,machine.outputTanks[i],mouseX,mouseY);if(legacyInfoHovered(mouseX,mouseY,6+x,53+y,18,18)){var recipe=machine.modules[i].getRecipe();graphics.renderComponentTooltip(font,recipe==null?List.of(Component.translatable("gui.recipe.setRecipe").withStyle(ChatFormatting.YELLOW)):recipe.tooltip(),mouseX,mouseY);}}
        if(legacyInfoHovered(mouseX,mouseY,232,149,7,52))tankInfo(graphics,machine.water,mouseX,mouseY);if(legacyInfoHovered(mouseX,mouseY,241,149,7,52))tankInfo(graphics,machine.lps,mouseX,mouseY);if(legacyInfoHovered(mouseX,mouseY,234,18,16,92))graphics.renderTooltip(font,Component.literal(machine.power+" / "+machine.maxPower+" HE"),mouseX,mouseY);
    }
    @Override public boolean mouseClicked(double mouseX,double mouseY,int button){
        boolean handled=super.mouseClicked(mouseX,mouseY,button);var machine=machine();if(machine!=null)for(int i=0;i<4;i++)if(legacyInfoHovered(mouseX,mouseY,6+(i%2)*109,53+(i/2)*56,18,18)){int field=i;minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));GenericRecipeSelectorScreen.open(machine.recipes(),machine.modules[i].getRecipeName(),Blueprints.pool(menu.getSlot(4+i*14).getItem()),this,selection->PacketDistributor.sendToServer(new AssemblyFactorySelection(menu.containerId,field,selection)));return true;}return handled;
    }
}

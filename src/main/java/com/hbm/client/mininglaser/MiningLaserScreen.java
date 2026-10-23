package com.hbm.client.mininglaser;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.mininglaser.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Source176x222 background, controls, tank and progress pixels; the two published upgrade-cap inconsistencies are retained verbatim. */
public final class MiningLaserScreen extends LegacyContainerScreen<MiningLaserMenu> {
    public MiningLaserScreen(MiningLaserMenu menu,Inventory inv,Component title){super(menu,inv,title,ResourceLocation.parse("hbm:textures/gui/machine/gui_laser_miner.png"),176,222);}
    private MiningLaserEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof MiningLaserEntity be?be:null;}
    @Override protected void renderLabels(GuiGraphics g,int mouseX,int mouseY){g.drawString(font,title,88-font.width(title)/2,4,4210752,false);g.drawString(font,playerInventoryTitle,8,128,4210752,false);var be=machine();if(be!=null){String width=Integer.toString(be.width());g.drawString(font,width,43-font.width(width)/2,26,0xFFFFFF,false);}}
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){
        super.renderBg(g,partial,mouseX,mouseY);var be=machine();if(be==null)return;
        if(be.isOn)drawLegacyRegion(g,61,17,200,0,18,18);
        int energy=(int)(be.power*88/MiningLaserEntity.MAX_POWER);drawLegacyRegion(g,8,106-energy,176,88-energy,16,energy);
        int progress=(int)(be.breakProgress*34);drawLegacyRegion(g,66,36,192,0,8,progress);
        g.blit(ResourceLocation.parse("hbm:textures/gui/gui_utility.png"),leftPos+87,topPos+31,0,32,8,8,256,256);
        int height=be.tank.getFill()*52/be.tank.getMaxFill();if(height>0&&be.tank.getTankType()!=HbmFluids.NONE){int color=be.tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int row=0;row<height;row+=16){int h=Math.min(16,height-row);g.blit(be.tank.getTankType().getTexture(),leftPos+35,topPos+124-row-h,0,16-h,7,h,16,16);}g.setColor(1,1,1,1);}
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial){
        super.render(g,mouseX,mouseY,partial);var be=machine();if(be==null)return;
        if(legacyInfoHovered(mouseX,mouseY,8,18,16,88))g.renderComponentTooltip(font,List.of(Component.literal(be.power+"/"+MiningLaserEntity.MAX_POWER+" HE")),mouseX,mouseY);
        if(legacyInfoHovered(mouseX,mouseY,87,31,8,8)){var info=new ArrayList<Component>();for(int i=0;i<10;i++)info.add(Component.translatable("gui.hbm.mining_laser.upgrades."+i));g.renderComponentTooltip(font,info,mouseX,mouseY);}
        if(legacyInfoHovered(mouseX,mouseY,35,72,7,52)){var info=new ArrayList<Component>();info.add(be.tank.getTankType().displayName());info.add(Component.literal(be.tank.getFill()+"/"+be.tank.getMaxFill()+" mB"));FluidInfo.append(be.tank.getTankType(),info,hasShiftDown());g.renderComponentTooltip(font,info,mouseX,mouseY);}
    }
    @Override public boolean mouseClicked(double mouseX,double mouseY,int button){boolean handled=super.mouseClicked(mouseX,mouseY,button);if(mouseX>=leftPos+61&&mouseX<leftPos+79&&mouseY>topPos+17&&mouseY<=topPos+35){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new MiningLaserControl(menu.containerId));return true;}return handled;}
}

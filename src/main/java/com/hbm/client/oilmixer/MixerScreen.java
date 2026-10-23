package com.hbm.client.oilmixer;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.*;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.oilmixer.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Original176x204 pixel layout, tank widths7/7/16 and literal recipe-cycle region. */
public final class MixerScreen extends LegacyContainerScreen<MixerMenu> {
 public MixerScreen(MixerMenu menu,Inventory inv,Component title){super(menu,inv,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_mixer.png"),176,204);}
 private MixerEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof MixerEntity mixer?mixer:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,108-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,110,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var m=machine();if(m==null)return;int power=(int)(m.getPower()*52/10000);drawLegacyRegion(g,12,70-power,176,52-power,16,power);if(m.processTime>0&&m.progress>0)drawLegacyRegion(g,71,31,192,0,m.progress*52/m.processTime,44);tank(g,m.tanks[0],52,7);tank(g,m.tanks[1],61,7);tank(g,m.tanks[2],126,16);g.blit(ResourceLocation.parse("hbm:textures/gui/gui_utility.png"),leftPos+152,topPos+55,0,32,8,8,256,256);}
 private void tank(GuiGraphics g,FluidTank tank,int x,int width){int height=tank.getFill()*52/tank.getMaxFill();if(height<=0||tank.getTankType()==HbmFluids.NONE)return;int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int y=0;y<height;y+=16){int h=Math.min(16,height-y);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+70-y-h,0,16-h,width,h,16,16);}g.setColor(1,1,1,1);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var m=machine();if(m==null)return;if(legacyInfoHovered(x,y,12,18,16,52))g.renderComponentTooltip(font,List.of(Component.literal(m.power+"/10000 HE"),Component.translatable("gui.hbm.mixer.consumption",m.consumption)),x,y);if(legacyInfoHovered(x,y,152,55,8,8))g.renderComponentTooltip(font,List.of(Component.translatable("desc.gui.upgrade"),Component.translatable("desc.gui.upgrade.speed"),Component.translatable("desc.gui.upgrade.power"),Component.translatable("desc.gui.upgrade.overdrive")),x,y);for(int i=0;i<3;i++){int tx=i==0?52:i==1?61:126,w=i<2?7:16;if(legacyInfoHovered(x,y,tx,18,w,52)){var tank=m.tanks[i];var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+" mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}}
  var variants=m.variants();if(variants.size()>1&&legacyInfoHovered(x,y,71,17,12,12)){var r=m.selected();var lines=new ArrayList<Component>();lines.add(Component.translatable("gui.hbm.mixer.recipe",m.recipeIndex+1,variants.size()).withStyle(ChatFormatting.YELLOW));for(var fluid:r.inputFluids())lines.add(Component.literal("-").append(fluid.type.displayName()));if(!r.inputItems().isEmpty()){var examples=r.inputItems().getFirst().processingExamples();if(!examples.isEmpty())lines.add(Component.literal("-").append(examples.get((int)(minecraft.level.getGameTime()/20%examples.size())).getHoverName()));}lines.add(Component.translatable("gui.hbm.mixer.change").withStyle(ChatFormatting.RED));g.renderComponentTooltip(font,lines,x,y);}}
 @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,71,17,12,12)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new MixerControl(menu.containerId));return true;}return handled;}
}

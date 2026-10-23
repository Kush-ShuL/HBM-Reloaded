package com.hbm.client.oilflare;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTraits;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.oilflare.*;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x203 screen, source sprites and deliberately narrow clickable centers of valve and dial. */
public final class FlareScreen extends LegacyContainerScreen<FlareMenu> {
    public FlareScreen(FlareMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/generators/gui_flare_stack.png"),176,203);}
    private FlareEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof FlareEntity entity?entity:null;}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,playerInventoryTitle,8,109,4210752,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var f=machine();if(f==null)return;int power=f.power.multiply(BigInteger.valueOf(52)).divide(FlareEntity.CAPACITY).intValue();drawLegacyRegion(g,143,69-power,176,94-power,16,power);if(f.isOn)drawLegacyRegion(g,79,15,176,0,35,10);if(f.doesBurn)drawLegacyRegion(g,79,49,176,10,35,14);if(f.isOn&&f.doesBurn&&f.tank.getFill()>0&&f.tank.getTankType().hasTrait(FluidTraits.Flammable.class))drawLegacyRegion(g,88,29,176,24,18,18);int height=f.tank.getFill()*52/f.tank.getMaxFill(),color=f.tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int drawn=0;drawn<height;){int part=Math.min(16,height-drawn);g.blit(f.tank.getTankType().getTexture(),leftPos+35,topPos+69-drawn-part,0,16-part,16,part,16,16);drawn+=part;}g.setColor(1,1,1,1);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);boolean valve=legacyInfoHovered(x,y,89,16,16,10),dial=legacyInfoHovered(x,y,89,50,16,14);if(valve||dial){minecraft.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,1));net.neoforged.neoforge.network.PacketDistributor.sendToServer(new FlareControl(menu.position,valve));return true;}return handled;}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var f=machine();if(f==null)return;if(legacyInfoHovered(x,y,79,16,35,10))g.renderTooltip(font,Component.translatable("flare.valve"),x,y);if(legacyInfoHovered(x,y,79,50,35,14))g.renderTooltip(font,Component.translatable("flare.ignition"),x,y);if(legacyInfoHovered(x,y,143,17,16,52))g.renderTooltip(font,Component.literal(f.power+"/"+FlareEntity.CAPACITY+"HE"),x,y);if(legacyInfoHovered(x,y,35,17,16,52)){var lines=new ArrayList<Component>();lines.add(f.tank.getTankType().displayName());lines.add(Component.literal(f.tank.getFill()+"/"+f.tank.getMaxFill()+"mB"));FluidInfo.append(f.tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}}
}

package com.hbm.client.network.cargo;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.network.cargo.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
public final class DroneCrateScreen extends LegacyContainerScreen<DroneCrateMenu> {
 public DroneCrateScreen(DroneCrateMenu menu,Inventory inv,Component title){super(menu,inv,title,ResourceLocation.parse("hbm:textures/gui/storage/gui_crate_drone.png"),176,185);}
 private DroneCrateEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof DroneCrateEntity c?c:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,91,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var tile=machine();if(tile==null)return;drawLegacyRegion(g,151,16,194,tile.itemType?0:18,18,18);drawLegacyRegion(g,151,52,176,tile.sendingMode?18:0,18,18);var tank=tile.tank;int height=tank.getFill()*34/tank.getMaxFill();if(height>0){int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);g.blit(tank.getTankType().getTexture(),leftPos+125,topPos+51-height,0,16-height,16,height,16,16);g.setColor(1,1,1,1);}}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var tile=machine();if(tile!=null&&legacyInfoHovered(x,y,125,17,16,34)){var tank=tile.tank;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}}
 @Override public boolean mouseClicked(double x,double y,int button){boolean result=super.mouseClicked(x,y,button);String operation=null;if(legacyInfoHovered(x,y,151,16,18,18))operation="type";if(legacyInfoHovered(x,y,151,52,18,18))operation="mode";if(operation!=null){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));var tag=new CompoundTag();tag.putBoolean(operation,true);PacketDistributor.sendToServer(new CargoCrateControl(menu.position,tag));return true;}return result;}
}

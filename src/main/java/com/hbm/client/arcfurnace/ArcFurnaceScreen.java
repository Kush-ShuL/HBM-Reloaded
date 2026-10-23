package com.hbm.client.arcfurnace;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.arcfurnace.*;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.material.HbmMaterials;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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
import org.lwjgl.glfw.GLFW;
/** Exact original176x256 artwork,70px gauges, mode button, molten stack and source slot layout. */
public final class ArcFurnaceScreen extends LegacyContainerScreen<ArcFurnaceMenu> {
 public ArcFurnaceScreen(ArcFurnaceMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_arc_furnace.png"),176,256);}
 @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,6,0xffffff,false);graphics.drawString(font,playerInventoryTitle,8,162,4210752,false);}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var arc=menu.machine;if(arc.liquidMode)drawLegacyRegion(graphics,151,17,190,18,18,18);if(arc.isProgressing)drawLegacyRegion(graphics,7,17,190,0,18,18);int p=(int)(arc.power*70/ArcFurnaceBlockEntity.MAX_POWER);drawLegacyRegion(graphics,8,106-p,176,70-p,7,p);int progress=(int)(arc.progress*70);drawLegacyRegion(graphics,17,106-progress,183,70-progress,7,progress);
  int lastHeight=0,lastQuant=0;RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);
  for(var stack:arc.liquids){int target=(lastQuant+stack.amount)*70/ArcFurnaceBlockEntity.MAX_LIQUID;if(lastHeight==target)continue;int rgb=stack.material.moltenColor();graphics.setColor((rgb>>16&255)/255F,(rgb>>8&255)/255F,(rgb&255)/255F,1);drawLegacyRegion(graphics,152,106-target,208,70-target,16,target-lastHeight);RenderSystem.enableBlend();graphics.setColor(1,1,1,.3F);drawLegacyRegion(graphics,152,106-target,208,70-target,16,target-lastHeight);RenderSystem.disableBlend();lastQuant+=stack.amount;lastHeight=target;}
  RenderSystem.defaultBlendFunc();graphics.setColor(1,1,1,1);
 }
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var arc=menu.machine;if(legacyInfoHovered(x,y,8,36,7,70))graphics.renderTooltip(font,Component.literal(BatteryItem.shortNumber(arc.power)+"/"+BatteryItem.shortNumber(ArcFurnaceBlockEntity.MAX_POWER)+"HE"),x,y);
  if(legacyInfoHovered(x,y,152,36,16,70)){var lines=new ArrayList<Component>();if(arc.liquids.isEmpty())lines.add(Component.translatable("gui.hbm.arc_furnace.empty").withStyle(ChatFormatting.RED));boolean shift=GLFW.glfwGetKey(minecraft.getWindow().getWindow(),GLFW.GLFW_KEY_LEFT_SHIFT)==GLFW.GLFW_PRESS;for(var mat:arc.liquids)lines.add(mat.material.displayName().copy().append(": ").append(HbmMaterials.formatAmount(mat.amount,shift)).withStyle(ChatFormatting.YELLOW));graphics.renderComponentTooltip(font,lines,x,y);}
 }
 @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,151,17,18,18)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new ArcFurnaceControl(menu.containerId));return true;}return handled;}
 @Override protected List<Component> getTooltipFromContainerItem(ItemStack stack){var lines=new ArrayList<>(super.getTooltipFromContainerItem(stack));if(stack.getItem() instanceof FurnaceUpgradeItem upgrade&&upgrade.type()==FurnaceUpgradeItem.Type.SPEED){int level=upgrade.tier();lines.add(Component.literal(">>> ").append(Component.translatable("block.hbm.machine_arc_furnace")).append(" <<<").withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable("upgrade.delay","-"+(100-100/(level*2+1))+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.consumption","+"+((int)Math.pow(5,level)*100-100)+"%").withStyle(ChatFormatting.RED));}return lines;}
}

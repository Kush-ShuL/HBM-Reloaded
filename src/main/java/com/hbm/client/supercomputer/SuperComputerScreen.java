package com.hbm.client.supercomputer;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.recipe.generic.GenericRecipeSelectorScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.supercomputer.*;
import com.hbm.recipe.generic.Blueprints;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
/** Exact source176×211 GUI, two reversed-U horizontalfluids, energy/progress/LEDs and3inputghosts. */
public final class SuperComputerScreen extends LegacyContainerScreen<SuperComputerMenu> {
 public SuperComputerScreen(SuperComputerMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_supercomputer.png"),176,211);}
 private SuperComputerEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof SuperComputerEntity computer?computer:null;}
 @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,70-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,117,4210752,false);}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var computer=machine();if(computer==null)return;var recipe=computer.computerModule.getRecipe();int p=computer.maxPower.signum()>0?computer.power.multiply(BigInteger.valueOf(61)).divide(computer.maxPower).intValue():0;drawLegacyRegion(graphics,152,79-p,176,61-p,16,p);if(computer.computerModule.progress>0)drawLegacyRegion(graphics,62,81,176,61,(int)Math.ceil(70*computer.computerModule.progress),16);if(computer.didProcess){drawLegacyRegion(graphics,51,76,195,0,3,6);drawLegacyRegion(graphics,56,76,195,0,3,6);}else if(recipe!=null){drawLegacyRegion(graphics,51,76,192,0,3,6);if(computer.power.compareTo(BigInteger.valueOf(recipe.power()))>=0)drawLegacyRegion(graphics,56,76,192,0,3,6);}graphics.renderItem(recipe!=null?recipe.icon():new ItemStack(Blueprints.FOLDER.get()),leftPos+8,topPos+81);
  if(recipe!=null){for(int i=0;i<Math.min(recipe.inputItems().size(),3);i++){var slot=menu.getSlot(i+2);if(slot.hasItem())continue;var examples=recipe.inputItems().get(i).processingExamples();if(!examples.isEmpty())graphics.renderItem(examples.get((int)(System.currentTimeMillis()/1000%examples.size())),leftPos+slot.x,topPos+slot.y);}graphics.pose().pushPose();graphics.pose().translate(0,0,300);graphics.setColor(1,1,1,.5F);for(int i=0;i<Math.min(recipe.inputItems().size(),3);i++){var slot=menu.getSlot(i+2);if(!slot.hasItem())drawLegacyRegion(graphics,slot.x,slot.y,slot.x,slot.y,16,16);}graphics.setColor(1,1,1,1);graphics.pose().popPose();}tank(graphics,computer.inputTank,8);tank(graphics,computer.outputTank,80);
 }
 private void tank(GuiGraphics graphics,FluidTank tank,int x){if(tank.getMaxFill()==0)return;int fill=tank.getFill()*52/tank.getMaxFill();if(fill<=0)return;graphics.flush();RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.setShader(GameRenderer::getPositionTexColorShader);RenderSystem.setShaderTexture(0,tank.getTankType().getTexture());var output=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);int color=0xff000000|tank.getTankType().getTint();float a=leftPos+x,b=topPos+54,c=a+fill,d=b+16,v=1-fill/16F;var matrix=graphics.pose().last().pose();output.addVertex(matrix,a,d,0).setUv(1,1).setColor(color);output.addVertex(matrix,c,d,0).setUv(v,1).setColor(color);output.addVertex(matrix,c,b,0).setUv(v,0).setColor(color);output.addVertex(matrix,a,b,0).setUv(1,0).setColor(color);BufferUploader.drawWithShader(output.buildOrThrow());RenderSystem.disableBlend();}
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var computer=machine();if(computer==null)return;var recipe=computer.computerModule.getRecipe();if(legacyInfoHovered(x,y,7,80,18,18))graphics.renderComponentTooltip(font,recipe==null?List.of(Component.translatable("gui.recipe.setRecipe").withStyle(ChatFormatting.YELLOW)):recipe.tooltip(),x,y);if(legacyInfoHovered(x,y,152,18,16,61))graphics.renderTooltip(font,Component.literal(computer.power+" / "+computer.maxPower+" HE"),x,y);tankInfo(graphics,computer.inputTank,x,y,8);tankInfo(graphics,computer.outputTank,x,y,80);}
 private void tankInfo(GuiGraphics graphics,FluidTank tank,int x,int y,int left){if(!legacyInfoHovered(x,y,left,54,52,16))return;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0)lines.add(Component.literal(tank.getPressure()+" PU"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
 @Override public boolean mouseClicked(double x,double y,int button){if(legacyInfoHovered(x,y,7,80,18,18)){var computer=machine();if(computer!=null){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));GenericRecipeSelectorScreen.open(computer.recipes(),computer.computerModule.recipeName,Blueprints.pool(menu.getSlot(1).getItem()),this,selection->PacketDistributor.sendToServer(new SuperComputerSelection(menu.containerId,0,selection)));return true;}}return super.mouseClicked(x,y,button);}
}

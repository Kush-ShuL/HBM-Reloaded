package com.hbm.client.reactor.iter.torus;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.recipe.generic.GenericRecipeSelectorScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.battery.BatteryItem;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.reactor.iter.*;
import com.hbm.machine.reactor.iter.torus.*;
import com.hbm.recipe.generic.Blueprints;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
/** Exact source230×244 GUI, needle geometry/oldZrotation, LEDs,6fluids and true recipe selector. */
public final class TorusScreen extends LegacyContainerScreen<TorusMenu> {
 public TorusScreen(TorusMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_fusion_torus.png"),230,244);}
 private TorusEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof TorusEntity torus?torus:null;}
 @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,106-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,35,151,4210752,false);var torus=machine();if(torus!=null){graphics.drawString(font,"\u00a7b/123K",190,32,4210752,false);int heat=(int)Math.ceil(torus.temperature);String label=(heat>123?"\u00a7c":"\u00a7b")+heat+"K";graphics.drawString(font,label,220-font.width(label),22,4210752,false);}}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var torus=machine();if(torus==null)return;var recipe=(FusionRecipe)torus.fusionModule.getRecipe();int p=(int)(torus.getPower()*62/torus.getMaxPower());drawLegacyRegion(graphics,8,80-p,230,62-p,16,p);if(torus.fusionModule.progress>0)drawLegacyRegion(graphics,98,81,0,244,(int)Math.ceil(70*torus.fusionModule.progress),6);if(torus.fusionModule.bonus>0)drawLegacyRegion(graphics,98,91,0,250,(int)Math.min(Math.ceil(70*torus.fusionModule.bonus),70),6);
  if(recipe!=null&&torus.getPower()>=recipe.power())drawLegacyRegion(graphics,160,115,246,14,8,8);if(Math.ceil(torus.temperature)<=123)drawLegacyRegion(graphics,170,115,246,14,8,8);if(torus.didProcess)drawLegacyRegion(graphics,180,115,246,14,8,8);
  if(torus.didProcess){drawLegacyRegion(graphics,87,76,249,0,3,6);drawLegacyRegion(graphics,92,76,249,0,3,6);}else if(recipe!=null){drawLegacyRegion(graphics,87,76,246,0,3,6);if(torus.getPower()>=torus.getPower())drawLegacyRegion(graphics,92,76,246,0,3,6);}
  double input=recipe==null?0:Math.min((double)torus.klystronEnergy/recipe.ignitionTemp(),1.5)/1.5,output=recipe==null?0:Math.min((double)torus.plasmaEnergy/recipe.outputTemp(),1);gauge(graphics,leftPos+52,topPos+124,input);gauge(graphics,leftPos+88,topPos+124,output);gauge(graphics,leftPos+124,topPos+124,torus.fuelConsumption);graphics.renderItem(recipe!=null?recipe.icon():new ItemStack(Blueprints.FOLDER.get()),leftPos+44,topPos+81);int[] xs={44,62,80,152};for(int i=0;i<4;i++)tank(graphics,torus.fuelTanks[i],xs[i],70);tank(graphics,torus.coolantTanks[0],188,98);tank(graphics,torus.coolantTanks[1],206,98);
 }
 private void tank(GuiGraphics graphics,FluidTank tank,int x,int bottom){if(tank.getMaxFill()==0)return;int fill=tank.getFill()*52/tank.getMaxFill();if(fill<=0)return;int color=tank.getTankType().getTint();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);graphics.blit(tank.getTankType().getTexture(),leftPos+x,topPos+bottom-fill,0,16-fill,16,fill,16,16);graphics.setColor(1,1,1,1);}
 private static void gauge(GuiGraphics graphics,int x,int y,double progress){graphics.flush();RenderSystem.setShader(GameRenderer::getPositionColorShader);var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.POSITION_COLOR);float angle=(float)Math.toRadians(-Mth.clamp(progress,0,1)*270-45);double sin=Math.sin(angle),cos=Math.cos(angle);for(int layer=0;layer<2;layer++){float scale=layer==0?1.5F:1;for(double[] point:new double[][]{{0,5},{1,-2},{-1,-2}})buffer.addVertex(graphics.pose().last().pose(),(float)(x+(point[0]*cos+point[1]*sin)*scale),(float)(y+(point[1]*cos-point[0]*sin)*scale),0).setColor(layer==0?0xff000000:0xffa00000);}BufferUploader.drawWithShader(buffer.buildOrThrow());}
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var torus=machine();if(torus==null)return;var recipe=(FusionRecipe)torus.fusionModule.getRecipe();if(legacyInfoHovered(x,y,8,18,16,62))graphics.renderTooltip(font,Component.literal(torus.getPower()+" / "+torus.getMaxPower()+" HE"),x,y);int[] xs={44,62,80,152};for(int i=0;i<4;i++)tankInfo(graphics,torus.fuelTanks[i],x,y,xs[i],18);tankInfo(graphics,torus.coolantTanks[0],x,y,188,46);tankInfo(graphics,torus.coolantTanks[1],x,y,206,46);
  if(legacyInfoHovered(x,y,43,115,18,18))graphics.renderTooltip(font,recipe==null?Component.literal("0KyU / 0KyU"):Component.literal("\u00a7a-> \u00a7r"+BatteryItem.shortNumber(torus.klystronEnergy)+"KyU / "+BatteryItem.shortNumber(recipe.ignitionTemp())+"KyU"),x,y);if(legacyInfoHovered(x,y,79,115,18,18))graphics.renderTooltip(font,recipe==null?Component.literal("0TU / 0TU"):Component.literal("\u00a7c<- \u00a7r"+BatteryItem.shortNumber(torus.plasmaEnergy)+"TU / "+BatteryItem.shortNumber(recipe.outputTemp())+"TU"),x,y);
  if(recipe!=null&&legacyInfoHovered(x,y,115,115,18,18)){var lines=new ArrayList<Component>();for(var fluid:recipe.inputFluids())lines.add(Component.literal("\u00a7a-> \u00a7r"+(int)Math.ceil(fluid.fill*torus.fuelConsumption)+"mB/t ").append(fluid.type.displayName()));graphics.renderComponentTooltip(font,lines,x,y);}if(legacyInfoHovered(x,y,43,80,18,18))graphics.renderComponentTooltip(font,recipe==null?List.of(Component.translatable("gui.recipe.setRecipe").withStyle(ChatFormatting.YELLOW)):recipe.tooltip(),x,y);
 }
 private void tankInfo(GuiGraphics graphics,FluidTank tank,int mouseX,int mouseY,int x,int y){if(!legacyInfoHovered(mouseX,mouseY,x,y,16,52))return;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0)lines.add(Component.literal(tank.getPressure()+" PU").withStyle(ChatFormatting.RED));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,mouseX,mouseY);}
 @Override public boolean mouseClicked(double x,double y,int button){if(legacyInfoHovered(x,y,43,80,18,18)){var torus=machine();if(torus!=null){GenericRecipeSelectorScreen.open(torus.recipes(),torus.fusionModule.getRecipeName(),Blueprints.pool(menu.getSlot(1).getItem()),this,selection->PacketDistributor.sendToServer(new TorusSelection(menu.containerId,0,selection)));return true;}}return super.mouseClicked(x,y,button);}
}

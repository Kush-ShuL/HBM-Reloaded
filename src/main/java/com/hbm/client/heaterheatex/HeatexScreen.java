package com.hbm.client.heaterheatex;
import com.hbm.client.gui.LegacyContainerScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.heaterheatex.*;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
/** Source176x204 GUI with its exact two green5-character unbordered controls and native tank rectangles. */
public final class HeatexScreen extends LegacyContainerScreen<HeatexMenu> {
 private EditBox cycles,delay;
 public HeatexScreen(HeatexMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/machine/gui_heatex.png"),176,204);}
 private HeatexBlockEntity machine(){return minecraft.level.getBlockEntity(menu.position) instanceof HeatexBlockEntity heater?heater:null;}
 @Override protected void init(){super.init();var machine=machine();cycles=field(31,machine==null?24000:machine.amountToCool,0);delay=field(49,machine==null?1:machine.tickDelay,1);}
 private EditBox field(int y,int value,int field){var box=new EditBox(font,leftPos+73,topPos+y,30,10,Component.translatable(field==0?"gui.hbm.heater_heatex.cycles":"gui.hbm.heater_heatex.delay"));box.setTextColor(0x00ff00);box.setTextColorUneditable(0x00ff00);box.setBordered(false);box.setMaxLength(5);box.setValue(Integer.toString(value));box.setResponder(text->{int parsed;try{parsed=Integer.parseInt(text);}catch(NumberFormatException invalid){parsed=0;}PacketDistributor.sendToServer(new HeatexControl(menu.containerId,field,Math.max(parsed,1)));});return addRenderableWidget(box);}
 @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,(176-font.width(title))/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,110,4210752,false);}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var heater=machine();if(heater!=null){tank(graphics,heater.tanks[0],44);tank(graphics,heater.tanks[1],116);}}
 private void tank(GuiGraphics graphics,FluidTank tank,int x){if(tank.getMaxFill()==0)return;int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;int tint=tank.getTankType().getTint();graphics.flush();RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();graphics.setColor((tint>>16&255)/255F,(tint>>8&255)/255F,(tint&255)/255F,1);graphics.blit(tank.getTankType().getTexture(),leftPos+x,topPos+88-height,0,16-height,16,height,16,16);graphics.flush();graphics.setColor(1,1,1,1);RenderSystem.disableBlend();}
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var heater=machine();if(heater!=null){if(legacyInfoHovered(x,y,44,36,16,52))tankInfo(graphics,heater.tanks[0],x,y);if(legacyInfoHovered(x,y,116,36,16,52))tankInfo(graphics,heater.tanks[1],x,y);}if(legacyInfoHovered(x,y,70,26,36,18))graphics.renderTooltip(font,Component.translatable("gui.hbm.heater_heatex.cycles"),x,y);if(legacyInfoHovered(x,y,70,44,36,18))graphics.renderTooltip(font,Component.translatable("gui.hbm.heater_heatex.delay"),x,y);}
 private void tankInfo(GuiGraphics graphics,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED));}FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
 @Override public boolean keyPressed(int key,int scan,int modifiers){EditBox focused=cycles!=null&&cycles.isFocused()?cycles:delay!=null&&delay.isFocused()?delay:null;if(focused!=null){if(focused.keyPressed(key,scan,modifiers))return true;if(key!=GLFW.GLFW_KEY_ESCAPE)return true;}return super.keyPressed(key,scan,modifiers);}
 @Override public boolean charTyped(char value,int modifiers){if(cycles!=null&&cycles.isFocused()&&cycles.charTyped(value,modifiers))return true;if(delay!=null&&delay.isFocused()&&delay.charTyped(value,modifiers))return true;return super.charTyped(value,modifiers);}
}

package com.hbm.client.reactor.pwr;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.reactor.pwr.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Exact source176x188 three-slot GUI, needle/progress/control artwork, text edit and miniature fuel count. */
public final class PwrScreen extends LegacyContainerScreen<PwrMenu> {
    private EditBox field;
    public PwrScreen(PwrMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_pwr.png"),176,188);}
    private PwrControllerBlockEntity controller(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof PwrControllerBlockEntity controller?controller:null;}
    @Override protected void init(){super.init();field=new EditBox(font,leftPos+57,topPos+63,30,8,Component.translatable("gui.hbm.pwr.control"));field.setTextColor(0x00ff00);field.setTextColorUneditable(0x008000);field.setBordered(false);field.setMaxLength(3);var core=controller();field.setValue((100-(core==null?100:core.rodTarget))+"");addWidget(field);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,playerInventoryTitle,8,94,4210752,false);var core=controller();if(core==null)return;String flux=String.format(Locale.US,"%,.1f",core.flux);graphics.pose().pushPose();graphics.pose().scale(.8F,.8F,1);graphics.drawString(font,flux,(int)(165*1.25-font.width(flux)),80,0x00ff00,false);graphics.pose().popPose();}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var core=controller();if(core==null)return;if(core.hullHeat>PwrControllerBlockEntity.hullHeatCapacityBase*.8||core.coreHeat>core.coreHeatCapacity*.8)drawLegacyRegion(graphics,147,0,176,14,26,26);int progress=(int)(core.progress*33/core.processTime);if(progress>0)drawLegacyRegion(graphics,54,33,176,0,progress,14);int control=(int)(core.rodLevel*52/100);if(control>0)drawLegacyRegion(graphics,53,54,176,40,control,2);gauge(graphics,leftPos+124,topPos+40,(double)core.coreHeat/core.coreHeatCapacity);gauge(graphics,leftPos+160,topPos+40,(double)core.hullHeat/PwrControllerBlockEntity.hullHeatCapacityBase);if(core.typeLoaded!=-1&&core.amountLoaded>0){var display=PwrContent.fuel(PwrFuelType.from(core.typeLoaded));graphics.pose().pushPose();graphics.pose().translate(0,0,232);graphics.renderItem(display,leftPos+89,topPos+5);String amount=ChatFormatting.YELLOW+""+core.amountLoaded+"/"+core.rodCount;graphics.pose().scale(.5F,.5F,1);graphics.renderItemDecorations(font,display,(leftPos+89+font.width(amount)/4)*2,(topPos+5+15)*2,amount);graphics.pose().popPose();}tank(graphics,core.tanks[0],8);tank(graphics,core.tanks[1],26);field.render(graphics,x,y,partial);}
    private void tank(GuiGraphics graphics,FluidTank tank,int x){int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int offset=0;offset<height;offset+=16){int strip=Math.min(16,height-offset);graphics.blit(tank.getTankType().getTexture(),leftPos+x,topPos+57-offset-strip,0,16-strip,16,strip,16,16);}graphics.setColor(1,1,1,1);}
    private static void gauge(GuiGraphics graphics,int x,int y,double progress){graphics.flush();RenderSystem.setShader(GameRenderer::getPositionColorShader);var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.POSITION_COLOR);float angle=(float)Math.toRadians(-Mth.clamp(progress,0,1)*270-45);double sin=Math.sin(angle),cos=Math.cos(angle);for(int layer=0;layer<2;layer++){float scale=layer==0?1.5F:1;for(double[] point:new double[][]{{0,5},{1,-2},{-1,-2}})buffer.addVertex(graphics.pose().last().pose(),(float)(x+(point[0]*cos+point[1]*sin)*scale),(float)(y+(point[1]*cos-point[0]*sin)*scale),0).setColor(layer==0?0xff000000:0xff7f0000);}BufferUploader.drawWithShader(buffer.buildOrThrow());}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var core=controller();if(core==null)return;if(legacyInfoHovered(x,y,115,31,18,18))graphics.renderTooltip(font,Component.translatable("gui.hbm.pwr.core",String.format(Locale.US,"%,d",core.coreHeat),String.format(Locale.US,"%,d",core.coreHeatCapacity)),x,y);if(legacyInfoHovered(x,y,151,31,18,18))graphics.renderTooltip(font,Component.translatable("gui.hbm.pwr.hull",String.format(Locale.US,"%,d",core.hullHeat),String.format(Locale.US,"%,d",PwrControllerBlockEntity.hullHeatCapacityBase)),x,y);if(legacyInfoHovered(x,y,52,31,36,18))graphics.renderTooltip(font,Component.literal((int)(core.progress*100/core.processTime)+"%"),x,y);if(legacyInfoHovered(x,y,52,53,54,4))graphics.renderTooltip(font,Component.translatable("gui.hbm.pwr.level",100-Math.round(core.rodLevel*100)/100),x,y);if(core.typeLoaded!=-1&&core.amountLoaded>0&&legacyInfoHovered(x,y,88,4,18,18))graphics.renderTooltip(font,PwrContent.fuel(PwrFuelType.from(core.typeLoaded)),x,y);if(legacyInfoHovered(x,y,8,5,16,52))tankInfo(graphics,core.tanks[0],x,y);if(legacyInfoHovered(x,y,26,5,16,52))tankInfo(graphics,core.tanks[1],x,y);}
    private void tankInfo(GuiGraphics graphics,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);field.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,88,58,18,18))try{double value=Double.parseDouble(field.getValue());if(Double.isFinite(value)){int level=(int)Mth.clamp(value,0,100);field.setValue(level+"");PacketDistributor.sendToServer(new PwrPackets.Control(menu.position(),100-level));minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));return true;}}catch(NumberFormatException ignored){}return handled;}
    @Override public boolean keyPressed(int key,int scan,int modifiers){if(field.keyPressed(key,scan,modifiers))return true;return super.keyPressed(key,scan,modifiers);}
    @Override public boolean charTyped(char character,int modifiers){if(field.charTyped(character,modifiers))return true;return super.charTyped(character,modifiers);}
}

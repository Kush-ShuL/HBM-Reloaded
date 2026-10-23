package com.hbm.client.reactor.iter.klystron;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.battery.BatteryItem;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.reactor.iter.klystron.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Original194×200GUI/threegauges/LEDs/key-edit normalization, unmodified artwork and font positions. */
public final class KlystronScreen extends LegacyContainerScreen<KlystronMenu> {
    private EditBox field;private boolean normalizing;
    public KlystronScreen(KlystronMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_fusion_klystron.png"),194,200);}
    private KlystronEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof KlystronEntity machine?machine:null;}
    @Override protected void init(){super.init();field=new EditBox(font,leftPos+84,topPos+22,102,12,Component.translatable("gui.hbm.klystron.target"));field.setTextColor(0x00ff00);field.setTextColorUneditable(0x00ff00);field.setBordered(false);var machine=machine();field.setValue(machine==null?"0":""+machine.outputTarget);field.setResponder(this::edited);addWidget(field);}
    private void edited(String text){if(normalizing)return;normalizing=true;try{if(text.startsWith("0"))text=text.substring(1);if(text.isEmpty())text="0";if(!field.getValue().equals(text))field.setValue(text);if(text.chars().allMatch(Character::isDigit)){long amount;try{amount=Long.parseLong(text);}catch(NumberFormatException sourceToLongDefault){amount=0;}PacketDistributor.sendToServer(new KlystronControl(menu.position,amount));}}finally{normalizing=false;}}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,115-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,35,imageHeight-93,4210752,false);var machine=machine();if(machine!=null){String result="= "+BatteryItem.shortNumber(machine.outputTarget)+"KyU";if(machine.outputTarget==KlystronEntity.MAX_OUTPUT)result+=" "+Component.translatable("gui.hbm.klystron.maximum").getString();graphics.drawString(font,result,183-font.width(result),40,0x00ff00,false);}}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var machine=machine();if(machine==null)return;int progress=machine.maxPower==0?0:(int)(machine.getPower()*52/machine.maxPower);drawLegacyRegion(graphics,8,70-progress,194,52-progress,16,progress);double outputGauge=machine.outputTarget<=0?0:(double)machine.output/machine.outputTarget,airGauge=(double)machine.compair.getFill()/machine.compair.getMaxFill(),powerGauge=KlystronEntity.speedScaled(machine.maxPower,machine.getPower());if(powerGauge>=.5&&machine.output>0)drawLegacyRegion(graphics,160,71,210,8,8,8);else if(powerGauge>0)drawLegacyRegion(graphics,160,71,210,0,8,8);if(airGauge>=.5&&machine.output>0)drawLegacyRegion(graphics,170,71,210,8,8,8);else if(airGauge>0)drawLegacyRegion(graphics,170,71,210,0,8,8);if(machine.output>=machine.outputTarget&&machine.output>0)drawLegacyRegion(graphics,180,71,210,8,8,8);else if(machine.output>0)drawLegacyRegion(graphics,180,71,210,0,8,8);gauge(graphics,leftPos+52,topPos+80,outputGauge);gauge(graphics,leftPos+88,topPos+80,airGauge);gauge(graphics,leftPos+124,topPos+80,powerGauge);field.render(graphics,x,y,partial);}
    private static void gauge(GuiGraphics graphics,int x,int y,double progress){graphics.flush();RenderSystem.setShader(GameRenderer::getPositionColorShader);var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.POSITION_COLOR);float angle=(float)Math.toRadians(-Mth.clamp(progress,0,1)*270-45);double sin=Math.sin(angle),cos=Math.cos(angle);for(int layer=0;layer<2;layer++){float scale=layer==0?1.5F:1;for(double[] p:new double[][]{{0,5},{1,-2},{-1,-2}})buffer.addVertex(graphics.pose().last().pose(),(float)(x+(p[0]*cos+p[1]*sin)*scale),(float)(y+(p[1]*cos-p[0]*sin)*scale),0).setColor(layer==0?0xff000000:0xffa00000);}BufferUploader.drawWithShader(buffer.buildOrThrow());}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var machine=machine();if(machine==null)return;if(legacyInfoHovered(x,y,8,18,16,52))graphics.renderTooltip(font,Component.literal(machine.getPower()+" / "+machine.maxPower+" HE"),x,y);if(legacyInfoHovered(x,y,43,71,18,18))graphics.renderTooltip(font,Component.literal("\u00a7c<- \u00a7r"+BatteryItem.shortNumber(machine.output)+"KyU / "+BatteryItem.shortNumber(machine.outputTarget)+"KyU"),x,y);if(legacyInfoHovered(x,y,115,71,18,18))graphics.renderTooltip(font,Component.literal("\u00a7a-> \u00a7r"+BatteryItem.shortNumber(machine.output)+"HE / "+BatteryItem.shortNumber(machine.outputTarget)+"HE"),x,y);if(legacyInfoHovered(x,y,76,71,18,18)){var lines=new ArrayList<Component>();lines.add(machine.compair.getTankType().displayName());lines.add(Component.literal(machine.compair.getFill()+" / "+machine.compair.getMaxFill()+" mB"));FluidInfo.append(machine.compair.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}}
    @Override public boolean mouseClicked(double x,double y,int button){boolean result=super.mouseClicked(x,y,button);return field.mouseClicked(x,y,button)||result;}@Override public boolean keyPressed(int key,int scan,int modifiers){if(field.keyPressed(key,scan,modifiers))return true;return super.keyPressed(key,scan,modifiers);}@Override public boolean charTyped(char c,int modifiers){if(field.charTyped(c,modifiers))return true;return super.charTyped(c,modifiers);}
}

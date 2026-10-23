package com.hbm.client.annihilator;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.machine.annihilator.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Exact source176x208 GUI, 20-character green pool field and arbitrary BigInteger monitor tooltip. */
public final class AnnihilatorScreen extends LegacyContainerScreen<AnnihilatorMenu> {
 private EditBox pool;
 public AnnihilatorScreen(AnnihilatorMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_annihilator.png"),176,208);}
 private AnnihilatorEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof AnnihilatorEntity entity?entity:null;}
 @Override protected void init(){super.init();pool=new EditBox(font,leftPos+31,topPos+85,80,8,Component.translatable("gui.hbm.annihilator.pool"));pool.setTextColor(0x00ff00);pool.setTextColorUneditable(0x00ff00);pool.setBordered(false);pool.setMaxLength(20);var machine=machine();pool.setValue(machine==null?"Recycling":machine.pool);pool.setResponder(text->PacketDistributor.sendToServer(new AnnihilatorPackets.Control(menu.position,text)));addWidget(pool);}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,70-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,114,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);pool.render(g,x,y,partial);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var machine=machine();var stack=menu.getSlot(8).getItem();if(machine==null||stack.isEmpty()||!legacyInfoHovered(x,y,151,35,18,18))return;Component name=stack.getItem() instanceof FluidIdentifier identifier?identifier.getType(null,menu.position,stack).displayName():stack.getHoverName();g.renderComponentTooltip(font,List.of(name.copy().append(":"),Component.literal(String.format(Locale.US,"%,d",machine.monitorBigInt))),x,y);}
 @Override public boolean keyPressed(int key,int scan,int modifiers){if(pool.isFocused()&&key!=256){if(pool.keyPressed(key,scan,modifiers)||pool.canConsumeInput())return true;}return super.keyPressed(key,scan,modifiers);}
}

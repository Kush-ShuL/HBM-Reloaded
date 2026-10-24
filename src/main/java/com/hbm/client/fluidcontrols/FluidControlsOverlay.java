package com.hbm.client.fluidcontrols;
import com.hbm.item.settings.client.LegacyInfoSystem;
import com.hbm.network.fluidcontrols.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
/** Source two real look hooks, using the sole no-black-background information panel. Old hidden pump has no invented hook. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class FluidControlsOverlay {
 @SubscribeEvent public static void render(RenderGuiLayerEvent.Pre event){if(!VanillaGuiLayers.CROSSHAIR.equals(event.getName()))return;var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||mc.player==null||!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled()||!(mc.hitResult instanceof BlockHitResult hit))return;var pos=hit.getBlockPos();var block=mc.level.getBlockState(pos).getBlock();
  if(mc.level.getBlockEntity(pos) instanceof FluidSwitchEntity pipe){var fluid=pipe.getFluidType();LegacyInfoSystem.printGeneric(event.getGuiGraphics(),Component.translatable("tile.fluid_switch.name"),0xffff00,0x404000,List.of(fluid.displayName().copy().withStyle(style->style.withColor(fluid.getColor()))));}
  else if(block instanceof FlowPumpBlock pumpBlock){var pump=pumpBlock.core(mc.level,pos);if(pump==null)return;var lines=new ArrayList<Component>();lines.add(Component.literal("-> ").withStyle(ChatFormatting.GREEN).append(pump.tank.getTankType().displayName().copy().withStyle(ChatFormatting.RESET)).append(" ("+pump.tank.getPressure()+" PU): "+pump.bufferSize+"mB/t").append(Component.literal(" ->").withStyle(ChatFormatting.RED)));lines.add(Component.translatable("gui.hbm.fluid_pump.priority").append(" ").append(Component.literal(pump.priority.name()).withStyle(ChatFormatting.YELLOW)));if(pump.redstone)lines.add(Component.translatable("gui.hbm.fluid_pump.redstone").withStyle(ChatFormatting.RED));if(pump.tank.getFill()>0)lines.add(Component.translatable("gui.hbm.fluid_pump.buffered",pump.tank.getFill()));LegacyInfoSystem.printGeneric(event.getGuiGraphics(),Component.translatable("tile.flow_control_pump.name"),0xffff00,0x404000,lines);}
 }
 private FluidControlsOverlay(){}
}

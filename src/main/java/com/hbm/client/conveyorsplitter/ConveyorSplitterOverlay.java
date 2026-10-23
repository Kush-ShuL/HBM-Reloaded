package com.hbm.client.conveyorsplitter;
import com.hbm.item.settings.client.LegacyInfoSystem;import com.hbm.machine.conveyorsplitter.*;import java.util.List;import net.minecraft.client.Minecraft;import net.minecraft.network.chat.Component;import net.minecraft.world.phys.BlockHitResult;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
/** Source printHook ratio at the real crosshair, through the one generic panel with no black background. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ConveyorSplitterOverlay{
 @SubscribeEvent public static void render(RenderGuiLayerEvent.Pre event){if(!VanillaGuiLayers.CROSSHAIR.equals(event.getName()))return;var client=Minecraft.getInstance();if(client.options.hideGui||client.level==null||!(client.hitResult instanceof BlockHitResult hit)||!(client.level.getBlockState(hit.getBlockPos()).getBlock()instanceof ConveyorSplitterBlock block))return;var core=block.findCore(client.level,hit.getBlockPos());if(core==null||!(client.level.getBlockEntity(core)instanceof ConveyorSplitterEntity splitter))return;LegacyInfoSystem.printGeneric(event.getGuiGraphics(),Component.translatable("tile.crane_splitter.name"),0xffff00,0x404000,List.of(Component.translatable("hud.hbm.conveyor_splitter.ratio",splitter.leftRatio,splitter.rightRatio)));}
 private ConveyorSplitterOverlay(){}
}

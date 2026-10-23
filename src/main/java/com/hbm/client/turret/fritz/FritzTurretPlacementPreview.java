package com.hbm.client.turret.fritz;
import com.hbm.client.turret.rotary.RotaryTurretPlacementPreview;
import com.hbm.machine.turret.fritz.FritzTurretBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
/** Reuses the one original2x2 turret bounds/eight-port preview; no duplicate shader/placement engine. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class FritzTurretPlacementPreview {
    @SubscribeEvent public static void render(RenderHighlightEvent.Block event){var player=Minecraft.getInstance().player;if(player!=null&&player.getMainHandItem().getItem() instanceof BlockItem item&&item.getBlock() instanceof FritzTurretBlock block)RotaryTurretPlacementPreview.renderFor(event,block);}
    private FritzTurretPlacementPreview(){}
}

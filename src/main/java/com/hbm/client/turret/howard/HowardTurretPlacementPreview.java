package com.hbm.client.turret.howard;
import com.hbm.client.turret.rotary.RotaryTurretPlacementPreview;
import com.hbm.machine.turret.howard.HowardTurretBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
/** Same original TurretBaseNT geometry and eight ports: reuse the unique exact preview implementation. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class HowardTurretPlacementPreview {
 @SubscribeEvent public static void render(RenderHighlightEvent.Block event){var p=Minecraft.getInstance().player;if(p!=null&&p.getMainHandItem().getItem() instanceof BlockItem item&&item.getBlock() instanceof HowardTurretBlock block)RotaryTurretPlacementPreview.renderFor(event,block);}
}

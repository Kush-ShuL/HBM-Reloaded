package com.hbm.world.structure.templates.materials;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
/** Same START step dispatch as the already migrated source asphalt blocks/slabs, only for this source staircase. */
@EventBusSubscriber(modid="hbm")
public final class TemplateMaterialMovement {
    @SubscribeEvent public static void step(PlayerTickEvent.Pre event){var player=event.getEntity();if(!player.level().isClientSide||player.getAbilities().flying||player.zza==0&&player.xxa==0)return;if(player.level().getBlockState(BlockPos.containing(player.getX(),player.getY()-.01D,player.getZ())).getBlock() instanceof TemplateAsphaltStairs)player.setDeltaMovement(player.getDeltaMovement().multiply(1.5D,1D,1.5D));}
    private TemplateMaterialMovement(){}
}

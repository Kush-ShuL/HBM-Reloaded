package com.hbm.item.debugwand;

import com.hbm.world.structure.CraneDebugBuild;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.HitResult;

/** ItemWandD: only its one active crane.build call, not its many commented experiments. */
public final class DebugWandItem extends Item {
    public DebugWandItem(){super(new Properties().stacksTo(1));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);if(!(level instanceof ServerLevel server))return InteractionResultHolder.consume(stack);
        var eye=player.getEyePosition(1F);CraneDebugBuild.submit(server,player.getUUID(),eye,eye.add(player.getViewVector(1F).scale(500)));
        return InteractionResultHolder.consume(stack);
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flags){tooltip.add(Component.translatable("tooltip.hbm.debug_wand"));}
}

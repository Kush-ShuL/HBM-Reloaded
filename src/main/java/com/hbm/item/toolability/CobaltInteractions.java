package com.hbm.item.toolability;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.*;

/** Retains source's interaction-declaration heuristic without invoking a second block interaction. */
final class CobaltInteractions {
    private static final Map<Class<?>,Boolean> INTERACTIVE=new ConcurrentHashMap<>();
    static void cycle(ServerPlayer player){
        var stack=player.getMainHandItem();if(!(stack.getItem() instanceof SourceAbilityTool tool)||!tool.canOperate(stack))return;
        var hit=player.pick(player.blockInteractionRange(),1,false);
        if(hit instanceof BlockHitResult blockHit&&hit.getType()==HitResult.Type.BLOCK&&INTERACTIVE.computeIfAbsent(player.serverLevel().getBlockState(blockHit.getBlockPos()).getBlock().getClass(),CobaltInteractions::declaresUse))return;
        var config=CobaltPresets.read(stack);if(config.presets.size()<2)return;config.current=player.isShiftKeyDown()?0:(config.current+1)%config.presets.size();config.apply(stack);CobaltPayloads.status(player,config.active());
        player.serverLevel().playSound(null,player,SoundEvents.EXPERIENCE_ORB_PICKUP,SoundSource.PLAYERS,.25F,config.active().isNone()?.75F:1.25F);
    }
    private static boolean declaresUse(Class<?> type){
        // Source compares declaring classes of onBlockActivated. The modern API split it into
        // protected useWithoutItem/useItemOn; only inspect declarations, never access or invoke them.
        for(Class<?> current=type;current!=null&&current!=Block.class;current=current.getSuperclass()){
            try{current.getDeclaredMethod("useWithoutItem",net.minecraft.world.level.block.state.BlockState.class,Level.class,BlockPos.class,Player.class,BlockHitResult.class);return true;}catch(Throwable ignored){}
            try{current.getDeclaredMethod("useItemOn",ItemStack.class,net.minecraft.world.level.block.state.BlockState.class,Level.class,BlockPos.class,Player.class,InteractionHand.class,BlockHitResult.class);return true;}catch(Throwable ignored){}
        }
        return false;
    }
    private CobaltInteractions(){}
}

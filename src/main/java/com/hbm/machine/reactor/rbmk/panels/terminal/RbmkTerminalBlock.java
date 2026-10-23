package com.hbm.machine.reactor.rbmk.panels.terminal;
import com.hbm.machine.reactor.rbmk.panels.RbmkRadioPanelBlock;
import com.hbm.api.tool.ToolableBlock.ToolType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** Original terminal opens onordinary non-sneaking click, not the other panels' screwdriver action. */
public final class RbmkTerminalBlock extends RbmkRadioPanelBlock{
    public RbmkTerminalBlock(){super(()->RbmkTerminalContent.ENTITY.get());}@Override protected MapCodec<? extends RbmkTerminalBlock> codec(){return MapCodec.unit(this);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.isClientSide)OPEN.accept(pos);return InteractionResult.sidedSuccess(level.isClientSide);}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){return false;}
}

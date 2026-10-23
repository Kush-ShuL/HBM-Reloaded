package com.hbm.machine.reactor.rbmk.panels.keypad;
import com.hbm.machine.reactor.rbmk.panels.RbmkRadioPanelBlock;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
public final class RbmkKeypadBlock extends RbmkRadioPanelBlock{
    public RbmkKeypadBlock(){super(()->RbmkKeypadContent.ENTITY.get());}@Override protected MapCodec<? extends RbmkKeypadBlock> codec(){return MapCodec.unit(this);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;float x=(float)(hit.getLocation().x-pos.getX()),y=(float)(hit.getLocation().y-pos.getY()),z=(float)(hit.getLocation().z-pos.getZ());if(x!=0&&x!=1&&z!=0&&z!=1&&hit.getDirection().getAxis()!=Direction.Axis.Y&&level.getBlockEntity(pos) instanceof RbmkKeypadEntity panel){int meta=state.getValue(DummyableBlock.LEGACY_META),index=0;if(meta==2&&x<.5||meta==3&&x>.5||meta==4&&z>.5||meta==5&&z<.5)index=1;if(y<.5)index+=2;if(!panel.keys[index].active)return InteractionResult.PASS;panel.keys[index].click();}return InteractionResult.CONSUME;}
}

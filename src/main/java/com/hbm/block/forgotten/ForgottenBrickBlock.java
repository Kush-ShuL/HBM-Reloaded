package com.hbm.block.forgotten;

import com.hbm.block.ore.*;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Original BlockForgottenBrick's seven icons and one coal hole, distinct from directional red-room brick_red. */
public class ForgottenBrickBlock extends LegacyOreBlock {
    private static OreSpec source(boolean lock){return new OreSpec(lock?"brick_forgotten_lock":"brick_forgotten",lock?"BlockForgottenLock":"BlockForgottenBrick","rock","stone",-1,399_999.6F,0,true,true,null,null,0,1,0,"NONE",0,lock?List.of("0","1","2"):List.of("0","1","2","3","4","5","6"),"COMMON",false,0,false,false);}
    public ForgottenBrickBlock(){this(false);}
    protected ForgottenBrickBlock(boolean lock){super(source(lock),Properties.of().mapColor(MapColor.STONE).strength(-1,399_999.6F).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends LegacyOreBlock> codec(){return MapCodec.unit(this);}
    private InteractionResult activate(BlockState state,Level level,BlockPos pos,Player player){
        if(state.getValue(LEGACY_META)!=3||!player.getMainHandItem().isEmpty())return InteractionResult.PASS;
        // This one-cell source metadata write is never allowed to synchronously read/load a cold notification neighbour.
        if(level instanceof ServerLevel server&&!ForgottenBricksContent.haloLoaded(server,pos))return InteractionResult.PASS;
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(HbmContent.item("coal_eternal")));
        level.setBlock(pos,state.setValue(LEGACY_META,4),3);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(state,level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return activate(state,level,pos,player).consumesAction()?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;}
}

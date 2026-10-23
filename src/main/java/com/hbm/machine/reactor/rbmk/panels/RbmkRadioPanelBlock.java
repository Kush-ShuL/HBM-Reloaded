package com.hbm.machine.reactor.rbmk.panels;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.machine.reactor.rbmk.display.RbmkMiniPanelBlock;
import com.mojang.serialization.MapCodec;
import java.util.function.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Source screwdriver-only configuration inherited by true ROR mini-panels. Interactive panels override their actual front hit behavior. */
public class RbmkRadioPanelBlock extends RbmkMiniPanelBlock implements ToolableBlock{
    public static Consumer<BlockPos> OPEN=pos->{};private final Supplier<? extends BlockEntityType<? extends RbmkRadioPanelEntity>> entity;
    public RbmkRadioPanelBlock(Supplier<? extends BlockEntityType<? extends RbmkRadioPanelEntity>> entity){this.entity=entity;}
    @Override protected MapCodec<? extends RbmkRadioPanelBlock> codec(){return MapCodec.unit(this);}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return entity.get().create(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return type==entity.get()?(world,pos,blockState,tile)->RbmkRadioPanelEntity.tick(world,pos,blockState,(RbmkRadioPanelEntity)tile):null;}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;if(level.isClientSide)OPEN.accept(pos);return true;}
}

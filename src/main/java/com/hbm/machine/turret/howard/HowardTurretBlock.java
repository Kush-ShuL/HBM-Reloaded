package com.hbm.machine.turret.howard;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
/** Original TurretBaseNT's2x2 dimensions, zero offset, eight declared outward faces, half-height selection and collision. */
public final class HowardTurretBlock extends DummyableBlock {
    public final HowardTurretKind kind;public HowardTurretBlock(HowardTurretKind kind){super(Properties.of().strength(5,kind.damaged?360:36).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());this.kind=kind;}
    @Override protected MapCodec<? extends HowardTurretBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{0,0,1,0,1,0};}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Block.box(0,0,0,16,8,16);}@Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return getShape(state,level,pos,context);}
    @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){return kind.damaged?net.minecraft.world.InteractionResult.PASS:super.useWithoutItem(state,level,pos,player,hit);}
    @Override protected java.util.List<net.minecraft.world.item.ItemStack> getDrops(BlockState state,net.minecraft.world.level.storage.loot.LootParams.Builder params){return kind.damaged?java.util.List.of():super.getDrops(state,params);}
    @Override public void setPort(Level level,BlockPos pos,boolean enabled){if(!kind.damaged){super.setPort(level,pos,enabled);return;}var state=level.getBlockState(pos);if(!state.is(this)||isCore(state)||isPort(state)==enabled)return;safely(()->level.setBlock(pos,state.setValue(LEGACY_META,state.getValue(LEGACY_META)+(enabled?6:-6)),UPDATE_ALL));level.invalidateCapabilities(pos);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return DummyableBlock.isCore(state)?new HowardTurretEntity(kind,pos,state):kind.damaged?null:new HowardTurretProxy(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return DummyableBlock.isCore(state)?(world,pos,s,tile)->{if(tile instanceof HowardTurretEntity turret){if(world instanceof ServerLevel server)turret.tickServer(server);else turret.tickClient();}}:null;}
}

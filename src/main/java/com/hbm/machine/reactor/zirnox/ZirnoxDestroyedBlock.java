package com.hbm.machine.reactor.zirnox;
import com.hbm.hazard.HbmHazards;
import com.hbm.material.HbmMaterials;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
/** Source low wreck structure, original material salvage and one scheduled gas/foam reaction after placement. */
public final class ZirnoxDestroyedBlock extends DummyableBlock {
    private static final MapCodec<ZirnoxDestroyedBlock> CODEC=simpleCodec(ignored->new ZirnoxDestroyedBlock());
    public ZirnoxDestroyedBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(100,800).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}@Override public int[] dimensions(){return new int[]{1,0,2,2,2,2};}@Override public int offset(){return 2;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ZirnoxDestroyedEntity(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ZirnoxContent.DESTROYED_ENTITY.get(),ZirnoxDestroyedEntity::tick):null;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return InteractionResult.PASS;}
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){super.onPlace(state,level,pos,old,moving);if(level instanceof ServerLevel server){if(server.random.nextInt(4)==0)ZirnoxDestroyedEntity.flame(server,pos);server.scheduleTick(pos,this,100+server.random.nextInt(20));}}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){var above=pos.above();var block=level.getBlockState(above);if(block.isAir()){if(random.nextInt(10)==0)level.setBlock(above,HbmContent.block("gas_meltdown").defaultBlockState(),3);}else{String id=net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString();if((id.equals("hbm:foam_layer")||id.equals("hbm:block_foam"))&&random.nextInt(25)==0){var core=findCore(level,pos);if(core!=null&&level.getBlockEntity(core) instanceof ZirnoxDestroyedEntity wreck)wreck.onFire=false;}}if(random.nextInt(10)==0&&level.getBlockState(above).isAir())level.setBlock(above,HbmContent.block("gas_meltdown").defaultBlockState(),3);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){var pipe=new ItemStack(HbmContent.item("pipe"),4);pipe.set(HbmHazards.LEGACY_METADATA.get(),HbmMaterials.byName("Steel").id());return List.of(new ItemStack(HbmContent.block("concrete_smooth"),6),pipe,new ItemStack(ZirnoxContent.GRATE.get(),2),new ItemStack(HbmContent.item("debris_metal"),6),new ItemStack(HbmContent.item("debris_graphite"),2),new ItemStack(HbmContent.item("fallout"),4));}
}

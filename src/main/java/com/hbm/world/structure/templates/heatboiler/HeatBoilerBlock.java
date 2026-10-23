package com.hbm.world.structure.templates.heatboiler;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
/** Actual MachineHeatBoiler, not the obsolete MachineBoiler furnace block. */
public final class HeatBoilerBlock extends DummyableBlock {
    public HeatBoilerBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{3,0,1,1,1,1};}
    @Override public int offset(){return 1;}
    /** Original preview-only orange heat-port plane: U/D/F/B/L/R/color, not a fabricated collision volume. */
    public double[][] getAABBExtras(){return new double[][]{{0,0,.5,-.3,.4,-.4,1}};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new HeatBoilerBlockEntity(pos,state):new HeatBoilerProxy(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,HeatBoilerContent.ENTITY.get(),HeatBoilerBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){super.setPlacedBy(level,pos,state,player,stack);if(level.isClientSide)return;var core=findCore(level,pos);if(core==null)return;for(var port:HeatBoilerBlockEntity.portPositions(core,level.getBlockState(core)))setPort(level,port,true);if(HeatBoilerContent.metadata(stack)==1&&level.getBlockEntity(core) instanceof HeatBoilerBlockEntity boiler){boiler.hasExploded=true;boiler.setChanged();}}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(!player.isShiftKeyDown()&&stack.getItem() instanceof FluidIdentifier identifier){if(level.isClientSide)return ItemInteractionResult.SUCCESS;var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof HeatBoilerBlockEntity boiler))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;var type=identifier.getType(level,core,stack);if(boiler.identify(type))player.displayClientMessage(Component.translatable("message.hbm.heat_boiler.type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);return ItemInteractionResult.CONSUME;}return super.useItemOn(stack,state,level,pos,player,hand,hit);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return level.isClientSide||player.isShiftKeyDown()?InteractionResult.SUCCESS:InteractionResult.PASS;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){if(!replacement.is(this)&&level.getBlockEntity(pos) instanceof HeatBoilerProxy proxy){var parent=pos.relative(Direction.from3DDataValue(state.getValue(LEGACY_META)%6).getOpposite());var core=findCore(level,parent);if(core!=null&&level.getBlockEntity(core) instanceof HeatBoilerBlockEntity boiler)proxy.rememberDropState(boiler.hasExploded);}super.onRemove(state,level,pos,replacement,moving);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){var captured=params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);if(captured instanceof HeatBoilerBlockEntity boiler&&boiler.hasExploded||captured instanceof HeatBoilerProxy proxy&&proxy.burstForDrops())return salvage();var origin=params.getOptionalParameter(LootContextParams.ORIGIN);if(origin!=null){var core=findCore(params.getLevel(),BlockPos.containing(origin));if(core!=null&&params.getLevel().getBlockEntity(core) instanceof HeatBoilerBlockEntity boiler&&boiler.hasExploded)return salvage();}return List.of(new ItemStack(this));}
    private List<ItemStack> salvage(){return List.of(new ItemStack(HbmContent.item("ingot_steel"),4),new ItemStack(HbmContent.item("plate_copper"),8));}
}

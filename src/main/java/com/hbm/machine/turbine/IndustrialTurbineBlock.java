package com.hbm.machine.turbine;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
/** Original3x3x7 offset3 body and physical front lever; ordinary dummy cells keep their collision. */
public final class IndustrialTurbineBlock extends DummyableBlock {
    private static final MapCodec<IndustrialTurbineBlock> CODEC=simpleCodec(ignored->new IndustrialTurbineBlock());
    public IndustrialTurbineBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}
    @Override public int[] dimensions(){return new int[]{2,0,3,3,1,1};}
    @Override public int offset(){return 3;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new IndustrialTurbineBlockEntity(pos,state):new IndustrialTurbineProxy(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,IndustrialTurbineContent.ENTITY.get(),IndustrialTurbineBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=findCore(level,pos);if(core==null)return;BlockState actual=level.getBlockState(core);for(var port:IndustrialTurbineBlockEntity.fluidPortPositions(core,actual))setPort(level,port,true);setPort(level,IndustrialTurbineBlockEntity.powerPortPosition(core,actual),true);}
    private InteractionResult activate(Level level,BlockPos pos,Player player){
        if(player.isShiftKeyDown())return InteractionResult.PASS;
        var core=findCore(level,pos);if(core==null)return InteractionResult.SUCCESS;
        if(!(level.getBlockEntity(core) instanceof IndustrialTurbineBlockEntity turbine))return InteractionResult.PASS;
        Direction direction=facing(turbine.getBlockState());if(!pos.equals(core.relative(direction,3).above()))return InteractionResult.PASS;
        if(!level.isClientSide){if(!turbine.operational){level.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,HbmSounds.get("block.chungusLever"),SoundSource.BLOCKS,1.5F,1);turbine.onLeverPull();}else player.displayClientMessage(Component.translatable("message.hbm.industrial_turbine.running").withStyle(ChatFormatting.RED),false);}
        return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(activate(level,pos,player).consumesAction())return ItemInteractionResult.SUCCESS;return super.useItemOn(stack,state,level,pos,player,hand,hit);}
}

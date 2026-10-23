package com.hbm.machine.soyuzlauncher;

import com.hbm.multiblock.*;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

/** Deprecated but actively registered source SoyuzLauncher. Fixed EAST, core four blocks above its base. */
public final class SoyuzLauncherBlock extends DummyableBlock {
    private static final ThreadLocal<Boolean> ASSEMBLING=ThreadLocal.withInitial(()->false);
    private final Random drops=new Random();
    public SoyuzLauncherBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(defaultBlockState().setValue(LEGACY_META,15));}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(properties->new SoyuzLauncherBlock());}
    @Override public int[] dimensions(){return new int[]{0,0,0,0,0,0};}
    @Override public int heightOffset(){return 4;}
    @Override protected BlockPos coreForPlacement(BlockPlaceContext context){return context.getClickedPos().above(4);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        var core=context.getClickedPos().above(4);
        for(var p:SoyuzLauncherLayout.parts(core))if(!context.getLevel().isInWorldBounds(p)||!context.getLevel().hasChunkAt(p)||!p.equals(context.getClickedPos())&&!context.getLevel().getBlockState(p).canBeReplaced())return null;
        return defaultBlockState().setValue(LEGACY_META,0); // Invisible DOWN parent prediction until the true elevated core arrives.
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        if(!level.isClientSide){
            // This clicked META0 cell is only a placement prediction, not a layout
            // part. Match the parent's guarded removal before building the core.
            if(state.is(this)&&state.getValue(LEGACY_META)==0&&level.getBlockState(pos).equals(state))safely(()->level.removeBlock(pos,false));
            assemble(level,pos.above(4));
            if(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)&&level.getBlockEntity(pos.above(4)) instanceof NamedMultiblock named)named.setCustomName(stack.getHoverName());
        }
    }
    public void assemble(Level level,BlockPos core){boolean prior=ASSEMBLING.get();ASSEMBLING.set(true);try{safely(()->{
        level.setBlock(core,defaultBlockState(),UPDATE_ALL);
        for(var p:SoyuzLauncherLayout.parts(core))if(!p.equals(core)){
            Direction away=p.getY()<core.getY()?Direction.DOWN:p.getY()>core.getY()?Direction.UP:p.getX()<core.getX()?Direction.WEST:p.getX()>core.getX()?Direction.EAST:p.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;
            level.setBlock(p,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);
        }
        // Source makeExtra does not create absent cells (notably the upper perimeter layer).
        for(int x=-6;x<=6;x++)for(int z=-6;z<=6;z++)if(x==6||x==-6||z==6||z==-6)for(int y=0;y<2;y++){
            var p=core.offset(x,y,z);if(level.getBlockState(p).is(this)&&!isCore(level.getBlockState(p)))setPort(level,p,true);
        }
    });}finally{ASSEMBLING.set(prior);}}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new SoyuzLauncherBlockEntity(pos,state):isPort(state)?new SoyuzLauncherPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,SoyuzLauncherContent.ENTITY.get(),SoyuzLauncherBlockEntity::tick):null;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){
        if(!level.isClientSide&&!player.isShiftKeyDown()&&findCore(level,pos)!=null){var persisted=player.getPersistentData().getCompound("PlayerPersisted");persisted.putLong("fbiMark",level.getGameTime()+20L*60*20);player.getPersistentData().put("PlayerPersisted",persisted);}
        return super.useWithoutItem(state,level,pos,player,hit);
    }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return List.of();}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){
        if(!replacement.is(this)&&!level.isClientSide&&!ASSEMBLING.get()&&isCore(state)&&level.getBlockEntity(pos) instanceof SoyuzLauncherBlockEntity machine){
            for(int slot=0;slot<27;slot++){
                var stack=machine.getItem(slot);if(stack.isEmpty())continue;float x=drops.nextFloat()*.8F+.1F,y=drops.nextFloat()*.8F+.1F,z=drops.nextFloat()*.8F+.1F;
                while(!stack.isEmpty()){var drop=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack.split(Math.min(drops.nextInt(21)+10,stack.getCount())));drop.setDeltaMovement((float)drops.nextGaussian()*.05F,(float)drops.nextGaussian()*.05F+.2F,(float)drops.nextGaussian()*.05F);level.addFreshEntity(drop);}
            }
            for(int i=0;i<6;i++)material(level,pos,"struct_launcher",64);for(int i=0;i<4;i++)material(level,pos,"concrete_smooth",64);for(int i=0;i<6;i++)material(level,pos,"struct_scaffold",64);
            material(level,pos,"struct_launcher",30);material(level,pos,"struct_scaffold",63);material(level,pos,"concrete_smooth",38);
            level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,new ItemStack(SoyuzLauncherContent.STRUCTURE_ITEM.get())));
            level.updateNeighbourForOutputSignal(pos,this);
        }
        super.onRemove(state,level,pos,replacement,moving);
    }
    private void material(Level level,BlockPos pos,String symbol,int count){level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,new ItemStack(HbmContent.block(symbol),count)));}
}

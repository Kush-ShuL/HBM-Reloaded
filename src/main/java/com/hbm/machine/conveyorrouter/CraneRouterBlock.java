package com.hbm.machine.conveyorrouter;

import api.hbm.conveyor.*;
import com.hbm.conveyor.MovingItem;
import com.hbm.conveyor.MovingPackage;
import com.hbm.conveyor.belt.ConveyorWandItem;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.*;

/** Actual CraneRouter, independently of BlockCraneBase: six filters, no input/output-side override. */
public final class CraneRouterBlock extends BaseEntityBlock implements IEnterableBlock {
    public CraneRouterBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new CraneRouterEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return (world,pos,s,tile)->{if(tile instanceof CraneRouterEntity router)router.serverTick();};}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return stack.getItem()instanceof ConveyorWandItem?ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.getMainHandItem().getItem()instanceof ConveyorWandItem)return InteractionResult.PASS;if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.getBlockEntity(pos)instanceof CraneRouterEntity router)player.openMenu(router);return InteractionResult.CONSUME;}
    @Override public boolean canItemEnter(Level level,int x,int y,int z,Direction side,IConveyorItem item){return true;}
    @Override public boolean canPackageEnter(Level level,int x,int y,int z,Direction side,IConveyorPackage pack){return true;}
    @Override public void onItemEnter(Level level,int x,int y,int z,Direction side,IConveyorItem item){
        var pos=new BlockPos(x,y,z);var sorted=sort(level,pos,item.getItemStack());
        for(int i=0;i<7;i++)for(var stack:sorted.get(i))sendItem(level,pos,stack,i==6?null:Direction.from3DDataValue(i));
    }
    @Override public void onPackageEnter(Level level,int x,int y,int z,Direction side,IConveyorPackage pack){
        var pos=new BlockPos(x,y,z);var sorted=sort(level,pos,pack.getItemStacks());
        for(int i=0;i<7;i++){
            var stacks=sorted.get(i);if(stacks.isEmpty())continue;
            if(i==6){for(var stack:stacks)drop(level,Vec3.atCenterOf(pos),stack);continue;}
            var direction=Direction.from3DDataValue(i);var next=pos.relative(direction);var output=output(pos,direction);
            if(level.getBlockState(next).getBlock()instanceof IConveyorBelt belt){
                var moving=new MovingPackage(level);moving.setPos(belt.getClosestSnappingPosition(level,next.getX(),next.getY(),next.getZ(),output));moving.setItemStacks(stacks.toArray(ItemStack[]::new));level.addFreshEntity(moving);
            }else for(var stack:stacks)drop(level,output,stack);
        }
    }
    private static void sendItem(Level level,BlockPos pos,ItemStack stack,Direction direction){
        if(direction==null){drop(level,Vec3.atCenterOf(pos),stack);return;}
        var next=pos.relative(direction);var output=output(pos,direction);
        if(level.getBlockState(next).getBlock()instanceof IConveyorBelt belt){var moving=new MovingItem(level);moving.setPos(belt.getClosestSnappingPosition(level,next.getX(),next.getY(),next.getZ(),output));moving.setItemStack(stack);level.addFreshEntity(moving);}else drop(level,output,stack);
    }
    private static Vec3 output(BlockPos pos,Direction side){return Vec3.atCenterOf(pos).add(side.getStepX()*.55,side.getStepY()*.55,side.getStepZ()*.55);}
    private static void drop(Level level,Vec3 point,ItemStack stack){level.addFreshEntity(new ItemEntity(level,point.x,point.y,point.z,stack));}
    /** Source copies only the matching argument, retaining the actual input stacks in the output lists. */
    public static List<List<ItemStack>> sort(Level level,BlockPos pos,ItemStack... stacks){
        var router=(CraneRouterEntity)level.getBlockEntity(pos);var output=new ArrayList<List<ItemStack>>(7);for(int i=0;i<7;i++)output.add(new ArrayList<>());
        for(var stack:stacks)if(stack!=null&&!stack.isEmpty()){Direction direction=getOutputDir(router,stack.copy());output.get(direction==null?6:direction.get3DDataValue()).add(stack);}return output;
    }
    /** Fixed source direction order, blacklist/whitelist first, wildcard only when no route matches. */
    public static Direction getOutputDir(CraneRouterEntity router,ItemStack stack){
        var valid=new ArrayList<Direction>();
        for(int side=0;side<6;side++){
            int mode=router.modes[side];if(mode==CraneRouterEntity.MODE_NONE||mode==CraneRouterEntity.MODE_WILDCARD)continue;
            boolean matches=false;
            for(int slot=0;slot<5;slot++){var filter=router.slots[side*5+slot];if(!filter.isEmpty()&&router.patterns[side].isValidForFilter(filter,slot,stack)){matches=true;break;}}
            if(mode==CraneRouterEntity.MODE_WHITELIST&&matches||mode==CraneRouterEntity.MODE_BLACKLIST&&!matches)valid.add(Direction.from3DDataValue(side));
        }
        if(valid.isEmpty())for(int side=0;side<6;side++)if(router.modes[side]==CraneRouterEntity.MODE_WILDCARD)valid.add(Direction.from3DDataValue(side));
        // nextInt(1) is intentional: source consumes this draw even with a single valid route.
        return valid.isEmpty()?null:valid.get(router.getLevel().random.nextInt(valid.size()));
    }
    // Source has no breakBlock inventory drop: all thirty entries are non-owning patterns.
}

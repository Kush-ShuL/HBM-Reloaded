package com.hbm.worldgen.redroom;

import com.hbm.block.door.*;
import com.hbm.machine.pedestal.*;
import com.hbm.registry.*;
import com.hbm.world.structure.loot.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;

/** Original two9x9x5 rooms, decorations, source pools and post-placement item cleanup. */
public final class RedRoomGenerator {
    public static InteractionResult open(Level level,BlockPos pos,Player player,Direction side,boolean black){
        if(side.getAxis()==Direction.Axis.Y)return InteractionResult.PASS;
        var held=player.getMainHandItem();boolean cracked=held.is(HbmContent.item("key_red_cracked"));
        if(!cracked&&!held.is(HbmContent.item("key_red")))return InteractionResult.PASS;
        if(level.isClientSide)return InteractionResult.SUCCESS;
        var server=(ServerLevel)level;
        if(black){LegacyStructureLoot.requireReady(server,"POOL_BLACK_SLAB");LegacyStructureLoot.requireReady(server,"POOL_BLACK_PART");}
        else LegacyStructureLoot.requireReady(server,"POOL_RED_PEDESTAL");
        if(cracked)held.shrink(1);
        BlockPos center=pos.relative(side,-4).below(2);
        if(black)black(server,center,side);else red(server,center);
        var bottom=pos.below();var block=SourceDoorContent.RED.get();
        var state=block.defaultBlockState().setValue(DoorBlock.FACING,side.getOpposite()).setValue(DoorBlock.HINGE,SourceDoorBlock.sourceHinge(level,bottom,side.getOpposite(),block));
        level.setBlock(bottom,state,Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
        level.setBlock(bottom.above(),state.setValue(DoorBlock.HALF,DoubleBlockHalf.UPPER),Block.UPDATE_CLIENTS|Block.UPDATE_KNOWN_SHAPE);
        level.updateNeighborsAt(bottom,block);level.updateNeighborsAt(bottom.above(),block);
        level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("block.lockOpen"),SoundSource.BLOCKS,1,1);
        if(player instanceof ServerPlayer sp){var advancement=sp.server.getAdvancements().get(ResourceLocation.parse("hbm:redroom"));if(advancement!=null)for(var criterion:advancement.value().criteria().keySet())sp.getAdvancements().award(advancement,criterion);}
        return InteractionResult.CONSUME;
    }
    private static BlockState brick(int meta){return RedRooms.BRICK.get().defaultBlockState().setValue(RoomBrickBlock.META,meta);}
    private static void put(ServerLevel level,BlockPos center,int x,int y,int z,BlockState state){level.setBlock(center.offset(x,y,z),state,3);}
    private static void shell(ServerLevel level,BlockPos center,Direction opening){
        for(int i=-4;i<=4;i++)for(int y:new int[]{0,4}){
            put(level,center,i,y,4,brick(6));put(level,center,i,y,-4,brick(6));put(level,center,4,y,i,brick(6));put(level,center,-4,y,i,brick(6));
        }
        for(int y=1;y<=3;y++){
            for(int x:new int[]{-4,4})for(int z:new int[]{-4,4})put(level,center,x,y,z,brick(6));
            for(int j=-3;j<=3;j++){
                if(opening!=Direction.EAST)put(level,center,4,y,j,brick(opening==null?4:6));
                if(opening!=Direction.WEST)put(level,center,-4,y,j,brick(opening==null?5:6));
                if(opening!=Direction.SOUTH)put(level,center,j,y,4,brick(opening==null?2:6));
                if(opening!=Direction.NORTH)put(level,center,j,y,-4,brick(opening==null?3:6));
            }
        }
    }
    private static void interior(ServerLevel level,BlockPos center,boolean black){
        for(int x=-3;x<=3;x++)for(int z=-3;z<=3;z++){
            put(level,center,x,0,z,brick(black?6:1));put(level,center,x,4,z,brick(black?6:0));
            for(int y=1;y<=3;y++)put(level,center,x,y,z,Blocks.AIR.defaultBlockState());
        }
    }
    private static void red(ServerLevel level,BlockPos center){
        var random=level.random;shell(level,center,null);
        if(random.nextInt(1)==0){int side=random.nextInt(4);int[][] coordinates={{4,0,4},{-4,0,5},{0,4,2},{0,-4,3}};var c=coordinates[side];put(level,center,c[0],2,c[1],RedRooms.INNER.get().defaultBlockState().setValue(RoomBrickBlock.META,c[2]));}
        interior(level,center,false);
        for(int off:new int[]{-2,2}){
            put(level,center,3,2,off,Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING,Direction.WEST));
            put(level,center,-3,2,off,Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING,Direction.EAST));
        }
        for(int off:new int[]{-2,2}){
            put(level,center,off,2,3,Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING,Direction.NORTH));
            put(level,center,off,2,-3,Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING,Direction.SOUTH));
        }
        if(random.nextInt(4)==0)for(int x=-3;x<=3;x++)for(int z=-3;z<=3;z++)if(random.nextBoolean())put(level,center,x,3,z,Blocks.COBWEB.defaultBlockState());
        var redConcrete=HbmContent.block("concrete_colored").defaultBlockState().setValue(com.hbm.block.ore.LegacyOreBlock.LEGACY_META,14);
        if(random.nextInt(4)==0)for(int y=1;y<=3;y++)for(int x:new int[]{2,-2})for(int z:new int[]{2,-2})put(level,center,x,y,z,redConcrete);
        if(random.nextInt(4)==0){for(int x:new int[]{3,-3})for(int z:new int[]{3,-3})put(level,center,x,0,z,Blocks.NETHERRACK.defaultBlockState());for(int x:new int[]{3,-3})for(int z:new int[]{3,-3})put(level,center,x,1,z,Blocks.FIRE.defaultBlockState());}
        if(random.nextInt(4)==0)for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)if(x!=0||z!=0)put(level,center,x,0,z,redConcrete);
        if(random.nextInt(4)==0){int[][] positions={{2,3},{1,3},{-2,3},{-1,3},{2,-3},{1,-3},{-2,-3},{-1,-3},{3,2},{3,1},{-3,2},{-3,1},{3,-2},{3,-1},{-3,-2},{-3,-1}};for(var p:positions)put(level,center,p[0],0,p[1],Blocks.LAVA.defaultBlockState());}
        if(random.nextInt(20)==0){
            var pos=center.above();level.setBlock(pos,StructureLootContent.PILE.get().defaultBlockState(),3);
            var loot=(LootPileBlockEntity)level.getBlockEntity(pos);boolean ncr=random.nextInt(5)==0;String prefix=ncr?"ncrpa_":"trenchmaster_";
            for(String suffix:new String[]{"helmet","plate","legs","boots"})loot.add(new ItemStack(HbmContent.item(prefix+suffix)),0,0,0);loot.sync();
        }else pedestal(level,center.above(),LegacyStructureLoot.template(level,"POOL_RED_PEDESTAL",random));
        cleanup(level,center);
    }
    private static void black(ServerLevel level,BlockPos center,Direction opening){
        shell(level,center,opening);interior(level,center,true);
        pedestal(level,center.above(),LegacyStructureLoot.sample(level,"POOL_BLACK_SLAB",level.random));
        for(var offset:new int[][]{{2,0},{-2,0},{0,2},{0,-2}})if(level.random.nextBoolean())pedestal(level,center.offset(offset[0],1,offset[1]),LegacyStructureLoot.sample(level,"POOL_BLACK_PART",level.random));
        cleanup(level,center);
    }
    private static void pedestal(ServerLevel level,BlockPos pos,ItemStack item){level.setBlock(pos,PedestalContent.BLOCK.get().defaultBlockState(),3);((PedestalBlockEntity)level.getBlockEntity(pos)).setItem(item);}
    private static void cleanup(ServerLevel level,BlockPos center){var bounds=new AABB(center.getX()+.5,center.getY(),center.getZ()+.5,center.getX()+.5,center.getY()+5,center.getZ()+.5).inflate(4.5,0,4.5);for(var item:level.getEntitiesOfClass(ItemEntity.class,bounds))item.discard();}
    private RedRoomGenerator(){}
}

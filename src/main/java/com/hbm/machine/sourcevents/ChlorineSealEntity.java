package com.hbm.machine.sourcevents;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.block.gas.GasBlock;
import com.hbm.block.gas.GasContent;
import com.hbm.block.gas.GasKind;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** TileEntityChlorineSeal's single 0..50 random walk; this is the actual existing chlorine block, not an EntityMist. */
public final class ChlorineSealEntity extends BlockEntity {
    private final NuclearChunkAccess access=new NuclearChunkAccess();
    private BlockPos cursor;
    private int index;
    public ChlorineSealEntity(BlockPos pos,BlockState state){super(SourceVentContent.SEAL_ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,ChlorineSealEntity entity){
        if(!(level instanceof ServerLevel server))return;
        if(!server.hasNeighborSignal(pos)){entity.clear();return;}
        try{
            if(entity.cursor==null){entity.cursor=pos;entity.index=0;}
            var gas=GasContent.BLOCKS_BY_KIND.get(GasKind.CHLORINE).get();
            while(entity.index<=50){
                BlockPos next=entity.cursor;boolean outside=server.isOutsideBuildHeight(next);
                if(!outside&&server.getChunkSource().getChunkNow(next.getX()>>4,next.getZ()>>4)==null&&entity.access.get(server,next.getX()>>4,next.getZ()>>4)==null)return;
                BlockState at=server.getBlockState(next);
                // Source GasBlock overrides isReplaceable=true, separately from vanilla material.replaceable.
                boolean replaceable=at.getBlock()instanceof GasBlock||LegacyMaterials.of(at).replaceable();
                if(replaceable){
                    // A real neighbour-updating mutation needs its own ready halo; no synchronous getChunk/join.
                    if(!outside&&entity.access.getWithNeighbors(server,next.getX()>>4,next.getZ()>>4)==null)return;
                    server.setBlock(next,gas.defaultBlockState(),Block.UPDATE_ALL);
                    at=server.getBlockState(next);
                }
                if(!at.is(gas)&&!at.is(SourceVentContent.SEAL.get())){entity.clear();return;}
                int direction=server.random.nextInt(6);
                entity.cursor=switch(direction){case 0->next.east();case 1->next.west();case 2->next.above();case 3->next.below();case 4->next.south();default->next.north();};
                entity.index++;
            }
            entity.clear();
        }catch(RuntimeException|Error failure){entity.clear();throw failure;}
    }
    private void clear(){cursor=null;index=0;access.close();}
    @Override public void setRemoved(){clear();super.setRemoved();}
    @Override public void onChunkUnloaded(){clear();super.onChunkUnloaded();}
}

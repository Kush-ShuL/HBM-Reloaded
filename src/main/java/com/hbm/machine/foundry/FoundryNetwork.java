package com.hbm.machine.foundry;

import com.hbm.material.NtmMaterial;
import java.util.ArrayDeque;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/** FoundryNetwork has no distribution tick in X5846; horizontal nodes only reject incompatible incoming pours. */
public final class FoundryNetwork {
    static final Direction[] HORIZONTAL={Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST};
    private FoundryNetwork() {}
    public static boolean accepts(Level level, BlockPos origin, NtmMaterial material) {
        var queue=new ArrayDeque<BlockPos>();var visited=new HashSet<BlockPos>();queue.add(origin);visited.add(origin);
        while(!queue.isEmpty()) {
            BlockPos pos=queue.removeFirst();
            if(!(level.getBlockEntity(pos) instanceof FoundryBlockEntity node)||node.kind()!=FoundryContent.Kind.CHANNEL)continue;
            if(!node.nodeReady)continue;
            if(node.nodeType!=null&&node.nodeType!=material)return false;
            for(Direction side:HORIZONTAL){BlockPos next=pos.relative(side);if(level.hasChunkAt(next)&&visited.add(next)&&level.getBlockState(next).is(FoundryContent.block(FoundryContent.Kind.CHANNEL)))queue.addLast(next);}
        }
        return true;
    }
}

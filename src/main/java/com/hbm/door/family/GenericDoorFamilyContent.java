package com.hbm.door.family;

import com.hbm.door.generic.*;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Only seven genuinely missing source declarations; the one existing GenericDoor backend owns all state and permissions. */
public final class GenericDoorFamilyContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final Map<GenericDoorDeclaration,DeferredBlock<GenericDoorBlock>> DOORS=new LinkedHashMap<>();
    public static final Map<GenericDoorDeclaration,DeferredItem<BlockItem>> DOOR_ITEMS=new LinkedHashMap<>();
    static{for(var declaration:new GenericDoorDeclaration[]{GenericDoorDeclaration.LARGE_VEHICLE_DOOR,GenericDoorDeclaration.SECURE_ACCESS_DOOR,GenericDoorDeclaration.ROUND_AIRLOCK_DOOR,GenericDoorDeclaration.QE_CONTAINMENT,GenericDoorDeclaration.QE_SLIDING,GenericDoorDeclaration.SLIDING_SEAL_DOOR,GenericDoorDeclaration.WATER_DOOR}){var block=BLOCKS.register(declaration.id,()->new GenericDoorBlock(declaration));DOORS.put(declaration,block);DOOR_ITEMS.put(declaration,ITEMS.registerSimpleBlockItem(declaration.id,block));}}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?DOOR_ITEMS.values().stream().map(item->new ItemStack(item.get())).toList():List.of();}
    public static Block[] withHosts(Block... original){var result=new ArrayList<Block>(Arrays.asList(original));for(var block:DOORS.values())result.add(block.get());return result.toArray(Block[]::new);}
    public static boolean containsItem(Item item){for(var entry:DOOR_ITEMS.values())if(entry.get()==item)return true;return false;}
    /** Do not advance source cell callbacks across a missing/non-FULL chunk. No chunk future, ticket or synchronous lookup is requested. */
    public static boolean animationReady(GenericDoorBlockEntity door){
        if(!(door.getLevel() instanceof ServerLevel server))return true;var facing=com.hbm.multiblock.DummyableBlock.facing(door.getBlockState());int rotation=GenericDoorBlockEntity.sourceRotation(facing);if(facing.getAxis()==net.minecraft.core.Direction.Axis.X)rotation=(rotation+2)&3;
        var chunks=new HashSet<Long>();for(var range:door.declaration().ranges())for(int j=0;j<Math.abs(range[3]);j++)for(int k=0;k<range[4];k++){
            var step=switch(range[5]){case 0->new BlockPos(0,k,(int)Math.signum(range[3])*j);case 1->new BlockPos(k,(int)Math.signum(range[3])*j,0);default->new BlockPos((int)Math.signum(range[3])*j,k,0);};var cell=GenericDoorBlockEntity.rotate(new BlockPos(range[0],range[1],range[2]).offset(step),rotation).offset(door.getBlockPos());
            int x=cell.getX()>>4,z=cell.getZ()>>4;long key=net.minecraft.world.level.ChunkPos.asLong(x,z);if(chunks.add(key)&&server.getChunkSource().getChunkNow(x,z)==null)return false;
            // Source flags3 also notify all six direct neighbours. Vertical neighbours deduplicate to the same chunk.
            for(var side:net.minecraft.core.Direction.values()){var neighbour=cell.relative(side);int nx=neighbour.getX()>>4,nz=neighbour.getZ()>>4;long adjacent=net.minecraft.world.level.ChunkPos.asLong(nx,nz);if(chunks.add(adjacent)&&server.getChunkSource().getChunkNow(nx,nz)==null)return false;}
        }return true;
    }
    private GenericDoorFamilyContent(){}
}

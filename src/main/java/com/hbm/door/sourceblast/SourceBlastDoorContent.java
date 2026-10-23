package com.hbm.door.sourceblast;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;

/** Only source blast_door + dummy_block_blast. No dependency on sliding/generic-door declarations or seal/hatch. */
public final class SourceBlastDoorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<SourceBlastDoorBlock> BLOCK=BLOCKS.register("blast_door",SourceBlastDoorBlock::new);
    public static final DeferredBlock<SourceBlastDoorDummyBlock> DUMMY=BLOCKS.register("dummy_block_blast",SourceBlastDoorDummyBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK),DUMMY_ITEM=ITEMS.registerSimpleBlockItem(DUMMY);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SourceBlastDoorEntity>> ENTITY=ENTITIES.register("blast_door",()->BlockEntityType.Builder.of(SourceBlastDoorEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SourceBlastDoorDummy>> DUMMY_ENTITY=ENTITIES.register("dummy_block_blast",()->BlockEntityType.Builder.of(SourceBlastDoorDummy::new,DUMMY.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(SourceBlastDoorState::register);NeoForge.EVENT_BUS.addListener(SourceBlastDoorBreaks::loaded);NeoForge.EVENT_BUS.addListener(SourceBlastDoorBreaks::tick);NeoForge.EVENT_BUS.addListener(SourceBlastDoorBreaks::unload);NeoForge.EVENT_BUS.addListener(SourceBlastDoorBreaks::stopped);}
    public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("blast_door",ITEM.get(),"dummy_block_blast",DUMMY_ITEM.get());}
    public static boolean full(Level level,BlockPos pos){return level instanceof ServerLevel server?server.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null:level.hasChunkAt(pos);}
    /** The source seven-block column and flags3 callbacks remain wholly inside this already-FULL neighbourhood. No synchronous load. */
    public static boolean neighborsReady(Level level,BlockPos pos){if(level==null)return false;int x=pos.getX()>>4,z=pos.getZ()>>4;if(!(level instanceof ServerLevel server))return level.hasChunkAt(pos);for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++)if(server.getChunkSource().getChunkNow(x+dx,z+dz)==null)return false;return true;}
    static void deferParentBreak(Level level,BlockPos pos){if(level instanceof ServerLevel server)SourceBlastDoorBreaks.submit(server,pos);}
    private SourceBlastDoorContent(){}
}

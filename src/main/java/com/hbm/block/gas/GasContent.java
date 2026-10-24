package com.hbm.block.gas;

import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmDeepslateOres;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GasContent {
    private static final Direction[] COAL_DUST_DIRECTIONS={Direction.DOWN,Direction.UP,Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST};
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ParticleType<?>> PARTICLES=DeferredRegister.create(Registries.PARTICLE_TYPE,"hbm");
    public static final EnumMap<GasKind,DeferredBlock<GasBlock>> BLOCKS_BY_KIND=new EnumMap<>(GasKind.class);
    public static final EnumMap<GasKind,DeferredItem<BlockItem>> ITEMS_BY_KIND=new EnumMap<>(GasKind.class);
    public static final DeferredHolder<ParticleType<?>,SimpleParticleType> CLOUD=PARTICLES.register("gas_cloud",()->new SimpleParticleType(false));
    static{
        for(var kind:GasKind.values()){
            var block=BLOCKS.register(kind.id(),()->new GasBlock(kind));BLOCKS_BY_KIND.put(kind,block);
            ITEMS_BY_KIND.put(kind,ITEMS.register(kind.id(),()->new BlockItem(block.get(),new Item.Properties())));
        }
    }
    private GasContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);PARTICLES.register(bus);HbmExposure.register(bus);NeoForge.EVENT_BUS.addListener(GasContent::brokenCoal);}
    private static void brokenCoal(BlockEvent.BreakEvent event){
        if(!(event.getPlayer() instanceof ServerPlayer)||!(event.getLevel() instanceof ServerLevel level))return;
        var state=event.getState();
        if(!state.is(BlockTags.COAL_ORES)&&!state.is(Blocks.COAL_BLOCK)&&!HbmDeepslateOres.equivalent(state,"ore_lignite"))return;
        var dust=BLOCKS_BY_KIND.get(GasKind.COAL).get().defaultBlockState();
        for(var direction:COAL_DUST_DIRECTIONS){
            var adjacent=event.getPos().relative(direction);
            // Source consumes one world RNG draw per face, even when that neighbour is solid.
            if(level.random.nextInt(2)==0&&level.getBlockState(adjacent).isAir())level.setBlockAndUpdate(adjacent,dust);
        }
    }
    public static List<ItemStack> creativeStacks(){return ITEMS_BY_KIND.values().stream().map(item->new ItemStack(item.get())).toList();}
}

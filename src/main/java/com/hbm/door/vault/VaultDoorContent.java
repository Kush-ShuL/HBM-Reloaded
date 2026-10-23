package com.hbm.door.vault;

import com.hbm.door.generic.GenericDoorBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Real source VaultDoor; existing sole GenericDoor type/state/lock packet and source callback. */
public final class VaultDoorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<VaultDoorBlock> BLOCK=BLOCKS.register("vault_door",VaultDoorBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("vault_door",BLOCK);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Block[] withHost(Block[] previous){var blocks=Arrays.copyOf(previous,previous.length+1);blocks[previous.length]=BLOCK.get();return blocks;}
    /** Original DoorDecl.onDoorUpdate executes before openTicks increments; its fixed-volume sounds intentionally ignore source muffling. */
    public static void beforeTick(GenericDoorBlockEntity door){if(!(door.getLevel() instanceof ServerLevel server))return;String sound=null;if(door.state==GenericDoorBlockEntity.OPENING){if(door.openTicks==0)sound="block.vaultScrapeNew";for(int i=45;i<=115;i+=10)if(door.openTicks==i)sound="block.vaultThudNew";}else if(door.state==GenericDoorBlockEntity.CLOSING){if(door.openTicks==30)sound="block.vaultScrapeNew";for(int i=45;i<=115;i+=10)if(door.openTicks==i)sound="block.vaultThudNew";}if(sound!=null){var pos=door.getBlockPos();server.playSound(null,pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get(sound),SoundSource.BLOCKS,1,1);}}
    private VaultDoorContent(){}
}

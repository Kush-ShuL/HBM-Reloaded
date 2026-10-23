package com.hbm.block.forgotten;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;

/** Actual source hidden block/items only; existing red-room/key/eternal-coal identities are not replaced. */
public final class ForgottenBricksContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<ForgottenBrickBlock> BRICK=BLOCKS.register("brick_forgotten",()->new ForgottenBrickBlock());
    public static final DeferredBlock<ForgottenLockBlock> LOCK=BLOCKS.register("brick_forgotten_lock",ForgottenLockBlock::new);
    public static final DeferredItem<ForgottenBrickItem> BRICK_ITEM=ITEMS.register("brick_forgotten",()->new ForgottenBrickItem(BRICK.get()));
    public static final DeferredItem<ForgottenBrickItem> LOCK_ITEM=ITEMS.register("brick_forgotten_lock",()->new ForgottenBrickItem(LOCK.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);NeoForge.EVENT_BUS.addListener(ForgottenTunnel::loaded);NeoForge.EVENT_BUS.addListener(ForgottenTunnel::tick);NeoForge.EVENT_BUS.addListener(ForgottenTunnel::unload);NeoForge.EVENT_BUS.addListener(ForgottenTunnel::stopped);}
    public static Map<String,Item> indexedItems(){return Map.of("brick_forgotten",BRICK_ITEM.get(),"brick_forgotten_lock",LOCK_ITEM.get());}
    public static boolean ownItem(Item item){return item==BRICK_ITEM.get()||item==LOCK_ITEM.get();}
    public static boolean viewerHidden(Item item){return ownItem(item)||item==com.hbm.registry.HbmContent.item("coal_eternal");}
    /** All legal world nibble variants, including raw fallback icons, for original NEI wildcard hiding. No creative/extra ingredients. */
    public static List<ItemStack> hiddenStacks(){var values=new ArrayList<ItemStack>();for(var item:List.of(BRICK_ITEM.get(),LOCK_ITEM.get()))for(int meta=0;meta<16;meta++){var stack=new ItemStack(item);stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),meta);values.add(stack);}values.add(new ItemStack(com.hbm.registry.HbmContent.item("coal_eternal")));return values;}
    static boolean haloLoaded(ServerLevel level,BlockPos pos){int x=pos.getX()>>4,z=pos.getZ()>>4;for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++)if(level.getChunkSource().getChunkNow(x+dx,z+dz)==null)return false;return true;}
    private ForgottenBricksContent(){}
}

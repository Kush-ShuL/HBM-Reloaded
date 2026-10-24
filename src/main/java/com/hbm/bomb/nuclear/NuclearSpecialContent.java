package com.hbm.bomb.nuclear;

import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Nuclear content that uses timed or variant TileEntities rather than the nine static assemblies. */
public final class NuclearSpecialContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredBlock<NuclearBalefireBlock> BALEFIRE=BLOCKS.register("balefire",NuclearBalefireBlock::new);
 public static final DeferredItem<BasicBlockItem> BALEFIRE_ITEM=ITEMS.register("balefire",()->new BasicBlockItem(BALEFIRE.get(),new Item.Properties(),"Balefire","balefire"));
 public static final DeferredBlock<NuclearDudBlock> DUD=BLOCKS.register("crashed_bomb",NuclearDudBlock::new);
 public static final DeferredItem<NuclearDudItem> DUD_BALEFIRE_ITEM=ITEMS.register("crashed_bomb_balefire",()->new NuclearDudItem(DUD.get(),NuclearDudBlock.Type.BALEFIRE));
 public static final DeferredItem<NuclearDudItem> DUD_CONVENTIONAL_ITEM=ITEMS.register("crashed_bomb_conventional",()->new NuclearDudItem(DUD.get(),NuclearDudBlock.Type.CONVENTIONAL));
 public static final DeferredItem<NuclearDudItem> DUD_NUKE_ITEM=ITEMS.register("crashed_bomb_nuke",()->new NuclearDudItem(DUD.get(),NuclearDudBlock.Type.NUKE));
 public static final DeferredItem<NuclearDudItem> DUD_SALTED_ITEM=ITEMS.register("crashed_bomb_salted",()->new NuclearDudItem(DUD.get(),NuclearDudBlock.Type.SALTED));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<NuclearDudBlockEntity>> DUD_ENTITY=BLOCK_ENTITIES.register("crashed_bomb",()->BlockEntityType.Builder.of(NuclearDudBlockEntity::new,DUD.get()).build(null));
 public static final DeferredHolder<EntityType<?>,EntityType<NuclearBalefireExplosion>> BALEFIRE_EXPLOSION=ENTITIES.register("balefire_explosion",()->EntityType.Builder.<NuclearBalefireExplosion>of(NuclearBalefireExplosion::new,MobCategory.MISC).fireImmune().clientTrackingRange(0).updateInterval(20).build("hbm:balefire_explosion"));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BLOCK_ENTITIES.register(bus);ENTITIES.register(bus);}
 public static List<NuclearDudItem> dudItems(){return List.of(DUD_BALEFIRE_ITEM.get(),DUD_CONVENTIONAL_ITEM.get(),DUD_NUKE_ITEM.get(),DUD_SALTED_ITEM.get());}
 public static ItemStack dudStack(NuclearDudBlock.Type type){return new ItemStack(switch(type){case BALEFIRE->DUD_BALEFIRE_ITEM.get();case CONVENTIONAL->DUD_CONVENTIONAL_ITEM.get();case NUKE->DUD_NUKE_ITEM.get();case SALTED->DUD_SALTED_ITEM.get();});}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("nuke")?dudItems().stream().map(ItemStack::new).toList():List.of();}
 private NuclearSpecialContent(){}
}

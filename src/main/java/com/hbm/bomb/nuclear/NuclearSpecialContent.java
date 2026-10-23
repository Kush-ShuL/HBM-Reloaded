package com.hbm.bomb.nuclear;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.BasicBlockItem;
import java.util.ArrayList;
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
 public static final DeferredItem<NuclearDudItem> DUD_ITEM=ITEMS.register("crashed_bomb",()->new NuclearDudItem(DUD.get()));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<NuclearDudBlockEntity>> DUD_ENTITY=BLOCK_ENTITIES.register("crashed_bomb",()->BlockEntityType.Builder.of(NuclearDudBlockEntity::new,DUD.get()).build(null));
 public static final DeferredHolder<EntityType<?>,EntityType<NuclearBalefireExplosion>> BALEFIRE_EXPLOSION=ENTITIES.register("balefire_explosion",()->EntityType.Builder.<NuclearBalefireExplosion>of(NuclearBalefireExplosion::new,MobCategory.MISC).fireImmune().clientTrackingRange(0).updateInterval(20).build("hbm:balefire_explosion"));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BLOCK_ENTITIES.register(bus);ENTITIES.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("nuke"))return List.of();var result=new ArrayList<ItemStack>();for(int i=0;i<4;i++){var stack=new ItemStack(DUD_ITEM.get());stack.set(HbmHazards.LEGACY_METADATA.get(),i);result.add(stack);}return result;}
 private NuclearSpecialContent(){}
}

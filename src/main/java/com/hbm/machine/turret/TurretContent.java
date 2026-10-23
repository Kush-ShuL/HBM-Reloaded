package com.hbm.machine.turret;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
/** Real Brown/Edwin blocks and the single actually registered AI chip. */
public final class TurretContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<SentryBlock> SENTRY=BLOCKS.register("turret_sentry",()->new SentryBlock(false)),DAMAGED=BLOCKS.register("turret_sentry_damaged",()->new SentryBlock(true));
    public static final DeferredItem<BlockItem> SENTRY_ITEM=ITEMS.register("turret_sentry",()->new BlockItem(SENTRY.get(),new Item.Properties())),DAMAGED_ITEM=ITEMS.register("turret_sentry_damaged",()->new BlockItem(DAMAGED.get(),new Item.Properties()));
    public static final DeferredItem<TurretChipItem> CHIP=ITEMS.register("turret_chip",TurretChipItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SentryEntity>> SENTRY_ENTITY=ENTITIES.register("turret_sentry",()->BlockEntityType.Builder.of(SentryEntity::new,SENTRY.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DamagedSentryEntity>> DAMAGED_ENTITY=ENTITIES.register("turret_sentry_damaged",()->BlockEntityType.Builder.of(DamagedSentryEntity::new,DAMAGED.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<TurretMenu>> MENU=MENUS.register("turret_sentry",()->IMenuTypeExtension.create((id,inventory,buffer)->new TurretMenu(id,inventory,buffer.readBlockPos())));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(TurretControl::register);bus.addListener(TurretEffects::register);}
    public static List<ItemStack> creativeStacks(String tab){return "weapon".equals(tab)?List.of(new ItemStack(SENTRY_ITEM.get()),new ItemStack(DAMAGED_ITEM.get()),new ItemStack(CHIP.get())):List.of();}
    private TurretContent(){}
}

package com.hbm.machine.reactor.rbmk.channel;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;
public final class RbmkFuelChannels {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm"); private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm"); private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    public static final EnumMap<RbmkChannelKind, DeferredBlock<RbmkFuelChannelBlock>> CHANNELS = new EnumMap<>(RbmkChannelKind.class);
    public static final EnumMap<RbmkChannelKind, DeferredItem<BlockItem>> CHANNEL_ITEMS = new EnumMap<>(RbmkChannelKind.class);
    static { for (var kind : RbmkChannelKind.values()) { var block = BLOCKS.register(kind.id, () -> new RbmkFuelChannelBlock(kind)); CHANNELS.put(kind, block); CHANNEL_ITEMS.put(kind, ITEMS.registerSimpleBlockItem(block)); } }
    private static net.minecraft.world.level.block.Block[] blocks() { return CHANNELS.values().stream().map(DeferredBlock::get).toArray(net.minecraft.world.level.block.Block[]::new); }
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RbmkFuelChannelEntity>> ENTITY = ENTITIES.register("rbmk_fuel_channel", () -> BlockEntityType.Builder.of(RbmkFuelChannelEntity::new, blocks()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RbmkChannelPort>> PORT = ENTITIES.register("rbmk_channel_port", () -> BlockEntityType.Builder.of(RbmkChannelPort::new, blocks()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<RbmkChannelMenu>> MENU = MENUS.register("rbmk_fuel_channel", () -> IMenuTypeExtension.create(RbmkChannelMenu::new));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus); bus.addListener(RbmkFuelChannels::capabilities); }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ENTITY.get(), (entity, side) -> side == null ? new InvWrapper(entity) : new SidedInvWrapper(entity, side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PORT.get(), (entity, side) -> side == null ? new InvWrapper(entity) : new SidedInvWrapper(entity, side));
    }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("machine") ? CHANNEL_ITEMS.values().stream().map(item -> new ItemStack(item.get())).toList() : List.of(); }
    private RbmkFuelChannels() { }
}

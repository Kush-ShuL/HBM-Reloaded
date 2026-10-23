package com.hbm.machine.batterysocket;

import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BatterySocketContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final DeferredRegister<EntityType<?>> PROJECTILES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredBlock<BatterySocketBlock> BLOCK=BLOCKS.register("machine_battery_socket",BatterySocketBlock::new);
    public static final DeferredItem<BatterySocketItem> ITEM=ITEMS.register("machine_battery_socket",BatterySocketItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BatterySocketBlockEntity>> ENTITY=ENTITIES.register("machine_battery_socket",()->BlockEntityType.Builder.of(BatterySocketBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BatterySocketPort>> PORT_ENTITY=ENTITIES.register("battery_socket_port",()->BlockEntityType.Builder.of(BatterySocketPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<BatterySocketMenu>> MENU=MENUS.register("machine_battery_socket",()->IMenuTypeExtension.create(BatterySocketMenu::new));
    public static final DeferredHolder<EntityType<?>,EntityType<SocketDischargeBeam>> DISCHARGE=PROJECTILES.register("battery_socket_discharge",()->EntityType.Builder.<SocketDischargeBeam>of(SocketDischargeBeam::new,MobCategory.MISC).sized(.5F,.5F).clientTrackingRange(16).updateInterval(1).fireImmune().noSave().build("hbm:battery_socket_discharge"));
    public static BooleanSupplier detailedTooltip=()->false;
    private BatterySocketContent() {}
    public static void register(IEventBus bus) {BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);PROJECTILES.register(bus);bus.addListener(BatterySocketContent::capabilities);bus.addListener(SocketKnockback::register);}
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT_ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));
    }
    public static List<ItemStack> creativeStacks(String tab) {return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
}

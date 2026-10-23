package com.hbm.machine.turret.richard;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.*;

public final class RichardTurretContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<RichardTurretBlock> BLOCK=BLOCKS.register("turret_richard",RichardTurretBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("turret_richard",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RichardTurretEntity>> ENTITY=ENTITIES.register("turret_richard",()->BlockEntityType.Builder.of(RichardTurretEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RichardTurretProxy>> PROXY=ENTITIES.register("turret_richard_proxy",()->BlockEntityType.Builder.of(RichardTurretProxy::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<RichardTurretMenu>> MENU=MENUS.register("turret_richard",()->IMenuTypeExtension.create((id,inventory,buffer)->new RichardTurretMenu(id,inventory,buffer.readBlockPos())));
    public static ResourceLocation id(String name) { return ResourceLocation.fromNamespaceAndPath("hbm",name); }
    public static void register(IEventBus bus,ModContainer container) { BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(RichardTurretContent::capabilities);container.registerConfig(ModConfig.Type.SERVER,RichardTurretSettings.SPEC,"hbm-turret-richard-server.toml"); }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        // Source access is only ammo1..9 and no extraction. Null-side automation must not bypass ISidedInventory.
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.DOWN:side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PROXY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.DOWN:side));
    }
    public static List<ItemStack> creativeStacks(String tab) { return "weapon".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of(); }
    private RichardTurretContent() {}
}

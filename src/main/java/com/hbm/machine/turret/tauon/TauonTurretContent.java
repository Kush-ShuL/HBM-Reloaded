package com.hbm.machine.turret.tauon;
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

public final class TauonTurretContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<TauonTurretBlock> BLOCK=BLOCKS.register("turret_tauon",TauonTurretBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("turret_tauon",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TauonTurretEntity>> ENTITY=ENTITIES.register("turret_tauon",()->BlockEntityType.Builder.of(TauonTurretEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TauonTurretProxy>> PROXY=ENTITIES.register("turret_tauon_proxy",()->BlockEntityType.Builder.of(TauonTurretProxy::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<TauonTurretMenu>> MENU=MENUS.register("turret_tauon",()->IMenuTypeExtension.create((id,inventory,buffer)->new TauonTurretMenu(id,inventory,buffer.readBlockPos())));
    public static ResourceLocation id(String name) { return ResourceLocation.fromNamespaceAndPath("hbm",name); }
    public static void register(IEventBus bus,ModContainer container) { BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(TauonTurretContent::capabilities);container.registerConfig(ModConfig.Type.SERVER,TauonTurretSettings.SPEC,"hbm-turret-tauon-server.toml"); }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        // Source access is only ammo1..9 and no extraction. Null-side automation must not bypass ISidedInventory.
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.DOWN:side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PROXY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.DOWN:side));
    }
    public static List<ItemStack> creativeStacks(String tab) { return "weapon".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of(); }
    private TauonTurretContent() {}
}

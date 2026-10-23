package com.hbm.machine.turret.fritz;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
public final class FritzTurretContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<FritzTurretBlock> BLOCK=BLOCKS.register("turret_fritz",FritzTurretBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("turret_fritz",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FritzTurretEntity>> ENTITY=ENTITIES.register("turret_fritz",()->BlockEntityType.Builder.of(FritzTurretEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FritzTurretProxy>> PROXY=ENTITIES.register("turret_fritz_proxy",()->BlockEntityType.Builder.of(FritzTurretProxy::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<FritzTurretMenu>> MENU=MENUS.register("turret_fritz",()->IMenuTypeExtension.create((id,inventory,buffer)->new FritzTurretMenu(id,inventory,buffer.readBlockPos())));
    public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return "weapon".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}
    private FritzTurretContent(){}
}

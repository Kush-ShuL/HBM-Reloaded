package com.hbm.machine.barrel;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** The five ordinary source fluid barrels; explosive barrels are a separate module. */
public final class BarrelContent {
    public enum Variant {
        PLASTIC("plastic",12000,SoundType.STONE),CORRODED("corroded",16000,SoundType.METAL),STEEL("steel",16000,SoundType.METAL),TCALLOY("tcalloy",24000,SoundType.METAL),ANTIMATTER("antimatter",16000,SoundType.METAL);
        public final String name;public final int capacity;public final SoundType sound;
        Variant(String name,int capacity,SoundType sound){this.name=name;this.capacity=capacity;this.sound=sound;}
        public String id(){return "barrel_"+name;}
    }
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final Map<Variant,DeferredBlock<BarrelBlock>> BARRELS=new LinkedHashMap<>();
    public static final Map<Variant,DeferredItem<BarrelItem>> BARREL_ITEMS=new LinkedHashMap<>();
    static{for(var variant:Variant.values()){var block=BLOCKS.register(variant.id(),()->new BarrelBlock(variant));BARRELS.put(variant,block);BARREL_ITEMS.put(variant,ITEMS.register(variant.id(),()->new BarrelItem(block.get())));}}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BarrelBlockEntity>> ENTITY=ENTITIES.register("fluid_barrel",()->BlockEntityType.Builder.of(BarrelBlockEntity::new,BARRELS.values().stream().map(DeferredBlock::get).toArray(BarrelBlock[]::new)).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<BarrelMenu>> MENU=MENUS.register("fluid_barrel",()->new MenuType<>((IContainerFactory<BarrelMenu>)BarrelMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(BarrelContent::capabilities);bus.addListener(BarrelModePacket::register);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?BARREL_ITEMS.entrySet().stream().filter(entry->entry.getKey()!=Variant.CORRODED).map(entry->new ItemStack(entry.getValue().get())).toList():List.of();}
    private BarrelContent(){}
}

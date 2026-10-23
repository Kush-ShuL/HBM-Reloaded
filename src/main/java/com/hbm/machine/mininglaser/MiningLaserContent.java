package com.hbm.machine.mininglaser;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class MiningLaserContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<MiningLaserBlock> BLOCK=BLOCKS.register("machine_mining_laser",MiningLaserBlock::new);
    public static final DeferredItem<MiningLaserItem> ITEM=ITEMS.register("machine_mining_laser",()->new MiningLaserItem(BLOCK.get()));
    public static final Map<String,DeferredItem<MiningLaserUpgradeItem>> UPGRADES=new LinkedHashMap<>();
    static{for(String kind:List.of("smelter","shredder","centrifuge","crystallizer","nullifier"))UPGRADES.put(kind,ITEMS.register("upgrade_"+kind,()->new MiningLaserUpgradeItem(kind)));}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MiningLaserEntity>> ENTITY=ENTITIES.register("machine_mining_laser",()->BlockEntityType.Builder.of(MiningLaserEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MiningLaserPort>> PORT=ENTITIES.register("mining_laser_port",()->BlockEntityType.Builder.of(MiningLaserPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<MiningLaserMenu>> MENU=MENUS.register("mining_laser",()->new MenuType<>((IContainerFactory<MiningLaserMenu>)MiningLaserMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(MiningLaserControl::register);bus.addListener(MiningLaserContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(port,side)->port.inventoryPort()?(side==null?new InvWrapper(port):new SidedInvWrapper(port,side)):null);
    }
    public static List<ItemStack> creativeStacks(String tab){if(tab.equals("machine"))return List.of(new ItemStack(ITEM.get()));if(tab.equals("control"))return UPGRADES.values().stream().map(i->new ItemStack(i.get())).toList();return List.of();}
    public static List<ItemStack> indexedItems(){var result=new ArrayList<ItemStack>();result.add(new ItemStack(ITEM.get()));UPGRADES.values().forEach(i->result.add(new ItemStack(i.get())));return List.copyOf(result);}
    private MiningLaserContent(){}
}

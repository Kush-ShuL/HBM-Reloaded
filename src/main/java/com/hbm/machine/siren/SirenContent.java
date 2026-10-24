package com.hbm.machine.siren;

import java.util.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class SirenContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<SirenBlock> BLOCK=BLOCKS.register("machine_siren",SirenBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    private static final List<DeferredItem<CassetteItem>> CASSETTES=registerCassettes();
    private static List<DeferredItem<CassetteItem>> registerCassettes(){var items=new ArrayList<DeferredItem<CassetteItem>>();for(var track:SirenTrack.values())items.add(ITEMS.register("siren_track_"+track.name().toLowerCase(Locale.ROOT),()->new CassetteItem(track)));return List.copyOf(items);}
    public static List<CassetteItem> cassetteItems(){return CASSETTES.stream().map(DeferredItem::get).toList();}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SirenBlockEntity>> ENTITY=ENTITIES.register("machine_siren",()->BlockEntityType.Builder.of(SirenBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<SirenMenu>> MENU=MENUS.register("machine_siren",()->new MenuType<>((IContainerFactory<SirenMenu>)SirenMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(SirenPacket::register);bus.addListener(SirenContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->new SidedInvWrapper(machine,side==null?Direction.UP:side));}
    public static ItemStack cassette(int ordinal){return new ItemStack(CASSETTES.get(SirenTrack.getEnum(ordinal).ordinal()).get());}
    public static Map<String,Item> indexedItems(){var items=new LinkedHashMap<String,Item>();items.put("machine_siren",ITEM.get());for(var track:SirenTrack.values())items.put("siren_track_"+track.name().toLowerCase(Locale.ROOT),CASSETTES.get(track.ordinal()).get());return items;}
    public static List<ItemStack> cassetteStacks(){var stacks=new ArrayList<ItemStack>();for(int i=1;i<SirenTrack.values().length;i++)stacks.add(cassette(i));return stacks;}
    public static List<ItemStack> allStacks(){var stacks=new ArrayList<>(cassetteStacks());stacks.add(0,new ItemStack(ITEM.get()));return stacks;}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):tab.equals("template")?cassetteStacks():List.of();}
    private SirenContent(){}
}

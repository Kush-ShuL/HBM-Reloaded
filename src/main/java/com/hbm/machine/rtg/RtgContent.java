package com.hbm.machine.rtg;

import com.hbm.item.rtg.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;

public final class RtgContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<RtgBlock> BLOCK=BLOCKS.register("machine_rtg_grey",RtgBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RtgBlockEntity>> ENTITY=ENTITIES.register("machine_rtg",()->BlockEntityType.Builder.of(RtgBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<RtgMenu>> MENU=MENUS.register("machine_rtg",()->new MenuType<>(RtgMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final List<DeferredItem<DepletedRtgPelletItem>> DEPLETED=registerDepleted();
    private static List<DeferredItem<DepletedRtgPelletItem>> registerDepleted(){var items=new ArrayList<DeferredItem<DepletedRtgPelletItem>>();for(String material:List.of("bismuth","mercury","neptunium","lead","zirconium","nickel"))items.add(ITEMS.register("pellet_rtg_depleted_"+material,()->new DepletedRtgPelletItem(material)));return List.copyOf(items);}
    /** Source fuel descriptors select their fixed decay output; no saved-stack metadata is read. */
    public static ItemStack depleted(int material){return new ItemStack(DEPLETED.get(material).get());}
    public static final List<DeferredItem<RtgPelletItem>> PELLETS=List.of(
        pellet("pellet_rtg_radium",3,3,3,16F,480000000),pellet("pellet_rtg_weak",5,5,3,1F,480000000),
        pellet("pellet_rtg",10,10,3,87.7F,4800000),pellet("pellet_rtg_strontium",15,15,4,29F,4800000),
        pellet("pellet_rtg_cobalt",15,15,5,5.3F,4800000),pellet("pellet_rtg_actinium",20,20,3,21.8F,4800000),
        pellet("pellet_rtg_americium",20,20,2,4.7F,480000000),pellet("pellet_rtg_polonium",50,50,3,138F,48000),
        pellet("pellet_rtg_gold",100,200,1,2.7F,48000),pellet("pellet_rtg_lead",200,600,0,.3F,48000));
    public static final DeferredBlock<RtgLootCrateBlock> LEAD_CRATE=BLOCKS.register("crate_lead",()->new RtgLootCrateBlock("leadList",true));
    public static final DeferredBlock<RtgLootCrateBlock> METAL_CRATE=BLOCKS.register("crate_metal",()->new RtgLootCrateBlock("metalList",false));
    public static final DeferredItem<BlockItem> LEAD_CRATE_ITEM=ITEMS.registerSimpleBlockItem(LEAD_CRATE),METAL_CRATE_ITEM=ITEMS.registerSimpleBlockItem(METAL_CRATE);
    private static DeferredItem<RtgPelletItem> pellet(String name,int heat,int decayingHeat,int depleted,float halfLife,int unit){return ITEMS.register(name,()->new RtgPelletItem(name,heat,decayingHeat,depleted,halfLife,unit));}
    public static void register(IEventBus bus,ModContainer container){RtgConfig.register(container);SourceRtgRecyclingRecipes.register();BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(RtgContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){var result=new ArrayList<ItemStack>();if(tab.equals("machine"))result.add(new ItemStack(ITEM.get()));if(tab.equals("consumable")){result.add(new ItemStack(LEAD_CRATE_ITEM.get()));result.add(new ItemStack(METAL_CRATE_ITEM.get()));}if(tab.equals("control")){for(var material:DEPLETED)result.add(new ItemStack(material.get()));for(var pellet:PELLETS)result.add(new ItemStack(pellet.get()));}return List.copyOf(result);}
    private RtgContent(){}
}

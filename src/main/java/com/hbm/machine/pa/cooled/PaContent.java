package com.hbm.machine.pa.cooled;
import com.hbm.machine.pa.recipe.PaRecipeContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class PaContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final EnumMap<PaKind,DeferredBlock<PaBlock>> BLOCK=new EnumMap<>(PaKind.class);public static final EnumMap<PaKind,DeferredItem<BlockItem>> ITEM=new EnumMap<>(PaKind.class);static{for(var kind:PaKind.values()){var block=BLOCKS.register(kind.id,()->new PaBlock(kind));BLOCK.put(kind,block);ITEM.put(kind,ITEMS.register(kind.id,()->new PaBlockItem(block.get())));}}
 private static net.minecraft.world.level.block.Block[] blocks(){return BLOCK.values().stream().map(DeferredBlock::get).toArray(net.minecraft.world.level.block.Block[]::new);}
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PaCooledEntity>> ENTITY=ENTITIES.register("pa_cooled",()->BlockEntityType.Builder.of((pos,state)->create(((PaBlock)state.getBlock()).kind,pos,state),blocks()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PaPort>> PORT=ENTITIES.register("pa_port",()->BlockEntityType.Builder.of(PaPort::new,blocks()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<PaMenu>> MENU=MENUS.register("pa_machine",()->new MenuType<>((IContainerFactory<PaMenu>)PaMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static PaCooledEntity create(PaKind kind,BlockPos pos,BlockState state){return switch(kind){case SOURCE->new PaSourceEntity(pos,state);case RFC->new PaRfcEntity(pos,state);case QUADRUPOLE->new PaQuadrupoleEntity(pos,state);case DIPOLE->new PaDipoleEntity(pos,state);case DETECTOR->new PaDetectorEntity(pos,state);};}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(PaControl::register);bus.addListener(PaContent::capabilities);PaRecipeContent.register(bus);}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(be,side)->side==null?new InvWrapper(be):new SidedInvWrapper(be,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(be,side)->be.core()!=null&&be.core().inventoryProxy()?side==null?new InvWrapper(be):new SidedInvWrapper(be,side):null);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?ITEM.values().stream().map(value->new ItemStack(value.get())).toList():List.of();}private PaContent(){}
}

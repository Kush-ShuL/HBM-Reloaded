package com.hbm.machine.reactor.icf.pellet;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
/** One actual source pellet/press family; empty and depleted capsules retain their existing ordinary items. */
public final class IcfPelletPressContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<IcfPelletPressBlock> BLOCK=BLOCKS.register("machine_icf_press",IcfPelletPressBlock::new);
 public static final DeferredItem<BlockItem> ITEM=ITEMS.register("machine_icf_press",()->new BlockItem(BLOCK.get(),new Item.Properties()){
  @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);for(String line:net.minecraft.locale.Language.getInstance().getOrDefault("tile.machine_icf_press.desc").split("\\$"))lines.add(net.minecraft.network.chat.Component.literal(line).withStyle(net.minecraft.ChatFormatting.YELLOW));}
 });
 public static final DeferredItem<IcfPelletItem> PELLET=ITEMS.register("icf_pellet",IcfPelletItem::new);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<IcfPelletPressEntity>> ENTITY=ENTITIES.register("machine_icf_press",()->BlockEntityType.Builder.of(IcfPelletPressEntity::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<IcfPelletPressMenu>> MENU=MENUS.register("machine_icf_press",()->IMenuTypeExtension.create(IcfPelletPressMenu::new));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(IcfPelletPressContent::capabilities);}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(press,side)->side==null?new InvWrapper(press):new SidedInvWrapper(press,side));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):tab.equals("control")?IcfPelletItem.creativeStacks():List.of();}
 private IcfPelletPressContent(){}
}

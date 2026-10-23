package com.hbm.machine.lambda;

import com.hbm.entity.rocket.EntityRocketLambda;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class LambdaContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<LambdaBlock> BLOCK=BLOCKS.register("launchpad_lambda",LambdaBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("launchpad_lambda",BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LambdaEntity>> ENTITY=TILES.register("launchpad_lambda",()->BlockEntityType.Builder.of(LambdaEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LambdaPort>> PORT=TILES.register("launchpad_lambda_port",()->BlockEntityType.Builder.of(LambdaPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<LambdaMenu>> MENU=MENUS.register("launchpad_lambda",()->new MenuType<>((IContainerFactory<LambdaMenu>)LambdaMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityRocketLambda>> ROCKET=ENTITIES.register("entity_lambda_rocket",()->EntityType.Builder.<EntityRocketLambda>of(EntityRocketLambda::new,MobCategory.MISC).sized(.6F,1.8F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_lambda_rocket"));
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);TILES.register(bus);MENUS.register(bus);ENTITIES.register(bus);bus.addListener(LambdaContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->new SidedInvWrapper(machine,side==null?Direction.DOWN:side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->new SidedInvWrapper(machine,side==null?Direction.DOWN:side));}
    public static List<ItemStack> creativeStacks(String tab){return "missile".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}
    private LambdaContent(){}
}

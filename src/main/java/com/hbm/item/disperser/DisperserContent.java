package com.hbm.item.disperser;

import com.hbm.entity.grenade.disperser.EntityDisperserCanister;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidItemContent;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Existing empty shells and Glyphid drop producers are reused; these are the two missing real filled items. */
public final class DisperserContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredItem<DisperserItem> CANISTER=ITEMS.register("disperser_canister",()->new DisperserItem(false)),GLAND=ITEMS.register("glyphid_gland",()->new DisperserItem(true));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityDisperserCanister>> ENTITY=ENTITIES.register("entity_disperser",()->EntityType.Builder.<EntityDisperserCanister>of(EntityDisperserCanister::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_disperser"));
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);}
    /** Called at the exact original per-fluid position by the sole FluidContainerRegistry.rebuild. */
    public static void registerContainers(int index){var type=HbmFluids.fromID(index);FluidContainerRegistry.registerContainer(new FluidContainerRegistry.Container(FluidItemContent.stack(CANISTER.get(),type),new ItemStack(HbmContent.item("disperser_canister_empty")),type,2000));FluidContainerRegistry.registerContainer(new FluidContainerRegistry.Container(FluidItemContent.stack(GLAND.get(),type),new ItemStack(HbmContent.item("glyphid_gland_empty")),type,4000));}
    public static List<ItemStack> creativeStacks(String tab){return "weapon".equals(tab)?variants():List.of();}
    public static List<ItemStack> variants(){var result=new ArrayList<ItemStack>();result.addAll(CANISTER.get().creativeStacks());result.addAll(GLAND.get().creativeStacks());return result;}
    private DisperserContent(){}
}

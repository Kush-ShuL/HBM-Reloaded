package com.hbm.machine.reactor.watz.fuel;

import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.*;

/** Only the actual two source ItemWatzPellet registrations; no reactor shell placeholders. */
public final class WatzFuelContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS=DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS,"hbm");
    public static final DeferredItem<WatzPelletItem> FRESH=ITEMS.register("watz_pellet",()->new WatzPelletItem(false)),DEPLETED=ITEMS.register("watz_pellet_depleted",()->new WatzPelletItem(true));
    private static final DeferredHolder<MapCodec<? extends ICondition>,MapCodec<com.hbm.recipe.ItemTagExistsCondition>> TAG_CONDITION=CONDITIONS.register("item_tag_exists",()->com.hbm.recipe.ItemTagExistsCondition.CODEC);
    public static void register(IEventBus bus){ITEMS.register(bus);CONDITIONS.register(bus);}
    public static ItemStack stack(boolean depleted,int meta){var stack=new ItemStack(depleted?DEPLETED.get():FRESH.get());stack.set(HbmHazards.LEGACY_METADATA.get(),meta);return stack;}
    public static List<ItemStack> stacks(){var result=new ArrayList<ItemStack>();for(boolean depleted:new boolean[]{false,true})for(int i=0;i<WatzFuelType.values().length;i++)result.add(stack(depleted,i));return result;}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?stacks():List.of();}
    private WatzFuelContent(){}
}

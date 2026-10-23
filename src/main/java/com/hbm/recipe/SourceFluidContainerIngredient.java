package com.hbm.recipe;

import com.hbm.fluid.FluidContainerConfig;
import com.hbm.fluid.HbmFluids;
import com.hbm.registry.HbmOreDictionary;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.*;

/** Actual FluidType.getDict(quantity): the source config chooses container/ntmcontainer at use time. */
public record SourceFluidContainerIngredient(String fluid,int amount) implements ICustomIngredient {
 public static final MapCodec<SourceFluidContainerIngredient> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(Codec.STRING.fieldOf("fluid").forGetter(SourceFluidContainerIngredient::fluid),Codec.intRange(1,Integer.MAX_VALUE).fieldOf("amount").forGetter(SourceFluidContainerIngredient::amount)).apply(instance,SourceFluidContainerIngredient::new));
 private static final DeferredRegister<IngredientType<?>> TYPES=DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES,"hbm");
 public static final DeferredHolder<IngredientType<?>,IngredientType<SourceFluidContainerIngredient>> TYPE=TYPES.register("source_fluid_container",()->new IngredientType<>(CODEC));
 public static void register(IEventBus bus){TYPES.register(bus);}
 private String tag(){return "hbm:legacy_ore_dict/"+HbmFluids.fromName(fluid).getDict(amount,FluidContainerConfig.CONTAINER_COMPAT.get()).toLowerCase(Locale.US);}
 @Override public boolean test(ItemStack stack){return !stack.isEmpty()&&HbmOreDictionary.tags(stack).contains(tag());}
 @Override public Stream<ItemStack> getItems(){return HbmOreDictionary.items(tag());}
 @Override public boolean isSimple(){return false;}
 @Override public IngredientType<?> getType(){return TYPE.get();}
}

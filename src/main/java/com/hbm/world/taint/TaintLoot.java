package com.hbm.world.taint;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Original dropFewItems uses two integer draws; modern rounded-float Looting has a different distribution. */
public final class TaintLoot {
 private static final DeferredRegister<LootItemFunctionType<?>> TYPES=DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE,"hbm");
 private static final DeferredHolder<LootItemFunctionType<?>,LootItemFunctionType<SourceQuantity>> TYPE=TYPES.register("source_mob_quantity",()->new LootItemFunctionType<>(SourceQuantity.CODEC));
 public static void register(IEventBus bus){TYPES.register(bus);}
 public static final class SourceQuantity extends LootItemConditionalFunction {
  public static final MapCodec<SourceQuantity> CODEC=RecordCodecBuilder.mapCodec(builder->commonFields(builder).apply(builder,SourceQuantity::new));
  SourceQuantity(List<LootItemCondition> conditions){super(conditions);}
  @Override protected ItemStack run(ItemStack stack,LootContext context){int count=context.getRandom().nextInt(3),looting=0;var killer=context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);if(killer instanceof LivingEntity living){var ench=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);looting=EnchantmentHelper.getItemEnchantmentLevel(ench.getOrThrow(Enchantments.LOOTING),living.getMainHandItem());}if(looting>0)count+=context.getRandom().nextInt(looting+1);stack.setCount(count);return stack;}
  @Override public LootItemFunctionType<? extends LootItemConditionalFunction> getType(){return TYPE.get();}
 }
 private TaintLoot(){}
}

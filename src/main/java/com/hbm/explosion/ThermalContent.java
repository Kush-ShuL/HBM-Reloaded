package com.hbm.explosion;

import com.hbm.block.ore.LegacyOreItem;
import com.hbm.entity.effect.EntityEMPBlast;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThermalContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final List<DeferredItem<LegacyOreItem>> ALL=new ArrayList<>();
    public static final DeferredHolder<EntityType<?>,EntityType<EntityEMPBlast>> EMP=ENTITIES.register("entity_emp_blast",()->EntityType.Builder.<EntityEMPBlast>of(EntityEMPBlast::new,MobCategory.MISC)
            .sized(1.5F,1.5F).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:entity_emp_blast"));
    static {
        for(String id:List.of("frozen_grass","frozen_dirt","frozen_log","frozen_planks")) {
            var block=BLOCKS.register(id,()->new FrozenTerrainBlock(id));
            ALL.add(ITEMS.register(id,()->new LegacyOreItem(block.get())));
        }
    }
    private ThermalContent() { }
    public static void register(IEventBus bus) { BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("block")?ALL.stream().map(item->new ItemStack(item.get())).toList():List.of(); }
}

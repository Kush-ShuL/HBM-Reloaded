package com.hbm.missile.rusted;
import com.hbm.entity.missile.*;
import com.hbm.entity.missile.EntityMissileTier4.EntityMissileDoomsdayRusted;
import com.hbm.item.missile.MissileItem;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.registries.*;
public final class RustedMissileContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredItem<MissileItem> ITEM=ITEMS.register("missile_doomsday_rusted",()->new MissileItem("missile_doomsday_rusted",MissileItem.MissileFormFactor.ATLAS,MissileItem.MissileTier.TIER4).notLaunchable());
    public static final DeferredHolder<EntityType<?>,EntityType<EntityMissileDoomsdayRusted>> ENTITY=ENTITIES.register("entity_missile_doomsday_rusted",()->EntityType.Builder.<EntityMissileDoomsdayRusted>of(EntityMissileDoomsdayRusted::new,MobCategory.MISC).sized(1.5F,1.5F).clientTrackingRange(63).updateInterval(1).build("hbm:entity_missile_doomsday_rusted"));
    public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);bus.addListener((RegisterTicketControllersEvent event)->event.register(EntityMissileBaseNT.TICKETS));}
    public static List<ItemStack> creativeStacks(String tab){return "missile".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}
    private RustedMissileContent(){}
}

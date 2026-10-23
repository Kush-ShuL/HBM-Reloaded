package com.hbm.equipment.npcheadgear;

import com.hbm.armor.LegacyArmorDamageHandler;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.registries.*;

public final class NpcHeadgearContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<LeatherGasMaskItem> OLDE = ITEMS.register("gas_mask_olde", LeatherGasMaskItem::new);
    public static final DeferredItem<MiningHelmetItem> NO9 = ITEMS.register("no9", MiningHelmetItem::new);
    public static final DeferredItem<NpcHeadgearItem> INFAMY = ITEMS.register("mask_of_infamy", () ->
            new NpcHeadgearItem(ArmorMaterials.IRON, 165, "mask_of_infamy", "textures/armor/maskofinfamy.png"));
    public static final DeferredItem<FabulousHatItem> HAT = ITEMS.register("nossy_hat", FabulousHatItem::new);
    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        bus.addListener(NpcHeadgearContent::creative);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, NpcHeadgearContent::attack);
    }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) for (var item : List.of(OLDE, NO9, INFAMY, HAT)) event.accept(item.get());
    }
    private static void attack(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        for (var armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof LegacyArmorDamageHandler handler && handler.sourceCancelAttack(player, armor, event.getSource(), event.getAmount())) event.setCanceled(true);
        }
    }
    private NpcHeadgearContent() {}
}

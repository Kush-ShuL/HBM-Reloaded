package com.hbm.weapon.legacymelee;
import com.hbm.item.legacysword.LegacySwordBlocking;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Remaining actual ModSword identity; lead pipe and crowbar remain their existing real registrations. */
public final class LegacyModSwordItem extends LegacySwordBase implements LegacySwordBlocking {
    public LegacyModSwordItem(){super(LegacyMeleeMaterials.TITANIUM,"reer_graar",0,Rarity.COMMON);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){lines.add(Component.translatable("tooltip.hbm.legacy_melee.reer0"));lines.add(Component.literal("555-10-3728-ZX7-INFINITE"));}
}

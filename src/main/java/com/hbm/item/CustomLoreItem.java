package com.hbm.item;

import com.hbm.hazard.HbmHazards;
import com.hbm.LegacyStartup;
import java.util.List;
import java.util.Random;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** X5846 ItemCustomLore's original keys, dollar-separated lines and glint. */
public class CustomLoreItem extends BasicItem {
    private static int registrySize;
    private final String id;
    private final String legacyKey;
    private final boolean effect;

    public CustomLoreItem(Properties properties, String container, String id, boolean effect) {
        super(properties, container);
        this.id = id;
        this.legacyKey = "item." + id;
        this.effect = effect;
    }

    @Override
    public boolean isFoil(ItemStack stack) { return effect; }

    @Override
    public Component getName(ItemStack stack) {
        if (id.equals("undefined") && stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0) == 99) {
            return Component.literal(UndefinedNameState.NAME.getResult());
        }
        return Component.literal(Language.getInstance().getOrDefault(legacyKey + ".name").trim());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        Language language = Language.getInstance();
        String key = legacyKey + ".desc";
        if (LegacyStartup.polaroidId == 11 && language.has(key + ".P11")) key += ".P11";
        String translated = language.getOrDefault(key);
        if (!translated.equals(key)) {
            for (String line : translated.split("\\$")) lines.add(Component.literal(line));
        }
        if (id.equals("undefined")) {
            if (context.level() == null || context.level().random.nextInt(10) == 0) {
                lines.add(Component.literal("\u00a74UNDEFINED"));
            } else {
                Random random = new Random(System.currentTimeMillis() / 500);
                if (registrySize == 0) registrySize = BuiltInRegistries.ITEM.size();
                int numericId = random.nextInt(registrySize);
                var item = BuiltInRegistries.ITEM.byId(numericId);
                lines.add(item == null ? Component.literal("\u00a7cERROR #" + numericId) : new ItemStack(item).getHoverName());
            }
        }
    }
}

package com.hbm.compat.recipeviewer;

import com.hbm.client.config.ClientEffectsConfig;
import com.hbm.item.EnumMultiItem;
import com.hbm.item.meteoritesword.MeteoriteSwordContent;
import com.hbm.recipe.generic.Blueprints;
import com.hbm.weapon.sedna.ammo.FlatAmmoContent;
import com.hbm.weapon.sedna.ammo.FlatAmmoItem;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** One display policy: global secret entries and recipe exclusions have intentionally distinct scopes. */
public final class RecipeViewerVisibility {
    private static final ResourceLocation SECRET_MATERIAL = ResourceLocation.fromNamespaceAndPath("hbm", "item_secret");

    public static boolean hideSecretsEnabled() { return ClientEffectsConfig.recipeViewerHideSecretsEnabled(); }

    public static boolean secretEntry(ItemStack stack) {
        if (stack.isEmpty()) return false;
        var item = stack.getItem();
        return ItemGunBaseNT.secrets.contains(item)
                || item instanceof FlatAmmoItem ammo && ammo.sourceFamily.equals("ammo_secret")
                || SecretMaterials.ITEMS.contains(item);
    }

    /** Index the real source identities, including all five material variants without a creative tab. */
    public static List<ItemStack> secretStacks() {
        var stacks = new ArrayList<ItemStack>();
        ItemGunBaseNT.secrets.forEach(item -> stacks.add(new ItemStack(item)));
        FlatAmmoContent.indexedItems().stream().filter(RecipeViewerVisibility::secretEntry).forEach(stacks::add);
        SecretMaterials.STACKS.forEach(stack -> stacks.add(stack.copy()));
        return stacks;
    }

    public static boolean visible(List<String> pools, List<List<ItemStack>> inputs, List<List<ItemStack>> outputs) {
        if (!hideSecretsEnabled()) return true;
        if (pools.stream().anyMatch(pool -> pool.startsWith(Blueprints.SECRET))) return false;
        for (var alternatives : inputs) for (var stack : alternatives) if (RecipeExclusions.ITEMS.contains(stack.getItem())) return false;
        for (var possibilities : outputs) for (var stack : possibilities) if (RecipeExclusions.ITEMS.contains(stack.getItem())) return false;
        return true;
    }

    // 秘密材料族的唯一条目与物品身份入口；索引与配方判定共用真实注册实例。
    private static final class SecretMaterials {
        private static final List<ItemStack> STACKS = ((EnumMultiItem) BuiltInRegistries.ITEM.get(SECRET_MATERIAL)).creativeStacks();
        private static final Set<Item> ITEMS = STACKS.stream().map(ItemStack::getItem)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    // 配方排除表按真实物品身份建立；它不等于全局秘密条目表，也不会解除物品自身的独立隐藏规则。
    private static final class RecipeExclusions {
        private static final Set<Item> ITEMS = create();
        private static Set<Item> create() {
            var items = new HashSet<Item>();
            items.addAll(SecretMaterials.ITEMS);
            MeteoriteSwordContent.indexedItems().forEach(stack -> items.add(stack.getItem()));
            return Set.copyOf(items);
        }
    }

    private RecipeViewerVisibility() {}
}

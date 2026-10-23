package com.hbm.armor;

import com.hbm.exposure.ExposureProtection.Hazard;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Original IGasMask behavior; capability is supplied by real implementing items. */
public interface GasMask {
    default List<Hazard> blacklist(ItemStack mask,LivingEntity wearer){return List.of();}
    default ItemStack filter(ItemStack mask,LivingEntity wearer){return GasMaskFilters.filter(mask,wearer.level().registryAccess());}
    default boolean acceptsFilter(ItemStack mask,LivingEntity wearer,ItemStack filter){return true;}
}

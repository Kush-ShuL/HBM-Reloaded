package com.hbm.equipment.vaultclothing;
import com.hbm.armor.*;
import com.hbm.exposure.ExposureProtection.Hazard;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Actual ArmorGasMask mono branch, same original M65 mesh with true mono PNG and distinct filter blacklist. */
public final class MonoGasMaskItem extends VaultArmorItem implements GasMask {
    public MonoGasMaskItem(){super(ArmorMaterials.IRON,Type.HELMET,15,"gas_mask_mono","textures/models/modelm65mono.png");}
    @Override public List<Hazard> blacklist(ItemStack stack,LivingEntity entity){return List.of(Hazard.GAS_LUNG,Hazard.GAS_BLISTERING,Hazard.BACTERIA);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);if(player.isShiftKeyDown()){var filter=filter(stack,player);if(!filter.isEmpty()){GasMaskFilters.remove(stack);if(!player.getInventory().add(filter))player.drop(filter,true);return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}}
        return super.use(level,player,hand);
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){
        GasMaskFilters.tooltip(stack,context,lines,flags);lines.add(Component.translatable("hazard.neverProtects").withStyle(ChatFormatting.RED));
        for(String key:List.of("hazard.gasChlorine","hazard.corrosive","hazard.bacteria"))lines.add(Component.literal(" -").append(Component.translatable(key)).withStyle(ChatFormatting.DARK_RED));
    }
}

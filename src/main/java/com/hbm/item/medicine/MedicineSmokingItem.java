package com.hbm.item.medicine;

import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** ItemCigarette's original lung damage, healing, consumption and smoke. */
public final class MedicineSmokingItem extends Item {
    private final boolean cigarette;
    MedicineSmokingItem(boolean cigarette) {super(new Properties().stacksTo(cigarette?16:1));this.cigarette=cigarette;}
    @Override public Component getName(ItemStack stack) {return Component.translatable("item."+(cigarette?"cigarette":"crackpipe")+".name");}
    @Override public UseAnim getUseAnimation(ItemStack stack) {return UseAnim.BOW;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity) {return 30;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {player.startUsingItem(hand);return InteractionResultHolder.consume(player.getItemInHand(hand));}
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity entity) {
        stack.shrink(1);
        if(!level.isClientSide&&entity instanceof Player player) {
            if(cigarette) {
                HbmExposure.incrementBlackLung(player,2000);HbmExposure.incrementAsbestos(player,2000);HbmExposure.incrementRadiation(player,100);
                var helmet=BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("hbm","no9"));
                if(helmet.isPresent()&&player.getItemBySlot(EquipmentSlot.HEAD).is(helmet.get())&&player instanceof ServerPlayer server)MedicineContent.achievement(server,"no9");
            } else {
                HbmExposure.incrementBlackLung(player,500);player.addEffect(new MobEffectInstance(MobEffects.CONFUSION,200,0));player.heal(10);
            }
            level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("player.cough"),SoundSource.PLAYERS,1,1);MedicineVisuals.smoke(player);
        }
        return stack;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag) {
        if(cigarette)for(String line:List.of("✓ Asbestos filter","✓ High in tar","✓ Tobacco contains 100% Polonium-210","✓ Yum"))tooltip.add(Component.literal(line).withStyle(ChatFormatting.RED));
        else {
            ChatFormatting[] colors={ChatFormatting.RED,ChatFormatting.GOLD,ChatFormatting.YELLOW,ChatFormatting.GREEN,ChatFormatting.AQUA,ChatFormatting.BLUE,ChatFormatting.DARK_PURPLE,ChatFormatting.LIGHT_PURPLE};
            tooltip.add(Component.literal("This can't be good for me, but I feel ").append(Component.literal("GREAT").withStyle(colors[(int)(System.currentTimeMillis()%2000*colors.length/2000)])));
        }
    }
}

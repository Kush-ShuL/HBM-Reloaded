package com.hbm.item.medicine;

import com.hbm.damage.HbmDamageSources;
import com.hbm.exposure.ExposureData;
import com.hbm.exposure.ExposureEffects;
import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;

/** X5846 consumables, including the original final-stack metal-syringe cooldown branch. */
public final class MedicineItem extends Item {
    public enum Kind { PILL, SIMPLE, SYRINGE }
    private final String id;
    private final Kind kind;
    MedicineItem(String id,Kind kind) {
        super(properties(id,kind));this.id=id;this.kind=kind;
    }
    public static MedicineItem sourceJetpackTank() { return new MedicineItem("jetpack_tank",Kind.SYRINGE); }
    private static Properties properties(String id,Kind kind) {
        var props=new Properties();
        if(kind==Kind.PILL)props.food(new FoodProperties.Builder().nutrition(0).saturationModifier(.6F).alwaysEdible().build());
        if(id.equals("syringe_awesome"))props.rarity(Rarity.UNCOMMON);
        if(id.equals("cbt_device"))props.stacksTo(1);
        if(id.equals("jetpack_tank"))props.stacksTo(16);
        return props;
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item."+id+".name"); }
    @Override public boolean isFoil(ItemStack stack) { return id.equals("syringe_awesome"); }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return kind==Kind.PILL?UseAnim.EAT:UseAnim.NONE; }
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity) { return kind==Kind.PILL?10:0; }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {
        ItemStack stack=player.getItemInHand(hand);
        if(kind==Kind.PILL) {
            if(MedicineEffects.hasPotionSickness(player))return InteractionResultHolder.pass(stack);
            player.startUsingItem(hand);return InteractionResultHolder.consume(stack);
        }
        if(level.isClientSide)return InteractionResultHolder.sidedSuccess(stack,true);
        if(kind==Kind.SIMPLE)useSimple(stack,player,player);
        else return InteractionResultHolder.success(useSyringe(stack,player,player,true));
        return InteractionResultHolder.success(stack);
    }
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity entity) {
        if(kind!=Kind.PILL)return stack;
        if(entity instanceof Player player) {
            if(player instanceof ServerPlayer server)CriteriaTriggers.CONSUME_ITEM.trigger(server,stack);
            player.getFoodData().eat(0,.6F);player.awardStat(Stats.ITEM_USED.get(this));
            level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.PLAYER_BURP,SoundSource.PLAYERS,.5F,level.random.nextFloat()*.1F+.9F);
            if(!level.isClientSide)pill(player);
        }
        // ItemFood.onEaten decremented pills even for creative players in the supplied source.
        stack.shrink(1);return stack;
    }
    @Override public boolean hurtEnemy(ItemStack stack,LivingEntity target,LivingEntity source) {
        if(!target.level().isClientSide) {
            if(kind==Kind.SIMPLE&&id.startsWith("syringe_"))useSimple(stack,target,source);
            else if(kind==Kind.SYRINGE)useSyringe(stack,target,source,false);
        }
        return false;
    }
    private static void effect(LivingEntity entity,Holder<MobEffect> effect,int duration,int amplifier) {
        entity.addEffect(new MobEffectInstance(effect,duration,amplifier));
    }
    private static void cleanse(LivingEntity entity) {
        for(var effect:List.of(MobEffects.BLINDNESS,MobEffects.CONFUSION,MobEffects.DIG_SLOWDOWN,MobEffects.HUNGER,MobEffects.MOVEMENT_SLOWDOWN,MobEffects.POISON,MobEffects.WEAKNESS,MobEffects.WITHER,ExposureEffects.RADIATION))entity.removeEffect(effect);
    }
    private void pill(Player player) {
        MedicineEffects.potionSickness(player,5);
        switch(id) {
            case "pill_iodine" -> cleanse(player);
            case "plan_c" -> cyanide(player);
            case "pill_red" -> effect(player,MedicineEffects.DEATH,60*60*20,0);
            case "radx" -> effect(player,ExposureEffects.RADX,3*60*20,0);
            case "siox","pill_herbal" -> {
                HbmExposure.setAsbestos(player,0);HbmExposure.setBlackLung(player,Math.min(HbmExposure.blackLung(player),ExposureData.MAX_BLACK_LUNG/5));
                if(id.equals("pill_herbal")) {
                    HbmExposure.incrementRadiation(player,-100);
                    effect(player,MobEffects.CONFUSION,10*20,0);effect(player,MobEffects.WEAKNESS,10*60*20,2);
                    effect(player,MobEffects.DIG_SLOWDOWN,10*60*20,2);effect(player,MobEffects.POISON,5*20,2);
                    MedicineEffects.uncurable(player,MedicineEffects.POTION_SICKNESS,10*60*20,0);
                }
            }
            case "xanax" -> HbmExposure.setDigamma(player,Math.max(HbmExposure.digamma(player)-.5F,0));
            case "chocolate" -> {
                if(player.getRandom().nextInt(25)==0)player.hurt(HbmDamageSources.source(player.level(),HbmDamageSources.overdose),1000);
                effect(player,MobEffects.DIG_SPEED,60*20,3);effect(player,MobEffects.MOVEMENT_SPEED,60*20,3);effect(player,MobEffects.JUMP,60*20,3);
            }
            case "fmn" -> { HbmExposure.setDigamma(player,Math.min(HbmExposure.digamma(player),2));effect(player,MobEffects.BLINDNESS,60,0); }
            case "five_htp" -> { HbmExposure.setDigamma(player,0);effect(player,ExposureEffects.STABILITY,10*60*20,0); }
            default -> throw new IllegalStateException("Unknown original pill "+id);
        }
    }
    static void cyanide(Player player) {
        for(int i=0;i<10;i++)player.hurt(HbmDamageSources.source(player.level(),player.getRandom().nextBoolean()?HbmDamageSources.euthanizedSelf:HbmDamageSources.euthanizedSelf2),1000);
    }
    private void useSimple(ItemStack stack,LivingEntity target,LivingEntity source) {
        switch(id) {
            case "syringe_antidote" -> {
                if(MedicineEffects.hasPotionSickness(target))return;
                target.removeAllEffects();consume(stack,source,"item.syringe","syringe_empty");MedicineEffects.potionSickness(target,5);
            }
            case "syringe_poison" -> {
                target.hurt(target==source?HbmDamageSources.source(target.level(),target.getRandom().nextBoolean()?HbmDamageSources.euthanizedSelf:HbmDamageSources.euthanizedSelf2):HbmDamageSources.euthanized(source,source),30);
                consume(stack,source,"item.syringe","syringe_empty");
            }
            case "syringe_awesome" -> {
                if(MedicineEffects.hasPotionSickness(target))return;
                consume(stack,source,"item.syringe","syringe_empty");
                effect(target,MobEffects.REGENERATION,1000,9);effect(target,MobEffects.DAMAGE_RESISTANCE,1000,9);effect(target,MobEffects.FIRE_RESISTANCE,1000,0);
                effect(target,MobEffects.DAMAGE_BOOST,1000,24);effect(target,MobEffects.DIG_SPEED,1000,9);effect(target,MobEffects.MOVEMENT_SPEED,1000,6);
                effect(target,MobEffects.JUMP,1000,9);effect(target,MobEffects.HEALTH_BOOST,1000,9);effect(target,MobEffects.ABSORPTION,1000,4);
                effect(target,MobEffects.CONFUSION,100,4);effect(target,ExposureEffects.RADX,1000,9);MedicineEffects.potionSickness(target,5);
            }
            case "iv_empty" -> {
                if(!(source instanceof Player player)||source instanceof FakePlayer)return;
                consume(stack,source,"item.syringe","iv_blood");player.setHealth(Math.max(player.getHealth()-5,0));
                if(player.getHealth()<=0)player.die(player.damageSources().magic());
            }
            case "iv_blood" -> {consume(stack,source,"item.radaway","iv_empty");source.heal(5);}
            case "iv_xp_empty" -> {
                if(!(source instanceof Player player)||source instanceof FakePlayer)return;
                int xp=totalXp(player);if(xp<100)return;
                consume(stack,source,"item.syringe","iv_xp");setXp(player,xp-100);
            }
            case "iv_xp" -> {
                consume(stack,source,SoundEvents.EXPERIENCE_ORB_PICKUP,"iv_xp_empty");
                if(source instanceof Player player)addXp(player,100,false);
            }
            case "radaway","radaway_strong","radaway_flush" -> {
                int duration=switch(id){case "radaway"->140;case "radaway_strong"->350;default->500;};
                consume(stack,source,"item.radaway","iv_empty");
                var old=source.getEffect(ExposureEffects.RADAWAY);if(old!=null)duration+=old.getDuration();
                effect(source,ExposureEffects.RADAWAY,duration,0);
            }
            default -> throw new IllegalStateException("Unknown original simple consumable "+id);
        }
    }
    private ItemStack useSyringe(ItemStack stack,LivingEntity target,LivingEntity source,boolean selfUse) {
        if(id.startsWith("syringe_metal_")) {
            if(MedicineEffects.hasPotionSickness(target))return stack;
            int seconds=5;
            switch(id) {
                case "syringe_metal_stimpak" -> target.heal(5);
                case "syringe_metal_medx" -> effect(target,MobEffects.DAMAGE_RESISTANCE,4*60*20,2);
                case "syringe_metal_psycho" -> {effect(target,MobEffects.DAMAGE_RESISTANCE,2*60*20,0);effect(target,MobEffects.DAMAGE_BOOST,2*60*20,0);}
                case "syringe_metal_super" -> {target.heal(25);effect(target,MobEffects.MOVEMENT_SLOWDOWN,10*20,0);seconds=15;}
                default -> throw new IllegalStateException(id);
            }
            if(!selfUse)MedicineEffects.potionSickness(target,seconds);
            stack.shrink(1);sound(source,"item.syringe");
            if(selfUse&&stack.isEmpty())return new ItemStack(HbmContent.item("syringe_metal_empty"));
            give(source,"syringe_metal_empty");
            if(selfUse)MedicineEffects.potionSickness(target,seconds);
        } else switch(id) {
            case "med_bag" -> {if(selfUse&&!MedicineEffects.hasPotionSickness(target)){target.setHealth(target.getMaxHealth());cleanse(target);MedicineEffects.potionSickness(target,15);stack.shrink(1);}}
            case "syringe_taint" -> {effect(target,MedicineEffects.TAINT,60*20,0);effect(target,MobEffects.CONFUSION,5*20,0);consume(stack,source,"item.syringe","syringe_metal_empty");give(source,"bottle2_empty");}
            case "syringe_mkunicorn" -> {if(!selfUse){MedicineDisease.setContagion(target,3*60*60*20);stack.shrink(1);sound(target,"item.syringe");if(com.hbm.logging.SourceActionLog.enabled())com.hbm.HbmReloaded.LOGGER.info("[MKU] {} used an MKU syringe!",source.getName().getString());}}
            case "jetpack_tank" -> {if(selfUse&&source instanceof Player player)com.hbm.equipment.jetpack.JetpackTankRefill.use(stack,player);}
            case "cbt_device" -> {if(selfUse){effect(target,MedicineEffects.BANG,30,0);stack.shrink(1);sound(target,"item.vice");}}
            default -> throw new IllegalStateException("Unknown original syringe "+id);
        }
        return stack;
    }
    private static int totalXp(Player player) {
        int xp=0;for(int level=0;level<player.experienceLevel;level++)xp+=xpCap(level);
        return xp+(int)(xpCap(player.experienceLevel)*player.experienceProgress);
    }
    private static int xpCap(int level) {return level>=30?62+(level-30)*7:level>=15?17+(level-15)*3:17;}
    private static void setXp(Player player,int xp) {
        // The bags retain their original 1.7.10 experience-point curve, independent of totalExperience.
        player.experienceLevel=0;player.experienceProgress=0;player.totalExperience=0;
        addXp(player,xp,true);
    }
    private static void addXp(Player player,int xp,boolean silent) {
        xp=Math.min(xp,Integer.MAX_VALUE-player.totalExperience);
        player.experienceProgress+=(float)xp/xpCap(player.experienceLevel);
        for(player.totalExperience+=xp;player.experienceProgress>=1;player.experienceProgress/=xpCap(player.experienceLevel)) {
            player.experienceProgress=(player.experienceProgress-1)*xpCap(player.experienceLevel);
            if(silent)player.experienceLevel++;else player.giveExperienceLevels(1);
        }
    }
    private static void consume(ItemStack stack,LivingEntity source,String sound,String container) {consume(stack,source,HbmSounds.get(sound),container);}
    private static void consume(ItemStack stack,LivingEntity source,SoundEvent sound,String container) {stack.shrink(1);sound(source,sound);give(source,container);}
    private static void give(LivingEntity source,String id) {
        if(source instanceof Player player){var returned=new ItemStack(HbmContent.item(id));if(!player.getInventory().add(returned))player.drop(returned,false);}
    }
    private static void sound(LivingEntity entity,String sound) {sound(entity,HbmSounds.get(sound));}
    private static void sound(LivingEntity entity,SoundEvent sound) {entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),sound,SoundSource.PLAYERS,1,1);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag) {
        String key="item."+id+".desc";String text=Language.getInstance().getOrDefault(key);
        if(!text.equals(key))for(String line:text.split("\\$"))tooltip.add(Component.literal(line));
        if(kind==Kind.SYRINGE)switch(id) {
            case "syringe_metal_medx" -> tooltip.add(Component.literal("Resistance III for 4 minutes"));
            case "syringe_metal_psycho" -> {tooltip.add(Component.literal("Resistance I for 2 minutes"));tooltip.add(Component.literal("Strength I for 2 minutes"));}
            case "syringe_metal_stimpak" -> tooltip.add(Component.literal("Heals 2.5 hearts"));
            case "syringe_metal_super" -> {tooltip.add(Component.literal("Heals 25 hearts"));tooltip.add(Component.literal("Slowness I for 10 seconds"));}
            case "med_bag" -> {tooltip.add(Component.literal("Full heal, regardless of max health"));tooltip.add(Component.literal("Removes negative effects"));}
            case "syringe_taint" -> {tooltip.add(Component.literal("Tainted I for 60 seconds"));tooltip.add(Component.literal("Nausea I for 5 seconds"));tooltip.add(Component.literal("Cloud damage + taint = tainted heart effect"));}
            case "jetpack_tank" -> tooltip.add(Component.translatable("tooltip.hbm.jetpack_tank"));
            case "syringe_mkunicorn" -> tooltip.add(Component.literal("?").withStyle(ChatFormatting.RED));
            default -> { }
        }
    }
}

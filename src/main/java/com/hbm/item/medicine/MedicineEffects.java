package com.hbm.item.medicine;

import com.hbm.damage.HbmDamageSources;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The six remaining HbmPotion registrations; the five exposure effects keep their existing identity. */
public final class MedicineEffects {
    private static final DeferredRegister<MobEffect> EFFECTS=DeferredRegister.create(Registries.MOB_EFFECT,"hbm");
    public static final DeferredHolder<MobEffect,MobEffect> TAINT=add("taint",true,0x800080);
    public static final DeferredHolder<MobEffect,MobEffect> BANG=add("bang",true,0x111111);
    public static final DeferredHolder<MobEffect,MobEffect> LEAD=add("lead",true,0x767682);
    public static final DeferredHolder<MobEffect,MobEffect> PHOSPHORUS=add("phosphorus",true,0xFFFF00);
    public static final DeferredHolder<MobEffect,MobEffect> POTION_SICKNESS=add("potionsickness",false,0xFF8080);
    public static final DeferredHolder<MobEffect,MobEffect> DEATH=add("death",false,0x111111);
    private MedicineEffects() {}
    private static DeferredHolder<MobEffect,MobEffect> add(String name,boolean bad,int color) {
        return EFFECTS.register(name,()->new SourceEffect(name,bad?MobEffectCategory.HARMFUL:MobEffectCategory.BENEFICIAL,color));
    }
    static void register(IEventBus bus) { EFFECTS.register(bus); }
    public static boolean hasPotionSickness(LivingEntity entity) { return entity.hasEffect(POTION_SICKNESS); }
    public static void potionSickness(LivingEntity entity,int seconds) {
        if(MedicineConfig.POTION_SICKNESS.get()==MedicineConfig.Sickness.OFF)return;
        if(MedicineConfig.POTION_SICKNESS.get()==MedicineConfig.Sickness.TERRARIA)seconds*=12;
        uncurable(entity,POTION_SICKNESS,seconds*20,0);
    }
    public static void uncurable(LivingEntity entity,Holder<MobEffect> effect,int duration,int amplifier) {
        var instance=new MobEffectInstance(effect,duration,amplifier);
        instance.getCures().clear();entity.addEffect(instance);
    }
    private static final class SourceEffect extends MobEffect {
        private final String name;
        SourceEffect(String name,MobEffectCategory category,int color) { super(category,color);this.name=name; }
        @Override public Component getDisplayName() { return Component.translatable("potion.hbm_"+name); }
        @Override public boolean shouldApplyEffectTickThisTick(int duration,int amplifier) {
            return switch(name) {case "taint"->duration%2==0;case "phosphorus"->true;case "bang"->duration<=10;case "lead"->duration%60==0;default->false;};
        }
        @Override public boolean applyEffectTick(LivingEntity entity,int amplifier) {
            if(!(entity.level() instanceof ServerLevel level))return true;
            switch(name) {
                case "taint" -> {
                    String id=BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
                    if(!id.equals("hbm:entity_mob_tainted_creeper")&&!id.equals("hbm:entity_taint_crab")&&level.random.nextInt(40)==0)
                        entity.hurt(HbmDamageSources.source(level,HbmDamageSources.taint),amplifier+1);
                    if(MedicineConfig.TAINT_TRAILS.get()) {
                        var pos=entity.blockPosition().below();var old=level.getBlockState(pos);
                        var block=BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath("hbm","taint"));
                        if(entity.blockPosition().getY()>1&&com.hbm.block.LegacyBlockTraits.isNormalCube(level,pos,old)&&!old.isAir()&&block.isPresent())
                            level.setBlock(pos,MedicineContent.taintTrail(block.get().defaultBlockState()),2);
                    }
                }
                case "lead" -> entity.hurt(HbmDamageSources.source(level,HbmDamageSources.lead),amplifier+1);
                case "phosphorus" -> entity.igniteForSeconds(1);
                case "bang" -> {
                    entity.hurt(HbmDamageSources.source(level,HbmDamageSources.bang),1000);entity.setHealth(0);
                    if(!(entity instanceof Player))entity.discard();
                    level.playSound(null,entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get("weapon.laserBang"),SoundSource.NEUTRAL,100,1);
                    MedicineVisuals.bang(entity);
                    if(entity instanceof Cow cow) {
                        var cheese=BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("hbm","cheese"));
                        if(cheese.isPresent())entity.spawnAtLocation(new ItemStack(cheese.get(),cow.isBaby()?10:3),1);
                    }
                }
                default -> { }
            }
            return true;
        }
    }
}

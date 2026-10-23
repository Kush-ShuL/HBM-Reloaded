package com.hbm.item.lemonfood;

import com.hbm.disease.DiseaseVisuals;
import com.hbm.item.structureloot.StructureFoodItem;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** The remaining real ItemLemon identities, sharing the existing source consumption path. */
public final class SourceLemonFoodItem extends StructureFoodItem {
    private final SourceLemonFoodKind kind;
    public SourceLemonFoodItem(SourceLemonFoodKind kind){super(kind.name,kind.nutrition,kind.saturation,kind.maxStack);this.kind=kind;}
    @Override protected void onFoodEaten(ItemStack stack,Level level,Player player){
        if(kind==SourceLemonFoodKind.MED_IPECAC||kind==SourceLemonFoodKind.MED_PTSD){
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER,50,49));
            // Source AuxParticlePacketNT writes no count/mode: ClientProxy reads count0 and emits none.
            // Keep the existing25-block auxiliary channel without inventing blood/normal particles.
            DiseaseVisuals.vomit(player,DiseaseVisuals.NORMAL,0);
            level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("entity.vomit"),SoundSource.PLAYERS,1,1);
        }
        // Source med_schizophrenia branch is intentionally empty.
        if(kind==SourceLemonFoodKind.LOOP_STEW){
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,20*20,1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,60*20,2));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,60*20,1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,20*20,2));
        }
    }
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity consumer){
        var result=super.finishUsingItem(stack,level,consumer);
        return kind==SourceLemonFoodKind.LOOP_STEW&&consumer instanceof Player?new ItemStack(Items.BOWL):result;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        super.appendHoverText(stack,context,tooltip,flag);
        String key=switch(kind){
            case LEMON->"item.lemon.desc";
            case MED_IPECAC->"item.med_ipecac.desc";
            case MED_PTSD->"item.med_ptsd.desc";
            case MED_SCHIZOPHRENIA->"item.med_schizophrenia.desc";
            case LOOPS->"item.loops.desc";
            case LOOP_STEW->"item.loop_stew.desc";
            case QUESADILLA->"item.cheese_quesadilla.desc";
            default->null;
        };
        if(key==null)return;
        if(kind==SourceLemonFoodKind.MED_IPECAC||kind==SourceLemonFoodKind.MED_PTSD||kind==SourceLemonFoodKind.MED_SCHIZOPHRENIA){
            for(var line:Language.getInstance().getOrDefault(key).split("\\$"))tooltip.add(Component.literal(line));
        }else tooltip.add(Component.translatable(key));
    }
}

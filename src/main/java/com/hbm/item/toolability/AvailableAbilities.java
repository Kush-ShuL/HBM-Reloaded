package com.hbm.item.toolability;

import com.hbm.item.ability.SourceWeaponDrops;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Source per-item ability definitions, not another preset store. First atomic handlers: recursion/silk/luck/stun/beheader. */
public final class AvailableAbilities {
    public enum Ability {
        RECURSION("tool.ability.recursion",1,7),SILK("tool.ability.silktouch",101,1),LUCK("tool.ability.luck",102,6),
        STUN("weapon.ability.stun",203,5),BEHEADER("weapon.ability.beheader",208,1),
        HAMMER("tool.ability.hammer",2,4),HAMMER_FLAT("tool.ability.hammer_flat",3,4),EXPLOSION("tool.ability.explosion",4,4),
        CRYSTALLIZER("tool.ability.crystallizer",106,1),PHOSPHORUS("weapon.ability.phosphorus",204,2),
        SMELTER("tool.ability.smelter",103,1),SHREDDER("tool.ability.shredder",104,1),CENTRIFUGE("tool.ability.centrifuge",105,1),MERCURY("tool.ability.mercury",107,1),
        VAMPIRE("weapon.ability.vampire",202,5),FIRE("weapon.ability.fire",206,2),CHAINSAW("weapon.ability.chainsaw",207,2),
        RADIATION("weapon.ability.radiation",201,3),BOBBLE("weapon.ability.bobble",209,1);
        public final String key;public final int order,levels;
        Ability(String key,int order,int levels){this.key=key;this.order=order;this.levels=levels;}
    }
    private static final int[] RADII={3,4,5,6,7,9,10},LUCK_POWER={1,2,3,4,5,9},STUN_SECONDS={2,3,5,10,15};
    private final HashMap<Ability,Integer> definitions=new HashMap<>();
    public static final AvailableAbilities COBALT=new AvailableAbilities().add(Ability.RECURSION,1).add(Ability.SILK,0).add(Ability.LUCK,0);
    public AvailableAbilities add(Ability ability,int level){definitions.put(ability,level<0||level>=ability.levels?ability.levels-1:level);return this;}
    public int maxLevel(Ability ability){
        boolean allowed=switch(ability){case HAMMER,HAMMER_FLAT->CobaltToolConfig.HAMMER.get();case EXPLOSION->CobaltToolConfig.EXPLOSION.get();case CRYSTALLIZER->CobaltToolConfig.CRYSTALLIZER.get();case SMELTER->CobaltToolConfig.FURNACE.get();case SHREDDER->CobaltToolConfig.SHREDDER.get();case CENTRIFUGE->CobaltToolConfig.CENTRIFUGE.get();case MERCURY->CobaltToolConfig.MERCURY.get();case RECURSION->CobaltToolConfig.VEIN.get();case SILK->CobaltToolConfig.SILK.get();case LUCK->CobaltToolConfig.LUCK.get();default->true;};
        return allowed?definitions.getOrDefault(ability,-1):-1;
    }
    public static Ability areaAbility(int index){return switch(index){case 1->Ability.RECURSION;case 2->Ability.HAMMER;case 3->Ability.HAMMER_FLAT;case 4->Ability.EXPLOSION;default->null;};}
    public static Ability harvestAbility(int index){return switch(index){case 1->Ability.SILK;case 2->Ability.LUCK;case 3->Ability.SMELTER;case 4->Ability.SHREDDER;case 5->Ability.CENTRIFUGE;case 6->Ability.CRYSTALLIZER;case 7->Ability.MERCURY;default->null;};}
    public boolean allowsArea(int index){var ability=areaAbility(index);return index==0||ability!=null&&maxLevel(ability)>=0;}
    public boolean allowsHarvest(int index){var ability=harvestAbility(index);return index==0||ability!=null&&maxLevel(ability)>=0;}
    public int maximumAreaLevel(int index){var ability=areaAbility(index);return ability==null?0:maxLevel(ability);}
    public int maximumHarvestLevel(int index){var ability=harvestAbility(index);return ability==null?0:maxLevel(ability);}
    public static String areaKey(int index){var ability=areaAbility(index);return ability==null?"":ability.key;}
    public static String harvestKey(int index){var ability=harvestAbility(index);return ability==null?"":ability.key;}
    public static String areaExtension(int index,int level){return switch(index){case 1->" ("+radius(level)+")";case 2,3->" ("+(level+1)+")";case 4->" ("+explosionPower(level)+")";default->"";};}
    public static float explosionPower(int level){return new float[]{2.5F,5F,10F,15F}[Math.clamp(level,0,3)];}
    public static int radius(int level){return RADII[Math.clamp(level,0,RADII.length-1)];}
    public static int luckPower(int level){return LUCK_POWER[Math.clamp(level,0,LUCK_POWER.length-1)];}
    public static AvailableAbilities of(ItemStack stack){return stack.getItem() instanceof SourceAbilityTool tool?tool.abilities():COBALT;}
    public void onHit(Player player,LivingEntity victim){
        // Same source HashMap -> getWeaponAbilities/toMap traversal. These first steel items each have one passive.
        definitions.entrySet().stream().filter(entry->entry.getKey()==Ability.STUN||entry.getKey()==Ability.BEHEADER||entry.getKey()==Ability.PHOSPHORUS||entry.getKey()==Ability.VAMPIRE||entry.getKey()==Ability.FIRE||entry.getKey()==Ability.RADIATION||entry.getKey()==Ability.BOBBLE||entry.getKey()==Ability.CHAINSAW)
            .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue)).forEach((ability,level)->{
                if(ability==Ability.STUN){int ticks=STUN_SECONDS[level]*20;victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,ticks,4));victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,ticks,4));}
                else if(ability==Ability.PHOSPHORUS)victim.addEffect(new MobEffectInstance(com.hbm.item.medicine.MedicineEffects.PHOSPHORUS,(level==0?60:90)*20,4));
                else if(ability==Ability.RADIATION)com.hbm.exposure.HbmExposure.contaminate(victim,com.hbm.exposure.HbmExposure.Contamination.CREATIVE,new float[]{15F,50F,500F}[level]);
                else if(ability==Ability.BOBBLE)SourceWeaponDrops.bobble(victim);
                else if(ability==Ability.CHAINSAW)com.hbm.item.chainsaw.ChainsawAbility.onHit(victim,level);
                else if(ability==Ability.VAMPIRE){
                    // Exact source direct-health drain, not another hurt/invulnerability-frame path. Do not resurrect an already dead victim.
                    if(victim.getHealth()>0){float amount=new float[]{2F,3F,5F,10F,50F}[level];victim.setHealth(victim.getHealth()-amount);if(victim.getHealth()<=0)victim.die(victim.damageSources().magic());player.heal(amount);}
                }
                else if(ability==Ability.FIRE)victim.igniteForSeconds(level==0?5:10);
                else if(victim.getHealth()<=0)SourceWeaponDrops.behead(victim);
            });
    }
    public void addInformation(List<Component> lines){
        var tools=definitions.keySet().stream().filter(a->a.order<200&&maxLevel(a)>=0).sorted(Comparator.comparingInt(a->a.order)).toList();
        if(!tools.isEmpty()){
            lines.add(Component.translatable("gui.hbm.tool_ability.abilities"));
            for(var ability:tools)lines.add(Component.literal("  ").append(label(ability,maxLevel(ability))).withStyle(ChatFormatting.GOLD));
            for(String hint:new String[]{"cycle_hint","sneak_hint","alt_hint"})lines.add(Component.translatable("gui.hbm.tool_ability."+hint));
        }
        var weapons=definitions.keySet().stream().filter(a->a.order>=200).sorted(Comparator.comparingInt(a->a.order)).toList();
        if(!weapons.isEmpty()){
            lines.add(Component.translatable("tooltip.hbm.gavel.weapon_modifiers"));
            for(var ability:weapons)lines.add(Component.literal("  ").append(label(ability,maxLevel(ability))).withStyle(ChatFormatting.RED));
        }
    }
    private static Component label(Ability ability,int level){String extension=switch(ability){case RECURSION->" ("+radius(level)+")";case LUCK->" ("+luckPower(level)+")";case HAMMER,HAMMER_FLAT->" ("+(level+1)+")";case EXPLOSION->" ("+explosionPower(level)+")";case PHOSPHORUS->" ("+(level==0?60:90)+")";case RADIATION->" ("+new float[]{15F,50F,500F}[level]+")";case VAMPIRE->" ("+new float[]{2F,3F,5F,10F,50F}[level]+")";case FIRE->" ("+(level==0?5:10)+")";case CHAINSAW->" (1:"+new int[]{15,10}[level]+")";case STUN->" ("+STUN_SECONDS[level]+")";default->"";};return Component.translatable(ability.key).append(extension);}
}

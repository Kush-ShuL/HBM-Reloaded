package com.hbm.item.toolability.swords;
import com.hbm.item.toolability.*;
import net.minecraft.world.item.*;
/** Original ItemSwordAbility, using the sole source combat/ability/wear/blocking paths. Only Schrabidium overrides enchanted rarity. */
public final class SourceAbilitySwordItem extends ItemSwordAbility {
 private final boolean sourceRare;
 public SourceAbilitySwordItem(String name,float damage,Tier material,AvailableAbilities abilities){this(name,damage,material,abilities,false);}
 public SourceAbilitySwordItem(String name,float damage,Tier material,AvailableAbilities abilities,boolean rare){super(name,damage,0,material,abilities,new SourceProperties().rarity(rare?Rarity.RARE:Rarity.COMMON));sourceRare=rare;}
 @Override public Rarity sourceRarityOverride(){return sourceRare?Rarity.RARE:null;}
}

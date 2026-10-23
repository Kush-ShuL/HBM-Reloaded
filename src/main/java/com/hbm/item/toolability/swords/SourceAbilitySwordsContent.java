package com.hbm.item.toolability.swords;
import com.hbm.block.junglemechanics.JungleWeaponTiers;
import com.hbm.weapon.legacymelee.LegacyMeleeMaterials;
import com.hbm.item.toolability.*;
import com.hbm.item.toolability.chlorophyte.UpstreamToolTier;
import com.hbm.item.toolability.mese.MeseTier;
import static com.hbm.item.toolability.AvailableAbilities.Ability.*;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
/** Only the seven genuinely missing swords. Existing steel/titanium/cobalt/star/Mese-gavel/meteorite identities are not registered twice. */
public final class SourceAbilitySwordsContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<SourceAbilitySwordItem> SCHRABIDIUM=ITEMS.register("schrabidium_sword",()->new SourceAbilitySwordItem("schrabidium_sword",75,SourceSwordTier.SCHRABIDIUM,new AvailableAbilities().add(RADIATION,1).add(VAMPIRE,0),true));
 public static final DeferredItem<SourceAbilitySwordItem> ALLOY=ITEMS.register("alloy_sword",()->new SourceAbilitySwordItem("alloy_sword",8,LegacyMeleeMaterials.ALLOY,new AvailableAbilities().add(STUN,0)));
 public static final DeferredItem<SourceAbilitySwordItem> CMB=ITEMS.register("cmb_sword",()->new SourceAbilitySwordItem("cmb_sword",35,JungleWeaponTiers.CMB,new AvailableAbilities().add(STUN,0).add(VAMPIRE,0)));
 public static final DeferredItem<PoweredAbilitySwordItem> ELECTRIC=ITEMS.register("elec_sword",PoweredAbilitySwordItem::new);
 public static final DeferredItem<SourceAbilitySwordItem> DESH=ITEMS.register("desh_sword",()->new SourceAbilitySwordItem("desh_sword",12.5F,SourceSwordTier.DESH,new AvailableAbilities().add(STUN,0)));
 public static final DeferredItem<SourceAbilitySwordItem> DECORATED_COBALT=ITEMS.register("cobalt_decorated_sword",()->new SourceAbilitySwordItem("cobalt_decorated_sword",15,UpstreamToolTier.DECORATED_COBALT,new AvailableAbilities().add(BOBBLE,0)));
 public static final DeferredItem<SourceAbilitySwordItem> DNT=ITEMS.register("dnt_sword",()->new SourceAbilitySwordItem("dnt_sword",12,MeseTier.INSTANCE,new AvailableAbilities()));
 public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(SourceAbilitySwordsContent::creative);}
 private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey()==CreativeModeTabs.COMBAT)for(var stack:indexedItems())event.accept(stack);}
 public static List<ItemStack> indexedItems(){return List.of(new ItemStack(SCHRABIDIUM.get()),new ItemStack(ALLOY.get()),new ItemStack(CMB.get()),new ItemStack(ELECTRIC.get()),new ItemStack(DESH.get()),new ItemStack(DECORATED_COBALT.get()),new ItemStack(DNT.get()));}
 private SourceAbilitySwordsContent(){}
}

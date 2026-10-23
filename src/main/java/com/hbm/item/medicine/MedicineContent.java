package com.hbm.item.medicine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import com.hbm.armor.ArmorModStorage;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.effect.MobEffectInstance;

/** Actual source ItemPill, ItemSyringe, and ItemSimpleConsumable registrations. */
public final class MedicineContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ParticleType<?>> PARTICLES=DeferredRegister.create(Registries.PARTICLE_TYPE,"hbm");
    public static final DeferredHolder<ParticleType<?>,SimpleParticleType> SMOKE=PARTICLES.register("medicine_smoke",()->new SimpleParticleType(false));
    public static final List<String> PILLS=List.of("radx","siox","pill_herbal","xanax","fmn","five_htp","pill_iodine","plan_c","pill_red","chocolate");
    public static final List<String> SIMPLE=List.of("syringe_antidote","syringe_poison","syringe_awesome","iv_empty","iv_blood","iv_xp_empty","iv_xp","radaway","radaway_strong","radaway_flush");
    public static final List<String> SYRINGES=List.of("syringe_metal_stimpak","syringe_metal_medx","syringe_metal_psycho","syringe_metal_super","med_bag","syringe_taint","syringe_mkunicorn","cbt_device");
    public static final Map<String,DeferredItem<MedicineItem>> ALL=new LinkedHashMap<>();
    public static final Map<String,DeferredItem<MedicineArmorModItem>> ARMOR_MODS=new LinkedHashMap<>();
    public static final DeferredItem<MedicineSmokingItem> CIGARETTE=ITEMS.register("cigarette",()->new MedicineSmokingItem(true));
    public static final DeferredItem<MedicineSmokingItem> CRACKPIPE=ITEMS.register("crackpipe",()->new MedicineSmokingItem(false));
    static {
        for(String id:PILLS)ALL.put(id,ITEMS.register(id,()->new MedicineItem(id,MedicineItem.Kind.PILL)));
        for(String id:SIMPLE)ALL.put(id,ITEMS.register(id,()->new MedicineItem(id,MedicineItem.Kind.SIMPLE)));
        for(String id:SYRINGES)ALL.put(id,ITEMS.register(id,()->new MedicineItem(id,MedicineItem.Kind.SYRINGE)));
        for(String id:List.of("serum","injector_5htp","injector_knife"))ARMOR_MODS.put(id,ITEMS.register(id,()->new MedicineArmorModItem(id)));
    }
    private MedicineContent() {}
    public static void register(IEventBus bus,ModContainer container) {
        ITEMS.register(bus);PARTICLES.register(bus);MedicineEffects.register(bus);MedicineConfig.register(container);MedicineDisease.register(bus);MedicineVisuals.register(bus);NeoForge.EVENT_BUS.addListener(MedicineContent::tickArmor);NeoForge.EVENT_BUS.addListener(MedicineContent::foodEaten);
    }
    public static List<ItemStack> creativeStacks(String tab) {
        if(!tab.equals("consumable"))return List.of();
        var result=new java.util.ArrayList<ItemStack>();
        ALL.entrySet().stream().filter(entry->!entry.getKey().equals("syringe_mkunicorn")&&!entry.getKey().equals("cbt_device")).map(entry->new ItemStack(entry.getValue().get())).forEach(result::add);
        result.add(new ItemStack(CIGARETTE.get()));result.add(new ItemStack(CRACKPIPE.get()));
        ARMOR_MODS.values().forEach(item->result.add(new ItemStack(item.get())));return List.copyOf(result);
    }
    private static void tickArmor(EntityTickEvent.Post event) {
        if(!(event.getEntity() instanceof net.minecraft.world.entity.LivingEntity entity)||entity.level().isClientSide)return;
        for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD}) {
            ItemStack armor=entity.getItemBySlot(slot);if(!ArmorModStorage.hasMods(armor))continue;
            ItemStack mod=ArmorModStorage.pryMod(armor,ArmorModStorage.EXTRA,entity.level().registryAccess());
            if(mod.getItem() instanceof MedicineArmorModItem item)item.update(entity,armor);
        }
    }
    static void achievement(ServerPlayer player,String id) {
        var advancement=player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath("hbm",id));
        if(advancement!=null)for(String criterion:advancement.value().criteria().keySet())player.getAdvancements().award(advancement,criterion);
    }
    private static void foodEaten(LivingEntityUseItemEvent.Finish event) {
        if(!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)||player.level().isClientSide)return;
        ItemStack eaten=event.getItem();if(eaten.getFoodProperties(player)==null)return;
        // Finish supplies the original pre-consumption stack, including its custom food data.
        var data=eaten.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
        if(data.getBoolean("ntmCyanide"))MedicineItem.cyanide(player);
        if(data.getBoolean("ntmRedPill"))for(int i=0;i<10;i++)player.addEffect(new MobEffectInstance(MedicineEffects.DEATH,60*60*20,0));
    }
    static BlockState taintTrail(BlockState state) {
        for(var property:state.getProperties())if(property instanceof IntegerProperty integer&&integer.getPossibleValues().contains(14))return state.setValue(integer,14);
        return state;
    }
}

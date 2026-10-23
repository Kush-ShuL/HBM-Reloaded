package com.hbm.item.redroom;
import com.hbm.armor.ArmorModStorage;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.*;
/** True source infusion state. Optional armor capacity is only supplied by actual ItemModShield implementations. */
public final class ShieldInfusion {
 public static final float CAP=100;
 public interface ArmorCapacity {float shieldCapacity(ItemStack mod);}
 private static final DeferredRegister<AttachmentType<?>> TYPES=DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,"hbm");
 public static final DeferredHolder<AttachmentType<?>,AttachmentType<ShieldData>> DATA=TYPES.register("shield_infusion",()->AttachmentType.serializable(ShieldData::new).copyOnDeath().sync((holder,player)->holder==player,StreamCodec.of((buffer,data)->{buffer.writeFloat(data.shield);buffer.writeFloat(data.maxShield);},buffer->{var data=new ShieldData();data.shield=buffer.readFloat();data.maxShield=buffer.readFloat();return data;})).build());
 public static void register(IEventBus bus){TYPES.register(bus);}
 public static ShieldData data(Player player){return player.getData(DATA);}
 public static float maximum(Player player){float maximum=data(player).maxShield;var chest=player.getItemBySlot(EquipmentSlot.CHEST);if(!chest.isEmpty()){var mod=ArmorModStorage.pryMod(chest,ArmorModStorage.KEVLAR,player.registryAccess());if(mod.getItem() instanceof ArmorCapacity capacity)maximum+=capacity.shieldCapacity(mod);}return maximum;}
 public static void drink(Player player){var data=data(player);data.maxShield=Math.min(CAP,data.maxShield+5);data.shield=Math.min(data.shield+5,maximum(player));player.syncData(DATA);}
 public static void tick(Player player){if(!player.hasData(DATA)&&!(ArmorModStorage.pryMod(player.getItemBySlot(EquipmentSlot.CHEST),ArmorModStorage.KEVLAR,player.registryAccess()).getItem() instanceof ArmorCapacity))return;var data=data(player);float maximum=maximum(player);if(data.shield<maximum&&player.tickCount>data.lastDamage+60){int since=player.tickCount-(data.lastDamage+60);data.shield+=Math.min(maximum-data.shield,.005F*since);}if(data.shield>maximum)data.shield=maximum;player.syncData(DATA);}
 private ShieldInfusion(){}
}

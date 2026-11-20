package com.hbm.item.medicine;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.BulletConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public record RedPillSwordBeam() implements CustomPacketPayload {
    public static final BulletConfig PROJECTILE = new BulletConfig("hbm:sword_beam").setVel(1.5F).setLife(60);
    private static final Type<RedPillSwordBeam> TYPE = new Type<>(ResourceLocation.parse("hbm:sword_beam_input"));
    private static final StreamCodec<RegistryFriendlyByteBuf,RedPillSwordBeam> CODEC = StreamCodec.unit(new RedPillSwordBeam());
    private static final String LAST_SWING = "hbmRedPillLastSwing";

    @Override public Type<RedPillSwordBeam> type() { return TYPE; }

    public static boolean canSwing(Player player) {
        if (!player.isAlive() || player.isSpectator() || player.isBlocking() || !player.hasEffect(MedicineEffects.ESTRADIOL)) return false;
        boolean[] attackingItem = {false};
        player.getMainHandItem().forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute,modifier) -> {
            if (attribute.equals(Attributes.ATTACK_DAMAGE)) attackingItem[0] = true;
        });
        return attackingItem[0];
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE,CODEC,(packet,context)->context.enqueueWork(()-> {
            if (!(context.player() instanceof ServerPlayer player) || !canSwing(player)) return;
            long now = player.level().getGameTime();
            var data = player.getPersistentData();
            long last = data.getLong(LAST_SWING);
            if (data.contains(LAST_SWING) && last <= now && now - last < 6) return;
            data.putLong(LAST_SWING,now);
            var bullet = new EntityBulletBaseMK4(player,PROJECTILE,10F,0F,-.1875,-.0625,.5);
            var movement = player.getDeltaMovement();
            bullet.setDeltaMovement(bullet.getDeltaMovement().add(movement.x,0,movement.z));
            player.level().addFreshEntity(bullet);
            player.level().playSound(null,player.blockPosition(),HbmSounds.get("weapon.teslaShoot"),SoundSource.PLAYERS,.5F,2F);
        }));
    }
}

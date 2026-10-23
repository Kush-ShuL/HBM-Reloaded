package com.hbm.bomb.conventional.grenade;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.*;

/** Original transient HbmPlayerProps.grenadeDeployment, without an unrelated persistent exposure state. */
public final class GrenadeDeployment {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS=DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,"hbm");
    private static final DeferredHolder<AttachmentType<?>,AttachmentType<State>> TYPE=ATTACHMENTS.register("grenade_deployment",()->AttachmentType.builder(State::new).build());
    private GrenadeDeployment(){}
    public static final class State{public int ticks;}
    public static State data(Player player){return player.getData(TYPE);}
    public static void register(IEventBus bus){ATTACHMENTS.register(bus);bus.addListener(GrenadeEquipEffects::register);}
}

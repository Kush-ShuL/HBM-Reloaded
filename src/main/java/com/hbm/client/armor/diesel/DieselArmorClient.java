package com.hbm.client.armor.diesel;

import com.hbm.armor.diesel.DieselArmorContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.equipment.jetpack.JetpackNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class DieselArmorClient {
    private static boolean exhaustInstalled;
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private DieselArmorItemRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new DieselArmorItemRenderer();
                return renderer;
            }
        }, DieselArmorContent.HELMET.get(), DieselArmorContent.PLATE.get(), DieselArmorContent.LEGS.get(), DieselArmorContent.BOOTS.get());
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var item : java.util.List.of(DieselArmorContent.HELMET, DieselArmorContent.PLATE, DieselArmorContent.LEGS, DieselArmorContent.BOOTS))
                T51ArmorRenderer.register(item.get(), DieselArmorRenderer::render);
        });
    }
    @SubscribeEvent public static void complete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            if (exhaustInstalled) return;
            exhaustInstalled = true;
            // The one existing payload/transporter remains; preserve every previous family handler and intercept only this real source mode.
            var previous = JetpackNetwork.CLIENT_EXHAUST;
            JetpackNetwork.CLIENT_EXHAUST = (player, mode) -> { if (mode == 3) exhaust(player); else previous.accept(player, mode); };
        });
    }
    private static void exhaust(int id) {
        var mc = Minecraft.getInstance(); var level = mc.level;
        if (level == null || mc.options.particles().get() == ParticleStatus.MINIMAL || !(level.getEntity(id) instanceof Player player)) return;
        float angle = (float) -Math.toRadians(player.yHeadRot - (player.yHeadRot - player.yBodyRot));
        float cosine = Mth.cos(angle), sine = Mth.sin(angle);
        double vx = -.6 * sine, vz = -.6 * cosine, ox = .275 * cosine, oz = -.275 * sine;
        double x = player.getX() + vx, y = player.getY() + player.getEyeHeight() - 1 + .4, z = player.getZ() + vz;
        // ClientProxy.effectNT's outer `player` is the local observer, distinct from its emitting `p`.
        if (mc.player != null && mc.player.isShiftKeyDown()) y += .25;
        double length = (float) Math.sqrt(vx * vx + vz * vz);
        double mx = length < 1E-4 ? 0 : vx / length * .025, mz = length < 1E-4 ? 0 : vz / length * .025;
        for (int i = 0; i < 2; i++) {
            int sign = i == 0 ? -1 : 1;
            mc.particleEngine.add(new DieselSmokeParticle(level, x + ox * sign, y, z + oz * sign, mx, mz));
        }
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        for (String part : new String[] {"helmet", "plate", "legs", "boots"}) {
            var key = ModelResourceLocation.inventory(DieselArmorContent.id("dieselsuit_" + part));
            var model = event.getModels().get(key);
            if (model != null) event.getModels().put(key, new BakedModelWrapper<>(model) {
                @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
            });
        }
    }
    private DieselArmorClient() { }
}

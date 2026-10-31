package com.hbm.client.render.mixin;

import com.hbm.client.render.WorldEffectsForward;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Restore NeoForge's complete-model frustum test for Sodium's global block-entity path. */
@Mixin(BlockEntityRenderDispatcher.class)
public abstract class ObjMachineCullingMixin {
    @Shadow
    public abstract <E extends BlockEntity> BlockEntityRenderer<E> getRenderer(E blockEntity);

    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V",
            at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void hbm$cullObjMachines(E blockEntity, float partialTick,
            PoseStack poses, MultiBufferSource buffers, CallbackInfo callback) {
        if (!ModList.get().isLoaded("sodium") || WorldEffectsForward.shadowPass()
                || !BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).getNamespace().equals("hbm")) return;

        BlockEntityRenderer<E> renderer = getRenderer(blockEntity);
        if (renderer == null || !renderer.shouldRenderOffScreen(blockEntity)) return;

        // Keep the global path: a large model can be visible while its core section is hidden.
        var frustum = Minecraft.getInstance().levelRenderer.getFrustum();
        if (frustum != null && !frustum.isVisible(renderer.getRenderBoundingBox(blockEntity))) callback.cancel();
    }
}

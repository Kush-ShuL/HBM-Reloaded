package com.hbm.machine.arcfurnace.mixin;
import com.hbm.machine.arcfurnace.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
/** Vanilla falling-entity failure drops a bare block item; source HBM sand preserves its exact metadata. */
@Mixin(FallingBlockEntity.class)
public abstract class FallingSandDropsMixin {
 @Redirect(method="tick",at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/item/FallingBlockEntity;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
 private ItemEntity hbm$sourceSandDrop(FallingBlockEntity falling,ItemLike item){return falling.getBlockState().getBlock() instanceof ArcSandBlock?falling.spawnAtLocation(ArcFurnaceContent.SAND_ITEM.get().stack(falling.getBlockState().getValue(ArcSandBlock.META))):falling.spawnAtLocation(item);}
}

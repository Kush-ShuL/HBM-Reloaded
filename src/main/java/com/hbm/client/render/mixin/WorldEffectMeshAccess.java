package com.hbm.client.render.mixin;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
/** Native MeshData's actual optional sorted index result; the original DrawState/index type stays unchanged. */
@Mixin(MeshData.class)
public interface WorldEffectMeshAccess {
 @Accessor("indexBuffer")void hbm$sourceIndices(ByteBufferBuilder.Result indices);
}

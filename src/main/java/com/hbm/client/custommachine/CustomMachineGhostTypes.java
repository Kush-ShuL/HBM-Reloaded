package com.hbm.client.custommachine;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;
/** Original block-atlas blend, cull enabled, depth testing retained, depth writes disabled. */
final class CustomMachineGhostTypes extends RenderType {
 static final RenderType GHOST=create("hbm_custom_machine_components",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,4096,false,true,CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new TextureStateShard(InventoryMenu.BLOCK_ATLAS,false,false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
 private CustomMachineGhostTypes(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}

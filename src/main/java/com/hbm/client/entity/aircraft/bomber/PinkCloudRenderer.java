package com.hbm.client.entity.aircraft.bomber;
import com.hbm.client.entity.particle.SourceMultiCloudRenderer;
import com.hbm.entity.aircraft.bomber.EntityPinkCloudFX;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
/** Original pink8frame choice; exact existing5billboard/material algorithm is the shared source renderer. */
public final class PinkCloudRenderer extends SourceMultiCloudRenderer<EntityPinkCloudFX> {
 public PinkCloudRenderer(EntityRendererProvider.Context context){super(context,"pc");}
}

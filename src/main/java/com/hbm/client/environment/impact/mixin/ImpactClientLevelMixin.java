package com.hbm.client.environment.impact.mixin;
import com.hbm.client.environment.impact.ImpactDimensionEffects;
import com.hbm.environment.impact.ImpactState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Literal WorldProviderNTM colors/sunlight; no changes to Nether/End/custom dimension registrations. */
@Mixin(value=ClientLevel.class,remap=false)
public abstract class ImpactClientLevelMixin {
    @Unique private ImpactDimensionEffects hbm$impactEffects;
    @Inject(method="effects",at=@At("RETURN"),cancellable=true)
    private void hbm$effects(CallbackInfoReturnable<DimensionSpecialEffects> cir){var world=(ClientLevel)(Object)this;if(world.dimension()!=Level.OVERWORLD)return;var original=cir.getReturnValue();if(hbm$impactEffects==null||hbm$impactEffects.delegate()!=original)hbm$impactEffects=new ImpactDimensionEffects(world,original);cir.setReturnValue(hbm$impactEffects);}
    @Inject(method="getSkyDarken(F)F",at=@At("RETURN"),cancellable=true)
    private void hbm$sun(float partial,CallbackInfoReturnable<Float> cir){var world=(ClientLevel)(Object)this;if(ImpactState.provider(world))cir.setReturnValue(cir.getReturnValue()*(1-ImpactState.get(world).dust()));}
    @Inject(method="getStarBrightness",at=@At("RETURN"),cancellable=true)
    private void hbm$stars(float partial,CallbackInfoReturnable<Float> cir){var world=(ClientLevel)(Object)this;if(ImpactState.provider(world))cir.setReturnValue(cir.getReturnValue()*(1-ImpactState.get(world).dust()));}
    @Inject(method="getCloudColor",at=@At("RETURN"),cancellable=true)
    private void hbm$clouds(float partial,CallbackInfoReturnable<Vec3> cir){var world=(ClientLevel)(Object)this;if(ImpactState.provider(world))cir.setReturnValue(cir.getReturnValue().scale(1-ImpactState.get(world).dust()));}
    @Inject(method="getSkyColor",at=@At("RETURN"),cancellable=true)
    private void hbm$sky(Vec3 position,float partial,CallbackInfoReturnable<Vec3> cir){var world=(ClientLevel)(Object)this;if(!ImpactState.provider(world))return;var data=ImpactState.get(world);var sky=cir.getReturnValue();float dust=data.dust(),fire=data.fire();double r,g,b;if(fire>0){r=sky.x*1.3F;g=sky.y*Math.max(1-dust*1.4F,0);b=sky.z*Math.max(1-dust*4,0);}else{r=sky.x;g=sky.y*(1-dust*.5F);b=sky.z*(1-dust);}cir.setReturnValue(new Vec3(r*(fire+(1-dust)),g*(fire+(1-dust)),b*(fire+(1-dust))));}
}

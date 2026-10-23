package com.hbm.network.menu.mixin;

import com.hbm.network.menu.SourceMenuOpeningData;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Only completes the contextual data missing from vanilla spectator MenuProvider opens. */
@Mixin(value=ServerPlayer.class,remap=false)
public abstract class ServerPlayerMenuDataMixin {
    @Inject(method="openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;",at=@At("HEAD"),cancellable=true,require=1,allow=1)
    private void hbm$completeSourceOpeningData(MenuProvider provider,Consumer<RegistryFriendlyByteBuf> explicitWriter,CallbackInfoReturnable<OptionalInt> callback){
        var player=(ServerPlayer)(Object)this;
        if(SourceMenuOpeningData.deniesSpectator(player,provider)){
            player.displayClientMessage(Component.translatable("container.spectatorCantOpen").withStyle(ChatFormatting.RED),true);
            callback.setReturnValue(OptionalInt.empty());
            return;
        }
        if(explicitWriter!=null)return;
        var writer=SourceMenuOpeningData.complete(provider,null);
        // The nonnull callback enters the native method exactly once; this outer HEAD is cancelled.
        // The nested invocation immediately passes the explicitWriter check above.
        if(writer!=null)callback.setReturnValue(player.openMenu(provider,writer));
    }
}

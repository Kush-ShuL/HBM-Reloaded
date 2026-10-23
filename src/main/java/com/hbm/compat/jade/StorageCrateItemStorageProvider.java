package com.hbm.compat.jade;

import com.hbm.machine.storage.StorageCrateBlockEntity;
import com.hbm.machine.storage.StorageCrateKind;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.addon.universal.ItemStorageProvider;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;

/** Server-side authorization precedes Jade's shared Container/IItemHandler collector and its target cache. */
public enum StorageCrateItemStorageProvider implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("hbm", "protected_storage_contents");

    @Override public ResourceLocation getUid() { return UID; }
    @Override public int getDefaultPriority() { return -10000; }

    @Override public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        if (!(accessor.getTarget() instanceof StorageCrateBlockEntity crate)) return null;
        // An empty non-null list terminates Jade15.10's extension search; null would expose the built-in fallback.
        if ((crate.kind == StorageCrateKind.SAFE || crate.isLocked()) && !crate.hasMatchingKey(accessor.getPlayer())) return List.of();
        return ItemStorageProvider.Extension.INSTANCE.getGroups(accessor);
    }

    @Override public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
        return ItemStorageProvider.Extension.INSTANCE.getClientGroups(accessor, groups);
    }
}

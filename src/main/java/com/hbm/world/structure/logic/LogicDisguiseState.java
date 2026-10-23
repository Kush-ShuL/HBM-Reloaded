package com.hbm.world.structure.logic;

import com.hbm.world.structure.LegacyStructureStates;
import com.hbm.world.structure.templates.TemplateStateMappings;
import com.hbm.world.structure.vanilla.SourceVanillaStates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

/** Original saved registry names (including hbm:tile.*) use the one real source-state decoder, never a substitute block. */
public final class LogicDisguiseState {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LogicDisguiseState.class);
    private static final java.util.Set<String> WARNED = java.util.concurrent.ConcurrentHashMap.newKeySet();
    public static BlockState resolve(String source,int metadata) {
        if (source == null) return null;
        try {
            if (TemplateStateMappings.hasMapping(source,metadata) || SourceVanillaStates.hasSource(source))
                return LegacyStructureStates.resolve(source,metadata,0);
            var id = ResourceLocation.tryParse(source.replace("hbm:tile.","hbm:"));
            if (id == null || !BuiltInRegistries.BLOCK.containsKey(id)) return null; // Original getBlockFromName returned null.
            return LegacyStructureStates.resolve(source,metadata,0);
        } catch (IllegalStateException | IllegalArgumentException invalidDisguise) {
            // A saved facade is optional visual data; retain the original logic cube and its complete program.
            // Strict template placement still reports missing source dependencies through its normal resolver.
            if (WARNED.add(source+":"+metadata)) LOGGER.warn("Cannot resolve saved structure disguise {}:{}",source,metadata,invalidDisguise);
            return null;
        }
    }
    private LogicDisguiseState() {}
}

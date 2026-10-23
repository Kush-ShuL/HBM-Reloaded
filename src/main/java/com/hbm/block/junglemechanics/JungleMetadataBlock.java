package com.hbm.block.junglemechanics;
import net.minecraft.world.level.block.state.BlockState;
/** Only source families which actually store the old nibble; no guessed property-name decoder. */
public interface JungleMetadataBlock {BlockState fromLegacy(int metadata);int sourceVariants();}

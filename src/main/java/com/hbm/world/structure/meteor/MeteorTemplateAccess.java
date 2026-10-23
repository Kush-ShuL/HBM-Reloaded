package com.hbm.world.structure.meteor;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/** Read the actual vanilla template without spending its connector shuffle draws. */
public interface MeteorTemplateAccess {
    StructureTemplate hbm$meteorTemplate(StructureTemplateManager manager);
}

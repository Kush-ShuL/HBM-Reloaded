package com.hbm.interfaces;

import net.minecraft.world.level.Level;

/** Original X5846 trigger protocol shared by remote detonators and every triggerable bomb. */
public interface IBomb {
    BombReturnCode explode(Level level, int x, int y, int z);
    enum BombReturnCode {
        UNDEFINED(false, ""), DETONATED(true, "bomb.detonated"), TRIGGERED(true, "bomb.triggered"),
        LAUNCHED(true, "bomb.launched"), ERROR_MISSING_COMPONENT(false, "bomb.missingComponent"),
        ERROR_INCOMPATIBLE(false, "bomb.incompatible"), ERROR_NO_BOMB(false, "bomb.nobomb");
        private final boolean success;
        private final String message;
        BombReturnCode(boolean success, String message) { this.success = success; this.message = message; }
        public String getUnlocalizedMessage() { return message; }
        public boolean wasSuccessful() { return success; }
    }
}

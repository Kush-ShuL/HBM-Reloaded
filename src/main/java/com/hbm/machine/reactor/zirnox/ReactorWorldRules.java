package com.hbm.machine.reactor.zirnox;
import net.minecraft.world.level.GameRules;
/** The original RBMK dial name/default used by actual Zirnox debris, saved by vanilla gamerules. */
public final class ReactorWorldRules {
    public static final GameRules.Key<GameRules.BooleanValue> PERMANENT_SCRAP=GameRules.register("dialEnablePermaScrap",GameRules.Category.MISC,GameRules.BooleanValue.create(true));
    private ReactorWorldRules(){}
    public static void initialize(){}
}

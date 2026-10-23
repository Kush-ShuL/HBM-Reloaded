package com.hbm.entity.rocket;

import com.hbm.machine.lambda.LambdaContent;
import java.util.function.Consumer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** The real one-payload SourceRocketBase carrier; no missile steering/collision substitution. */
public final class EntityRocketLambda extends SourceRocketBase {
    public static Consumer<EntityRocketLambda> CLIENT_TICK=rocket->{};
    public EntityRocketLambda(EntityType<? extends EntityRocketLambda> type,Level level){super(type,level,1);}
    public EntityRocketLambda(Level level){this(LambdaContent.ROCKET.get(),level);}
    public net.minecraft.util.RandomSource sourceRandom(){return random;}
    @Override public void tick(){super.tick();if(level().isClientSide)CLIENT_TICK.accept(this);}
}

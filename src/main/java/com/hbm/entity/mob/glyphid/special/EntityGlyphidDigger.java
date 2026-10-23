package com.hbm.entity.mob.glyphid.special;

import com.hbm.block.LegacyBlockTraits;
import com.hbm.bomb.conventional.RubbleProjectile;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.entity.mob.glyphid.GlyphidStats;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmContent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Source Digger: the common Glyphid task AI plus its original rubble ground-slam. */
public final class EntityGlyphidDigger extends EntityGlyphid {
    protected Entity lastTarget;
    protected double lastX, lastY, lastZ;
    public int timer;

    public EntityGlyphidDigger(EntityType<? extends EntityGlyphidDigger> type, Level level) { super(type, level); }
    public EntityGlyphidDigger(Level level) { this(GlyphidSpecialContent.DIGGER.get(), level); }
    @Override public ResourceLocation getSkin() { return ResourceLocation.parse("hbm:textures/entity/glyphid_digger.png"); }
    @Override public double sourceScale() { return 1.3D; }
    @Override public GlyphidStats.StatBundle getStats() { return GlyphidStats.getStats().getDigger(); }

    @Override public void tick() {
        super.tick();
        Entity target = legacyTarget;
        if (target != null && isAlive()) {
            // Deliberately before groundSlam, as in source. lastTarget is never assigned by source.
            lastX = target.getX(); lastY = target.getY(); lastZ = target.getZ();
            if (--timer <= 0) { groundSlam(); timer = 120; }
        }
    }

    public void groundSlam() {
        if (level().isClientSide || !(legacyTarget instanceof LivingEntity) || sourceDistanceTo(legacyTarget) >= 30) return;
        Entity target = legacyTarget;
        int bugX = (int)getX(), bugY = (int)getY(), bugZ = (int)getZ();
        LegacyVector look = sourceLook();
        List<BlockPos> positions = new ArrayList<>();
        appendPath(positions, bugX, bugY, bugZ, 6, look);
        for (int i = 0; i < 8; i++) {
            look.rotateAroundY(-1F / 16F);
            appendPath(positions, bugX, bugY - 1, bugZ, 6, look);
        }
        double vx = target.getX() - lastX, vy = target.getY() - lastY, vz = target.getZ() - lastZ;
        if (lastTarget != target) vx = vy = vz = 0;
        LegacyVector delta = LegacyVector.createVectorHelper(target.getX() - getX() + vx * 60,
                (target.getY() + target.getBbHeight() / 2) - (getY() + 1) + vy * 60,
                target.getZ() - getZ() + vz * 60);
        if (delta.lengthVector() < 3) return;
        double yaw = -Math.atan2(delta.xCoord, delta.zCoord);
        double x = Math.sqrt(delta.xCoord * delta.xCoord + delta.zCoord * delta.zCoord), y = delta.yCoord;
        double velocity = 1.2, v2 = velocity * velocity, gravity = .03D;
        double sign = sourceDistanceTo(target) > 20 ? 1 : -1;
        double pitch = Math.atan((v2 + Math.sqrt(v2 * v2 - gravity * (gravity * x * x + 2 * y * v2)) * sign) / (gravity * x));
        LegacyVector heading = null;
        if (!Double.isNaN(pitch)) {
            heading = LegacyVector.createVectorHelper(velocity, 0, 0);
            heading.rotateAroundZ((float)-pitch);
            heading.rotateAroundY((float)-(yaw + Math.PI * .5));
        }
        // Native context supplies the same source entity/explosion origin to contextual block resistance.
        Explosion context = new Explosion(level(), this, getX(), getY(), getZ(), 0, false, Explosion.BlockInteraction.DESTROY);
        float concreteResistance = HbmContent.block("concrete").getExplosionResistance();
        for (BlockPos pos : positions) {
            var state = level().getBlockState(pos);
            float resistance = state.getExplosionResistance(level(), pos, context);
            if (resistance < concreteResistance && LegacyBlockTraits.isNormalCube(level(), pos, state)
                    && !(state.getBlock() instanceof DummyableBlock) && level().getBlockEntity(pos) == null) {
                var rubble = new RubbleProjectile(level());
                rubble.setPos(pos.getX() + .5F, pos.getY() + 2, pos.getZ() + .5F);
                rubble.setMetaBasedOnBlock(state);
                if (heading != null) rubble.setThrowableHeading(heading.xCoord, heading.yCoord, heading.zCoord, (float)velocity, random.nextFloat());
                level().addFreshEntity(rubble);
                level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    /** Old sqrt_double keeps double coordinate differences until the final float result. */
    private float sourceDistanceTo(Entity target) { return (float)Math.sqrt(distanceToSqr(target)); }

    /** Official1.7.10 EntityLivingBase.getLook(1), including its float pi subtraction. */
    private LegacyVector sourceLook() {
        float yaw = -getYRot() * (float)(Math.PI / 180D) - (float)Math.PI;
        float pitch = -getXRot() * (float)(Math.PI / 180D);
        float c = Mth.cos(yaw), s = Mth.sin(yaw), cp = -Mth.cos(pitch), sp = Mth.sin(pitch);
        return LegacyVector.createVectorHelper((double)(s * cp), (double)sp, (double)(c * cp));
    }

    /** Original Library.getBlockPosInPath: inclusive six steps, truncating casts and repeated positions. */
    private static void appendPath(List<BlockPos> positions, int x, int y, int z, int length, LegacyVector direction) {
        for (int i = 0; i <= length; i++) positions.add(new BlockPos((int)(x + direction.xCoord * i), y, (int)(z + direction.zCoord * i)));
    }
    @Override public boolean isArmorBroken(float amount) { return random.nextInt(100) <= Math.min(Math.pow(amount * .25, 2), 100); }
    @Override protected boolean canDig() { return true; }
}

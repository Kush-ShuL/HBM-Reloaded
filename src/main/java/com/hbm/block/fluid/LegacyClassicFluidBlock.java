package com.hbm.block.fluid;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.RandomSource;

/** Mechanically adapted official Forge 1.7.10 BlockFluidClassic; King Lemming / OvermindDL1. */
public class LegacyClassicFluidBlock extends LegacyFluidBlock {
    protected final boolean[] isOptimalFlowDirection = new boolean[4];
    protected final int[] flowCost = new int[4];
    public LegacyClassicFluidBlock(WorldFluidSpec spec, Supplier<LegacyWorldFluid> fluid) { super(spec, fluid); }
    public int getQuantaValue(LegacyFluidWorld world, int x, int y, int z)
    {
        if (world.getBlock(x, y, z) == Blocks.AIR)
        {
            return 0;
        }

        if (world.getBlock(x, y, z) != this)
        {
            return -1;
        }

        int quantaRemaining = quantaPerBlock - world.getBlockMetadata(x, y, z);
        return quantaRemaining;
    }

    public boolean canCollideCheck(int meta, boolean fullHit)
    {
        return fullHit && meta == 0;
    }

    public int getMaxRenderHeightMeta()
    {
        return 0;
    }

    public int getLightValue(LegacyFluidWorld world, int x, int y, int z)
    {
        if (maxScaledLight == 0)
        {
            return super.getLightValue(world, x, y, z);
        }
        int data = quantaPerBlock - world.getBlockMetadata(x, y, z) - 1;
        return (int) (data / quantaPerBlockFloat * maxScaledLight);
    }

    public void updateTick(LegacyFluidWorld world, int x, int y, int z, RandomSource rand)
    {
        int quantaRemaining = quantaPerBlock - world.getBlockMetadata(x, y, z);
        int expQuanta = -101;

        // check adjacent block levels if non-source
        if (quantaRemaining < quantaPerBlock)
        {
            int y2 = y - densityDir;

            if (world.getBlock(x,     y2, z    ) == this ||
                world.getBlock(x - 1, y2, z    ) == this ||
                world.getBlock(x + 1, y2, z    ) == this ||
                world.getBlock(x,     y2, z - 1) == this ||
                world.getBlock(x,     y2, z + 1) == this)
            {
                expQuanta = quantaPerBlock - 1;
            }
            else
            {
                int maxQuanta = -100;
                maxQuanta = getLargerQuanta(world, x - 1, y, z,     maxQuanta);
                maxQuanta = getLargerQuanta(world, x + 1, y, z,     maxQuanta);
                maxQuanta = getLargerQuanta(world, x,     y, z - 1, maxQuanta);
                maxQuanta = getLargerQuanta(world, x,     y, z + 1, maxQuanta);

                expQuanta = maxQuanta - 1;
            }

            // decay calculation
            if (expQuanta != quantaRemaining)
            {
                quantaRemaining = expQuanta;

                if (expQuanta <= 0)
                {
                    world.setBlock(x, y, z, Blocks.AIR);
                }
                else
                {
                    world.setBlockMetadataWithNotify(x, y, z, quantaPerBlock - expQuanta, 3);
                    world.scheduleBlockUpdate(x, y, z, this, tickRate);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                }
            }
        }
        // This is a "source" block, set meta to zero, and send a server only update
        else if (quantaRemaining >= quantaPerBlock)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }

        // Flow vertically if possible
        if (canDisplace(world, x, y + densityDir, z))
        {
            flowIntoBlock(world, x, y + densityDir, z, 1);
            return;
        }

        // Flow outward if possible
        int flowMeta = quantaPerBlock - quantaRemaining + 1;
        if (flowMeta >= quantaPerBlock)
        {
            return;
        }

        if (isSourceBlock(world, x, y, z) || !isFlowingVertically(world, x, y, z))
        {
            if (world.getBlock(x, y - densityDir, z) == this)
            {
                flowMeta = 1;
            }
            boolean flowTo[] = getOptimalFlowDirections(world, x, y, z);

            if (flowTo[0]) flowIntoBlock(world, x - 1, y, z,     flowMeta);
            if (flowTo[1]) flowIntoBlock(world, x + 1, y, z,     flowMeta);
            if (flowTo[2]) flowIntoBlock(world, x,     y, z - 1, flowMeta);
            if (flowTo[3]) flowIntoBlock(world, x,     y, z + 1, flowMeta);
        }
    }

    public boolean isFlowingVertically(LegacyFluidWorld world, int x, int y, int z)
    {
        return world.getBlock(x, y + densityDir, z) == this ||
            (world.getBlock(x, y, z) == this && canFlowInto(world, x, y + densityDir, z));
    }

    public boolean isSourceBlock(LegacyFluidWorld world, int x, int y, int z)
    {
        return world.getBlock(x, y, z) == this && world.getBlockMetadata(x, y, z) == 0;
    }

    protected boolean[] getOptimalFlowDirections(LegacyFluidWorld world, int x, int y, int z)
    {
        for (int side = 0; side < 4; side++)
        {
            flowCost[side] = 1000;

            int x2 = x;
            int y2 = y;
            int z2 = z;

            switch (side)
            {
                case 0: --x2; break;
                case 1: ++x2; break;
                case 2: --z2; break;
                case 3: ++z2; break;
            }

            if (!canFlowInto(world, x2, y2, z2) || isSourceBlock(world, x2, y2, z2))
            {
                continue;
            }

            if (canFlowInto(world, x2, y2 + densityDir, z2))
            {
                flowCost[side] = 0;
            }
            else
            {
                flowCost[side] = calculateFlowCost(world, x2, y2, z2, 1, side);
            }
        }

        int min = flowCost[0];
        for (int side = 1; side < 4; side++)
        {
            if (flowCost[side] < min)
            {
                min = flowCost[side];
            }
        }
        for (int side = 0; side < 4; side++)
        {
            isOptimalFlowDirection[side] = flowCost[side] == min;
        }
        return isOptimalFlowDirection;
    }

    protected int calculateFlowCost(LegacyFluidWorld world, int x, int y, int z, int recurseDepth, int side)
    {
        int cost = 1000;
        for (int adjSide = 0; adjSide < 4; adjSide++)
        {
            if ((adjSide == 0 && side == 1) ||
                (adjSide == 1 && side == 0) ||
                (adjSide == 2 && side == 3) ||
                (adjSide == 3 && side == 2))
            {
                continue;
            }

            int x2 = x;
            int y2 = y;
            int z2 = z;

            switch (adjSide)
            {
                case 0: --x2; break;
                case 1: ++x2; break;
                case 2: --z2; break;
                case 3: ++z2; break;
            }

            if (!canFlowInto(world, x2, y2, z2) || isSourceBlock(world, x2, y2, z2))
            {
                continue;
            }

            if (canFlowInto(world, x2, y2 + densityDir, z2))
            {
                return recurseDepth;
            }

            if (recurseDepth >= 4)
            {
                continue;
            }

            int min = calculateFlowCost(world, x2, y2, z2, recurseDepth + 1, adjSide);
            if (min < cost)
            {
                cost = min;
            }
        }
        return cost;
    }

    protected void flowIntoBlock(LegacyFluidWorld world, int x, int y, int z, int meta)
    {
        if (meta < 0) return;
        if (displaceIfPossible(world, x, y, z))
        {
            world.setBlock(x, y, z, this, meta, 3);
        }
    }

    protected boolean canFlowInto(LegacyFluidWorld world, int x, int y, int z)
    {
        if (world.isAir(x, y, z)) return true;

        Block block = world.getBlock(x, y, z);
        if (block == this)
        {
            return true;
        }

        Boolean override = displacement(block.defaultBlockState());
        if (override != null) return override;

        LegacyMaterial material = LegacyMaterials.of(block.defaultBlockState());
        if (material.blocksMovement()  ||
            material == LegacyMaterial.water ||
            material == LegacyMaterial.lava  ||
            material == LegacyMaterial.portal)
        {
            return false;
        }

        int density = getDensity(world, x, y, z);
        if (density == Integer.MAX_VALUE) 
        {
             return true;
        }
        
        if (this.density > density)
        {
            return true;
        }
        else
        {
        	return false;
        }
    }

    protected int getLargerQuanta(LegacyFluidWorld world, int x, int y, int z, int compare)
    {
        int quantaRemaining = getQuantaValue(world, x, y, z);
        if (quantaRemaining <= 0)
        {
            return compare;
        }
        return quantaRemaining >= compare ? quantaRemaining : compare;
    }

}

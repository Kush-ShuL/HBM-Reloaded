package com.hbm.block.fluid;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.RandomSource;

/** Mechanically adapted official Forge 1.7.10 BlockFluidFinite; King Lemming / OvermindDL1. */
public class LegacyFiniteFluidBlock extends LegacyFluidBlock {
    public LegacyFiniteFluidBlock(WorldFluidSpec spec, Supplier<LegacyWorldFluid> fluid) { super(spec, fluid); }
    public int getQuantaValue(LegacyFluidWorld world, int x, int y, int z)
    {
        if (world.isAir(x, y, z))
        {
            return 0;
        }

        if (world.getBlock(x, y, z) != this)
        {
            return -1;
        }

        int quantaRemaining = world.getBlockMetadata(x, y, z) + 1;
        return quantaRemaining;
    }

    public boolean canCollideCheck(int meta, boolean fullHit)
    {
        return fullHit && meta == quantaPerBlock - 1;
    }

    public int getMaxRenderHeightMeta()
    {
        return quantaPerBlock - 1;
    }

    public void updateTick(LegacyFluidWorld world, int x, int y, int z, RandomSource rand)
    {
        boolean changed = false;
        int quantaRemaining = world.getBlockMetadata(x, y, z) + 1;

        // Flow vertically if possible
        int prevRemaining = quantaRemaining;
        quantaRemaining = tryToFlowVerticallyInto(world, x, y, z, quantaRemaining);

        if (quantaRemaining < 1)
        {
            return;
        }
        else if (quantaRemaining != prevRemaining)
        {
            changed = true;
            if (quantaRemaining == 1)
            {
                world.setBlockMetadataWithNotify(x, y, z, quantaRemaining - 1, 2);
                return;
            }
        }
        else if (quantaRemaining == 1)
        {
            return;
        }

        // Flow out if possible
        int lowerthan = quantaRemaining - 1;
        if (displaceIfPossible(world, x,     y, z - 1)) world.setBlock(x,     y, z - 1, Blocks.AIR);
        if (displaceIfPossible(world, x,     y, z + 1)) world.setBlock(x,     y, z + 1, Blocks.AIR);
        if (displaceIfPossible(world, x - 1, y, z    )) world.setBlock(x - 1, y, z,     Blocks.AIR);
        if (displaceIfPossible(world, x + 1, y, z    )) world.setBlock(x + 1, y, z,     Blocks.AIR);
        int north = getQuantaValueBelow(world, x,     y, z - 1, lowerthan);
        int south = getQuantaValueBelow(world, x,     y, z + 1, lowerthan);
        int west  = getQuantaValueBelow(world, x - 1, y, z,     lowerthan);
        int east  = getQuantaValueBelow(world, x + 1, y, z,     lowerthan);
        int total = quantaRemaining;
        int count = 1;

        if (north >= 0)
        {
            count++;
            total += north;
        }

        if (south >= 0)
        {
            count++;
            total += south;
        }

        if (west >= 0)
        {
            count++;
            total += west;
        }

        if (east >= 0)
        {
            ++count;
            total += east;
        }

        if (count == 1)
        {
            if (changed)
            {
                world.setBlockMetadataWithNotify(x, y, z, quantaRemaining - 1, 2);
            }
            return;
        }

        int each = total / count;
        int rem = total % count;
        if (north >= 0)
        {
            int newnorth = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++newnorth;
                --rem;
            }

            if (newnorth != north)
            {
                if (newnorth == 0)
                {
                    world.setBlock(x, y, z - 1, Blocks.AIR);
                }
                else
                {
                    world.setBlock(x, y, z - 1, this, newnorth - 1, 2);
                }
                world.scheduleBlockUpdate(x, y, z - 1, this, tickRate);
            }
            --count;
        }

        if (south >= 0)
        {
            int newsouth = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++newsouth;
                --rem;
            }

            if (newsouth != south)
            {
                if (newsouth == 0)
                {
                    world.setBlock(x, y, z + 1, Blocks.AIR);
                }
                else
                {
                    world.setBlock(x, y, z + 1, this, newsouth - 1, 2);
                }
                world.scheduleBlockUpdate(x, y, z + 1, this, tickRate);
            }
            --count;
        }

        if (west >= 0)
        {
            int newwest = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++newwest;
                --rem;
            }
            if (newwest != west)
            {
                if (newwest == 0)
                {
                    world.setBlock(x - 1, y, z, Blocks.AIR);
                }
                else
                {
                    world.setBlock(x - 1, y, z, this, newwest - 1, 2);
                }
                world.scheduleBlockUpdate(x - 1, y, z, this, tickRate);
            }
            --count;
        }

        if (east >= 0)
        {
            int neweast = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++neweast;
                --rem;
            }

            if (neweast != east)
            {
                if (neweast == 0)
                {
                    world.setBlock(x + 1, y, z, Blocks.AIR);
                }
                else
                {
                    world.setBlock(x + 1, y, z, this, neweast - 1, 2);
                }
                world.scheduleBlockUpdate(x + 1, y, z, this, tickRate);
            }
            --count;
        }

        if (rem > 0)
        {
            ++each;
        }
        world.setBlockMetadataWithNotify(x, y, z, each - 1, 2);
    }

    public int tryToFlowVerticallyInto(LegacyFluidWorld world, int x, int y, int z, int amtToInput)
    {
        int otherY = y + densityDir;
        if (otherY < world.getMinHeight() || otherY >= world.getHeight())
        {
            world.setBlock(x, y, z, Blocks.AIR);
            return 0;
        }

        int amt = getQuantaValueBelow(world, x, otherY, z, quantaPerBlock);
        if (amt >= 0)
        {
            amt += amtToInput;
            if (amt > quantaPerBlock)
            {
                world.setBlock(x, otherY, z, this, quantaPerBlock - 1, 3);
                world.scheduleBlockUpdate(x, otherY, z, this, tickRate);
                return amt - quantaPerBlock;
            }
            else if (amt > 0)
            {
                world.setBlock(x, otherY, z, this, amt - 1, 3);
                world.scheduleBlockUpdate(x, otherY, z, this, tickRate);
                world.setBlock(x, y, z, Blocks.AIR);
                return 0;
            }
            return amtToInput;
        }
        else
        {
            int density_other = getDensity(world, x, otherY, z);
            if (density_other == Integer.MAX_VALUE)
            {
                if (displaceIfPossible(world, x, otherY, z))
                {
                    world.setBlock(x, otherY, z, this, amtToInput - 1, 3);
                    world.scheduleBlockUpdate(x, otherY, z, this, tickRate);
                    world.setBlock(x, y, z, Blocks.AIR);
                    return 0;
                }
                else
                {
                    return amtToInput;
                }
            }

            if (densityDir < 0)
            {
                if (density_other < density) // then swap
                {
                    LegacyFluidBlock block = (LegacyFluidBlock)world.getBlock(x, otherY, z);
                    int otherData = world.getBlockMetadata(x, otherY, z);
                    world.setBlock(x, otherY, z, this,  amtToInput - 1, 3);
                    world.setBlock(x, y,      z, block, otherData, 3);
                    world.scheduleBlockUpdate(x, otherY, z, this,  tickRate);
                    world.scheduleBlockUpdate(x, y,      z, block, block.tickRate(world));
                    return 0;
                }
            }
            else
            {
                if (density_other > density)
                {
                    LegacyFluidBlock block = (LegacyFluidBlock)world.getBlock(x, otherY, z);
                    int otherData = world.getBlockMetadata(x, otherY, z);
                    world.setBlock(x, otherY, z, this,  amtToInput - 1, 3);
                    world.setBlock(x, y,      z, block, otherData, 3);
                    world.scheduleBlockUpdate(x, otherY, z, this,  tickRate);
                    world.scheduleBlockUpdate(x, y,      z, block, block.tickRate(world));
                    return 0;
                }
            }
            return amtToInput;
        }
    }

}

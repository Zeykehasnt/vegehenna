package org.ivangeevo.vegehenna.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.ivangeevo.vegehenna.block.interfaces.CropBlockAdded;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;


public class CropBlockHelper {

    public static void handleDailyCropGrowth(World world, BlockPos pos, BlockState state, Random rand, CropBlock cropBlock) {
        int timeOfDay = (int)(world.getTime() % 24000L);

        if (timeOfDay > 14000 && timeOfDay < 22000) {
            // night
            if (state.get(DailyGrowthCrop.HAS_GROWN_TODAY)) {
                setHasGrownToday(world, pos, false);
            }
        } else {
            if (!state.get(DailyGrowthCrop.HAS_GROWN_TODAY) && getWeedsGrowthLevel(world, pos) == 0 && canGrowAtCurrentLightLevel(world, pos, cropBlock)) {
                Block blockBelow = world.getBlockState(pos.down()).getBlock();

                if (blockBelow != null && blockBelow.isBlockHydratedForPlantGrowthOn(world, pos.down())) {
                    float growthChance = ((CropBlockAdded)cropBlock).vegehenna$getBaseGrowthChance();

                    if (blockBelow.getIsFertilizedForPlantGrowth(world, pos.down())) {
                        growthChance *= 2F;
                    }

                    if (rand.nextFloat() <= growthChance) {
                        incrementGrowthLevel(world, pos, state, cropBlock);
                        updateFlagForGrownToday(world, pos, cropBlock);
                    }
                }
            }
        }

    }

    protected static boolean canGrowAtCurrentLightLevel(World world, BlockPos pos, CropBlock cropBlock) {
        Block bwtLightBlock = Registries.BLOCK.get(Identifier.of("bwt", "light_block"));
        BlockState lightBlockState = FabricLoader.getInstance().isModLoaded("bwt")
                ? bwtLightBlock.getDefaultState()
                : Blocks.REDSTONE_LAMP.getDefaultState();

        if (((CropBlockAdded)cropBlock).vegehenna$requiresNaturalLight()) {
            return isLitLightBlock(world, pos.up(), lightBlockState) || isLitLightBlock(world, pos.up(2), lightBlockState);
        } else {
            return world.getLightLevel(pos) >= ((CropBlockAdded)cropBlock).vegehenna$getLightLevelForGrowth();
        }

    }

    private static boolean isLitLightBlock(World world, BlockPos pos, BlockState lightBlockState) {
        return world.getBlockState(pos).equals(lightBlockState.with(Properties.LIT, true));
    }

    protected static void updateFlagForGrownToday(World world, BlockPos pos, CropBlock cropBlock) {
        // fertilized crops can grow twice in a day
        Block blockBelow = world.getBlockState(pos.down()).getBlock();

        if ( blockBelow != null ) {
            if (!blockBelow.getIsFertilizedForPlantGrowth(world, pos.down()) || cropBlock.getAge(world.getBlockState(pos)) % 2 == 0 ) {
                setHasGrownToday(world, pos, true);
            }
        }
    }

    // Sets whether the block has grown today
    public static void setHasGrownToday(World world, BlockPos pos, boolean hasGrown) {
        BlockState currentState = world.getBlockState(pos);
        //TODO: confirm if the blockstate flag below is actually the correct one for what the comment below says it does.
        // This one "2" is Block.NOTIFY_LISTENERS, which is sus :D

        // No notify flag is equivalent to passing '2' for flags to suppress visual updates and block updates
        world.setBlockState(pos, currentState.with(DailyGrowthCrop.HAS_GROWN_TODAY, hasGrown), 2);
    }

    private static int getWeedsGrowthLevel(WorldAccess world, BlockPos pos) {
        // Implement logic for weeds growth level
        return 0;  // Default example, adapt to your logic
    }

    // Method to increment growth level for the crop (Block)
    public static void incrementGrowthLevel(World world, BlockPos pos, BlockState state, Block cropBlock) {
        if (cropBlock instanceof DailyGrowthCrop) {
            ((DailyGrowthCrop) cropBlock).vegehenna$incrementGrowthLevel(world, pos, state);
        }
    }
}

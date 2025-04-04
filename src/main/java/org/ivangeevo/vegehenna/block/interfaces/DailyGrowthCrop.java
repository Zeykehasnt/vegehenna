package org.ivangeevo.vegehenna.block.interfaces;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.ivangeevo.vegehenna.util.CropBlockHelper;

public interface DailyGrowthCrop extends CropBlockAdded {

    BooleanProperty HAS_GROWN_TODAY = BooleanProperty.of("has_grown_today");

    default void attemptToGrow(World world, BlockPos pos, BlockState state) {
        // Use CropBlockHelper for common growth logic
        CropBlockHelper.handleDailyCropGrowth(world, pos, state, world.getRandom(), (CropBlock)state.getBlock());
    }

    /** The new default age-to-shape outline for crop blocks **/
    VoxelShape[] NEW_DEFAULT_AGE_TO_SHAPE = new VoxelShape[] {
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 5.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 9.0, 14.0)
    };

    VoxelShape[] NEW_BEETS_AGE_TO_SHAPE = new VoxelShape[] {
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0)
    };

    VoxelShape[] NEW_CARROTS_AGE_TO_SHAPE = new VoxelShape[] {
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 5.0, 14.0)
    };

}

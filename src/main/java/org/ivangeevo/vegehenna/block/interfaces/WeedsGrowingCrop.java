package org.ivangeevo.vegehenna.block.interfaces;

import net.minecraft.block.Block;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.shape.VoxelShape;

public interface WeedsGrowingCrop {

    BooleanProperty HAS_WEEDS = BooleanProperty.of("has_weeds");
    IntProperty WEEDS_LEVEL = IntProperty.of("weeds_level", 0, 4);

    VoxelShape[] WEEDS_AGE_TO_SHAPE = new VoxelShape[] {
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 5.0, 14.0)
    };

}

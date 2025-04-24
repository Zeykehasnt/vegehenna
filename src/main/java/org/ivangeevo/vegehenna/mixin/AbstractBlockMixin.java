package org.ivangeevo.vegehenna.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.ivangeevo.vegehenna.block.interfaces.WeedsGrowingCrop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin implements WeedsGrowingCrop
{

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void removeWeeds(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {

        if (!isCropBlock()) {
            return;
        }

        // Only allow weed removal on bottom block of tall crops
        if (state.contains(Properties.DOUBLE_BLOCK_HALF) && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
            BlockState bottomState = world.getBlockState(pos);
            if (bottomState.get(HAS_WEEDS)) {
                world.setBlockState(pos, bottomState.with(HAS_WEEDS, false)); // Remove weeds
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Unique
    private boolean isCropBlock() {
        return (AbstractBlock)(Object)this instanceof CropBlock;
    }

}

package org.ivangeevo.vegehenna.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionTypes;
import org.ivangeevo.vegehenna.block.interfaces.WeedsGrowingCrop;
import org.ivangeevo.vegehenna.data.ModDataComponents;
import org.ivangeevo.vegehenna.tag.BTWRConventionalTags;
import org.ivangeevo.vegehenna.block.interfaces.DailyGrowthCrop;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock implements Fertilizable, DailyGrowthCrop, WeedsGrowingCrop {

    @Shadow @Final public static IntProperty AGE;
    @Shadow public abstract int getAge(BlockState state);
    @Shadow public abstract int getMaxAge();
    @Shadow protected abstract IntProperty getAgeProperty();

    public CropBlockMixin(Settings settings) {
        super(settings);
    }

    //@Inject(method = "appendProperties", at = @At("TAIL"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(HAS_GROWN_TODAY);
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void onAppendPropertiesWeeds(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(HAS_WEEDS, WEEDS_LEVEL);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInitWeeds(Settings settings, CallbackInfo ci) {
        this.setDefaultState(
                this.getStateManager().getDefaultState()
                        .with(this.getAgeProperty(), 0)
                        .with(HAS_WEEDS, false)
                        .with(WEEDS_LEVEL, 0)
        );
    }

    //@Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Settings settings, CallbackInfo ci) {
        this.setDefaultState(
                this.getStateManager().getDefaultState()
                        .with(this.getAgeProperty(), 0)
                        .with(HAS_GROWN_TODAY, false)
        );
    }

    // Custom outline shape
    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        int age = this.getAge(state);
        int weedsAge = this.getWeedsGrowthLevel((WorldAccess) world, pos);

        cir.setReturnValue(state.get(HAS_WEEDS) ? WEEDS_AGE_TO_SHAPE[weedsAge] : NEW_DEFAULT_AGE_TO_SHAPE[age]);
        //cir.setReturnValue(NEW_DEFAULT_AGE_TO_SHAPE[age]);
    }

    // Make it not fertilizable by the traditional way
    @Inject(method = "isFertilizable", at = @At("HEAD"), cancellable = true)
    private void injectedIsFertilizable(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "canPlantOnTop", at = @At("RETURN"), cancellable = true)
    private void injectedCanPlantOnTop(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
       cir.setReturnValue(floor.isIn(BTWRConventionalTags.Blocks.FARMLAND_BLOCKS) || floor.isOf(Blocks.FARMLAND));
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        // Ensure we only apply to the bottom block of two-block tall crops
        if (state.getBlock() instanceof CropBlock && state.contains(Properties.DOUBLE_BLOCK_HALF)) {
            // If it's the upper half of a two-block tall crop, do nothing
            if (state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
                return;
            }

            // For the bottom block of a tall crop, apply weeds
            if (random.nextFloat() < 0.1f) {
                world.setBlockState(pos, state.with(HAS_WEEDS, true).with(WEEDS_LEVEL, 0));
            }
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void slowGrowthWithWeeds(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity != null && entity.getComponents().contains(ModDataComponents.WEEDS_COMPONENT)) {
            if (random.nextFloat() < 0.3f) { // 70% chance to prevent growth
                ci.cancel();
            }
        }
    }

    // uncommented until checking for growth for skipping night regularly by sleeping if figured out, until then use the normal randomTick logic
    //@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.getDimensionEntry().matchesId(DimensionTypes.THE_END_ID) && state.isOf(this)) {
            if (state.getBlock() instanceof DailyGrowthCrop) {
                attemptToGrow(world, pos, state);
            }
        }

        ci.cancel();
    }

    @Override
    public void vegehenna$incrementGrowthLevel(World world, BlockPos pos, BlockState state) {
        int iGrowthLevel = this.getAge(state) + 1;

        world.setBlockState(pos, state.with(AGE, iGrowthLevel),2);

        if (this.getAge(state) >= this.getMaxAge()) {
            Block blockBelow = world.getBlockState(pos.down()).getBlock();

            if ( blockBelow != null ) {
                blockBelow.notifyOfFullStagePlantGrowthOn(world, pos.down(), this);
            }

        }
    }

    @Override
    public float vegehenna$getBaseGrowthChance() {
        return 0.05F;
    }

    @Override
    public int vegehenna$getLightLevelForGrowth() {
        return 9;
    }

    @Override
    public boolean vegehenna$requiresNaturalLight() {
        return true;
    }

    protected boolean canGrowAtCurrentLightLevel(World world, BlockPos pos) {
        Block bwtLightBlock = Registries.BLOCK.get(Identifier.of("bwt", "light_block"));
        BlockState lightBlockState = FabricLoader.getInstance().isModLoaded("bwt")
                ? bwtLightBlock.getDefaultState()
                : Blocks.REDSTONE_LAMP.getDefaultState();

        if (this.vegehenna$requiresNaturalLight()) {
            return isLitLightBlock(world, pos.up(), lightBlockState) || isLitLightBlock(world, pos.up(2), lightBlockState);
        } else {
            return world.getLightLevel(pos) >= vegehenna$getLightLevelForGrowth();
        }

    }

    private boolean isLitLightBlock(World world, BlockPos pos, BlockState lightBlockState) {
        return world.getBlockState(pos).equals(lightBlockState.with(Properties.LIT, true));
    }

}

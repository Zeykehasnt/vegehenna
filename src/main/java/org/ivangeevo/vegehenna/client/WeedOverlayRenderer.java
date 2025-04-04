package org.ivangeevo.vegehenna.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.ivangeevo.vegehenna.VegehennaMod;

import static org.ivangeevo.vegehenna.block.interfaces.WeedsGrowingCrop.WEEDS_LEVEL;

public class WeedOverlayRenderer extends BlockModelRenderer {

    private static final Identifier[] WEED_TEXTURES = new Identifier[]{
            Identifier.of(VegehennaMod.MOD_ID, "textures/block/weed_stage1.png"),
            Identifier.of(VegehennaMod.MOD_ID, "textures/block/weed_stage2.png"),
            Identifier.of(VegehennaMod.MOD_ID, "textures/block/weed_stage3.png")
    };

    public WeedOverlayRenderer(BlockColors colors) {
        super(colors);
    }


    @Override
    public void renderFlat(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random, long seed, int overlay) {
        if (state == null || !(state.getBlock() instanceof CropBlock)) return;

        int weedsLevel = state.get(WEEDS_LEVEL);

        if (weedsLevel == 0 || weedsLevel > 3) return;

        MinecraftClient client = MinecraftClient.getInstance();
        BlockRenderManager blockRenderer = client.getBlockRenderManager();
        Identifier texture = WEED_TEXTURES[weedsLevel - 1];

        // Use the MatrixStack directly to apply transformation
        matrices.push(); // Push a new matrix onto the stack
        matrices.translate(0, 0.2, 0); // Slightly above the crop

        // Render weed texture
        client.getTextureManager().bindTexture(texture);
        blockRenderer.getModelRenderer().render(
                matrices.peek(), vertexConsumer, null, model, 1f, 1f, 1f, state.getLuminance(), overlay
        );

        matrices.pop(); // Pop the matrix off the stack
    }

}

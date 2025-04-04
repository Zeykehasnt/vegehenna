package org.ivangeevo.vegehenna.data;

import com.mojang.serialization.Codec;

public class WeedsComponent {

    private int level;

    public WeedsComponent() {
        this(0);
    }

    public WeedsComponent(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void decrement() {
        this.level--;
    }

    public static final Codec<WeedsComponent> CODEC = Codec.INT.xmap(
            WeedsComponent::new,
            WeedsComponent::getLevel
    );
}

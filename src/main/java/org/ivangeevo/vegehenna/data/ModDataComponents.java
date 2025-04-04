package org.ivangeevo.vegehenna.data;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.ivangeevo.vegehenna.VegehennaMod;

public class ModDataComponents {

    public static final ComponentType<WeedsComponent> WEEDS_COMPONENT = ComponentType.<WeedsComponent>builder()
            .codec(WeedsComponent.CODEC)
            .build();

    public static void registerComponents() {
        register(Registries.DATA_COMPONENT_TYPE, WEEDS_COMPONENT, "weeds_component");
    }

    private static void register(Registry<ComponentType<?>> registryType, ComponentType<?> componentType, String stringName)
    {
        Registry.register(registryType, VegehennaMod.MOD_ID + ":" + stringName, componentType);
    }

}

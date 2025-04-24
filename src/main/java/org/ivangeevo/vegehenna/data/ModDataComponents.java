package org.ivangeevo.vegehenna.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.VegehennaMod;

public class ModDataComponents {

    public static final ComponentType<WeedsComponent> WEEDS_COMPONENT = ComponentType.<WeedsComponent>builder()
            .codec(WeedsComponent.CODEC)
            .build();

    public static AttachmentType<Boolean> HAS_WEEDS = AttachmentRegistry.createPersistent(Identifier.of(VegehennaMod.MOD_ID,"has_weeds"), Codec.BOOL);
    public static AttachmentType<Integer> WEEDS_LEVEL = AttachmentRegistry.createPersistent(Identifier.of(VegehennaMod.MOD_ID,"weeds_level"), Codec.INT);


    public static void registerComponents() {
        register(Registries.DATA_COMPONENT_TYPE, WEEDS_COMPONENT, "weeds_component");
    }

    private static void register(Registry<ComponentType<?>> registryType, ComponentType<?> componentType, String stringName)
    {
        Registry.register(registryType, VegehennaMod.MOD_ID + ":" + stringName, componentType);
    }

}

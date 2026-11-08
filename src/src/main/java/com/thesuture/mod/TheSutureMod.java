package com.thesuture.mod;

import com.thesuture.mod.director.ParanoiaDirector;
import com.thesuture.mod.entity.SutureEntity;
import com.thesuture.mod.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TheSutureMod implements ModInitializer {
    public static final String MOD_ID = "the_suture";

    public static final EntityType<SutureEntity> SUTURE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "suture"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SutureEntity::new)
                    .dimensions(EntityDimensions.fixed(1.95F, 4.2F)) // 2 blocks wide, 4.2 blocks tall
                    .trackRangeBlocks(96)
                    .build()
    );

    // Spawn Egg: Base color Red (0x8B0000), Dots color Black (0x050505)
    public static final Item SUTURE_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "suture_spawn_egg"),
            new SpawnEggItem(SUTURE_ENTITY, 0x8B0000, 0x050505, new Item.Settings())
    );

    @Override
    public void onInitialize() {
        // 1. Register Sound Events
        ModSounds.registerSounds();

        // 2. Register Entity Attributes
        FabricDefaultAttributeRegistry.register(SUTURE_ENTITY, SutureEntity.createAttributes());

        // 3. Add Spawn Egg to Spawn Eggs item group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(SUTURE_SPAWN_EGG);
        });

        // 4. Hook Paranoia Threat Director into server world tick
        ServerTickEvents.END_WORLD_TICK.register(ParanoiaDirector::tick);
    }
}

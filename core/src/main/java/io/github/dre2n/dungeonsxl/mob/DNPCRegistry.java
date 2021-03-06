/*
 * Copyright (C) 2012-2016 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.mob;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCCreateEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.MobType;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * @author Daniel Saukel
 */
public class DNPCRegistry implements NPCRegistry {

    NPCRegistry registry = CitizensAPI.getNPCRegistry();

    @Override
    public NPC createNPC(EntityType type, String name) {
        return createNPC(type, UUID.randomUUID(), 0, name);
    }

    @Override
    public NPC createNPC(EntityType type, UUID uuid, int id, String name) {
        NPC npc = null;
        try {
            Method method = registry.getClass().getDeclaredMethod("getByType", EntityType.class, UUID.class, int.class, String.class);
            method.setAccessible(true);
            npc = (NPC) method.invoke(registry, type, uuid, id, name);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            throw new IllegalStateException("Could not create NPC: " + exception.getClass().getSimpleName());
        }

        if (npc == null) {
            throw new IllegalStateException("Could not create NPC: npc is null");
        }

        Bukkit.getPluginManager().callEvent(new NPCCreateEvent(npc));

        if (type == EntityType.ARMOR_STAND && !npc.hasTrait(ArmorStandTrait.class)) {
            npc.addTrait(ArmorStandTrait.class);
        }

        return npc;
    }

    @Override
    public void deregister(NPC npc) {
        registry.deregister(npc);
    }

    @Override
    public void deregisterAll() {
        registry.deregisterAll();
    }

    @Override
    public NPC getById(int id) {
        return registry.getById(id);
    }

    @Override
    public NPC getByUniqueId(UUID uuid) {
        return registry.getByUniqueId(uuid);
    }

    @Override
    public NPC getByUniqueIdGlobal(UUID uuid) {
        return registry.getByUniqueIdGlobal(uuid);
    }

    @Override
    public NPC getNPC(Entity entity) {
        return registry.getNPC(entity);
    }

    @Override
    public boolean isNPC(Entity entity) {
        return registry.isNPC(entity);
    }

    @Override
    public Iterable<NPC> sorted() {
        return registry.sorted();
    }

    @Override
    public Iterator<NPC> iterator() {
        return registry.iterator();
    }

    /**
     * Clones an NPC without spamming the config.
     *
     * @param npc
     * the NPC to clone
     * @return
     * a clone of the NPC
     */
    public NPC createTransientClone(NPC npc) {
        NPC copy = createNPC(npc.getTrait(MobType.class).getType(), npc.getFullName());
        for (Trait trait : copy.getTraits()) {
            trait.onCopy();
        }
        return copy;
    }

}

package org.pat.abilities.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.pat.abilities.Abilities;
import org.pat.abilities.Objects.AbilityUtil;

import java.util.UUID;

public class Join implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        ItemStack item = p.getInventory().getItemInMainHand();
        Material mat = item != null ? item.getType() : null;
        AbilityUtil ability = Abilities.selectedAbility.containsKey(uuid) ? Abilities.selectedAbility.get(uuid) : null;

        FoodCancelInjector.inject(p);

    }

}

package org.pat.abilities.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.pat.abilities.Abilities;
import org.pat.abilities.Commands.Ability;
import org.pat.abilities.Objects.AbilityUtil;
import org.pat.abilities.TilsU;
import org.pat.pattyEssentialsV3.ColoredText;
import org.pat.pattyEssentialsV3.Utils;

import java.util.UUID;

public class ClickInv implements Listener {

    @EventHandler
    public void clickInv(InventoryClickEvent e) {

        if (e.getWhoClicked() instanceof Player p) {
            UUID uuid = p.getUniqueId();

            if (e.getCurrentItem() != null) {

                ItemStack item = e.getCurrentItem();
                Material mat = item.getType();
                String itemName = item.hasItemMeta() ? ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase()):"";

                if (e.getClickedInventory() != null) {
                    if (p.getOpenInventory().getTopInventory() != null) {
                        if (org.pat.pattyEssentialsV3.Listeners.ClickInv.guiInvs.contains(p.getOpenInventory().getTitle())) {

                            String title = ChatColor.stripColor(p.getOpenInventory().getTitle().toLowerCase());

                            switch (title) {
                                case "select an ability..":
                                    if (Abilities.abilityBanned.contains(uuid)) {
                                        p.sendMessage(Utils.fMsg("You've already selected an ability or you cannot select right now!"));
                                        break;
                                    }

                                    AbilityUtil ability = AbilityUtil.getAbilityFromName(itemName);
                                    if (ability != null) {
                                        p.closeInventory();
                                        AbilityUtil.selectAbility(p, ability);
                                        Abilities.abilityBanned.add(uuid);
                                        p.sendMessage(TilsU.createMsg(Utils.lightblue + "You've selected: &e&n" + ability.name() + Utils.lightblue + "!"));
                                    }
                                    break;
                            }

                        }
                    }
                }
            }

        }

    }

}

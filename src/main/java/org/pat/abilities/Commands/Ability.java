package org.pat.abilities.Commands;

import jdk.jshell.execution.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pat.abilities.Abilities;
import org.pat.abilities.Objects.AbilityUtil;
import org.pat.pattyEssentialsV3.Listeners.ClickInv;
import org.pat.pattyEssentialsV3.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ability implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmd, @NotNull String @NotNull [] args) {

        if (sender instanceof Player p) {
            UUID uuid = p.getUniqueId();

            openAbilitySelectionGUI(p);

        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmd, @NotNull String @NotNull [] args) {
        List<String> arguments = new ArrayList<>();

        return arguments;
    }

    public static void openAbilitySelectionGUI(Player p) {
        String title = "Select an Ability..";
        ClickInv.guiInvs.add(title);
        Inventory inv = Bukkit.createInventory(p, 18, title);

        for (int i = 0; i <= 8; i++) {
            inv.setItem(i, Utils.i.createItemstack(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        }

        int i = 9;
        for (var ability : AbilityUtil.values()) {
            if (ability != AbilityUtil.test) {
                List<String> lore = new ArrayList<>();
                lore.add(AbilityUtil.MaterialToTangibleLoreFormat(ability.getPrimaryMaterial()) + ": " + ability.getPrimaryName());
                lore.add(ability.getPrimaryDescription());
                lore.add(AbilityUtil.MaterialToTangibleLoreFormat(ability.getSecondaryMaterial()) + ": " + ability.getSecondaryName());
                lore.add(ability.getSecondaryDescription());
                lore.add("Shift: " + ability.getShiftPassiveName());
                lore.add(ability.getShiftPassiveDescription());
                lore.add("Passive: " + ability.getPassiveName());
                lore.add(ability.getPassiveName());
                lore.add(" ");
                lore.add("Click to select!");
                inv.setItem(i, Utils.i.createItemstack(Utils.formatMsg(ability.name()), ability.getGuiItem(), 1, lore));
                i++;
            }
        }

        p.openInventory(inv);
    }

}

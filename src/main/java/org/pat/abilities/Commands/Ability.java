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
import org.pat.abilities.Objects.Affinity;
import org.pat.pattyEssentialsV3.ColoredText;
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
        String title = ColoredText.t(Utils.guiyellow + "Select an Ability..");
        ClickInv.guiInvs.add(title);
        Inventory inv = Bukkit.createInventory(p, 27, title);

        for (int i = inv.getSize()-9; i <= inv.getSize()-1; i++) {
            inv.setItem(i, Utils.i.createItemstack(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        }

        int i = 0;
        for (var ability : AbilityUtil.values()) {
            if (ability != AbilityUtil.test) {
                List<String> lore = new ArrayList<>();
                lore.add("&f" + AbilityUtil.MaterialToTangibleLoreFormat(ability.getPrimaryMaterial()) + "&7 » &e" + ability.getPrimaryName());
                String primDesc = ability.getPrimaryDescription();
                for (String line : primDesc.split("\n")) {
                    lore.add("&7" + line);
                }

                lore.add("&f" + AbilityUtil.MaterialToTangibleLoreFormat(ability.getSecondaryMaterial()) + "&7 » &e" + ability.getSecondaryName());
                String secDesc = ability.getSecondaryDescription();
                for (String line : secDesc.split("\n")) {
                    lore.add("&7" + line);
                }

                lore.add("&fShift&7 » &e" + ability.getShiftPassiveName());
                String shiftPasDesc = ability.getShiftPassiveDescription();
                for (String line : shiftPasDesc.split("\n")) {
                    lore.add("&7" + line);
                }

                lore.add("&fShift&7 » &e" + ability.getPassiveName());
                String pasDesc = ability.getPassiveDescription();
                for (String line : pasDesc.split("\n")) {
                    lore.add("&7" + line);
                }

                lore.add(" ");
                String affinityStr = "";
                for (var v : ability.getAffinities()) {
                    if (affinityStr.equalsIgnoreCase("")) {
                        affinityStr = v.getText();
                    } else {
                        affinityStr = affinityStr + "&7, " + v.getText();
                    }
                }
                lore.add("&fAffinities&7 » " + affinityStr);
                lore.add(" ");
                lore.add(Utils.gold + "&nClick to select!");
                inv.setItem(i, Utils.i.createItemstack(Utils.formatMsg(Utils.red + ability.name()), ability.getGuiItem(), 1, lore));
                i++;
            }
        }

        p.openInventory(inv);
    }

}

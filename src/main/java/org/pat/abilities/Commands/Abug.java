package org.pat.abilities.Commands;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pat.abilities.Abilities;
import org.pat.abilities.Objects.AbilityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Abug implements TabExecutor {

    /**
     * Sophisticated debug command
     */

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmd, @NotNull String @NotNull [] args) {

        if (sender instanceof Player p) {
            UUID uuid = p.getUniqueId();
            if (p.isOp()) {
                if (Abilities.selectedAbility.containsKey(uuid)) {

                }
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmd, @NotNull String @NotNull [] args) {
        List<String> arguments = new ArrayList<>();

        return arguments;
    }

}

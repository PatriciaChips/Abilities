package org.pat.abilities.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
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
                    System.out.println("Secondary cooldown is now: " + AbilityUtil.test.getSecondaryCooldown());
                    AbilityUtil.test.setSecondaryCooldown(10);
                    AbilityUtil.test.saveToConfig();
                    System.out.println("Secondary cooldown is now: " + AbilityUtil.test.getSecondaryCooldown());
                } else {
                    Abilities.selectedAbility.put(uuid, AbilityUtil.test);
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

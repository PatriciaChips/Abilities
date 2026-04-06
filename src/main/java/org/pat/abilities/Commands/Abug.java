package org.pat.abilities.Commands;

import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pat.abilities.Abilities;
import org.pat.abilities.Objects.Abilities.Test_Ability;
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

                    ItemStack item = new ItemStack(Material.DIAMOND);
                    ItemMeta im = item.getItemMeta();
                } else {
                    Abilities.selectedAbility.put(uuid, AbilityUtil.test);

                    AbilityUtil ability = AbilityUtil.test;

                    ItemStack primary = new ItemStack(ability.getPrimaryMaterial());
                    if (ability.hasPrimaryHaveAnimation())
                        primary.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().animation(ItemUseAnimation.BRUSH).build());

                    ItemStack secondary = new ItemStack(ability.getSecondaryMaterial());
                    if (ability.hasSecondaryHaveAnimation())
                    secondary.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().animation(ItemUseAnimation.TRIDENT).build());

                    p.getInventory().addItem(primary);
                    p.getInventory().addItem(secondary);

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

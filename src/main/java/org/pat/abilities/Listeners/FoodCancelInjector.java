package org.pat.abilities.Listeners;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.pat.abilities.Objects.AbilityUtil;
import org.pat.abilities.TilsU;

import java.lang.reflect.Field;

public class FoodCancelInjector {

    public static Field CONNECTION_FIELD;
    public static Field REAL_CHANNEL_FIELD;

    static {
        try {
            CONNECTION_FIELD = ServerCommonPacketListenerImpl.class.getDeclaredField("connection");
            CONNECTION_FIELD.setAccessible(true);

            for (Field f : Connection.class.getDeclaredFields()) {
                if (Channel.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    REAL_CHANNEL_FIELD = f;
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void inject(Player p) {
        try {
            ServerPlayer sp = ((CraftPlayer) p).getHandle();
            Connection mcConn = (Connection) CONNECTION_FIELD.get(sp.connection);

            Channel channel = (Channel) REAL_CHANNEL_FIELD.get(mcConn);

            if (channel == null) {
                return;
            }

            String handlerName = "abilities_dig_" + p.getUniqueId();

            if (channel.pipeline().get(handlerName) != null)
                return;

            channel.pipeline().addBefore(
                    "packet_handler",
                    handlerName,
                    new ChannelDuplexHandler() {

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                            if (msg instanceof ServerboundPlayerActionPacket packet) {
                                ServerboundPlayerActionPacket.Action action = packet.getAction();
                                switch (action) {
                                    case RELEASE_USE_ITEM:
                                        AbilityUtil ability = TilsU.getSelectedAbility(p);
                                        ItemStack item = p.getInventory().getItemInMainHand();
                                        if (ability != null) {
                                            if (AbilityLogic.isEating.containsKey(p.getUniqueId())) {
                                                if (item.hasData(DataComponentTypes.CONSUMABLE)) {
                                                    if (ability.isPrimaryMaterial(item)) {
                                                        ability.cancelPrimaryCharge(p);
                                                    } else if (ability.isSecondaryMaterial(item)) {
                                                        ability.cancelSecondaryCharge(p);
                                                        AbilityLogic.isEating.remove(p.getUniqueId());
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                }
                            }

                            super.channelRead(ctx, msg);
                        }
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
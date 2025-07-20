package bwillows.itemstackplaceholderapi.versioned.v1_17_R1;

import bwillows.itemstackplaceholderapi.versioned.v1_17_R1.events.EntityEquipmentHandler;
import bwillows.itemstackplaceholderapi.versioned.v1_17_R1.events.SetSlotHandler;
import bwillows.itemstackplaceholderapi.versioned.v1_17_R1.events.WindowItemsHandler;
import io.netty.channel.*;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.NetworkManager;

public class NettyPacketHandler {

    private final Player player;

    public NettyPacketHandler(Player player) {
        this.player = player;
    }

    public void inject() {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        NetworkManager networkManager = entityPlayer.playerConnection.networkManager;
        Channel channel = networkManager.channel;

        if (channel.pipeline().get("itemstack-placeholder-handler") != null) return;

        channel.pipeline().addBefore("packet_handler", "itemstack-placeholder-handler", new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof Packet) {
                    // Intercept incoming packet if needed
                }
                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof Packet) {

                    if (msg.getClass().getSimpleName().equals("PacketPlayOutSetSlot")) {
                        SetSlotHandler.handle((Packet<?>) msg, player);
                    }
                    if (msg.getClass().getSimpleName().equals("PacketPlayOutEntityEquipment")) {
                        EntityEquipmentHandler.handle((Packet<?>) msg, player);
                    }

                    if (msg.getClass().getSimpleName().equals("PacketPlayOutWindowItems")) {
                        WindowItemsHandler.handle((Packet<?>) msg, player);
                    }
                }
                super.write(ctx, msg, promise);
            }
        });
    }

    public void uninject() {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        NetworkManager networkManager = entityPlayer.playerConnection.networkManager;
        Channel channel = networkManager.channel;

        if (channel.pipeline().get("itemstack-placeholder-handler") != null) {
            channel.pipeline().remove("itemstack-placeholder-handler");
        }
    }
}
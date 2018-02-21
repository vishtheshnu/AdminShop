package com.vnator.adminshop.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Packet sent from server to client on login to sync contents of shops.
 */
public class PacketSendShopSync implements IMessage {
	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}
}

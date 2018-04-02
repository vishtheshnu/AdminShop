package com.vnator.adminshop.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;

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

	public PacketSendShopSync(){

	}

	public PacketSendShopSync(ArrayList<String[]> buyNames, ArrayList<Float[]> buyPrices,
							  ArrayList<String[]> sellNames, ArrayList<Float[]> sellPrices){
		//Init values

	}

	public static class Handler implements IMessageHandler<PacketSendShopSync, IMessage> {
		@Override
		public IMessage onMessage(PacketSendShopSync message, MessageContext ctx) {
			return null;
		}
	}
}

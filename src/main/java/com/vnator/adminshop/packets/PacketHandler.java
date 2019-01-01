package com.vnator.adminshop.packets;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
	private static int packetId = 0;

	public static SimpleNetworkWrapper INSTANCE = null;

	public PacketHandler(){}

	public static int nextID(){
		return ++packetId;
	}

	public static void registerMessages(String channelName){
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
		registerMessages();
	}

	public static void registerMessages(){
		INSTANCE.registerMessage(PacketSendShopTransaction.Handler.class, PacketSendShopTransaction.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(PacketUpdateMoney.Handler.class, PacketUpdateMoney.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(PacketWithdrawMoney.Handler.class, PacketWithdrawMoney.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(PacketSendShopSync.Handler.class, PacketSendShopSync.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(PacketRequestReloadShop.Handler.class, PacketRequestReloadShop.class, nextID(), Side.SERVER);
	}
}

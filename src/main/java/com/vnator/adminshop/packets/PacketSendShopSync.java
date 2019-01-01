package com.vnator.adminshop.packets;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.blocks.shop.ShopLoader;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.Level;

import java.io.StringReader;

/**
 * Packet sent from server to client on login to sync contents of shops.
 */
public class PacketSendShopSync implements IMessage {

	/*
	* Transfer the entire contents of the shop file as a byte array, read it as a string reader
	* */

	private String myfile;
	private StringReader shopStream;

	@Override
	public void fromBytes(ByteBuf buf) {
		byte [] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		myfile = new String(bytes);
		shopStream = new StringReader(myfile);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBytes(myfile.getBytes());
	}

	public PacketSendShopSync(){}
	public PacketSendShopSync(String file){
		myfile = file;
	}

	public static class Handler implements IMessageHandler<PacketSendShopSync, IMessage> {
		@Override
		public IMessage onMessage(PacketSendShopSync message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		public void handle(PacketSendShopSync message, MessageContext ctx){
			//Load shop with shopstream
			AdminShop.logger.log(Level.INFO, "Received shop file:\n"+message.myfile);
			ShopLoader.getInstance().loadShop(Minecraft.getMinecraft().player, message.shopStream);
		}
	}
}

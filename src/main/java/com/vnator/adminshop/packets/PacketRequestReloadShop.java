package com.vnator.adminshop.packets;

import com.vnator.adminshop.blocks.shop.ShopLoader;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestReloadShop implements IMessage {
	@Override
	public void fromBytes(ByteBuf byteBuf) {

	}

	@Override
	public void toBytes(ByteBuf byteBuf) {

	}

	public PacketRequestReloadShop(){}

	public static class Handler implements IMessageHandler<PacketRequestReloadShop, IMessage>{

		@Override
		public IMessage onMessage(PacketRequestReloadShop message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketRequestReloadShop message, MessageContext ctx){
			ctx.getServerHandler().player.sendMessage(new TextComponentString("Beginning reload..."));
			//Reload shop from file, send requesting player the error log
			ICommandSender sendingPlayer = ctx.getServerHandler().player;
			ShopLoader.getInstance().loadShop(sendingPlayer);
			sendingPlayer.sendMessage(new TextComponentString("Finished loading shop. Errors (if any) are printed above"));

			//Send updated shop to all players
			for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()){
				PacketHandler.INSTANCE.sendTo(new PacketSendShopSync(ShopLoader.getInstance().getFileContents()),
						player);
			}
		}
	}
}

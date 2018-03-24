package com.vnator.adminshop.packets;

import com.vnator.adminshop.capabilities.BalanceAdapter;
import com.vnator.adminshop.capabilities.ledger.LedgerProvider;
import com.vnator.adminshop.capabilities.money.IMoney;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUpdateMoney implements IMessage {

	private float mymoney;

	@Override
	public void fromBytes(ByteBuf buf) {
		mymoney = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(mymoney);
	}

	public PacketUpdateMoney(float f){
		mymoney = f;
	}

	public PacketUpdateMoney(){}

	public static class Handler implements IMessageHandler<PacketUpdateMoney, IMessage>{

		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(PacketUpdateMoney message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		@SideOnly(Side.CLIENT)
		private void handle(PacketUpdateMoney message, MessageContext ctx){
			IMoney imon = Minecraft.getMinecraft().player.getCapability(MoneyProvider.MONEY_CAPABILITY, null);
			imon.setMoney(message.mymoney);
			BalanceAdapter.setMoney(Minecraft.getMinecraft().player, message.mymoney);
		}
	}
}

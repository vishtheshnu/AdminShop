package com.vnator.adminshop.capabilities.ledger;

import com.vnator.adminshop.capabilities.BalanceAdapter;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import com.vnator.adminshop.packets.PacketHandler;
import com.vnator.adminshop.packets.PacketUpdateMoney;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber
public class LedgerEventHandler {

	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
		BalanceAdapter.addPlayer(event.player);
		float money = BalanceAdapter.getMoneyServer((EntityPlayerMP)event.player);
		PacketHandler.INSTANCE.sendTo(new PacketUpdateMoney(money), (EntityPlayerMP) event.player);
	}
}

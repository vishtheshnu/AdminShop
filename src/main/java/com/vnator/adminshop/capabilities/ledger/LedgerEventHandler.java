package com.vnator.adminshop.capabilities.ledger;

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
		String username = event.player.getName();
		ILedger ledger = event.player.world.getCapability(LedgerProvider.LEDGER_CAPABILITY, null);
		//Add player to ledger with default starting money if new
		ledger.addPlayer(username);
		//Update player on their current balance
		event.player.getCapability(MoneyProvider.MONEY_CAPABILITY, null).setMoney(ledger.getMoney(username));
		PacketHandler.INSTANCE.sendTo(new PacketUpdateMoney(ledger.getMoney(username)), (EntityPlayerMP) event.player);
	}
}

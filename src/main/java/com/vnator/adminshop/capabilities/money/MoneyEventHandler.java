package com.vnator.adminshop.capabilities.money;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Subscribes to events that affect money
 */
@Mod.EventBusSubscriber
public class MoneyEventHandler {

	/**
	 * Copy over money to new player on player clone (eg. on death)
	 * @param event Instance of player clone event
	 */
	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event){
		EntityPlayer player = event.getEntityPlayer();
		IMoney money = player.getCapability(MoneyProvider.MONEY_CAPABILITY, null);
		IMoney oldmoney = event.getOriginal().getCapability(MoneyProvider.MONEY_CAPABILITY, null);
		money.setMoney(oldmoney.getMoney());
	}
}

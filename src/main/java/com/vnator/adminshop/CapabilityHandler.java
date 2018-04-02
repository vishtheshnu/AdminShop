package com.vnator.adminshop;

import com.vnator.adminshop.capabilities.ledger.LedgerProvider;
import com.vnator.adminshop.capabilities.money.MoneyProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Adds mod's capabilities to objects when the AttachCapabilitiesEvent is triggered.
 */
@Mod.EventBusSubscriber
public class CapabilityHandler {

	public static final ResourceLocation MONEY_CAPABILITY = new ResourceLocation(AdminShop.MODID, "money");
	public static final ResourceLocation LEDGER_CAPABILITY = new ResourceLocation(AdminShop.MODID, "ledger");

	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event){
		if(event.getObject() instanceof EntityPlayer){
			event.addCapability(MONEY_CAPABILITY, new MoneyProvider());
		}
	}

	@SubscribeEvent
	public static void attachWorldCapability(AttachCapabilitiesEvent<World> event){
		event.addCapability(LEDGER_CAPABILITY, new LedgerProvider());
	}
}

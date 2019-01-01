package com.vnator.adminshop;

import com.vnator.adminshop.blocks.shop.ShopLoader;
import com.vnator.adminshop.packets.PacketHandler;
import com.vnator.adminshop.packets.PacketSendShopSync;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Level;

@Mod.EventBusSubscriber
public class MyForgeEventHandler {

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load evt){
		if(evt.getWorld().provider.getDimension() == 0){ //only for overworld
			if(!evt.getWorld().isRemote) {
				AdminShop.logger.log(Level.INFO, "Loaded world!");
				//ShopLoader.getInstance().loadOnWorldStart();
			}
		}

	}

	@SubscribeEvent
	public static void onPlayerLoad(PlayerEvent.PlayerLoggedInEvent evt){
		AdminShop.logger.log(Level.INFO, "Player logged in!");
		ShopLoader.getInstance().printLog(evt.player);
		//Send shop contents from server
		PacketHandler.INSTANCE.sendTo(new PacketSendShopSync(ShopLoader.getInstance().getFileContents()), (EntityPlayerMP)evt.player);
	}

}

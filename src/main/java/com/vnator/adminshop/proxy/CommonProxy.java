package com.vnator.adminshop.proxy;

import com.vnator.adminshop.capabilities.ledger.ILedger;
import com.vnator.adminshop.capabilities.ledger.LedgerFactory;
import com.vnator.adminshop.capabilities.ledger.LedgerStorage;
import com.vnator.adminshop.capabilities.money.IMoney;
import com.vnator.adminshop.capabilities.money.MoneyFactory;
import com.vnator.adminshop.capabilities.money.MoneyStorage;
import com.vnator.adminshop.packets.PacketHandler;
import net.minecraft.command.CommandHandler;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.item.Item;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class CommonProxy {

    public static Configuration config;

    public void preInit(FMLPreInitializationEvent e){
        //File directory = e.getModConfigurationDirectory();
        //config = new Configuration(new File(directory.getPath(), "adminshop.cfg"));
        //ConfigHandler.readConfig();
		PacketHandler.registerMessages("AdminShop");
        CapabilityManager.INSTANCE.register(IMoney.class, new MoneyStorage(), new MoneyFactory());
        CapabilityManager.INSTANCE.register(ILedger.class, new LedgerStorage(), new LedgerFactory());
    }

    public void init(FMLInitializationEvent e){

    }

    public void postInit(FMLPostInitializationEvent e){

    }

    public String localize(String unlocalized, Object... args){
    	return I18n.translateToLocalFormatted(unlocalized, args);
	}

    /**Implemented in ClientProxy*/
    public void registerItemRenderer(Item item, int meta, String id){}
}

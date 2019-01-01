package com.vnator.adminshop.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class AdminShopCommand extends CommandTreeBase {

	public AdminShopCommand(){
		super.addSubcommand(new CommandReloadShop());
	}

	@Override
	public String getName() {
		return "adminshop";
	}

	@Override
	public String getUsage(ICommandSender iCommandSender) {
		return "commands.adminshop.usage";
	}
}

package com.vnator.adminshop.commands;

import com.vnator.adminshop.blocks.shop.ShopLoader;
import com.vnator.adminshop.packets.PacketHandler;
import com.vnator.adminshop.packets.PacketRequestReloadShop;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandReloadShop extends CommandBase {
	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String getUsage(ICommandSender iCommandSender) {
		return "commands.adminshop.reload.usage";
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 3; //3 = op. 4 = console
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		//ShopLoader.getInstance().loadShop(sender);
		sender.sendMessage(new TextComponentString("Requesting reload..."));
		PacketHandler.INSTANCE.sendToServer(new PacketRequestReloadShop());
	}
}

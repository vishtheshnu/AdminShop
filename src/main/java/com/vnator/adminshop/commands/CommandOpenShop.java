package com.vnator.adminshop.commands;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModGuiHandler;
import com.vnator.adminshop.blocks.shop.ContainerShop;
import com.vnator.adminshop.blocks.shop.GuiShop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;

public class CommandOpenShop extends CommandBase {
	@Override
	public String getName() {
		return "shop";
	}

	@Override
	public String getUsage(ICommandSender iCommandSender) {
		return "commands.adminshop.shop.usage";
	}

	@Override
	public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException {
		if(iCommandSender instanceof EntityPlayer && Minecraft.getMinecraft().world.isRemote) {
			AdminShop.logger.log(Level.INFO, "Opening shop!");
			EntityPlayer player = ((EntityPlayer) iCommandSender);
			//Minecraft.getMinecraft().displayGuiScreen(new GuiShop(new ContainerShop(), player));
		}else{
			AdminShop.logger.log(Level.ERROR, "Command executor isn't a player!");
		}


	}

	@Override
	public int getRequiredPermissionLevel(){
		return 1;
	}

}

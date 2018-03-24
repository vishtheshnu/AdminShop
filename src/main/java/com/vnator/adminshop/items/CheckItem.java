package com.vnator.adminshop.items;

import com.vnator.adminshop.capabilities.BalanceAdapter;
import com.vnator.adminshop.capabilities.ledger.LedgerProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import static net.minecraft.util.ActionResult.newResult;

public class CheckItem extends ItemBase {
	public CheckItem() {super("check");}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
		ItemStack check = player.getHeldItem(hand);
		System.out.println("onItemUse check!");
		if(check.getTagCompound() == null || !check.getTagCompound().hasKey("value")) {
			player.sendMessage(new TextComponentString("Check has no value field, it's blank!"));
			return super.onItemRightClick(worldIn, player, hand);
		}else{
			if(!worldIn.isRemote){
				float money = check.getTagCompound().getFloat("value");
				BalanceAdapter.deposit(player, money);
				player.sendMessage(new TextComponentString("Deposited $"+String.format("%.2f", money)+" into your account."));
			}

			if(!player.capabilities.isCreativeMode)
				check.shrink(1);
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, check);

	}
}

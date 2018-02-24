package com.vnator.adminshop.capabilities.ledger;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.HashMap;

public class LedgerStorage implements Capability.IStorage<ILedger> {
	@Nullable
	@Override
	public NBTBase writeNBT(Capability<ILedger> capability, ILedger instance, EnumFacing side) {
		NBTTagCompound nbt = new NBTTagCompound();
		HashMap<String, Float> map = instance.getMap();
		for(String key : map.keySet()){
			nbt.setFloat(key, map.get(key));
		}

		return nbt;
	}

	@Override
	public void readNBT(Capability<ILedger> capability, ILedger instance, EnumFacing side, NBTBase nbt) {
		instance.loadFromNBT((NBTTagCompound)nbt);
	}
}

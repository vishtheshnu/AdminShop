package com.vnator.adminshop.capabilities.money;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class MoneyStorage implements Capability.IStorage<IMoney> {
	@Nullable
	@Override
	public NBTBase writeNBT(Capability<IMoney> capability, IMoney instance, EnumFacing side) {
		return new NBTTagFloat(instance.getMoney());
	}

	@Override
	public void readNBT(Capability<IMoney> capability, IMoney instance, EnumFacing side, NBTBase nbt) {
		instance.setMoney(((NBTPrimitive) nbt).getFloat());
	}
}

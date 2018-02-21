package com.vnator.adminshop.capabilities.money;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MoneyProvider implements ICapabilitySerializable<NBTBase> {

	@CapabilityInject(IMoney.class)
	public static final Capability<IMoney> MONEY_CAPABILITY = null;
	private IMoney instance = MONEY_CAPABILITY.getDefaultInstance();

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == MONEY_CAPABILITY;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == MONEY_CAPABILITY ? MONEY_CAPABILITY.<T> cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		return MONEY_CAPABILITY.getStorage().writeNBT(MONEY_CAPABILITY, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		MONEY_CAPABILITY.getStorage().readNBT(MONEY_CAPABILITY, this.instance, null, nbt);
	}
}

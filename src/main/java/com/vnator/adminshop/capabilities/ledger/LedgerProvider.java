package com.vnator.adminshop.capabilities.ledger;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LedgerProvider implements ICapabilitySerializable<NBTBase> {

	@CapabilityInject(ILedger.class)
	public static final Capability<ILedger> LEDGER_CAPABILITY = null;
	private ILedger instance = LEDGER_CAPABILITY.getDefaultInstance();

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == LEDGER_CAPABILITY;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == LEDGER_CAPABILITY ? LEDGER_CAPABILITY.<T> cast (this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		return LEDGER_CAPABILITY.getStorage().writeNBT(LEDGER_CAPABILITY, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		LEDGER_CAPABILITY.getStorage().readNBT(LEDGER_CAPABILITY, this.instance, null, nbt);
	}
}

package com.vnator.adminshop.blocks.seller;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.EnergyStorage;

public class SellerBattery extends EnergyStorage {

	private TileEntity entity;

	public SellerBattery(int capacity, TileEntity entity) {
		super(capacity);
		this.entity = entity;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate){
		entity.markDirty();
		return 0;
	}

	public int deleteEnergy(){
		int temp = energy;
		energy = 0;
		entity.markDirty();
		return energy;
	}
}

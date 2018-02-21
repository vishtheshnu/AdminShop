package com.vnator.adminshop.proxy;

import com.vnator.adminshop.AdminShop;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public void preInit(FMLPreInitializationEvent event){
        super.preInit(event);
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id){
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(AdminShop.MODID+":"+id, "inventory"));
    }

    @Override
	public String localize(String unlocalized, Object... args){
    	return I18n.format(unlocalized, args);
	}
}

package com.vnator.adminshop.capabilities.money;

import java.util.concurrent.Callable;

public class MoneyFactory implements Callable<IMoney> {

	@Override
	public IMoney call() throws Exception {
		return new Money();
	}
}

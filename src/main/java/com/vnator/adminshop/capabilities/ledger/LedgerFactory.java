package com.vnator.adminshop.capabilities.ledger;

import java.util.concurrent.Callable;

public class LedgerFactory implements Callable<ILedger> {
	@Override
	public ILedger call() throws Exception {
		return new Ledger();
	}
}

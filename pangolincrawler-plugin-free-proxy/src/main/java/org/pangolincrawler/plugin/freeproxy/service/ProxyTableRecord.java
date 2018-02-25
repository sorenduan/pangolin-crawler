package org.pangolincrawler.plugin.freeproxy.service;

import org.jooq.Table;
import org.jooq.impl.UpdatableRecordImpl;

public class ProxyTableRecord extends UpdatableRecordImpl<ProxyTableRecord> {

	private static final long serialVersionUID = 8922211767274427948L;

	public ProxyTableRecord(Table<ProxyTableRecord> table) {
		super(table);
	}

}

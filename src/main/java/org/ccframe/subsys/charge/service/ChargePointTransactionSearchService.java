package org.ccframe.subsys.charge.service;

import org.springframework.stereotype.Service;

import org.ccframe.commons.base.BaseSearchService;
import org.ccframe.subsys.charge.domain.entity.ChargePointTransaction;
import org.ccframe.subsys.charge.search.ChargePointTransactionSearchRepository;

@Service
public class ChargePointTransactionSearchService extends BaseSearchService<ChargePointTransaction, ChargePointTransactionSearchRepository>{
	
}

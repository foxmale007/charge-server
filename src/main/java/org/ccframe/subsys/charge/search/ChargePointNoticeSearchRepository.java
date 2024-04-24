package org.ccframe.subsys.charge.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.ccframe.subsys.charge.domain.entity.ChargePointNotice;

public interface ChargePointNoticeSearchRepository extends ElasticsearchRepository<ChargePointNotice, Integer> {

}

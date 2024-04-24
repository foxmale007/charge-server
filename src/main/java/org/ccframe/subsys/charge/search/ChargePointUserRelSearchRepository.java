package org.ccframe.subsys.charge.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.ccframe.subsys.charge.domain.entity.ChargePointUserRel;

public interface ChargePointUserRelSearchRepository extends ElasticsearchRepository<ChargePointUserRel, Integer> {

}

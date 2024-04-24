package org.ccframe.subsys.charge.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.ccframe.subsys.charge.domain.entity.ChargePointPlatformInf;

public interface ChargePointPlatformInfSearchRepository extends ElasticsearchRepository<ChargePointPlatformInf, Integer> {

}

package org.ccframe.subsys.charge.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.ccframe.subsys.charge.domain.entity.ChargePointEquipment;

public interface ChargePointEquipmentSearchRepository extends ElasticsearchRepository<ChargePointEquipment, Integer> {

}

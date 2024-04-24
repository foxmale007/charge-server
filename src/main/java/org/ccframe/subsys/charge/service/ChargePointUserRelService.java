package org.ccframe.subsys.charge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ccframe.commons.base.BaseService;
import org.ccframe.subsys.charge.domain.entity.ChargePointUserRel;
import org.ccframe.subsys.charge.repository.ChargePointUserRelRepository;

@Service
public class ChargePointUserRelService extends BaseService<ChargePointUserRel, ChargePointUserRelRepository> {

}

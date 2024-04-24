package org.ccframe.subsys.charge.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.ccframe.commons.base.BaseSearchService;
import org.ccframe.commons.util.UtilDateTime;
import org.ccframe.config.GlobalEx;
import org.ccframe.subsys.charge.domain.entity.ChargePointEquipment;
import org.ccframe.subsys.charge.domain.entity.ChargePointUserRel;
import org.ccframe.subsys.charge.dto.ChargePointEquipmentRow;
import org.ccframe.subsys.charge.search.ChargePointEquipmentSearchRepository;
import org.ccframe.subsys.core.service.UserService;

@Service
public class ChargePointEquipmentSearchService extends BaseSearchService<ChargePointEquipment, ChargePointEquipmentSearchRepository>{

	@Autowired
	private ChargePointUserRelSearchService chargePointUserRelSearchService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private CacheChannel cacheChannel;

    /**
     * 设置设备心跳缓存
     * @param chargePointEquipmentId
     * @return
     */
    public void doHearbeat(Integer chargePointEquipmentId) {
    	cacheChannel.set(GlobalEx.CACHEREGION_HEARTBEAT, GlobalEx.CACHKEY_HEARTBEAT_PERFIX + chargePointEquipmentId, new Date());
    }
    
    /**
     * 获取设备心跳缓存
     * @param chargePointEquipmentId
     * @return
     */
    public Date getHearbeat(Integer chargePointEquipmentId) {
    	return (Date)cacheChannel.get(GlobalEx.CACHEREGION_HEARTBEAT, GlobalEx.CACHKEY_HEARTBEAT_PERFIX + chargePointEquipmentId).getValue();
    }
    
    /**
     * 获取指定设备心跳集合
     * @param chargePointEquipmentIdCollection
     * @return
     */
    public Map<Integer, Date> getHearbeats(Collection<Integer> chargePointEquipmentIdCollection){
    	Map<String, CacheObject> cacheMap = cacheChannel.get(GlobalEx.CACHEREGION_HEARTBEAT, chargePointEquipmentIdCollection.stream().map(item -> GlobalEx.CACHKEY_HEARTBEAT_PERFIX + item).collect(Collectors.toList()));
    	Map<Integer, Date> result = new HashMap<Integer, Date>();
    	for(Entry<String, CacheObject> entry: cacheMap.entrySet()) {
    		result.put(new Integer(entry.getKey().substring(GlobalEx.CACHKEY_HEARTBEAT_PERFIX.length())), (Date)entry.getValue().getValue());
    	}
    	return result;
    }
    
	/**
	 * 列出当前用户绑定设备
	 * @param userId
	 * @return
	 */
	public List<ChargePointEquipmentRow> listChargepointequipment(Integer userId) {
		return chargePointUserRelSearchService.findByKeyAsc(ChargePointUserRel.SYS_USER_ID, userId, ChargePointUserRel.CREATE_TIME).stream().map(item -> {
			ChargePointEquipmentRow chargePointEquipmentRow = new ChargePointEquipmentRow();
			BeanUtils.copyProperties(getJpaService().getById(item.getChargePointEquipmentId()), chargePointEquipmentRow);
			if(item.getSourceUserId() != null) {
				chargePointEquipmentRow.setSourceUserMobile(userService.getById(item.getSourceUserId()).getUserMobile());
			}
			Date date = getHearbeat(item.getChargePointEquipmentId());
			chargePointEquipmentRow.setOnline(date != null && date.after(UtilDateTime.addMinutes(new Date(), -3)));
			return chargePointEquipmentRow;
		}).collect(Collectors.toList());
	}
	
}

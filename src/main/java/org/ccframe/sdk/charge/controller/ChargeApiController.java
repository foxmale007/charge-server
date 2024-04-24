package org.ccframe.sdk.charge.controller;

import java.util.List;

import org.ccframe.commons.auth.RoleAuth;
import org.ccframe.commons.auth.TokenUser;
import org.ccframe.subsys.charge.dto.ChargePointEquipmentRow;
import org.ccframe.subsys.charge.service.ChargePointEquipmentSearchService;
import org.ccframe.subsys.charge.service.ChargePointEquipmentService;
import org.ccframe.subsys.core.domain.code.RoleCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("api/charge")
@Api(value = "充电桩前台API，兼容APP/小程序，需鉴权")
public class ChargeApiController {

	@Autowired
	private ChargePointEquipmentService chargePointEquipmentService;
	
	@Autowired
	private ChargePointEquipmentSearchService chargePointEquipmentSearchService;
	
	@PostMapping("bindUser")
	@RoleAuth({RoleCodeEnum.USER})
	@ApiOperation(value = "绑定设备到当前用户，支持直接绑定和分享扫码绑定", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiImplicitParams({
        @ApiImplicitParam(name = "chargePointSerialNumber", value = "设备编号 [maxlength=38]", required = true, paramType = "form"),
        @ApiImplicitParam(name = "sourceUserMobile", value = "分享源手机 [maxlength=11]", required = false, paramType = "form")
    })
	public void bindUser(String chargePointSerialNumber, String sourceUserMobile, @ApiIgnore TokenUser tokenUser) {
		chargePointEquipmentService.bindUser(tokenUser.getUserId(), chargePointSerialNumber, sourceUserMobile, tokenUser.getPlatformId());
	}

	@GetMapping("listChargepointequipment")
	@RoleAuth({RoleCodeEnum.USER})
	@ApiOperation(value = "列出当前会员绑定的设备列表")
	@ApiResponses({
		@ApiResponse(code = 200, message="设备列表", responseContainer="List", response = ChargePointEquipmentRow.class),
	})
	public List<ChargePointEquipmentRow> listChargepointequipment(@ApiIgnore TokenUser tokenUser) {
		return chargePointEquipmentSearchService.listChargepointequipment(tokenUser.getUserId());
	}

	@PostMapping("remoteStartTransaction")
	@RoleAuth({RoleCodeEnum.USER})
	@ApiOperation(value = "开始充电", notes="该操作仅向设备发送开始充电请求，设备确认充电需要依赖MQTT推送的状态消息", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiImplicitParams({
        @ApiImplicitParam(name = "chargePointSerialNumber", value = "设备编号 [maxlength=38]", required = true, paramType = "form"),
    })
	@ApiResponses({
		@ApiResponse(code = 500, message="业务异常：设备不存在 | 设备故障 | 启动充电失败")
	})
	public void remoteStartTransaction(String chargePointSerialNumber, @ApiIgnore TokenUser tokenUser) {
		chargePointEquipmentService.remoteStartTransaction(chargePointSerialNumber, tokenUser.getUserId(), tokenUser.getPlatformId());
	}

	@PostMapping("remoteStopTransaction")
	@RoleAuth({RoleCodeEnum.USER})
	@ApiOperation(value = "停止充电", notes="该操作仅向设备发送停止充电请求，设备确认需要依赖MQTT推送的状态消息", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiImplicitParams({
        @ApiImplicitParam(name = "chargePointSerialNumber", value = "设备编号 [maxlength=38]", required = true, paramType = "form"),
    })
	@ApiResponses({
		@ApiResponse(code = 500, message="业务异常：设备不存在 | 设备故障 | 停止充电失败")
	})
	public void remoteStopTransaction(String chargePointSerialNumber, @ApiIgnore TokenUser tokenUser) {
		chargePointEquipmentService.remoteStopTransaction(chargePointSerialNumber, tokenUser.getUserId(), tokenUser.getPlatformId());
	}
}

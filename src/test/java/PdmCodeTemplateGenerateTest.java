import java.io.IOException;

import org.junit.Test;

/**
 * 按要求根据PDM生成全部模块(包括所有Enum).
 * 
 * Entity、Code、repository、search、service、searchService、controller
 * 
 * 生成完毕后，只需要根据Enum自动填写字段即完成数据库基本配置.
 * 
 * @author JIM
 *
 */
public class PdmCodeTemplateGenerateTest {

	@Test
	public void generateTest() throws IOException {
		new PdmCodeTemplateGenerater()
		.setFile("D:\\eclipse\\projects\\charge-server\\doc\\2-Development\\2-High Level Design\\charge.pdm")
		.addTableGroup("charge", "PRD_CHARGE_POINT_EQUIPMENT", "PRD_CHARGE_POINT_USER_REL", "PRD_CHARGE_POINT_PLATFORM_INF", "PRD_CHARGE_POINT_TRANSACTION", "PRD_CHARGE_POINT_NOTICE")
		.doExport();
	}

}

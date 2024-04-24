import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import org.ccframe.commons.util.DESUtil;
import org.ccframe.commons.util.UtilDateTime;
import org.junit.Test;

public class DESUtilTest {

	private static final String PASS = "锄禾日当午汗滴禾下土谁知盘中餐粒粒皆辛苦"; 
	
	@Test
	public void testEncrypt() throws UnsupportedEncodingException {
		String data = UtilDateTime.convertTimeToCompactString(new Date());
		System.out.println("原始串 " + data);
		String encrypted = DESUtil.encrypt(PASS, data);
		System.out.println("编码串 " + encrypted);
		System.out.println("URL编码串 " + URLEncoder.encode(encrypted, "UTF-8"));
		System.out.println("解码串 " + DESUtil.decrypt(PASS, encrypted));
	}

	@Test
	public void generateEncrypt() {
		String data = DESUtil.encrypt(PASS, "20201119121145");
		try {
			System.out.println("编码串 " + URLEncoder.encode(data, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDecript() {
//		String data = URLDecoder.decode("rDwGNi0Bi1fBn3JHxnDoxg%3D%3D");
		String data = URLDecoder.decode("PaDDKpGwxXBuTD8BfJlyMw%3D%3D");
		System.out.println(data);
		data = DESUtil.decrypt(PASS, data);
		System.out.println(data);
	}
	
}

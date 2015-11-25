package multiemail;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestCyhper {

	@Test
	public void test(){
		String original = "This is an original";
		String encrypt = Cypher.encrypt(original);
		String decrypt = Cypher.decrypt(encrypt);
		assertEquals(original, decrypt);
	}
}

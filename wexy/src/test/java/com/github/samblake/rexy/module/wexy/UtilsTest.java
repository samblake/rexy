package com.github.samblake.rexy.module.wexy;

import com.google.common.truth.Truth;
import org.junit.Test;

public class UtilsTest {
	
	@Test
	public void testUntil() {
		Truth.assertThat(Utils.until("abc", '?')).isEqualTo("abc");
		Truth.assertThat(Utils.until("abc?123", '?')).isEqualTo("abc");
		Truth.assertThat(Utils.until("", '?')).isEmpty();
		Truth.assertThat(Utils.until("?", '?')).isEmpty();
	}
	
}
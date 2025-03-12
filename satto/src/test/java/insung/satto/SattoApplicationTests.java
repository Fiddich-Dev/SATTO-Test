package insung.satto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class SattoApplicationTests {

	@BeforeEach
	void beforeEach() {
		System.out.println("asd");
	}

	@Test
	void contextLoads() {
		System.out.println("asd");
		Assertions.assertThat(1).isEqualTo(1);
	}

}

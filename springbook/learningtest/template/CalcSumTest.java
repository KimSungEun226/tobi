package springbook.learningtest.template;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat; 

public class CalcSumTest {
	
	Calculator calculator;
	String numFilepath;
	
	@Before
	public void setUp() {
		this.calculator = new Calculator();
		this.numFilepath = getClass().getResource("numbers.txt").getPath();
	}
	
	@Test
	public void sumOfNumbers() throws IOException {
		int sum = calculator.calcSum(numFilepath);
		assertThat(sum, is(15));
	}
	
	@Test
	public void multiplyOfNumbers() throws IOException {
		int sum = calculator.calcMultiply(numFilepath);
		assertThat(sum, is(120));
	}
	
	@Test
	public void concatenate() throws IOException {
		String sum = calculator.concatenate(numFilepath);
		assertThat(sum, is("12345"));
	}

}

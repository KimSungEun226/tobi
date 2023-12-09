package springbook.learningtest.spring.pointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class PointCutTest {
		
	@Test 
	public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
		
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(public int " +
				"springbook.learningtest.spring.pointcut.Target.minus(int,int)" +
				"throws java.lang.RuntimeException)"); // Target 클래스 minus()메소드 시그니처
		
		// Target.minus()
		// 클래스 필터와 메소드 매처를 가져와 각각 비교한다.
		assertThat(pointcut.getClassFilter().matches(Target.class) &&
				pointcut.getMethodMatcher().matches(
						Target.class.getMethod("minus", int.class, int.class), null), is(true));	
		
		// Target.plus()
		// 클래스 필터와 메소드 매처를 가져와 각각 비교한다.
		assertThat(pointcut.getClassFilter().matches(Target.class) &&
				pointcut.getMethodMatcher().matches(
						Target.class.getMethod("plus", int.class, int.class), null), is(false));
		
		// Target.plus()
		// 클래스 필터와 메소드 매처를 가져와 각각 비교한다.
		assertThat(pointcut.getClassFilter().matches(Target.class) &&
				pointcut.getMethodMatcher().matches(
						Target.class.getMethod("method"), null), is(false));
	}
	
	public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, 
			String methodName, Class<?>... args) throws Exception {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(expression);
		
		assertThat(pointcut.getClassFilter().matches(clazz) //포인트컷의 클래스 필터와 메소드 매처 두 가지를 동시에 만족하는지 확인한다.
				&& pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null), is(expected));
	}
	
	public void targetClassPointcutMatches(String expression, boolean... expected) throws Exception {
		pointcutMatches(expression, expected[0], Target.class, "hello");
		pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
		pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
		pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
		pointcutMatches(expression, expected[4], Target.class, "method");
		pointcutMatches(expression, expected[5], Bean.class, "method");
	}
	
	@Test
	public void pointcut() throws Exception {
		//모든 메소드를 다 허용하는 표현식
		targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
	}
}

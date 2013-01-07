package org.mybatis.spring.mapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mybatis.spring.mapper.MapperBeanRegistrar;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Import;

/**
 * Use this annotation to register MyBatis mapper interfaces when using Java
 * Config. It performs when same work as {@link MapperScannerConfigurer} via
 * {@link MapperBeanRegistrar}.
 * 
 * @author lanyonm
 * @since 1.1.2
 * @see MapperBeanRegistrar
 * @see MapperScannerConfigurer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MapperBeanRegistrar.class)
public @interface EnableMyBatisMapperScanner {

	/**
	 * Alias for the {@link #basePackages()} attribute.
	 * Allows for more concise annotation declarations e.g.:
	 * {@code @EnableMyBatisMapperScanner("org.my.pkg")} instead of
	 * {@code @EnableMyBatisMapperScanner(basePackages={"org.my.pkg"})}.
	 */
	String[] value() default {};

	/**
	 * Base packages to scan for MyBatis interfaces. Note that only interfaces
	 * with at least one method will be registered; concrete classes will be
	 * ignored.
	 */
	String[] basePackages() default {};
}

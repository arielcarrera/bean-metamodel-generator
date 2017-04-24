package com.luckyend.generators.metamodel.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * IgnoreMetamodel annotation used to ignore a field from Metamodel generation
 *
 * @author Ariel Carrera &lt;carreraariel@gmail.com&gt;
 * @version 1.0
 * 
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface IgnoreMetamodel {

}

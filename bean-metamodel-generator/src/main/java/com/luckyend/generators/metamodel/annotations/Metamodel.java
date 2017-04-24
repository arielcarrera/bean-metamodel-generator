package com.luckyend.generators.metamodel.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Metamodel is an annotation used to capture attribute names of a Java Bean class in a static way.
 *
 * @author Ariel Carrera &lt;carreraariel@gmail.com&gt;
 * @version 1.0
 * 
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Metamodel {

}

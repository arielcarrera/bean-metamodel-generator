package com.luckyend.generators.metamodel.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MetamodelField annotation used to define representation of a field into the Metamodel
 *
 * @author Ariel Carrera &lt;carreraariel@gmail.com&gt;
 * @version 1.0
 * 
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface MetamodelField {

	/**
	 * Overwrite the name of the field on the metamodel
	 * 
	 * @return Output name of the field
	 */
	String value() default "";
}

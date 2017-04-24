package com.luckyend.generators.metamodel.model;

import java.util.Set;

import lombok.Data;

/**
 * 
 * Model for a annotated class type
 * @author Ariel Carrera &lt;carreraariel@gmail.com&gt;
 * @version 1.0
 * 
 */
@Data
public class ClassModel {

	/**
	 * Fields of annotated class
	 */
    Set<FieldModel> fields;
    
    /** 
     * Package name
     */
    String packageName;

    /** 
     * Class name
     */
    String className;

    /** 
     * fully qualified class name
     */
    String qualifiedClassName;

    /**
     * Java bean class name
     */
    String beanClassName;

    /** 
     * Java bean fully qualified class name
     */
    String beanQualifiedClassName;

}

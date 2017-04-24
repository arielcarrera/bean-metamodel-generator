package com.luckyend.generators.metamodel.model;

import lombok.Data;

/**
 * 
 * Model for a property of an annotated class
 * @author Ariel Carrera &lt;carreraariel@gmail.com&gt;
 * @version 1.0
 * 
 */
@Data
public class FieldModel {
    
	/**
	 * Real/Original field name 
	 */
    String realName;
    
    /**
     * Output field name. 
     */
    String name;
    
    /**
     * Define if a field must be ignored
     */
    boolean ignore = false;

}

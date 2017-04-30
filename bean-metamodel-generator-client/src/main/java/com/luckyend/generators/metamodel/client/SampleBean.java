package com.luckyend.generators.metamodel.client;

import com.luckyend.generators.metamodel.annotations.Metamodel;
import com.luckyend.generators.metamodel.annotations.MetamodelField;
import com.luckyend.generators.metamodel.annotations.MetamodelIgnore;

/**
 * Sample Bean annotated
 * @author Ariel Carrera &lt;carreraariel@gmail.com&gt;
 * @version 1.0
 *
 */
@Metamodel
public class SampleBean {

    /** Status enumeration */
    private enum StatusEnum { OK, DELETED };

    /** Identifier of Sample Bean */
    @MetamodelIgnore
    private String id;

    /** sample primitive attribute */
    @MetamodelField("attribute2")
    private int attribute;

    /** sample Enum attribute*/
    private StatusEnum status;

    
    public SampleBean() {
		super();
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the attribute
	 */
	public int getAttribute() {
		return attribute;
	}


	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}


	/**
	 * @return the status
	 */
	public StatusEnum getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(StatusEnum status) {
		this.status = status;
	}


	/**
     * sample annotated method with default data
     */
    public void activate() {
        this.setStatus(StatusEnum.OK);
    }

    
    /**
     * Sample annotated method with parameter
     *
     * @param cause the cause why the article is invalidated
     */
    public void delete(String cause) {
        this.setStatus(StatusEnum.DELETED);
    }

}

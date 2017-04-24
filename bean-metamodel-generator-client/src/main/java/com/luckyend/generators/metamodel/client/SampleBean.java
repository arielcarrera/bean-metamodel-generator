package com.luckyend.generators.metamodel.client;

import com.luckyend.generators.metamodel.annotations.IgnoreMetamodel;
import com.luckyend.generators.metamodel.annotations.Metamodel;
import com.luckyend.generators.metamodel.annotations.MetamodelField;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sample Bean annotated
 * @author Ariel Carrera &lt;carreraariel@gmail.com&gt;
 * @version 1.0
 *
 */
@NoArgsConstructor
@Metamodel
public class SampleBean {

    /** Status enumeration */
    private enum StatusEnum { OK, DELETED };

    /** Identifier of Sample Bean */
    @IgnoreMetamodel
    @Getter @Setter
    private String id;

    /** sample primitive attribute */
    @MetamodelField("attribute2")
    @Getter @Setter
    private int attribute;

    /** sample Enum attribute*/
    @Getter @Setter
    private StatusEnum status;

    /**
     * sample annotated method with default data
     */
    public void activate() {
        setStatus(StatusEnum.OK);
    }

    
    /**
     * Sample annotated method with parameter
     *
     * @param cause the cause why the article is invalidated
     */
    public void delete(String cause) {
        setStatus(StatusEnum.DELETED);
    }

}

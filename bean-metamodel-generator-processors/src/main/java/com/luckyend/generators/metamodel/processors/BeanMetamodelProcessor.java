package com.luckyend.generators.metamodel.processors;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.luckyend.generators.metamodel.annotations.IgnoreMetamodel;
import com.luckyend.generators.metamodel.annotations.Metamodel;
import com.luckyend.generators.metamodel.annotations.MetamodelField;
import com.luckyend.generators.metamodel.model.ClassModel;
import com.luckyend.generators.metamodel.model.FieldModel;

import lombok.NoArgsConstructor;


/**
 * Annotation processor for Metamodel annotation.
 * It generates a metamodel class using Apache Velocity template that represents the java bean annotated
 *
 * @author Ariel Carrera
 * @version 1.0
 */
@NoArgsConstructor
@SupportedAnnotationTypes({
	"com.luckyend.generators.metamodel.annotations.Metamodel"
//	,"com.luckyend.generators.metamodel.annotations.IgnoreMetamodel",
//	"com.luckyend.generators.metamodel.annotations.MetamodelField"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BeanMetamodelProcessor extends AbstractProcessor {
	
    /** String used to append to the class name when a class is created */
    private static final String BEAN_METAMODEL_SUFFIX = "_BeanModel";
    
    /** String used to get template file */
    private static final String TEMPLATE_PATH = "beanMetamodel.vm";
    
    /** String used to get properties file */
    private static final String PROPERTIES_PATH = "velocity.properties";
    
    
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Map<String, FactoryGroupedClasses> factoryClasses = new LinkedHashMap<String, FactoryGroupedClasses>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
      super.init(processingEnv);
      typeUtils = processingEnv.getTypeUtils();
      elementUtils = processingEnv.getElementUtils();
      filer = processingEnv.getFiler();
      messager = processingEnv.getMessager();
    }
    
    
    /**
     * Reads the Metamodel information and writes a full featured
     * Metamodel type with the help of an Apache Velocity template.
     *
     * @param annotations set of annotations found
     * @param roundEnv the environment for this processor round
     *
     * @return whether a new processor round would be needed
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.isEmpty()) {
            return true;
        }

        try {
            ClassModel model = null;
            Set<FieldModel> fields = new HashSet<>();

            model = prepareModel(roundEnv, model, fields);

            write(model, fields);
        } catch (ResourceNotFoundException | ParseErrorException | IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,e.getLocalizedMessage());
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,e.getLocalizedMessage());
        }

        return true;
    }

	private ClassModel prepareModel(RoundEnvironment roundEnv, ClassModel model,Set<FieldModel> fields) {
		for (Element e : roundEnv.getElementsAnnotatedWith(Metamodel.class)) {

		    if (e.getKind() == ElementKind.CLASS) {

		        model = new ClassModel();

		        TypeElement classElement = (TypeElement) e;
		        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
		        Metamodel annotation = classElement.getAnnotation(Metamodel.class);
		        
		        //if it is not the Metamodel annotation on the Class... it is skipped
		        if(annotation == null) continue;

		        model.setPackageName(packageElement.getQualifiedName().toString());//eg com.luckyend.generators.metamodel.client
		        model.setClassName(classElement.getSimpleName().toString());//eg Article
		        model.setQualifiedClassName(classElement.getQualifiedName().toString()); //eg com.luckyend.generators.metamodel.client.SampleBean
		        model.setBeanClassName(model.getClassName() + BEAN_METAMODEL_SUFFIX);//eg SampleBean_BeanModel
		        model.setBeanQualifiedClassName(model.getQualifiedClassName() + BEAN_METAMODEL_SUFFIX);//eg com.luckyend.generators.metamodel.client.SampleBean_BeanModel

		        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Annotated class: " + model.getQualifiedClassName(), e);

		    } 
		}
		return model;
	}

	private void write(ClassModel model, Set<FieldModel> fields) throws IOException {
		if (model != null) {

		    Properties props = new Properties();
		    URL url = this.getClass().getClassLoader().getResource(PROPERTIES_PATH);
		    props.load(url.openStream());

		    VelocityEngine ve = new VelocityEngine(props);
		    ve.init();

		    VelocityContext vc = new VelocityContext();

		    vc.put("model", model);
		    vc.put("fields", fields);

		    Template vt = ve.getTemplate(TEMPLATE_PATH);

		    JavaFileObject jfo = processingEnv.getFiler().createSourceFile(model.getBeanQualifiedClassName());

		    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"creating source file: " + jfo.toUri());

		    Writer writer = jfo.openWriter();

		    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"applying velocity template: " + vt.getName());

		    vt.merge(vc, writer);

		    writer.close();
		}
	}
	
	private boolean processFieldAnnotations(){
		
		else if (e.getKind() == ElementKind.FIELD) {

	        VariableElement varElement = (VariableElement) e;

	        FieldModel field = new FieldModel();
	        MetamodelField annotation = varElement.getAnnotation(MetamodelField.class);
	        IgnoreMetamodel ignoreAnnotation = varElement.getAnnotation(IgnoreMetamodel.class);
	        //if it is not the MetamodelField / IgnoreMetamodel annotations on the Class... it is skipped
	        if(annotation == null && ignoreAnnotation == null) continue;
	        
	        if(annotation != null) {
	        	field.setName(varElement.getSimpleName().toString());//eg id
	        	field.setName(annotation.value());//eg newName
	        	fields.add(field);
	        }
	        if (ignoreAnnotation != null){
	        	field.setIgnore(true);
	        }

	        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"annotated field: " + field.getName() + " // real field: " + field.getRealName() + " // ignore: " + field.isIgnore(), e);

	    } 
		return true;
	}
}

package com.luckyend.generators.metamodel.processors;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.google.auto.service.AutoService;
import com.luckyend.generators.metamodel.annotations.Metamodel;
import com.luckyend.generators.metamodel.annotations.MetamodelField;
import com.luckyend.generators.metamodel.annotations.MetamodelIgnore;
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
@AutoService(Processor.class)
@NoArgsConstructor
@SupportedAnnotationTypes({
	"com.luckyend.generators.metamodel.annotations.Metamodel"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BeanMetamodelProcessor extends AbstractProcessor {
	
    /** String used to append to the class name when a class is created */
    private static final String BEAN_METAMODEL_SUFFIX = "_Metamodel";
    
    /** String used to get template file */
    private static final String TEMPLATE_PATH = "beanMetamodel.vm";
    
    /** String used to get properties file */
    private static final String PROPERTIES_PATH = "velocity.properties";
    
    private static final boolean CLAIMED_EXCLUSIVELY = false;
    private static final boolean NOT_CLAIMED_EXCLUSIVELY = true;	
    
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Set<ClassModel> models = new HashSet<>();
    
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
    	
    	if (!roundEnv.processingOver() && !annotations.isEmpty()) {        	
	        try {
	            prepareModel(annotations, roundEnv);
	            write();
	        } catch (ResourceNotFoundException | ParseErrorException | IOException e) {
	        	messager.printMessage(Diagnostic.Kind.ERROR,e.getLocalizedMessage());
	        } catch (Exception e) {
	        	messager.printMessage(Diagnostic.Kind.ERROR,e.getLocalizedMessage());
	        }
        }
        return CLAIMED_EXCLUSIVELY;
    }

    /**
     * Prepare metamodel data
     * @param roundEnv
     */
	private void prepareModel(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element e : roundEnv.getElementsAnnotatedWith(Metamodel.class)) {
			if (e.getKind() == ElementKind.CLASS) {
		    	ClassModel model = new ClassModel();
		        TypeElement classElement = (TypeElement) e;
		        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
		    	model.setPackageName(packageElement.getQualifiedName().toString());//eg com.luckyend.generators.metamodel.client
		    	messager.printMessage(Diagnostic.Kind.NOTE, "Annotated class: " + model.getQualifiedClassName(), e);
		        
		        Metamodel annotation = classElement.getAnnotation(Metamodel.class);
		        //if it is not the Metamodel annotation on the Class... it is skipped
		        if(annotation == null) {
		        	messager.printMessage(Diagnostic.Kind.ERROR, "No Metamodel annotation for class: " + model.getQualifiedClassName(), e);
		        	continue;
		        }

		        model.setClassName(classElement.getSimpleName().toString());//eg Article
		        model.setQualifiedClassName(classElement.getQualifiedName().toString()); //eg com.luckyend.generators.metamodel.client.SampleBean
		        model.setBeanClassName(model.getClassName() + BEAN_METAMODEL_SUFFIX);//eg SampleBean_BeanModel
		        model.setBeanQualifiedClassName(model.getQualifiedClassName() + BEAN_METAMODEL_SUFFIX);//eg com.luckyend.generators.metamodel.client.SampleBean_BeanModel
		        //process fields for model class
		        model.setFields(processModelFields(classElement));
		        
		        models.add(model);
		    }
		}
	}

	/**
	 * Process a class element searching for fields and annotations
	 * @param classElement
	 * @return set of FieldModel
	 */
	private Set<FieldModel> processModelFields(TypeElement classElement) {
		Set<FieldModel> fieldsModel = new HashSet<>();
		List<? extends Element> members = elementUtils.getAllMembers(classElement);
		for (Element element : members) {
			if (element.getKind().equals(ElementKind.FIELD)) {
				FieldModel fm = new FieldModel();
				fm.setName(element.getSimpleName().toString());
				fm.setRealName(fm.getName());
				
				MetamodelField metamodelField = element.getAnnotation(MetamodelField.class);
				MetamodelIgnore ignoreAnnotation = element.getAnnotation(MetamodelIgnore.class);
				if (metamodelField != null){
					fm.setRealName(metamodelField.value());
				}
				if (ignoreAnnotation != null || (metamodelField != null && metamodelField.ignore())){
					fm.setIgnore(true);
					messager.printMessage(Diagnostic.Kind.NOTE,"ignored field: " + element.getSimpleName().toString());
				}
				fieldsModel.add(fm);
			}
		}
		return fieldsModel;
	}
	

	/**
	 * Write Metamodel Class
	 * @throws IOException
	 */
	private void write() throws IOException {
		if (!models.isEmpty()) {

		    Properties props = new Properties();
		    URL url = this.getClass().getClassLoader().getResource(PROPERTIES_PATH);
		    props.load(url.openStream());

		    VelocityEngine ve = new VelocityEngine(props);
		    ve.init();
		    Template vt = ve.getTemplate(TEMPLATE_PATH);
		    
		    for (ClassModel classModel : models) {
		    	//filling template context
		    	VelocityContext vc = new VelocityContext();
			    vc.put("model", classModel);
			    vc.put("fields", classModel.getFields());
			    //creating source file with Filer
			    JavaFileObject jfo = filer.createSourceFile(classModel.getBeanQualifiedClassName());
			    messager.printMessage(Diagnostic.Kind.NOTE,"Creating source file: " + jfo.toUri());
			    try(Writer writer = jfo.openWriter()) { //autoclose
				    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Applying template: " + vt.getName());
				    vt.merge(vc, writer);
			    }
			}
		    
		}
	}
}

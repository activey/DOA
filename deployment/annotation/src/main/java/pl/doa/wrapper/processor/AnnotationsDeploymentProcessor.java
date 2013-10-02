package pl.doa.wrapper.processor;

import org.scannotation.AnnotationDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.artifact.IArtifact;
import pl.doa.artifact.deploy.AbstractDeploymentProcessor;
import pl.doa.artifact.deploy.DeploymentContext;
import pl.doa.artifact.deploy.IDeploymentProcessor;
import pl.doa.container.IEntitiesContainer;
import pl.doa.entity.IEntity;
import pl.doa.templates.TemplateContext;
import pl.doa.wrapper.annotation.Aligner;
import pl.doa.wrapper.annotation.DocumentDefinition;
import pl.doa.wrapper.annotation.ServiceDefinition;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AnnotationsDeploymentProcessor extends AbstractDeploymentProcessor {

    private final static Logger log = LoggerFactory.getLogger(AnnotationsDeploymentProcessor.class);

    @Override
    public void process(IEntitiesContainer container) throws Exception {

        // scanning jar file
        File jarFile = deploymentContext.getArtifactJar();
        AnnotationDB db = new AnnotationDB();
        db.scanArchives(jarFile.toURI().toURL());

        // iterating annotations
        // TODO implement rest of annotations


        List<AwaitingProcessing> awaitingProcessing = new ArrayList<AwaitingProcessing>();
        int pass = 1;
        log.debug(String.format("Running pass nr: %d", pass));

        Map<String, Set<String>> index = db.getAnnotationIndex();
        iterateAnnotated(DocumentDefinition.class, new DocumentDefinitionIterator(deploymentContext, container), index, deploymentContext, awaitingProcessing);
        iterateAnnotated(ServiceDefinition.class, new ServiceDefinitionIterator(deploymentContext, container), index, deploymentContext, awaitingProcessing);
        iterateAnnotated(Aligner.class, new AlignerIterator(deploymentContext, container), index, deploymentContext, awaitingProcessing);


        iterateAwaiting(awaitingProcessing, 2, deploymentContext);

    }

    private void iterateAwaiting(List<AwaitingProcessing> awaitingProcessing, int pass, DeploymentContext deploymentContext) {
        log.debug(String.format("Running pass nr: %d", pass));
        for (int i = awaitingProcessing.size(); i > 0; i--) {
            AwaitingProcessing awaiting = awaitingProcessing.get(i - 1);
            String className = awaiting.getClassName();
            Class<Annotation> annotationClass = awaiting.getAnnotation();
            AbstractAnnotatedIterator<Annotation, IEntity> iterator = awaiting.getIterator();

            IIteratorResult<? extends IEntity> iteratorResult = iterator
                    .iterate(className, annotationClass, deploymentContext);

            try {
                IArtifact artifact = deploymentContext.getArtifact();
                artifact.registerEntity(iteratorResult.getResult());

                awaitingProcessing.remove(i - 1);
            } catch (WaitingForDependencyException e) {
                awaitingProcessing.add(awaiting);

                log.debug(String
                        .format("Can not unwrapDocumentDefinition required type: [%s] for: [%s], moving to queue ...", e
                                .getAwaitingWrapping().getName(), className));
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        }

        if (awaitingProcessing.size() > 0) {
            iterateAwaiting(awaitingProcessing, pass + 1, deploymentContext);
        }
    }

    private <T extends Annotation> void iterateAnnotated(Class<T> annotationClass, AbstractAnnotatedIterator<T, ? extends IEntity> iterator, Map<String, Set<String>> index, DeploymentContext deploymentContext, List<AwaitingProcessing> awaitingProcessing) {
        Set<String> classes = index.get(annotationClass.getName());
        if (classes == null || classes.size() == 0) {
            return;
        }
        for (String className : classes) {
            IIteratorResult<? extends IEntity> iteratorResult = iterator
                    .iterate(className, annotationClass, deploymentContext);
            AwaitingProcessing awaiting = new AwaitingProcessing();
            awaiting.setAnnotation((Class<Annotation>) annotationClass);
            awaiting.setClassName(className);
            awaiting.setIterator((AbstractAnnotatedIterator<Annotation, IEntity>) iterator);

            try {
                IArtifact artifact = deploymentContext.getArtifact();
                artifact.registerEntity(iteratorResult.getResult());

                awaitingProcessing.remove(awaiting);
            } catch (WaitingForDependencyException e) {
                awaitingProcessing.add(awaiting);

                log.debug(String
                        .format("Can not unwrapDocumentDefinition required type: [%s] for: [%s], moving to queue ...", e
                                .getAwaitingWrapping().getName(), className));
            } catch (GeneralDOAException e) {
                log.error("", e);
            }
        }
    }

    private class AwaitingProcessing {

        private String className;
        private Class<Annotation> annotation;
        private AbstractAnnotatedIterator<Annotation, IEntity> iterator;

        private String getClassName() {
            return className;
        }

        private void setClassName(String className) {
            this.className = className;
        }

        private Class<Annotation> getAnnotation() {
            return annotation;
        }

        private void setAnnotation(Class<Annotation> annotation) {
            this.annotation = annotation;
        }

        public boolean equals(AwaitingProcessing other) {
            return (other.getClassName().equals(this.className) && other.getAnnotation().getName()
                    .equals(this.annotation.getName()));
        }

        private AbstractAnnotatedIterator<Annotation, IEntity> getIterator() {
            return iterator;
        }

        private void setIterator(AbstractAnnotatedIterator<Annotation, IEntity> iterator) {
            this.iterator = iterator;
        }
    }
}

/**
 *
 */
package com.olender.backend.services;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.service.AsynchronousServiceDefinitionLogic;

import java.util.Iterator;

/**
 * @author activey
 */
public abstract class BaseServiceDefinitionLogic extends
        AsynchronousServiceDefinitionLogic {

    protected final IEntitiesContainer getContainer(
            IEntitiesContainer baseContainer, String containerLocation,
            boolean createIfNotExist) throws GeneralDOAException {
        IEntitiesContainer foundContainer =
                (IEntitiesContainer) baseContainer
                        .lookupEntityByLocation(containerLocation);
        if (foundContainer == null) {
            if (createIfNotExist) {
                Iterator<String> iterator =
                        new EntityLocationIterator(containerLocation);
                return createContainer(baseContainer, iterator);
            }
            return null;
        }
        return foundContainer;
    }

    protected final IEntitiesContainer getApplicationContainer(
            String containerLocation, boolean createIfNotExist)
            throws GeneralDOAException {
        return getContainer(getApplicationContainer(), containerLocation,
                createIfNotExist);
    }

    protected final IEntitiesContainer getApplicationContainer(
            String containerLocation) throws GeneralDOAException {
        return getApplicationContainer(containerLocation, false);
    }

    private IEntitiesContainer createContainer(IEntitiesContainer root,
                                               Iterator<String> nestedPath) throws GeneralDOAException {
        if (nestedPath.hasNext()) {
            String part = nestedPath.next();
            IEntitiesContainer current =
                    root.getEntityByName(part, IEntitiesContainer.class);
            if (current == null) {
                current = doa.createContainer(part, root);
            }
            return createContainer(current, nestedPath);
        }
        return root;
    }

    protected IDocument createOutputDocument(String possibleOutputName)
            throws GeneralDOAException {
        IDocumentDefinition outputDefinition =
                getPossibleOutputDefinition(possibleOutputName);
        if (outputDefinition == null) {
            return null;
        }
        return outputDefinition.createDocumentInstance();
    }

    protected void sendError(String errorMessage) {
        IDocument errorDocument = createExceptionDocument(errorMessage);
        setOutput(errorDocument);
    }

    private IEntitiesContainer getApplicationContainer() {
        return (IEntitiesContainer) doa
                .lookupEntityByLocation("/applications/olender-frontpage");
    }
}

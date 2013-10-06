/**
 *
 */
package com.olender.backend.services.backup;

import com.olender.backend.services.BaseServiceDefinitionLogic;
import com.olender.backend.utils.JsonConverter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.impl.EntityLocationIterator;
import pl.doa.resource.IStaticResource;
import pl.doa.service.annotation.EntityRef;
import pl.doa.utils.ContentTypeUtils;
import pl.doa.utils.PathIterator;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author activey
 */
public class RestoreService extends BaseServiceDefinitionLogic {

    private final static Logger log = LoggerFactory
            .getLogger(RestoreService.class);
    @EntityRef(location = "/applications/olender-frontpage/images/application")
    private IEntitiesContainer imagesContainer;
    @EntityRef(location = "/applications/olender-frontpage/documents/application")
    private IEntitiesContainer documentsContainer;
    @EntityRef(location = "/tmp")
    private IEntitiesContainer temp;

    /* (non-Javadoc)
     * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
     */
    @Override
    protected void alignAsync() throws GeneralDOAException {
        IDocument input = getInput();
        final IStaticResource backupFile =
                (IStaticResource) input.getFieldValue("backupFile");
        if (backupFile == null) {
            throw new GeneralDOAException("Wskaż plik!");
        }

        // czyszczenie kontenerow
        documentsContainer.purge(IEntityEvaluator.TYPE_DOCUMENT);
        imagesContainer.purge(IEntityEvaluator.TYPE_STATIC_RESOURCE);

        // tworzenie tymczasowego kontenera, do ktorego zostanie rozpakowany plik zip
        IEntitiesContainer restoreContainer =
                doa.createContainer("backup-restore", temp);

        // rozpakowywanie plikow
        ZipInputStream zipStream =
                new ZipInputStream(backupFile.getContentStream());
        try {
            ZipEntry entry = null;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                PathIterator<String> entryPath =
                        new EntityLocationIterator(entry.getName());

                // tworzenie struktury kontenerow
                IEntitiesContainer resourceContainer =
                        restoreContainers(restoreContainer, entryPath);

                // tworzenie zasobu statycznego
                String resourceName = entryPath.getCurrentPathPart();
                IStaticResource resource =
                        doa.createStaticResource(
                                resourceName,
                                ContentTypeUtils.findContentTypes(resourceName),
                                resourceContainer);
                resource.setContentFromStream(zipStream,
                        entry.getCompressedSize());
                zipStream.closeEntry();
            }
        } catch (IOException e) {
            throw new GeneralDOAException(e);
        } finally {
            try {
                zipStream.close();
            } catch (IOException e) {
                throw new GeneralDOAException(e);
            }
        }

        // przywracanie obiektow
        restoreObjects(restoreContainer, "/images", imagesContainer, "/");
        restoreObjects(restoreContainer, "/documents", documentsContainer, "/",
                false);
        restoreObjects(restoreContainer, "/documents/sections",
                documentsContainer, "/sections");
        restoreObjects(restoreContainer, "/documents/links",
                documentsContainer, "/links");


        // usuwanie tymczasowego kontenera
        restoreContainer.remove(true);

        setOutput(createOutputDocument("void"));
    }

    private void restoreObjects(IEntitiesContainer sourceContainer,
                                IEntitiesContainer destContainer, boolean deep)
            throws GeneralDOAException {
        Iterable<IEntity> sourceEntities =
                (Iterable<IEntity>) sourceContainer.getEntities();
        for (IEntity entity : sourceEntities) {
            if (entity instanceof IStaticResource) {
                IStaticResource resource = (IStaticResource) entity;
                if (entity.getName().endsWith(".json")) {
                    IStaticResource documentJson = (IStaticResource) entity;
                    try {
                        restoreDocument(documentJson, destContainer);
                    } catch (Exception e) {
                        throw new GeneralDOAException(e);
                    }
                    continue;
                }
                resource.setContainer(destContainer);
            } else if (entity instanceof IEntitiesContainer && deep) {
                IEntitiesContainer container =
                        destContainer.getEntityByName(entity.getName(),
                                IEntitiesContainer.class);
                if (container == null) {
                    container =
                            doa.createContainer(entity.getName(), destContainer);
                }
                restoreObjects((IEntitiesContainer) entity, container, deep);
            }
        }
    }

    private void restoreObjects(IEntitiesContainer backupContainer,
                                String backupLocation, IEntitiesContainer restoreContainer,
                                String restoreLocation) throws GeneralDOAException {
        restoreObjects(backupContainer, backupLocation, restoreContainer,
                restoreLocation, true);
    }

    private void restoreObjects(IEntitiesContainer backupContainer,
                                String backupLocation, IEntitiesContainer restoreContainer,
                                String restoreLocation, boolean deep) throws GeneralDOAException {
        IEntitiesContainer sourceContainer =
                (IEntitiesContainer) backupContainer
                        .lookupEntityByLocation(backupLocation);
        if (sourceContainer == null) {
            log.error(MessageFormat.format(
                    "Unable to find source container: {0}", backupLocation));
            return;
        }
        IEntitiesContainer destContainer =
                (IEntitiesContainer) restoreContainer
                        .lookupEntityByLocation(restoreLocation);
        if (destContainer == null) {
            log.error(MessageFormat.format(
                    "Unable to find destination container: {0}",
                    restoreLocation));
            return;
        }
        restoreObjects(sourceContainer, destContainer, deep);

    }

    private IDocument restoreDocument(IStaticResource documentJson,
                                      IEntitiesContainer destContainer) throws Exception {
        JSONObject jsonObject =
                new JSONObject(new String(documentJson.getContent()));
        IDocument document = new JsonConverter().readDocument(doa, jsonObject);
        document.setContainer(destContainer);
        return document;
    }

    private IEntitiesContainer restoreContainers(
            IEntitiesContainer restoreContainer, PathIterator<String> entryPath)
            throws GeneralDOAException {
        if (entryPath.hasNext()) {
            String part = entryPath.next();
            if (!entryPath.hasNext()) {
                return restoreContainer;
            }
            IEntitiesContainer current =
                    restoreContainer.getEntityByName(part,
                            IEntitiesContainer.class);
            if (current == null) {
                current = doa.createContainer(part, restoreContainer);
            }

            return restoreContainers(current, entryPath);
        }
        return restoreContainer;
    }

}

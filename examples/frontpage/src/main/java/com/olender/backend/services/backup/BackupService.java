/**
 *
 */
package com.olender.backend.services.backup;

import com.olender.backend.services.BaseServiceDefinitionLogic;
import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.ITransactionCallback;
import pl.doa.entity.event.impl.ServiceExecutedEvent;
import pl.doa.entity.event.source.impl.ServiceOutputProducedSource;
import pl.doa.resource.IStaticResource;
import pl.doa.service.IServiceDefinition;
import pl.doa.service.annotation.EntityRef;

/**
 * @author activey
 */
public class BackupService extends BaseServiceDefinitionLogic {

    @EntityRef(location = "/applications/olender-frontpage/images/application")
    private IEntitiesContainer imagesContainer;
    @EntityRef(location = "/applications/olender-frontpage/documents/application")
    private IEntitiesContainer documentsContainer;
    @EntityRef(location = "/applications/olender-frontpage/backup")
    private IEntitiesContainer archiveContainer;
    @EntityRef(location = "/applications/archiver/archive")
    private IServiceDefinition archiveService;

    /* (non-Javadoc)
     * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
     */
    @Override
    protected void alignAsync() throws GeneralDOAException {
        IDocument input = getInput();
        final IStaticResource backupFile =
                (IStaticResource) input.getFieldValue("backupFile");
        if (backupFile != null) {
            doa.doInTransaction(new ITransactionCallback<Void>() {

                public Void performOperation() throws Exception {
                    archiveContainer.addEntity(backupFile);
                    return null;
                }
            });
            setOutput(createOutputDocument("void"));
            return;
        }

        ServiceExecutedEvent event =
                waitForEvent(new ServiceOutputProducedSource(archiveService) {
                    @Override
                    protected void onBeforeRun(IServiceDefinition serviceToRun,
                                               IDocument input) throws GeneralDOAException {
                        IListDocumentFieldValue sourceContainers =
                                (IListDocumentFieldValue) input.getField(
                                        "sourceContainers", true);
                        sourceContainers.addReferenceField("images",
                                imagesContainer);
                        sourceContainers.addReferenceField("documents",
                                documentsContainer);

                        input.setFieldValue("destinationContainer",
                                archiveContainer);
                        input.setFieldValue("archiveName",
                                System.currentTimeMillis() + "");
                    }

                    @Override
                    protected boolean allowNullInput() {
                        return false;
                    }
                });
        setOutput(event.getServiceOutput());
    }
}

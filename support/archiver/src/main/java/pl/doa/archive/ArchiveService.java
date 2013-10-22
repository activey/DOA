/**
 * 
 */
package pl.doa.archive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.ITransactionCallback;
import pl.doa.resource.IStaticResource;
import pl.doa.service.AbstractServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class ArchiveService extends AbstractServiceDefinitionLogic {

	/* (non-Javadoc)
	 * @see pl.doa.service.AbstractServiceDefinitionLogic#align()
	 */
	@Override
	public void align() throws GeneralDOAException {
		IDocument input = getInput();
		final String archiveName = input.getFieldValueAsString("archiveName");
		final IEntitiesContainer destinationContainer =
				(IEntitiesContainer) input
						.getFieldValue("destinationContainer");

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zipPackage = new ZipOutputStream(outputStream);

		Iterable<IDocumentFieldValue> containersList =
				(Iterable<IDocumentFieldValue>) input
						.getFieldValue("sourceContainers");
		for (IDocumentFieldValue containerField : containersList) {
			IEntitiesContainer sourceContainer =
					(IEntitiesContainer) containerField.getFieldValue();

			Iterable<IEntity> entities =
					sourceContainer.lookupForEntities(new IEntityEvaluator() {

						public boolean isReturnableEntity(IEntity currentEntity) {
							return !(currentEntity instanceof IEntitiesContainer);
						}
					}, true);

			for (IEntity entity : entities) {
				if (entity instanceof IStaticResource) {
					IStaticResource resource = (IStaticResource) entity;
					String entryName =
							getZipEntryName(sourceContainer, resource,
									containerField.getFieldName());
					ZipEntry zipEntry = new ZipEntry(entryName);
					zipEntry.setSize(resource.getContentSize());
					try {
						zipPackage.putNextEntry(zipEntry);
						InputStream resourceStream =
								resource.getContentStream();
						byte[] buf = new byte[1024];
						int len;
						while ((len = resourceStream.read(buf)) > 0) {
							zipPackage.write(buf, 0, len);
						}
						zipPackage.closeEntry();
					} catch (IOException e) {
						throw new GeneralDOAException(e);
					}
				} else if (entity instanceof IDocument) {
					IDocument doc = (IDocument) entity;
					IStaticResource resource = doc.render("application/json");
					String entryName =
							getZipEntryName(sourceContainer, doc,
									containerField.getFieldName());
					ZipEntry zipEntry = new ZipEntry(entryName + ".json");
					zipEntry.setSize(resource.getContentSize());
					try {
						zipPackage.putNextEntry(zipEntry);
						InputStream resourceStream =
								resource.getContentStream();
						byte[] buf = new byte[1024];
						int len;
						while ((len = resourceStream.read(buf)) > 0) {
							zipPackage.write(buf, 0, len);
						}
						zipPackage.closeEntry();
					} catch (IOException e) {
						throw new GeneralDOAException(e);
					}
				}
			}
		}

		try {
			zipPackage.close();
			zipPackage.flush();
		} catch (IOException e) {
			throw new GeneralDOAException(e);
		}
		IStaticResource archiveResource =
				doa.doInTransaction(new ITransactionCallback<IStaticResource>() {

					public IStaticResource performOperation() throws Exception {
						IStaticResource archiveResource =
								doa.createStaticResource(archiveName,
										"application/zip", destinationContainer);
						archiveResource.setContentFromBytes(outputStream
								.toByteArray());
						return archiveResource;
					}
				});

		IDocument result =
				getPossibleOutputDefinition("archive_output")
						.createDocumentInstance();
		result.setFieldValue("archiveResource", archiveResource);
		result.setFieldValue("archiveSize", archiveResource.getContentSize());

		setOutput(result);
	}

	private String getZipEntryName(IEntitiesContainer sourceContainer,
			IEntity entity, String zipSubFolderName) {
		String sourceLocation = sourceContainer.getLocation();
		String entityLocation = entity.getLocation();
		return zipSubFolderName + "/"
				+ entityLocation.substring(sourceLocation.length() + 1);
	}
}

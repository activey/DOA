/**
 * 
 */
package com.olender.backend.services.sections.gallery;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import pl.doa.GeneralDOAException;
import pl.doa.container.IEntitiesContainer;
import pl.doa.document.IDocument;
import pl.doa.document.field.IDocumentFieldValue;
import pl.doa.document.field.IListDocumentFieldValue;
import pl.doa.entity.ITransactionCallback;
import pl.doa.resource.IStaticResource;
import pl.doa.service.annotation.EntityRef;

import com.olender.backend.services.BaseServiceDefinitionLogic;

/**
 * @author activey
 * 
 */
public class LinkImageService extends BaseServiceDefinitionLogic {

	@EntityRef(location = "/applications/olender-frontpage/documents/application/links/galleries")
	private IEntitiesContainer links = null;

	@EntityRef(location = "/applications/olender-frontpage/images/application/Thumbnails")
	private IEntitiesContainer thumbs = null;

	public static BufferedImage imageToBufferedImage(Image im) {
		BufferedImage bi =
				new BufferedImage(im.getWidth(null), im.getHeight(null),
						BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(im, 0, 0, null);
		bg.dispose();
		return bi;
	}

	/* (non-Javadoc)
	 * @see pl.doa.service.AsynchronousServiceDefinitionLogic#alignAsync()
	 */
	@Override
	protected void alignAsync() throws GeneralDOAException {
		final IDocument input = getInput();
		
		IListDocumentFieldValue images =
				(IListDocumentFieldValue) input.getField("toEntities");
		IDocumentFieldValue bigImageField = images.getListField("bigImage");
		IDocumentFieldValue smallImageField = images.getListField("smallImage");
		IStaticResource smallImageResource =
				(IStaticResource) ((smallImageField == null) ? null
						: smallImageField.getFieldValue());
		
		// generowanie miniatury
		if (smallImageResource == null) {
			IStaticResource bigImageResource =
					(IStaticResource) bigImageField.getFieldValue();
			BufferedImage bigImage;
			try {
				bigImage = ImageIO.read(bigImageResource.getContentStream());
			} catch (IOException e) {
				throw new GeneralDOAException(e);
			}
			BufferedImage resized =
					imageToBufferedImage(bigImage.getScaledInstance(-1, 270, 0));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				ImageIO.write(resized, "jpg", out);
			} catch (IOException e) {
				throw new GeneralDOAException(e);
			}
			IStaticResource thumbResource =
					doa.createStaticResource("image/jpeg", thumbs);
			thumbResource.setContentFromBytes(out.toByteArray());
			images.addReferenceField("smallImage", thumbResource);
		}

		IDocument output =
				doa.doInTransaction(new ITransactionCallback<IDocument>() {

					public IDocument performOperation() throws Exception {
						return links.addEntity(input.createCopy());
					}
				});
		if (output == null) {
			throw new GeneralDOAException("Unable to create link!");
		}
		setOutput(output);
	}

}

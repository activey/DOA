package pl.doa.utils.profile.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import pl.doa.document.IDocument;
import pl.doa.document.IDocumentDefinition;
import pl.doa.document.field.IDocumentFieldType;
import pl.doa.entity.IEntity;

import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class IncomingDocumentEvaluatorTest {

    @Mock
    Iterator<IDocumentFieldType> emptyDocumentFieldTypeIterator;
    @Mock
    IEntity entity;
    @Mock
    IDocumentDefinition definitionWithoutAuthorizableFields;
    @Mock
    IDocument documentWithNoAuthorizableFieldsDefinition;
    private IncomingDocumentEvaluator evaluator;
    private boolean result;

    @Before
    public void initMocks() {
        given(definitionWithoutAuthorizableFields.getAuthorizableFields()).willReturn(emptyDocumentFieldTypeIterator);
        given(documentWithNoAuthorizableFieldsDefinition.getDefinition()).willReturn(definitionWithoutAuthorizableFields);
    }

    @Test
    public void isNotReturnableEntityWhenDocumentHasNoAuthorizableFields() {
        // given
        evaluator = new IncomingDocumentEvaluator(documentWithNoAuthorizableFieldsDefinition);

        // when
        result = evaluator.isReturnableEntity(entity);

        // then
        assertNotReturnable(result);
    }

    private void assertNotReturnable(boolean result) {
        assertThat(result).isFalse();
    }
}

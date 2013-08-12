package pl.doa.wrapper.test;

import pl.doa.document.IDocument;
import pl.doa.wrapper.annotation.DocumentDefinition;
import pl.doa.wrapper.annotation.Field;

@DocumentDefinition
public interface TestType extends IDocument {

    @Field
    public String getTestField();

    @Field
    public void setTestField(String testField);
}

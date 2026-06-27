package com.karesh.demandlettergenerator.generator;

import jakarta.xml.bind.JAXBElement;
import org.docx4j.TextUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Text;

public class DocxInspector {

    public void inspect(WordprocessingMLPackage document) {

        inspectObject(document.getMainDocumentPart());
    }

    @SuppressWarnings("unchecked")
    private void inspectObject(Object object) {

        if (object instanceof JAXBElement<?>) {
            object = ((JAXBElement<Object>) object).getValue();
        }

        if (object instanceof ContentAccessor accessor) {

            for (Object child : accessor.getContent()) {
                inspectObject(child);
            }

        } else {

            String value = TextUtils.getText(object);

            if (object instanceof Text text) {
                System.out.println("TEXT: [" + text.getValue() + "]");
            }

        }

    }

}
package collabware.api.document;


import collabware.api.operations.OperationGenerator;

/**
 * Provides {@code Document}s of a specific document type.
 * <p> 
 * Collabware supports multiple document types (e.g. graph-based document, a string-based document, etc). 
 * The different types can be distinguished via their document type. 
 * Typically, one implementation of this interface will provide documents of one document type.</p>
 * @see Document
 */
public interface DocumentProvider extends PrimitiveOperationTransformer, PrimitiveOperationDeserializer {
	/**
	 * @see Document#getDocumentType()
	 * @return the document type provided by this DocumentProvider. 
	 */
	String getDocumentType();
	
	/**
	 * 
	 * @param contentType
	 * @return document with the given contentType
	 */
	ModifyableDocument createDocument(String contentType);
	
	/**
	 * 
	 * @return The operation generator that belongs to the provided document types.
	 */
	OperationGenerator createOperationGenerator();
}

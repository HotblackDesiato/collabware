package collabware.model.internal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import collabware.api.document.Document;
import collabware.model.ModelProvider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/bundle-context.xml"})
public class ModelProviderTest {
	@Autowired
	private ModelProvider modelProvider;

	@Test
	public void createModelProvider() throws Exception {
		String type = "someObscureType";
		
		Document document = modelProvider.createDocument(type);
		
		assertNotNull(document);
		assertEquals(type, document.getContentType());
	}
	
	@Test
	public void documentType() throws Exception {
		assertThat(modelProvider.getDocumentType(), is("collabware.model"));
		
	}
}

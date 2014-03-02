package collabware.collaboration.internal;

import static org.easymock.EasyMock.createMock;

import org.junit.Before;
import org.junit.Test;

import collabware.model.ModelProvider;
import collabware.transformer.TransformationProvider;

public class CollaborationProviderServiceLifeCycleTest {

	private CollaborationProviderImpl collaborationProvider;
	
	@Before
	public void setup() {
		collaborationProvider = new CollaborationProviderImpl();	
	}

	@Test(expected=IllegalStateException.class)
	public void startUpWithNotingSet() throws Exception {
		collaborationProvider.startup();		
	}

	@Test(expected=IllegalStateException.class)
	public void startUpWithoutModelProviderSet() throws Exception {
		collaborationProvider.setTransformationProvider(createMock(TransformationProvider.class));
		
		collaborationProvider.startup();		
	}

	@Test(expected=IllegalStateException.class)
	public void startUpWithoutTransformationProviderSet() throws Exception {
		collaborationProvider.setModelProvider(createMock(ModelProvider.class));
		
		collaborationProvider.startup();		
	}
	
	@Test
	public void startUp() throws Exception {
		collaborationProvider.setModelProvider(createMock(ModelProvider.class));
		collaborationProvider.setTransformationProvider(createMock(TransformationProvider.class));

		collaborationProvider.startup();				
	}
}

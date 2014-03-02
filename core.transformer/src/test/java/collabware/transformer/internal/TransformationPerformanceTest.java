package collabware.transformer.internal;


//@SuppressWarnings("restriction")
public class TransformationPerformanceTest extends AbstractTransformationTest {

//	private Transformer transformer;

//	@Before
//	public void setup() {
//		TransformationProviderImpl transformationProvider = new TransformationProviderImpl();
//		transformationProvider.setPartiallyInclusiveOperationalTransformation(createTransformationMatrixMock());
//		transformationProvider.setOperationsProvider(new GraphOperationsProviderImpl());
//		transformer = transformationProvider.createTransformer();
//	}
//	
//	@Test
//	public void transform_1x2() throws Exception {
//		BackedOperationsSet modelState = new BackedOperationsSetImpl();
//
//		PrimitiveOperation o1 = new PrimitiveOperationImpl(modelState.asContextForClient(0));
//		modelState.add(o1);
//		PrimitiveOperation o2 = new PrimitiveOperationImpl(modelState.asContextForClient(1));
//		modelState.add(o2);
//		PrimitiveOperation toBeTransformed = new PrimitiveOperationImpl(modelState.asContextForClient(2));
//		
//		PrimitiveOperation transformed = transformer.transform(toBeTransformed, modelState);
//		
//		assertTrue(transformed.getContext().contains(o1));
//		assertTrue(transformed.getContext().contains(o2));
//		assertTrue(modelState.equals(transformed.getContext()));
//	}
}

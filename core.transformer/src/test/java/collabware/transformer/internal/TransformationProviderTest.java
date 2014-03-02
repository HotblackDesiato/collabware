package collabware.transformer.internal;


//@SuppressWarnings("restriction")
public class TransformationProviderTest extends AbstractTransformationTest {
	
//	private Transformer transformer;
//	private Contextualizer contextualizer;
//	@Before
//	public void setup() {
//		TransformationProviderImpl transformationProvider = new TransformationProviderImpl();
//		transformationProvider.setPartiallyInclusiveOperationalTransformation(createTransformationMatrixMock());
//		transformer = transformationProvider.createTransformer();
//	}
//
//	@Test
//	public void transform_1x1() throws Exception {
//		BackedOperationsSet modelState = new BackedOperationsSetImpl();
//		PrimitiveOperation o1 = new PrimitiveOperationImpl(modelState.asContextForClient(0));
//		PrimitiveOperation toBeTransformed = new PrimitiveOperationImpl(modelState.asContextForClient(1));
//		modelState.add(o1);
//		
//		PrimitiveOperation transformed = transformer.transform(toBeTransformed, modelState);
//		
//		assertTrue(transformed.getContext().contains(o1));
//		assertTrue(modelState.equals(transformed.getContext()));
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
//
//	@Test
//	public void transform_1x2x2() throws Exception {
//		BackedOperationsSet modelState = new BackedOperationsSetImpl();
//		PrimitiveOperation o1 = new PrimitiveOperationImpl(modelState.asContextForClient(0));
//		PrimitiveOperation o3 = new PrimitiveOperationImpl(modelState.asContextForClient(2));
//		PrimitiveOperation toBeTransformed = new PrimitiveOperationImpl(modelState.asContextForClient(1));
//		
//		PrimitiveOperation o2 = new PrimitiveOperationImpl(o1.getContext().including(o1));
//		PrimitiveOperation o4 = new PrimitiveOperationImpl(o3.getContext().including(o3));
//		modelState.add(o1);
//		modelState.add(o2);
//
//		modelState.add(o3);
//		modelState.add(o4);
//
//		PrimitiveOperation transformed = transformer.transform(toBeTransformed, modelState);
//		
//		assertTrue(transformed.getContext().contains(o1));
//		assertTrue(transformed.getContext().contains(o2));
//		assertTrue(transformed.getContext().contains(o3));
//		assertTrue(transformed.getContext().contains(o4));
//		assertTrue(modelState.equals(transformed.getContext()));
//	}
//	
//	@Test
//	public void transformComplexOperation() throws Exception {
//		BackedOperationsSet modelState = new BackedOperationsSetImpl();
//		ComplexOperation complex = createComplexOperationOfLength(3, modelState.asContextForClient(0));
//		
//		ComplexOperation transformed = transformer.transform(complex, modelState);
//		
//		assertEquals(transformed.getContext(), complex.getContext());
//	}
//
//	@Test
//	public void transform2ComplexOperation() throws Exception {
//		BackedOperationsSet modelState = new BackedOperationsSetImpl();
//		ComplexOperation complex0 = createComplexOperationOfLength(3, modelState.asContextForClient(0));
//		ComplexOperation complex1 = createComplexOperationOfLength(3, modelState.asContextForClient(1));
//		complex1.addTo(modelState);
//		
//		ComplexOperation transformed = transformer.transform(complex0, modelState);
//	
//		assertTrue(modelState.equals(transformed.getContext()));
//		assertTrue(transformed.getContext().contains(complex1));
//	}
//
//	@Test
//	public void collision() throws Exception {
//		TransformationProviderImpl transformationProvider = new TransformationProviderImpl();
//		transformationProvider.setPartiallyInclusiveOperationalTransformation(createTransformationMatrixMockThrowingCollisionExceptions());
//		transformationProvider.setOperationsProvider(new GraphOperationsProviderImpl());
//		transformer = transformationProvider.createTransformer();
//		BackedOperationsSet modelState = new BackedOperationsSetImpl();
//		ComplexOperation complex0 = createComplexOperationOfLength(3, modelState.asContextForClient(0));
//		ComplexOperation complex1 = createComplexOperationOfLength(3, modelState.asContextForClient(1));
//		complex1.addTo(modelState);
//		
//		try {
//			transformer.transform(complex0, modelState);
//			fail();
//		} catch (CollisionException e) {
//			assertEquals(complex0, e.getOffender());
//			assertEquals(complex1, e.getOffended());
//		}
//	}
//	
//	private PartiallyInclusiveOperationalTransformation createTransformationMatrixMockThrowingCollisionExceptions() {
//		return new PartiallyInclusiveOperationalTransformation() {
//			public PrimitiveTransformation transform(final PrimitiveOperation op) {
//				return new PrimitiveTransformation() {
//					@Override
//					public PrimitiveOperation against(PrimitiveOperation against) throws CollisionException {
//						throw new CollisionException(op, against);
//					}
//				};
//			}
//		};
//	}
//
//	private ComplexOperation createComplexOperationOfLength(int numberOfPrimitiveOperations, Context baseContext) {
//		PrimitiveOperation[] primitives = createPrimitiveOperationsSequence(numberOfPrimitiveOperations, baseContext);
//		return new ComplexOperationImpl(primitives);
//	}
//
//	private PrimitiveOperation[] createPrimitiveOperationsSequence(int numberOfPrimitiveOperations, Context baseContext) {
//		Context nextContext = baseContext;
//		PrimitiveOperation[] primitives = new PrimitiveOperation[numberOfPrimitiveOperations];
//		for (int i = 0; i < numberOfPrimitiveOperations; i++) {
//			PrimitiveOperation primitiveOperation = new PrimitiveOperationImpl(nextContext);
//			nextContext = primitiveOperation.getSelfIncludingContext();
//			primitives[i] = primitiveOperation;
//		}
//		return primitives;
//	}
}

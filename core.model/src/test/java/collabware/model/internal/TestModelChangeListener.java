package collabware.model.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import collabware.api.document.change.Change;
import collabware.api.document.change.ChangeListener;
import collabware.model.ModelProvider;
import collabware.model.graph.ModifyableNode;
import collabware.model.internal.graph.changes.AttributeSetImpl;
import collabware.model.internal.graph.changes.NodeAddedChangeImpl;
import collabware.model.internal.graph.changes.NodeRemovedImpl;
import collabware.model.internal.graph.changes.ReferenceAddedImpl;
import collabware.model.internal.graph.changes.ReferenceRemovedImpl;
import collabware.model.internal.graph.changes.ReferenceSetImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/bundle-context.xml"})
public class TestModelChangeListener {

	@Autowired
	private ModelProvider modelProvider;
	
	private ModelImpl theModel = new ModelImpl("someType");

	private ChangeListener theListener;

	@After
	public void checkExpectation() {
		verify(theListener);
	}
	
	@Test
	public void whenAddingNode_ThenNodeAddedEventIsRaised() {
		givenListenerExpectingChange(new NodeAddedChangeImpl("n1"));

		theModel.getGraph().addNode("n1");
	}

	@Test
	public void givenModelWithOneNode_whenRemovingNode_ThenNodeRemovedEventIsRaised() {
		givenModel("n1");
		givenListenerExpectingChange(new NodeRemovedImpl("n1"));
		
		theModel.getGraph().detach(node("n1"));
	}

	@Test
	public void givenNodeWithAttribute_whenRemovingNode_ThenAttributeChangeEventIsRaised() {
		givenModel("n1, n1.attr=42");
		givenListenerExpectingChange(new NodeRemovedImpl("n1"), new AttributeSetImpl("n1", "attr", 42, null));
		
		theModel.getGraph().remove(node("n1"));
	}

	@Test
	public void givenModelWithOneNode_whenSettingAttribute_ThenAttributeChangedEventIsRaised() {
		givenModel("n1");
		givenListenerExpectingChange(new AttributeSetImpl("n1", "foo", null, "bar"));
		
		node("n1").getAttributes().set("foo", "bar");
	}

	@Test
	public void givenModelWithTwoNodes_whenSettingReference_ThenReferenceChangedEventIsRaised() {
		givenModel("n1,n2");
		givenListenerExpectingChange(new ReferenceSetImpl("n1", "foo", null, "n2"));
		
		node("n1").getUnaryReferences().set("foo", node("n2"));
	}

	@Test
	public void givenModelWithExistingReference_whenResettingReference_ThenReferenceChangedEventIsRaised() {
		givenModel("n1,n2,n3,n1.foo->n2");
		givenListenerExpectingChange(new ReferenceSetImpl("n1", "foo", "n2", "n3"));
		
		node("n1").getUnaryReferences().set("foo", node("n3"));
	}

	@Test
	public void givenModelWithExistingReference_whenUnsettingReference_ThenReferenceChangedEventIsRaised() {
		givenModel("n1,n2,n1.foo->n2");
		givenListenerExpectingChange(new ReferenceSetImpl("n1", "foo", "n2", null));
		
		node("n1").getUnaryReferences().set("foo", null);
	} 
	@Test
	public void givenModelWithTwoNodes_whenAddingReference_ThenReferenceAddedEventIsRaised() {
		givenModel("n1,n2");
		givenListenerExpectingChange(new ReferenceAddedImpl("n1", "foo", "n2", 0));
		
		node("n1").getNaryReferences().add("foo", 0, node("n2"));
	}
	
	@Test
	public void givenModelWithTwoNodesAndExistingRef_whenAddingReference_ThenReferenceAddedEventIsRaised() {
		givenModel("n1,n2,n3,n1.foo[0]->n2");
		givenListenerExpectingChange(new ReferenceAddedImpl("n1", "foo", "n3", 1));
		
		node("n1").getNaryReferences().add("foo", 1, node("n3"));
	}

	@Test
	public void givenModelWithTwoNodesAndExistingRef_whenRemovingReference_ThenReferenceAddedEventIsRaised() {
		givenModel("n1,n2,n1.foo[0]->n2");
		givenListenerExpectingChange(new ReferenceRemovedImpl("n1", "foo", "n2", 0));
		
		node("n1").getNaryReferences().remove("foo", 0);
	}

	@Test
	public void givenModelWithTwoNodesAndTwoExistingRef_whenRemovingReference_ThenReferenceAddedEventIsRaised() {
		givenModel("n1,n2,n3,n1.foo[0]->n2,n1.foo[1]->n3");
		givenListenerExpectingChange(new ReferenceRemovedImpl("n1", "foo", "n3", 1));
		
		node("n1").getNaryReferences().remove("foo", 1);
	}

	private void givenModel(String literal) {
		theModel= (ModelImpl) modelProvider.createModelFromLiteral("",literal);
	}

	private void givenListenerExpectingChange(Change ...changes) {
		theListener = createMock(ChangeListener.class);
		for (Change change:changes) {
			theListener.notifyChange(eq(change));
			expectLastCall().once();
		}
		replay(theListener);
		theModel.addChangeListener(theListener);
	}


	private ModifyableNode node(String id) {
		return theModel.getGraph().getNode(id);
	}

}

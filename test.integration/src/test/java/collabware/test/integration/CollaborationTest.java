package collabware.test.integration;


import org.junit.Test;

import collabware.collaboration.Client;
import collabware.collaboration.ConflictingOperationsException;

public class CollaborationTest extends AbstractCollaborationTest {

	@Test
	public void testIdenticalChangesOfLength1() throws Exception {
		client1Applies(complex( addNode("n1")));
		client2Applies(complex( addNode("n1")));
		
		assertModelEqualsTo("n1");
	}
	
	@Test
	public void testIdenticalChangesOfLength2() throws Exception {
		client1Applies(complex( addNode("n1"), addNode("n2")));
		client2Applies(complex( addNode("n1"), addNode("n2")));
		
		assertModelEqualsTo("n1, n2");
	}
	
	@Test
	public void testIdenticalChangesOfLength4() throws Exception {
		client1Applies(complex( addNode("n1"), addNode("n2"), setAttr("n1", "a", null, "blah"), setRef("n1", "r", null, "n2")));
		client2Applies(complex( addNode("n1"), addNode("n2"), setAttr("n1", "a", null, "blah"), setRef("n1", "r", null, "n2")));
		
		assertModelEqualsTo("n1, n2, n1.a='blah', n1.r->n2");
	}
	
	@Test
	public void testDifferentChangesOfLength4() throws Exception {
		client1Applies(complex( addNode("n1"), addNode("n2"), setAttr("n1", "a", null, "blah"), setRef("n1", "r", null, "n2")));
		client2Applies(complex( addNode("n3"), addNode("n4"), setAttr("n3", "a", null, "blah"), setRef("n3", "r", null, "n4")));
		
		assertModelEqualsTo("n1, n2, n3, n4, n1.a='blah', n3.a='blah', n1.r->n2, n3.r->n4");
	}

	@Test
	public void testSubsequentChangesOfLength4() throws Exception {
		client1Applies(0, complex( addNode("n1"), addNode("n2"), setAttr("n1", "a", null, "blah"), setRef("n1", "r", null, "n2")));
		client2Applies(1, complex( addNode("n3"), addNode("n4"), setAttr("n3", "a", null, "foo"), setRef("n3", "r", null, "n4")));
		
		assertModelEqualsTo("n1, n2, n3, n4, n1.a='blah', n3.a='foo', n1.r->n2, n3.r->n4");
	}
	
	@Test
	public void testSubsequentOverridingChangesOfLength4() throws Exception {
		client1Applies(0, complex( addNode("n1"), addNode("n2"), setAttr("n1", "a", null, "blah"), setRef("n1", "r", null, "n2")));
		client2Applies(1, complex( addNode("n3"), setAttr("n1", "a", "blah", "foo"), setRef("n1", "r", "n2", "n3")));
		
		assertModelEqualsTo("n1, n2, n3, n1.a='foo', n1.r->n3");
	}
	
	@Test
	public void testOverlappingChangesOfLength4() throws Exception {
		client1Applies(complex( addNode("n1"), addNode("n2"), setAttr("n1", "a", null, "blah"), setRef("n1", "r", null, "n2")));
		client2Applies(complex( addNode("n1"), addNode("n4"), setAttr("n4", "a", null, "blah"), setRef("n1", "r2", null, "n4")));
		
		assertModelEqualsTo("n1, n2, n4, n1.a='blah', n4.a='blah', n1.r->n2, n1.r2->n4");
	}
	
	@Test
	public void test8OverlappingChangesOfLength1() throws Exception {
		client1Applies(addNode("n1"));
		client1Applies(addNode("n2"));
		client1Applies(setAttr("n1", "a", null, "blah"));
		client1Applies(setRef("n1", "r", null, "n2"));
		client2Applies(addNode("n1"));
		client2Applies(addNode("n4"));
		client2Applies(setAttr("n4", "a", null, "blah"));
		client2Applies(setRef("n1", "r2", null, "n4"));
		
		assertModelEqualsTo("n1, n2, n4, n1.a='blah', n4.a='blah', n1.r->n2, n1.r2->n4");
	}
	
	@Test
	public void test8InterleavedChanges() throws Exception {
		client1Applies(addNode("n1"));
		client2Applies(addNode("n1"));
		client1Applies(addNode("n2"));
		client2Applies(addNode("n4"));
		client1Applies(setAttr("n1", "a", null, "blah"));
		client2Applies(setAttr("n4", "a", null, "blah"));
		client1Applies(setRef("n1", "r", null, "n2"));
		client2Applies(setRef("n1", "r2", null, "n4"));
		
		assertModelEqualsTo("n1, n2, n4, n1.a='blah', n4.a='blah', n1.r->n2, n1.r2->n4");
	}
	
	@Test
	public void testConflictingChangesOfLength4_firstWins() throws Exception {
		client1Applies(complex( addNode("n1"), addNode("n2"), setAttr("n1", "a", null, "blah"), setRef("n1", "r", null, "n2")));
		client2Applies(complex( addNode("n1"), addNode("n2"), setAttr("n1", "a", null, "foo"), setRef("n1", "r", null, "n2")));
		assertModelEqualsTo("n1, n2, n1.r->n2, n1.a='blah'");
	}
	
	@Test
	public void testClient1MakesChangesThenClient2JoinsAndMakesChanges() throws Exception {
		givenInitialModel("n1,n2,n3");
		
		Client c1 = join();
		c1.applyChangeToCollaboration(0, complex(addRef("n1", "ref", 0, "n2")));

		Client c2 = join();
		c2.applyChangeToCollaboration(0, complex(addRef("n1", "ref", 1, "n3")));
		
		assertModelEqualsTo("n1, n2, n3, n1.ref[0]->n2, n1.ref[1]->n3");
	}

	@Test
	public void testClient1MakesChangesThenClient2JoinsAndMakesChanges2() throws Exception {
		givenInitialModel("n1,n2,n3");
		
		Client c1 = join();
		c1.applyChangeToCollaboration(0, complex(addRef("n1", "ref", 0, "n2")));
		
		Client c2 = join();
		c2.applyChangeToCollaboration(0, complex(addRef("n1", "ref", 1, "n3")));
		
		c1.applyChangeToCollaboration(1, complex(addNode("n4"), addRef("n1", "ref", 2, "n4")));
		c1.applyChangeToCollaboration(1, complex(addNode("n5"), addRef("n1", "ref", 3, "n5")));

		c2.applyChangeToCollaboration(2, complex(addNode("n6"), addRef("n1", "ref", 4, "n6")));
		
		assertModelEqualsTo("n1, n2, n3,n4,n5,n6, n1.ref[0]->n2, n1.ref[1]->n3, n1.ref[2]->n4, n1.ref[3]->n5, n1.ref[4]->n6");
	}
}

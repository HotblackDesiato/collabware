package collabware.model.internal.ops;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import collabware.api.operations.NoOperation;
import collabware.model.ModifyableModel;
import collabware.model.graph.ModifyableGraph;

public class NoOperationTest {
	
	@Test
	public void apply() throws Exception {
		ModifyableGraph graph = createStrictMock(ModifyableGraph.class);
		EasyMock.replay(graph);
		ModifyableModel doc = createMock(ModifyableModel.class);
		expect(doc.getGraph()).andStubReturn(graph);
		
		NoOperation nop = NoOperation.NOP;
		nop.apply(doc);
		
		verify(graph);
	}
	
	@Test
	public void inverse() throws Exception {
		NoOperation nop = NoOperation.NOP;
		NoOperation inverse = nop.inverse();
		
		assertNotNull(inverse);
	}
	
	@Test
	public void serialize() throws Exception {
		Map<String, Object> serialized = NoOperation.NOP.serialize();
		
		assertThat(serialized, hasEntry("t", (Object)"no"));
	}
}

package collabware.collaboration.internal.client;

import static collabware.model.operations.OperationHelper.addNode;
import static collabware.model.operations.OperationHelper.complex;
import static java.util.Arrays.asList;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMock;
import org.junit.Test;

import collabware.api.document.ModifyableDocument;
import collabware.api.operations.ComplexOperation;
import collabware.collaboration.client.replay.ReplayListener;
import collabware.collaboration.internal.client.replay.ReplayControlImpl;
import collabware.model.ModifyableModel;
import collabware.model.internal.ModelImpl;

public class ReplayControlTest {
	
	private ModifyableModel theModel = new ModelImpl("");
	private ModifyableModel expectedModel = new ModelImpl("");
	private ReplayListener listener = createNiceMock(ReplayListener.class);
	
	private ReplayControlImpl replay = new ReplayControlImpl(theModel, listener);
	
	@Test
	public void nextAppliesFirstOperationInSequence() throws Exception {
		replay.setReplaySequence(asList(complex(addNode("n2"))));
		
		replay.next();
		assertThat(theModel.getGraph().getNode("n2"), is(not(nullValue())));
		assertThat(replay.getPosition(), is(1));
	}
	
	@Test
	public void nextCallsReplayedOnListener() throws Exception {
		ComplexOperation op = complex(addNode("n2"));
		listener.replayed(op);
		expectLastCall().once();
		EasyMock.replay(listener);
		
		replay.setReplaySequence(asList(op));
		
		replay.next();
		verify(listener);
	}

	@Test
	public void nextAfterNextAppliesSecondOperationInSequence() throws Exception {
		replay.setReplaySequence(asList(complex(addNode("n2")), complex(addNode("n3"))));
		
		replay.next();
		replay.next();
		
		assertThat(theModel.getGraph().getNode("n3"), is(not(nullValue())));
		assertThat(replay.getPosition(), is(2));
	}

	@Test
	public void canNextIsTrueIfEndOfSequenceIsNotReached() throws Exception {
		replay.setReplaySequence(asList(complex(addNode("n2"))));
		
		assertThat(replay.canNext(), is(true));
	}
	
	@Test
	public void canNextIsFalseIfEndOfSequenceIsReached() throws Exception {
		replay.setReplaySequence(asList(complex(addNode("n2"))));
		
		replay.next();
		
		assertThat(replay.canNext(), is(false));
	}
	
	@Test
	public void previousUndoesNext() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n2"))));
		
		replay.next();
		replay.previous();
		
		assertThat(replay.getPosition(), is(0));
		assertThat(theModel.getGraph(), is(expectedModel.getGraph()));
	}
	
	@Test
	public void previousCallsReplayedOnListener() throws Exception {
		ComplexOperation op = complex(addNode("n2"));
		listener.replayed(op);
		expectLastCall().times(2);
		EasyMock.replay(listener);
		
		replay.setReplaySequence(asList(op));
		
		replay.next();
		replay.previous();
		verify(listener);
	}
	
	@Test
	public void previousPreviousUndoesNextNext() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n2")), complex(addNode("n3"))));
		
		replay.next();
		replay.next();
		replay.previous();
		replay.previous();
		
		assertThat(replay.getPosition(), is(0));
		assertThat(theModel.getGraph(), is(expectedModel.getGraph()));
	}
	
	@Test
	public void canPreviousIsFalseWhenAtBeginning() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n2")), complex(addNode("n3"))));
		
		assertThat(replay.canPrevious(), is(false));
	}

	@Test
	public void canPreviousIsTrueWhenNotAtBeginning() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n2")), complex(addNode("n3"))));
		
		replay.next();
		
		assertThat(replay.canPrevious(), is(true));
	}
	
	@Test
	public void seekZero() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n2")), complex(addNode("n3"))));
		
		replay.seek(0);
		
		assertThat(theModel.getGraph(), is(expectedModel.getGraph()));
	}

	@Test
	public void seekOne() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n2")), complex(addNode("n3"))));
		
		replay.seek(1);
		
		apply(expectedModel, complex(addNode("n2")));
		assertThat(theModel.getGraph(), is(expectedModel.getGraph()));
	}

	@Test
	public void seekTwo() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n2")), complex(addNode("n3"))));
		
		replay.seek(2);
		
		apply(expectedModel, complex(addNode("n2")), complex(addNode("n3")));
		assertThat(theModel.getGraph(), is(expectedModel.getGraph()));
	}

	@Test
	public void seekBackwards() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n2")), complex(addNode("n3")), complex(addNode("n4"))));
		
		replay.seek(3);
		
		replay.seek(0);
		assertThat(theModel.getGraph(), is(expectedModel.getGraph()));
	}

	@Test
	public void end() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n2")), complex(addNode("n3"))));
		
		replay.end();
		
		apply(expectedModel, complex(addNode("n2")), complex(addNode("n3")));
		assertThat(theModel.getGraph(), is(expectedModel.getGraph()));
	}

	@Test
	public void beginning() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n2")), complex(addNode("n3"))));
		
		replay.end();
		replay.beginning();
		
		assertThat(theModel.getGraph(), is(expectedModel.getGraph()));
	}
	
	@Test
	public void lengthIsLengthOfReplaySequence() throws Exception {
		
		replay.setReplaySequence(asList(complex(addNode("n1")), complex(addNode("n2")), complex(addNode("n3"))));
		
		assertThat(replay.length(), is(3));
		replay.setReplaySequence(asList(complex(addNode("n1")),complex(addNode("n2"))));
		
		assertThat(replay.length(), is(2));
	}

	private void apply(ModifyableDocument modifyableModel, ComplexOperation ... ops) {
		for (ComplexOperation o : ops) {
			modifyableModel.apply(o);
		}
	}
}

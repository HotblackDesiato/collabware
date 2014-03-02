package collabware.registry.internal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import collabware.registry.EditorReference;


public class EditorReferenceTest {

	@Test
	public void editorIdMustOnlyContainLettersNumbers() throws Exception {
		EditorReference reference = new EditorReference("Some-id.12_3", "The Name", "Some description", this.getClass().getClassLoader());
		
		assertThat(reference.getContentType(), is("Some-id.12_3"));
		assertThat(reference.getName(), is("The Name"));
		assertThat(reference.getDescription(), is("Some description"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void illegalId() throws Exception {
		new EditorReference("/illegal&$", "The Name", "Some description", this.getClass().getClassLoader());
	}
	
	
}

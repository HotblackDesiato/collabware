package collabware.web.client.js;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import collabware.api.document.change.Change;
import collabware.api.document.change.ChangeListener;
import collabware.collaboration.client.Command;
import collabware.collaboration.client.Session;
import collabware.model.Model;
import collabware.model.graph.ModifyableGraph;

import com.google.gwt.core.client.JavaScriptObject;

@Export
@ExportPackage("collabware")
public class JSDocument implements Exportable {
	
	private JSGraph graphWrapper;
	private Model model;
	private Session session;
	private Collection<AttributeBinding> bindings = new HashSet<AttributeBinding>();
	private final BufferedListener bufferedListener = new BufferedListener();
	
	public JSDocument(Model model, Session session) {
		this.model = model;
		this.session = session;
		this.graphWrapper = new JSGraph((ModifyableGraph) model.getGraph());
		bufferedListener.addChangeListener(new ChangeListener() {
			public void notifyChange(Change change) {
				for (AttributeBinding binding : bindings)
					binding.notifyChange(change);
			}
		});
		model.addChangeListener(bufferedListener);
	}
	
	/**
	 * 
	 * @return the graph.
	 */
	@Export 
	public JSGraph getGraph() {
		return graphWrapper;
	}
	
	/**
	 * Applies a change to this document. Any manipulation performed by this change are synchronized will all other participants.
	 * @param change The function that performs the change. It is assumed to have the following signature :  <pre>function(graph) {}</pre> 
	 * where graph is an instance of {@link JSGraph}. 
	 * @param description description of the change.
	 */
	@Export
	public void applyChange(final ChangeFunction change, final String description) {
		Command c = new JSCommand(description, change);
		session.applyChange(c);
	}
	
	/**
	 * Adds a listener to be notified about changes to the document. This listener can implement any of the following methods.
	 * <pre>
	 * {
	 *	nodeAdded: function (nodeId) {
	 *		// update UI
	 *	},
	 *	nodeRemoved: function (nodeId) {
	 *		// update UI
	 *	},
	 *	referenceAdded: function(nodeId, referenceName, index, targetId) {
	 *		// update UI
	 *	},
	 *	referenceRemoved: function(nodeId, referenceName, index, targetId) {
	 *		// update UI
	 *	},
	 *	unaryReferenceSet: function(nodeId, referenceName, newTargetId) {
	 *		// update UI
	 *		// newTargetId my be undefined if the reference was unset
	 *	},
	 *	attributeSet: function(nodeId, attributeName, value) {
	 *		// update UI
	 *	},
	 *	complexChangeEnded: function(description) {
	 *		// done doing a complex change (i.e. sequence of the above changes)
	 *	}
	 * }
	 * </pre>}
	 * 
	 * @param l the listener.
	 */
	@Export
	public void addDocumentChangeListener(JavaScriptObject l) {
		bufferedListener.addChangeListener(new JSChangeListener(l));
	}

	@Export
	public AttributeBinding bindAttribute(String attribute, JSNode node) {
		AttributeBinding attributeBinding = new AttributeBinding(node, attribute);
		bindings.add(attributeBinding);
		return attributeBinding;
	}
	
	@Export
	public void unbindAttribute(String attribute, JSNode node) {
		for (Iterator<AttributeBinding> i = bindings.iterator(); i.hasNext();) {
			AttributeBinding binding = i.next();
			if (binding.getNodeId().equals(node.getId()) && binding.getAttribute().equals(attribute)) {
				i.remove();
			}
		}
	}

	@Export
	public void unbindAttribute(JSNode node) {
		for (Iterator<AttributeBinding> i = bindings.iterator(); i.hasNext();) {
			AttributeBinding binding = i.next();
			if (binding.getNodeId().equals(node.getId())) {
				i.remove();
			}
		}
	}
	
	/**
	 * Whether or not this document is empty.
	 * @return {@code true} if and only if the document is empty.
	 */
	@Export
	public boolean isEmpty() {
		return model.isEmpty();
	}
	
}

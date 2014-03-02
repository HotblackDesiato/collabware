package collabware.web.cometd;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletResponse;

import org.cometd.annotation.ServerAnnotationProcessor;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.cometd.server.ext.AcknowledgedMessagesExtension;
import org.cometd.server.ext.TimesyncExtension;

public class CometdServlet extends GenericServlet {

	private static final long serialVersionUID = -4588601354344725616L;

	@Override
	public void init() throws ServletException {
		super.init();
		final BayeuxServerImpl bayeux = (BayeuxServerImpl) getServletContext()
				.getAttribute(BayeuxServer.ATTRIBUTE);

		if (bayeux == null)
			throw new UnavailableException("No BayeuxServer!");

		// Create extensions
		bayeux.addExtension(new TimesyncExtension());
		bayeux.addExtension(new AcknowledgedMessagesExtension());

		// Deny unless granted

		bayeux.createIfAbsent("/**", new ServerChannel.Initializer() {
			public void configureChannel(ConfigurableServerChannel channel) {
				channel.addAuthorizer(GrantAuthorizer.GRANT_NONE);
			}
		});

		// Allow anybody to handshake
		bayeux.getChannel(ServerChannel.META_HANDSHAKE).addAuthorizer(
				GrantAuthorizer.GRANT_PUBLISH);

		ServerAnnotationProcessor processor = new ServerAnnotationProcessor(bayeux);
		processor.process(new CometdProtocolEndpoint());
	}

	 @Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		((HttpServletResponse)res).sendError(503);
	}

}

package collabware.web.cometd;

import java.util.List;

import javax.annotation.PostConstruct;

import org.cometd.annotation.Configure;
import org.cometd.annotation.Listener;
import org.cometd.annotation.Service;
import org.cometd.annotation.Session;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.bayeux.server.ServerSession.RemoveListener;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import collabware.api.document.DocumentProvider;
import collabware.api.operations.ComplexOperation;
import collabware.collaboration.Collaboration;
import collabware.collaboration.CollaborationProvider;
import collabware.collaboration.Participant;
import collabware.collaboration.server.Server;
import collabware.collaboration.server.ServerEndpoint;
import collabware.collaboration.server.ServerEndpointListener;
import collabware.collaboration.server.ServerException;
import collabware.userManagement.User;
import collabware.userManagement.UserManagement;
import collabware.web.cometd.messages.ApplyChangeMessage;
import collabware.web.cometd.messages.ChangeRequest;
import collabware.web.cometd.messages.ChangeResponse;
import collabware.web.cometd.messages.ErrorResponse;
import collabware.web.cometd.messages.JoinRequest;
import collabware.web.cometd.messages.JoinResponse;
import collabware.web.cometd.messages.LeaveRequest;
import collabware.web.cometd.messages.ParticipantAddedResponse;
import collabware.web.cometd.messages.Response;

@Component
@Service
public class CometdProtocolEndpoint implements RemoveListener, InitializingBean {
	private static Logger LOGGER = LoggerFactory.getLogger(CometdProtocolEndpoint.class);
	
	private class CometdServerEndpoint implements ServerEndpoint{

		@Override
		public void addServerEndpointListener(ServerEndpointListener l) {
		}

		@Override
		public void initializeClient(String clientId, ComplexOperation op, List<Participant> participants, Participant localParticipant) {
			ServerSession remote = bayeux.getSession(clientId);
			LOGGER.info(String.format("Initialzing client '%s' on behalf of '%s' with %s.", clientId, localParticipant.getUser().getDisplayName(), op));
			JoinResponse reply = new JoinResponse(op, localParticipant, participants);
	    	remote.deliver(serverSession, "/initialize", reply.getPayload(), null);
		}

		@Override
		public void sendUpdate(String clientId, ComplexOperation change, int clientSequenceNumber, int serverSequenceNumber) {
			ServerSession remote = bayeux.getSession(clientId);
			if (remote != null) {
				ApplyChangeMessage applyChange = new ApplyChangeMessage(clientSequenceNumber, serverSequenceNumber, change);
				LOGGER.info(String.format("Sending out operation '%s' to client '%s'.", change, clientId));
				remote.deliver(serverSession, "/update", applyChange.getPayload(), null);
			} else {
				LOGGER.error(String.format("No such cometD server session for client with id %s.", clientId));
			}
		}

		@Override
		public void sendParticipantAdded(Collaboration collaboration, Participant addedParticipant) {
			String channelId = "/participantAdded/"+collaboration.getId();
			Response msg = new ParticipantAddedResponse(addedParticipant);
			ServerChannel channel = bayeux.getChannel(channelId);
			if (channel != null) {
				channel.publish(serverSession, msg.getPayload(), null);
			}
		}

		@Override
		public void initializeClient(String clientId, String errorMessage) {
			LOGGER.info(String.format("Error while initializing client %s: %s", clientId, errorMessage));
			ServerSession remote = bayeux.getSession(clientId);
			ErrorResponse reply = new ErrorResponse(errorMessage);
	    	remote.deliver(serverSession, "/initialize", reply.getPayload(), null);
		}

		@Override
		public void acknowledge(String clientId, int clientSequenceNumber, int serverSequenceNumber) {
			ServerSession remote = bayeux.getSession(clientId);
			Response r = new ChangeResponse(clientSequenceNumber, serverSequenceNumber);
			remote.deliver(serverSession, "/acknowledge", r.getPayload(), null);
		}
	}
	
	@javax.inject.Inject
    BayeuxServer bayeux;
	
    @Session
    ServerSession serverSession;
	
    CollaborationProvider collaborationProvider;
    
    @Autowired
    public void setCollaborationProvider(CollaborationProvider collaborationProvider) {
		this.collaborationProvider = collaborationProvider;
    }

	@Autowired
	DocumentProvider operationsProvider;
    
    @Autowired
    UserManagement userManagement;
    
    @Autowired
    SecurityContext securityContext;
    
    private Server server;
    
    @Override
	public void afterPropertiesSet() throws Exception {
		startServer();
		
	}

	@PostConstruct
    public void startServer() {
    	server = new Server(new CometdServerEndpoint(), collaborationProvider);
    }
    
	@Configure("/applyChange/*")
	public void configureSeverChange(ConfigurableServerChannel channel) {
		channel.setLazy(true);
		channel.addAuthorizer(GrantAuthorizer.GRANT_CREATE_SUBSCRIBE);
		channel.addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
	}
	
	@Configure("/participantAdded/*")
	public void configureparticipantAdded(ConfigurableServerChannel channel) {
		channel.setLazy(true);
		channel.addAuthorizer(GrantAuthorizer.GRANT_CREATE_SUBSCRIBE);
		channel.addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
	}

    @Configure("/service/clientChange/*")
    public void configureClientChange(ConfigurableServerChannel channel) {
    	channel.setLazy(true);
    	channel.addAuthorizer(GrantAuthorizer.GRANT_CREATE_SUBSCRIBE);
    	channel.addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
    }
    
    @Configure("/service/join/*")
    public void configureJoin(ConfigurableServerChannel channel) {
    	channel.setLazy(true);
    	channel.addAuthorizer(GrantAuthorizer.GRANT_CREATE_SUBSCRIBE);
    	channel.addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
    }
    
    @Configure("/service/leave/*")
    public void configureLeave(ConfigurableServerChannel channel) {
    	channel.setLazy(true);
    	channel.addAuthorizer(GrantAuthorizer.GRANT_CREATE_SUBSCRIBE);
    	channel.addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
    }
        
    @Listener("/service/join/*")
    public void join(ServerSession remote, ServerMessage.Mutable message) {
    	JoinRequest joinRequest = new JoinRequest(remote, message);
		LOGGER.info(String.format("Client %s on behalf of %s is requesting to join %s.",joinRequest.getRequesterId(), getCurrentParticipant(), joinRequest.getCollaborationId()));
		try {
			server.joinClient(joinRequest.getRequesterId(), getCurrentParticipant(), joinRequest.getCollaborationId());
			remote.addListener(this);
		} catch (ServerException e) {
			LOGGER.error(String.format("Joining collaboration has failed: %s",e.getMessage()));
		}
    }
    
    @Listener("/service/leave/*")
    public void leave(ServerSession remote, ServerMessage.Mutable message) {
    	LeaveRequest leaveRequest = new LeaveRequest(remote, message);
    	LOGGER.info(String.format("Client %s is leaving", leaveRequest.getRequesterId()));
		server.leave(leaveRequest.getRequesterId());
    }

	private Participant getCurrentParticipant() {
		return collaborationProvider.getParticipant(getCurrentUser());
	}

	private User getCurrentUser() {
		return (User)securityContext.getAuthentication().getPrincipal();
	}

    @Listener("/service/clientChange/*")
    public void applyClientChange(ServerSession remote, ServerMessage.Mutable message) {
		try {
			ChangeRequest change = new ChangeRequest(remote, message, this.operationsProvider);
			LOGGER.info(String.format("Applying change by client %s on behalf of %s to collaboration %s: %s", change.getRequesterId(), getCurrentParticipant(), change.getCollaborationId(), change.getOperation()));
			server.applyClientChange(change.getRequesterId(), change.getCollaborationId(), change.getOperation(), change.getClientSequenceNumber(), change.getServerSequenceNumber());
		} catch (Exception e) {
			LOGGER.error("Error while applying client change ",e);
			LOGGER.error("Disconnecting client");
			remote.disconnect();
	    }
    }

	public void removed(ServerSession remote, boolean timeout) {
		LOGGER.info(String.format("Client %s was removed", remote.getId()));
		server.leave(remote.getId());
	}
}
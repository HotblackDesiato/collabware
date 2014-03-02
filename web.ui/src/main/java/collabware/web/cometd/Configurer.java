package collabware.web.cometd;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.cometd.annotation.ServerAnnotationProcessor;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.server.BayeuxServerImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;


@Component
public class Configurer implements DestructionAwareBeanPostProcessor, ServletContextAware {
	private BayeuxServer bayeuxServer;
	private ServerAnnotationProcessor processor;

	@Autowired
	public void setBayeuxServer(BayeuxServer bayeuxServer) {
		this.bayeuxServer = bayeuxServer;
	}

	@PostConstruct
	public void init() {
		this.processor = new ServerAnnotationProcessor(bayeuxServer);
	}

	public Object postProcessBeforeInitialization(Object bean, String name)
			throws BeansException {
		if (processor == null) init();
		processor.processDependencies(bean);
		processor.processConfigurations(bean);
		processor.processCallbacks(bean);
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String name)
			throws BeansException {
		return bean;
	}

	public void postProcessBeforeDestruction(Object bean, String name)
			throws BeansException {
		processor.deprocessCallbacks(bean);
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public BayeuxServer bayeuxServer() {
		BayeuxServerImpl bean = new BayeuxServerImpl();
		return bean;
	}

	public void setServletContext(ServletContext servletContext) {
		servletContext.setAttribute(BayeuxServer.ATTRIBUTE, bayeuxServer);
	}
}
package collabware.web.resources;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import collabware.registry.EditorRegistry;

/**
 * Exposes the resources provided by editors via URLs
 */
@Controller
public class EditorResourceServlet {

	@Autowired
	private EditorRegistry editorRegistry;
	
	@RequestMapping(method=RequestMethod.GET, value="/{editorId}/**")
	protected void getEditorResource(@PathVariable("editorId") String editorId, HttpServletRequest request, HttpServletResponse resp) throws Exception {
		String path = (String) request.getAttribute(
		        HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		URL urlToResource = editorRegistry.getResource(editorId, path);
		if (urlToResource == null) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else {
			writeResource(urlToResource, resp);
		}

	}

	private void writeResource(URL urlToResource, HttpServletResponse resp) throws IOException {
		resp.setStatus(HttpServletResponse.SC_OK);
		String contentType = guessContentType(urlToResource);
		resp.setContentType(contentType);
		writeEncoded(resp, urlToResource);
	}

	private String guessContentType(URL urlToResource) {
		String fileName = urlToResource.getFile();
		if (fileName.endsWith("css"))
			return "text/css";
		if (fileName.endsWith("html"))
			return "text/html";
		if (fileName.endsWith("js"))
			return "application/javascript";
		if (fileName.endsWith("png"))
			return "image/png";
		String guessedContentType = URLConnection.guessContentTypeFromName(fileName);
		if (guessedContentType != null)
			return guessedContentType;
		return "text/plain";

	}

	private void writeEncoded(HttpServletResponse resp, URL urlToResource) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = urlToResource.openStream();
			out = resp.getOutputStream();
			byte[] buffer = new byte[2048];
			int bytesRead;    
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			close(in);
		}
	}

	private void close(Closeable stream) throws IOException {
		if (stream != null)
			stream.close();
	}


}

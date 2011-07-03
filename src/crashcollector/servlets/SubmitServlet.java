package crashcollector.servlets;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.google.appengine.api.datastore.Text;

import crashcollector.Application;
import crashcollector.data.*;

@SuppressWarnings("serial")
public class SubmitServlet extends HttpServlet {
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static final Logger log = Logger.getLogger(SubmitServlet.class.getName());
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Get the full XML so we can store it later on
		
		StringWriter writer = new StringWriter();
		IOUtils.copy(req.getInputStream(), writer);
		String xml = writer.toString();
		
		// Create a reader for it for the DocumentBuilder
		
		InputSource reader = new InputSource(new StringReader(xml));
		
		try {
			// Load the document
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document doc = db.parse(reader);
			
			Element root = doc.getDocumentElement();
			
			root.normalize();
			
			// Retrieve all parameters retrieved from the root object
			
			String value;
			
			String crashApplication = root.getAttribute("application");
			String crashApplicationVersion = root.getAttribute("applicationVersion");
			String crashUuid = root.getAttribute("uuid");
			String crashPlatform = root.getAttribute("platform");
			String crashOs = root.getAttribute("os");
			String crashOsVersion = root.getAttribute("osVersion");
			String crashCpu = root.getAttribute("cpu");
			String crashCpuInfo = root.getAttribute("cpuInfo");
			String crashReason = root.getAttribute("reason");
			String crashEmailAddress = root.getAttribute("emailAddress");
			String crashComments = root.getAttribute("comments");
			String crashThread = root.getAttribute("thread");
			Date crashInstallation = null;
			Date crashStartup = null;
			Date crashLastReport = null;
			
			value = root.getAttribute("installation");
			if (!StringUtils.isBlank(value))
				crashInstallation = dateFormat.parse(value);

			value = root.getAttribute("startup");
			if (!StringUtils.isBlank(value))
				crashStartup = dateFormat.parse(value);
				
			value = root.getAttribute("lastReport");
			if (!StringUtils.isBlank(value))
				crashLastReport = dateFormat.parse(value);
			
			// Validate all parameters retrieved from the root object
			
			if (
				StringUtils.isBlank(crashApplication) ||
				StringUtils.isBlank(crashApplicationVersion) ||
				StringUtils.isBlank(crashUuid) ||
				StringUtils.isBlank(crashPlatform) ||
				StringUtils.isBlank(crashOs) ||
				StringUtils.isBlank(crashOsVersion) ||
				StringUtils.isBlank(crashCpu) ||
				StringUtils.isBlank(crashCpuInfo) ||
				StringUtils.isBlank(crashReason) ||
				crashInstallation == null ||
				crashStartup == null
			) {
				throw new IllegalStateException("Invalid parameters");
			}
			
			// Get the exception resolver to get the rest of the details
			
			ExceptionResolver exceptionResolver = ExceptionResolver.getExceptionResolver(crashPlatform);
			
			Element exceptionElement = exceptionResolver.getExceptionElement(root);

			if (exceptionElement == null) {
				throw new IllegalStateException("Exception element not available");
			}
			
			// Get all parameters from the exception element
			
			String crashException = exceptionElement.getAttribute("exception");
			String crashModule = exceptionElement.getAttribute("module");
			// String crashModuleVersion = exceptionElement.getAttribute("moduleVersion");
			String crashMessage = exceptionElement.getAttribute("message");
			// String crashSource = exceptionElement.getAttribute("source");
			
			// Validate all parameters from the exception element
			
			if (
				StringUtils.isBlank(crashException) ||
				StringUtils.isBlank(crashModule) ||
				StringUtils.isBlank(crashMessage)
			) {
				throw new IllegalStateException("Invalid exception parameters");
			}
			
			// Write it all to the database
			
			PersistenceManager pm = Application.getPersistenceManager();
			
			try {
				// Get or create the crash request
				
				String signature = exceptionResolver.getSignature(exceptionElement);
				
				if (signature == null) {
					throw new IllegalStateException("Could not resolve signature");
				}
				
				Crash crash = getOrCreateCrash(pm, crashApplication, crashApplicationVersion, signature, crashPlatform);
				String stackTrace = exceptionResolver.getStackTrace(root);
				
				Report report = new Report(
					crash.getId(), crashUuid, new Date(), crashOs, crashOsVersion,
					crashCpu, crashCpuInfo, crashReason, req.getRemoteAddr(),
					crashEmailAddress, crashComments == null ? null : new Text(crashComments), crashModule,
					exceptionResolver.getFilename(exceptionElement),
					exceptionResolver.getFunction(exceptionElement),
					crashThread, stackTrace == null ? null : new Text(stackTrace),
					crashMessage, null, new Text(xml), crashInstallation, crashStartup,
					crashLastReport
				);
				
				pm.makePersistent(report);
			} finally {
				pm.close();
			}
		} catch (IllegalStateException ex) {
			log.log(Level.WARNING, ex.getMessage(), ex);
			resp.sendError(500, ex.getMessage());
		} catch (Exception ex) {
			log.log(Level.WARNING, ex.getMessage(), ex);
			resp.sendError(500, "Unable to process request");
		}
	}

	private Crash getOrCreateCrash(
			PersistenceManager pm, String crashApplication, String crashApplicationVersion,
			String signature, String crashPlatform) throws IllegalStateException
	{
		CrashRepository crashRepository = new CrashRepository(pm);
		ProductRepository productRepository = new ProductRepository(pm);
		VersionRepository versionRepository = new VersionRepository(pm);
		
		Date currentDate = new Date();
		
		// Ensure a correct product
		
		Product product = productRepository.getByUuid(crashApplication);
		
		if (product == null) {
			throw new IllegalStateException("Product not found");
		}
		
		// Ensure a correct version
		
		Version version = versionRepository.getByProductIdAndLabel(
			product.getId(), crashApplicationVersion
		);
		
		if (version == null) {
			if (Application.autoCreateVersion()) {
				version = new Version(
					product.getId(), currentDate, crashApplicationVersion
				);
				
				pm.makePersistent(version);
			} else {
				throw new IllegalStateException("Version not found");
			}
		}
		
		// Get or create a crash
		
		Transaction tx = pm.currentTransaction();
		Crash crash;
		
		try {
			tx.begin();
			
			crash = crashRepository.getMatchingCrash(
				product.getId(), version.getId(), signature, crashPlatform
			);
			
			if (crash == null) {
				crash = new Crash(
					product.getId(), version.getId(), currentDate, currentDate,
					signature, 1, null, crashPlatform
				);
			} else {
				crash.setHits(crash.getHits() + 1);
			}
			
			pm.makePersistent(crash);
			
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
		
		return crash;
	}
}

package crashcollector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.jdo.*;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.users.*;

public final class Application {
	private static final PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	private static final UserService us = UserServiceFactory.getUserService();
	
	private Application() {
	}
	
	public static PersistenceManagerFactory getPersistenceManagerFactor() {
		return pmf;
	}
	
	public static PersistenceManager getPersistenceManager() {
		return pmf.getPersistenceManager();
	}
	
	public static UserService getUserService() {
		return us;
	}
	
	public static User getCurrentUser() {
		return us.getCurrentUser();
	}
	
	public static void requireAdministrator() throws UnauthorizedAccessException {
		if (!isUserAdmin()) {
			throw new UnauthorizedAccessException();
		}
	}
	
	public static boolean isUserAdmin() {
		return us.isUserLoggedIn() && us.isUserAdmin();
	}
	
	public static String getGoogleAnalyticsKey() {
		return System.getProperty("crashcollector.google-analytics-key");
	}
	
	public static String getApplicationTitle() {
		return System.getProperty("crashcollector.application-title");
	}
	
	public static String getBugUrl() {
		return System.getProperty("crashcollector.bug-url");
	}
	
	public static boolean autoCreateVersion() {
		String value = System.getProperty("crashcollector.auto-create-version");
		
		if (!StringUtils.isBlank(value)) {
			return value.equalsIgnoreCase("true");
		}
		
		return false;
	}
	
	public static final DateFormat defaultDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}

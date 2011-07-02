package crashcollector.servlets;

import org.w3c.dom.Element;

import java.lang.IllegalStateException;

public abstract class ExceptionResolver {
	protected ExceptionResolver() {
	}
	
	public abstract String getSignature(Element exceptionElement);
	
	public abstract String getStackTrace(Element exceptionElement);
	
	public abstract Element getExceptionElement(Element root);

	public abstract String getFilename(Element exceptionElement);

	public abstract String getFunction(Element exceptionElement);
	
	public static ExceptionResolver getExceptionResolver(String platform) throws IllegalStateException {
		if (".net".equals(platform)) {
			return new ExceptionResolverDotNet();
		} else {
			throw new IllegalStateException("Unknown platform");
		}
	}
}

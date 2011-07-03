package crashcollector.servlets;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.*;

public class ExceptionResolverDotNet extends ExceptionResolver {
	@Override
	public String getSignature(Element exceptionElement) {
		String stackTrace = exceptionElement.getTextContent();
		
		if (stackTrace == null) {
			return null;
		}
		
		stackTrace = stackTrace.trim();
		
		int startPos = stackTrace.indexOf("at ");
		
		if (startPos == -1) {
			return null;
		}
		
		int endInPos = stackTrace.indexOf(" in ");
		int endLinePos = stackTrace.indexOf("\n");
		int endPos;
		
		if (endInPos == -1 && endLinePos == -1) {
			endPos = stackTrace.length();
		} else if (endInPos == -1) {
			endPos = endLinePos;
		} else if (endLinePos == -1) {
			endPos = endInPos;
		} else {
			endPos = Math.min(endLinePos, endInPos);
		}
		
		return stackTrace.substring(startPos + 3, endPos).trim();
	}

	@Override
	public String getStackTrace(Element root) {
		StringBuilder sb = new StringBuilder();
		NodeList nodes = root.getChildNodes();
		boolean hadOne = false;
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			
			if (node instanceof Element && "exception".equals(node.getNodeName())) {
				if (hadOne) {
					sb.append("\n=== Caused by ====\n\n");
				} else {
					hadOne = true;
				}
				
				Element element = (Element)node;
				
				sb.append(element.getAttribute("message"));
				sb.append(" (");
				sb.append(element.getAttribute("exception"));
				sb.append(")\n");
				
				String stackTrace = ((Element)node).getTextContent();
				
				if (!StringUtils.isBlank(stackTrace)) {
					// Convert all \r\n to \n
					
					stackTrace = stackTrace.replace("\r", "");
					
					// Trim the end
					
					stackTrace = stackTrace.replaceAll("\\s+$", "");
					
					sb.append("\n");
					sb.append(stackTrace);
					sb.append("\n");
				}
			}
		}
		
		return sb.toString();
	}
	
	@Override
	public Element getExceptionElement(Element root) {
		// Retrieve the first Exception element from the root element
		
		NodeList nodes = root.getChildNodes();
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			
			if (
				node instanceof Element &&
				"exception".equals(node.getNodeName()) &&
				!StringUtils.isBlank(node.getTextContent())
			) {
				return (Element)node;
			}
		}
		
		return null;
	}

	@Override
	public String getFilename(Element exceptionElement) {
		String stackTrace = getStackTrace(exceptionElement);
		
		if (stackTrace == null) {
			return null;
		}
		
		int endPos = stackTrace.indexOf("\n");
		int startPos = stackTrace.indexOf(" in ");
		
		if (endPos == -1 || startPos == -1 || startPos > endPos) {
			return null;
		}
		
		return stackTrace.substring(startPos + 4, endPos).trim();
	}

	@Override
	public String getFunction(Element exceptionElement) {
		return getSignature(exceptionElement);
	}
}

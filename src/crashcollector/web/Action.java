package crashcollector.web;

public class Action {
	private String label;
	private String action;
	
	public Action(String label, String action) {
		this.label = label;
		this.action = action;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getAction() {
		return action;
	}
}

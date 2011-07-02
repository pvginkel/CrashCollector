package crashcollector.data;

import javax.jdo.annotations.*;


@PersistenceCapable
public class Product extends Entity {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gea.pk-id", value="true")
	private Long id;
	
	@Persistent
	private String label;
	
	@Persistent
	private String uuid;
	
	public Product(String label, String uuid) {
		this.label = label;
		this.uuid = uuid;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}

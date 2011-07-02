package crashcollector.data;

import java.util.Date;
import javax.jdo.annotations.*;

@PersistenceCapable
public class Version extends Entity {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gea.pk-id", value="true")
	private Long id;
	
	@Persistent
	private Long productId;
	
	@Persistent
	private Date created;
	
	@Persistent
	private String label;
	
	public Version(Long productId, Date created, String label) {
		this.productId = productId;
		this.created = created;
		this.label = label;
	}
	
	public Long getId() {
		return id;
	}

	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
}

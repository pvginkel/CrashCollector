package crashcollector.data;

import java.util.Date;

import javax.jdo.annotations.*;

@PersistenceCapable
public class Crash extends Entity {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gea.pk-id", value="true")
	private Long id;
	
	@Persistent
	private Long productId;
	
	@Persistent
	private Long versionId;
	
	@Persistent
	private Date created;
	
	@Persistent
	private Date modified;
	
	@Persistent
	private String signature;
	
	@Persistent
	private Integer hits;
	
	@Persistent
	private String issues;
	
	@Persistent
	private String platform;
	
	public Crash(Long productId, Long versionId, Date created, Date modified, String signature, Integer hits, String issues, String platform)
	{
		this.productId = productId;
		this.versionId = versionId;
		this.created = created;
		this.modified = modified;
		this.signature = signature;
		this.hits = hits;
		this.issues = issues;
		this.platform = platform;
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
	
	public Long getVersionId() {
		return versionId;
	}
	
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getModified() {
		return modified;
	}
	
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public Integer getHits() {
		return hits;
	}
	
	public void setHits(Integer hits) {
		this.hits = hits;
	}
	
	public String getIssues() {
		return issues;
	}
	
	public void setIssues(String issues) {
		this.issues = issues;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPlatform() {
		return platform;
	}
}

package crashcollector.data;

import java.util.Date;
import javax.jdo.annotations.*;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class Report extends Entity {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gea.pk-id", value="true")
	private Long id;

	@Persistent
	private Long crashId;
	
	@Persistent
	private String uuid;
	
	@Persistent
	private Date created;
	
	@Persistent
	private String os;
	
	@Persistent
	private String osVersion;
	
	@Persistent
	private String cpu;
	
	@Persistent
	private String cpuInfo;
	
	@Persistent
	private String reason;
	
	@Persistent
	private String host;
	
	@Persistent
	private String email;
	
	@Persistent
	private Text comments;
	
	@Persistent
	private String module;
	
	@Persistent
	private String filename;
	
	@Persistent
	private String function;
	
	@Persistent
	private String crashedThread;
	
	@Persistent
	private Text stackTrace;
	
	@Persistent
	private String message;
	
	@Persistent
	private String extra;
	
	@Persistent
	private Text unparsedReport;
	
	@Persistent
	private Date installationDate;
	
	@Persistent
	private Date lastStartupDate;
	
	@Persistent
	private Date lastReportDate;
	
	public Report(Long crashId, String uuid, Date created, String os, String osVersion, String cpu,
			String cpuInfo, String reason, String host, String email, Text comments, String module,
			String filename, String function, String crashedThread, Text stackTrace, String message,
			String extra, Text unparsedReport, Date installationDate, Date lastStartupDate, Date lastReportDate) {
		this.setCrashId(crashId);
		this.uuid = uuid;
		this.created = created;
		this.os = os;
		this.osVersion = osVersion;
		this.cpu = cpu;
		this.cpuInfo = cpuInfo;
		this.reason = reason;
		this.host = host;
		this.email = email;
		this.comments = comments;
		this.module = module;
		this.filename = filename;
		this.function = function;
		this.crashedThread = crashedThread;
		this.stackTrace = stackTrace;
		this.message = message;
		this.extra = extra;
		this.unparsedReport = unparsedReport;
		this.installationDate = installationDate;
		this.lastStartupDate = lastStartupDate;
		this.lastReportDate = lastReportDate;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setCrashId(Long crashId) {
		this.crashId = crashId;
	}

	public Long getCrashId() {
		return crashId;
	}

	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public String getOs() {
		return os;
	}
	
	public void setOs(String os) {
		this.os = os;
	}
	
	public String getOsVersion() {
		return osVersion;
	}
	
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	
	public String getCpu() {
		return cpu;
	}
	
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	
	public String getCpuInfo() {
		return cpuInfo;
	}
	
	public void setCpuInfo(String cpuInfo) {
		this.cpuInfo = cpuInfo;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Text getComments() {
		return comments;
	}
	
	public void setComments(Text comments) {
		this.comments = comments;
	}
	
	public String getModule() {
		return module;
	}
	
	public void setModule(String module) {
		this.module = module;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFunction() {
		return function;
	}
	
	public void setFunction(String function) {
		this.function = function;
	}
	
	public String getCrashedThread() {
		return crashedThread;
	}
	
	public void setCrashedThread(String crashedThread) {
		this.crashedThread = crashedThread;
	}
	
	public Text getStackTrace() {
		return stackTrace;
	}
	
	public void setStackTrace(Text stackTrace) {
		this.stackTrace = stackTrace;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getExtra() {
		return extra;
	}
	
	public void setExtra(String extra) {
		this.extra = extra;
	}

	public void setUnparsedReport(Text unparsedReport) {
		this.unparsedReport = unparsedReport;
	}

	public Text getUnparsedReport() {
		return unparsedReport;
	}

	public void setInstallationDate(Date installationDate) {
		this.installationDate = installationDate;
	}

	public Date getInstallationDate() {
		return installationDate;
	}

	public void setLastStartupDate(Date lastStartupDate) {
		this.lastStartupDate = lastStartupDate;
	}

	public Date getLastStartupDate() {
		return lastStartupDate;
	}

	public void setLastReportDate(Date lastReportDate) {
		this.lastReportDate = lastReportDate;
	}

	public Date getLastReportDate() {
		return lastReportDate;
	}
}

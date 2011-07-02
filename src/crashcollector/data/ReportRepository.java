package crashcollector.data;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class ReportRepository extends Repository<Report> {
	public ReportRepository(PersistenceManager pm) {
		super(pm, Report.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Report> getAllByCrashId(Long crashId) {
		Query query = getDefaultQuery();
		
		query.setFilter("crashId == :p1");
		
		return (List<Report>)query.execute(crashId);
	}
}

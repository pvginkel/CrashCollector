package crashcollector.data;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class CrashRepository extends Repository<Crash> {
	public CrashRepository(PersistenceManager pm) {
		super(pm, Crash.class);
	}
	
	@Override
	protected Query getDefaultQuery() {
		Query query = super.getDefaultQuery();
		
		query.setOrdering("hits desc");
		
		return query;
	}
	
	@SuppressWarnings("unchecked")
	public List<Crash> getAllByVersionId(Long versionId, long offset, long limit) {
		Query query = getDefaultQuery();
		
		query.setRange(offset, limit);
		query.setFilter("versionId == :p1");
		
		return (List<Crash>)query.execute(versionId);
	}
	
	@SuppressWarnings("unchecked")
	public List<Crash> getAllByProductId(Long productId, long offset, long limit) {
		Query query = getDefaultQuery();
		
		query.setRange(offset, limit);
		query.setFilter("productId == :p1");
		
		return (List<Crash>)query.execute(productId);
	}
	
	@SuppressWarnings("unchecked")
	public List<Crash> getAll(long offset, long limit) {
		Query query = getDefaultQuery();
		
		query.setRange(offset, limit);
		
		return (List<Crash>)query.execute();
	}

	@SuppressWarnings("unchecked")
	public Crash getMatchingCrash(Long productId, Long versionId, String signature, String platform) {
		Query query = getQuery();
		
		query.setFilter("productId == :p1 && versionId == :p2 && signature == :p3 && platform == :p4");
		
		return getSingleOrDefault(
			(List<Crash>)query.executeWithArray(productId, versionId, signature, platform)
		);
	}
}

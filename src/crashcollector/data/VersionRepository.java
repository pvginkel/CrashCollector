package crashcollector.data;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class VersionRepository extends Repository<Version> {
	public VersionRepository(PersistenceManager pm) {
		super(pm, Version.class);
	}

	@Override
	protected Query getDefaultQuery() {
		Query query = super.getDefaultQuery();
		
		query.setOrdering("productId asc, created desc");
		
		return query;
	}
	
	@SuppressWarnings("unchecked")
	public List<Version> getAllByProductId(Long productId) {
		Query query = getDefaultQuery();
		
		query.setFilter("productId == :p1");
		
		return (List<Version>)query.execute(productId);
	}
	
	@SuppressWarnings("unchecked")
	public Version getByProductIdAndLabel(Long productId, String label) {
		Query query = getQuery();
		
		query.setFilter("productId == :p1 && label == :p2");
		
		return getSingleOrDefault((List<Version>)query.execute(productId, label));
	}
}

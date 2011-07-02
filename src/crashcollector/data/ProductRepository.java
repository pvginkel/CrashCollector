package crashcollector.data;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class ProductRepository extends Repository<Product> {
	public ProductRepository(PersistenceManager pm) {
		super(pm, Product.class);
	}
	
	@Override
	protected Query getDefaultQuery() {
		Query query = super.getDefaultQuery();
		
		query.setOrdering("label asc");
		
		return query;
	}
	
	@SuppressWarnings("unchecked")
	public Product getByLabel(String label) {
		Query query = getQuery();
		
		query.setFilter("label == :p1");
		
		return getSingleOrDefault((List<Product>)query.execute(label));
	}

	@SuppressWarnings("unchecked")
	public Product getByUuid(String uuid) {
		Query query = getQuery();
		
		query.setFilter("uuid == :p1");
		
		return getSingleOrDefault(
			(List<Product>)query.execute(uuid.toLowerCase())
		);
	}
}

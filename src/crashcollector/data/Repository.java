package crashcollector.data;

import java.util.*;
import javax.jdo.*;

public class Repository<T extends Entity> {
	private PersistenceManager pm;
	private Class<T> repositoryClass;
	
	protected Repository(PersistenceManager pm, Class<T> repositoryClass) {
		this.pm = pm;
		this.repositoryClass = repositoryClass;
	}
	
	public T getById(long id) {
		return (T)pm.getObjectById(repositoryClass, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		Query query = getDefaultQuery();
		
		return (List<T>)query.execute();
	}
	
	public Map<Long, T> getAllAsMap() {
		return convertToMap(getAll());
	}
	
	protected Query getQuery() {
		return pm.newQuery(repositoryClass);
	}
	
	protected Query getDefaultQuery() {
		return getQuery();
	}
	
	public Map<Long, T> convertToMap(List<T> entities) {
		Map<Long, T> result = new HashMap<Long, T>();
		
		for (T entity : entities) {
			result.put(entity.getId(), entity);
		}
		
		return result;
	}
	
	public void deleteById(Long id) {
		pm.deletePersistent(getById(id));
	}
	
	protected T getSingleOrDefault(List<T> entities) {
		if (entities.size() == 1) {
			return entities.get(0);
		} else {
			return null;
		}
	}
}

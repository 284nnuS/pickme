package tech.zoomidsoon.pickme_restful_api.mappers;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tech.zoomidsoon.pickme_restful_api.models.Entity;

// An abstract class to map SQL ResultSet to List of Object
public abstract class RowMapper<T extends Entity> {
	public List<T> processResultSet(ResultSet rs, Class<T> cls) throws SQLException {
		List<T> result = new ArrayList<T>();
		T obj = newGenericInstance(cls);

		while (rs.next()) {
			if (mapRow(rs, obj)) {
				if (obj != null && !obj.isEmpty())
					result.add(obj);
				obj = newGenericInstance(cls);
				rs.previous(); // Rollback previous row so when call next(), it won't change index
			}
		}

		if (obj != null && !obj.isEmpty())
			result.add(obj);

		return result;
	}

	// Create a new instance from empty constructor
	private T newGenericInstance(Class<T> cls) {
		try {
			return cls.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
		}
		return null;
	}

	// Return true to store the object of previous rows to list else continue
	public abstract boolean mapRow(ResultSet rs, T obj) throws SQLException;
}
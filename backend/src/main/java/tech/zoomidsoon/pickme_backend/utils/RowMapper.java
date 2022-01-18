package tech.zoomidsoon.pickme_backend.utils;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// An abstract class to map SQL ResultSet to List of Object
public abstract class RowMapper<T> {
	public List<T> processResultSet(ResultSet rs, Class<T> cls) throws Exception {
		List<T> result = new ArrayList<T>();
		T obj = newGenericInstance(cls);
		boolean first = true;
		boolean isNew = true;

		while (rs.next()) {
			if (isNew = mapRow(rs, obj, first, isNew)) {
				result.add(obj);
				obj = newGenericInstance(cls);
				if (!rs.previous()) // Rollback previous row so when call next(), it won't change index
					throw new Exception("Something wrong happened");
			}
			first = false;
		}

		if (!isNew)
			result.add(obj);
		return result;
	}

	// Create a new instance from empty constructor
	private T newGenericInstance(Class<T> cls) throws Exception {
		try {
			return cls.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new Exception("Failed to create a new instance instance from empty constructor");
		}
	}

	// Return true to store the object of previous rows to list else continue
	abstract boolean mapRow(ResultSet rs, T obj, boolean first, boolean isNew) throws SQLException;
}

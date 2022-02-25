package tech.zoomidsoon.pickme_restful_api.mappers;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tech.zoomidsoon.pickme_restful_api.models.Entity;

// An abstract class to map SQL ResultSet to List of Object
public abstract class RowMapper<E extends Entity> {
	public List<E> processResultSet(ResultSet rs, Class<E> cls) throws SQLException {
		List<E> result = new ArrayList<E>();
		E obj = newGenericInstance(cls);

		while (rs.next()) {
			Boolean next = mapRow(rs, obj);
			if (next == null || next) {
				if (obj != null && !obj.isEmpty())
					result.add(obj);
				obj = newGenericInstance(cls);
				if (next != null && next)
					rs.previous(); // Rollback previous to row so when call next(), it won't change index
			}
		}

		if (obj != null && !obj.isEmpty())
			result.add(obj);

		return result;
	}

	// Create a new instance from empty constructor
	private E newGenericInstance(Class<E> cls) {
		try {
			return cls.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
		}
		return null;
	}

	// - True: Store object and rollback to previous row
	// - False: Do nothing
	// - Null: Store object and NOT rollback to previous row
	public abstract Boolean mapRow(ResultSet rs, E obj) throws SQLException;
}
package tech.zoomidsoon.pickme_restful_api.repos;

import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;

public interface Repository<E> {
	E create(E entity);

	List<E> read(Criteria criteria);

	E update(E entity);

	E delete(E entity);

	List<E> readAll();

	public static interface Criteria {
		ResultSet query(Connection conn);
	}
}
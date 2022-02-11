package tech.zoomidsoon.pickme_restful_api.repos;

import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;

public interface Repository<E> {
	E create(E entity) throws Exception;

	List<E> read(Criteria criteria) throws Exception;

	E update(E entity) throws Exception;

	E delete(E entity) throws Exception;

	List<E> readAll() throws Exception;

	public static interface Criteria {
		ResultSet query(Connection conn) throws Exception;
	}
}
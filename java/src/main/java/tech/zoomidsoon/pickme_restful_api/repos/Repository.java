package tech.zoomidsoon.pickme_restful_api.repos;

import java.util.List;

import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.models.Entity;

import java.sql.ResultSet;
import java.sql.Connection;

public interface Repository<E extends Entity> {
	Result<E, JsonAPIResponse.Error> create(Connection conn, E entity) throws Exception;

	List<E> read(Connection conn, Criteria criteria) throws Exception;

	Result<E, JsonAPIResponse.Error> update(Connection conn, E entity) throws Exception;

	Result<E, JsonAPIResponse.Error> delete(Connection conn, E entity) throws Exception;

	List<E> readAll(Connection conn) throws Exception;

	static interface Criteria {
		ResultSet query(Connection conn) throws Exception;
	}
}
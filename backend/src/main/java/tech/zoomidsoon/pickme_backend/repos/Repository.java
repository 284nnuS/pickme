package tech.zoomidsoon.pickme_backend.repos;

import java.util.List;

public interface Repository<T, C> {
	T create(T entity);

	List<T> read(C criteria);

	T update(T entity);

	T delete(T entity);

	List<T> readAll();
}

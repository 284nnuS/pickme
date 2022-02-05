package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import tech.zoomidsoon.pickme_restful_api.models.User;

public class UserRepository implements Repository<User>{

	@Override
	public User create(User entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> read(Criteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User update(User entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User delete(User entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> readAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public static class FindById implements Criteria{
        
		
		@Override
		public ResultSet query(Connection conn) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}

import org.junit.jupiter.api.*;

import helpers.MockResultSet;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import tech.zoomidsoon.pickme_restful_api.mappers.UserRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.User;

class TestUser {
	@Test
	void testRowMapper() throws Exception {
		List<User> expected = Arrays.asList(new User[] {
				new User(1, "Gavin Anderson", "gavin@xyz.com", "admin", 'M', "", ""),
				new User(2, "Diana Clarkson", "diana@xyz.com", "mod", 'F', "", ""),
				new User(3, "Gordon Cameron", "gordon@xyz.com", "user", 'M', "", ""),
				new User(4, "Dorothy Chapman", "dorothy@xyz.com", "user", 'F', "", ""),
				new User(5, "Frank Davies", "frank@xyz.com", "user", 'O', "", ""),
				new User(6, "Emma Ellison", "emma@xyz.com", "user", 'O', "", ""),
		});

		String[] columnNames = new String[] { "userid", "role", "email", "name", "gender", "avatar", "bio" };

		MockResultSet mockResultSet = new MockResultSet(expected, columnNames);
		ResultSet rs = mockResultSet.build();
		List<User> actual = UserRowMapper.getInstance().processResultSet(rs, User.class);

		Assertions
				.assertEquals(expected, actual);
	}
}
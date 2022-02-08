import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import tech.zoomidsoon.pickme_restful_api.mappers.UserRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;
import utils.MockResultSet;

class TestUser {
	@Test
	void testRowMapper() throws Exception {
		List<User> examples = Arrays.asList(new User[] {
				new User(1, "Gavin Anderson", "gavin@xyz.com", "admin", 'M',
						"Elit ad ea ad enim fugiat dolore laboris fugiat culpa.", ""),
				new User(2, "Diana Clarkson", "diana@xyz.com", "mod", 'F',
						"Nulla fugiat tempor laboris aliquip tempor aute amet incididunt aliqua reprehenderit minim mollit in.",
						""),
				new User(3, "Gordon Cameron", "gordon@xyz.com", "user", 'M',
						"Aliqua eu ut voluptate magna proident officia et proident adipisicing Lorem dolore aliquip.", ""),
				new User(4, "Dorothy Chapman", "dorothy@xyz.com", "user", 'F',
						"Nulla excepteur amet reprehenderit ex exercitation labore exercitation velit qui ullamco excepteur nulla sunt.",
						""),
				new User(5, "Frank Davies", "frank@xyz.com", "user", 'O',
						"Pariatur duis laboris do enim dolore enim amet cillum aliqua pariatur nisi officia ad.", ""),
				new User(6, "Emma Ellison", "emma@xyz.com", "user", 'O',
						"Lorem dolor reprehenderit consectetur id elit culpa amet sunt anim consequat.", ""),
		});

		String[] columnNames = new String[] { "userId", "role", "email", "name", "gender", "avatar", "bio" };

		Object[][] values = examples.stream().map(el -> {
			try {
				return Utils.fieldsToArray(el, columnNames);
			} catch (Exception e) {
			}
			return null;
		}).toArray(Object[][]::new);

		MockResultSet mockResultSet = new MockResultSet(columnNames, values);

		ResultSet rs = mockResultSet.build();
		UserRowMapper urm = new UserRowMapper();
		List<User> users = urm.processResultSet(rs, User.class);

		assertTrue(examples.size() == users.size() && users.containsAll(examples) && examples.containsAll(users));
	}
}
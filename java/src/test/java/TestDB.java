
import org.junit.jupiter.api.*;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

class TestDB {

	@Test
	void testConnection() throws SQLException {
		DBContext.getConnection();
	}
}
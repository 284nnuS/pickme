import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

class TestDB {

	@Test
	void testConnection() throws SQLException {
		DBContext.getConnection();
	}
}
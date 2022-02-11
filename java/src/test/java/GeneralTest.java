import org.junit.jupiter.api.*;

import tech.zoomidsoon.pickme_restful_api.mappers.RowMapper;
import tech.zoomidsoon.pickme_restful_api.models.Entity;

import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.util.List;

public class GeneralTest {
	@Test
	void testRowMapperWithEmptyResultSet() throws Exception {
		ResultSet rs = mock(ResultSet.class);

		when(rs.next()).thenReturn(false);

		@SuppressWarnings("unchecked")
		List<Entity> result = mock(RowMapper.class).processResultSet(rs, Entity.class);

		Assertions.assertTrue(result.isEmpty());
	}
}

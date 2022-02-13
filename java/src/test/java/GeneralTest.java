import org.junit.jupiter.api.*;

import tech.zoomidsoon.pickme_restful_api.mappers.RowMapper;
import tech.zoomidsoon.pickme_restful_api.models.Entity;
import tech.zoomidsoon.pickme_restful_api.repos.UserRepository;
import tech.zoomidsoon.pickme_restful_api.repos.Repository.Criteria;

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

		Criteria c = new UserRepository.FindById(1);

		System.out.println(c.getClass().getNestHost().getSimpleName());

		Assertions.assertTrue(result.isEmpty());
	}
}

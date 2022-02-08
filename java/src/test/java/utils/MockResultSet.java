package utils;

import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockResultSet {
	private final Map<String, Integer> columnIndexes;
	private final Object[][] values;
	private int rowIndex = -1;

	public MockResultSet(final String[] columnName, final Object[][] values) {
		this.values = values;
		columnIndexes = IntStream.range(0, columnName.length).boxed()
				.collect(Collectors.toMap(i -> columnName[i], i -> i));
	}

	public ResultSet build() throws SQLException {
		ResultSet rs = mock(ResultSet.class);

		// rs.next()
		doAnswer(answer -> {
			rowIndex++;
			return rowIndex < values.length;
		}).when(rs).next();

		// rs.previous()
		doAnswer(answer -> {
			rowIndex--;
			return rowIndex >= 0;
		}).when(rs).previous();

		// rs.getString(String columnName)
		doAnswer(answer -> {
			final var columnName = answer.getArgumentAt(0, String.class);
			final var columnIndex = columnIndexes.get(columnName);
			return (String) values[rowIndex][columnIndex];
		}).when(rs).getString(anyString());

		// rs.getString(int columnIndex)
		doAnswer(answer -> {
			final var columnIndex = answer.getArgumentAt(0, Integer.class) - 1;
			return (String) values[rowIndex][columnIndex];
		}).when(rs).getString(anyInt());

		// rs.getInt(String columnName)
		doAnswer(answer -> {
			final var columnName = answer.getArgumentAt(0, String.class);
			final var columnIndex = columnIndexes.get(columnName);
			return (Integer) values[rowIndex][columnIndex];
		}).when(rs).getInt(anyString());

		// rs.getString(int columnIndex)
		doAnswer(answer -> {
			final var columnIndex = answer.getArgumentAt(0, Integer.class) - 1;
			return (Integer) values[rowIndex][columnIndex];
		}).when(rs).getInt(anyInt());

		// rs.getObject(String columnName)
		doAnswer(answer -> {
			final var columnName = answer.getArgumentAt(0, String.class);
			final var columnIndex = columnIndexes.get(columnName);
			return values[rowIndex][columnIndex];
		}).when(rs).getObject(anyString());

		// rs.getObject(int columnIndex)
		doAnswer(answer -> {
			final var columnIndex = answer.getArgumentAt(0, Integer.class) - 1;
			return values[rowIndex][columnIndex];
		}).when(rs).getObject(anyInt());

		return rs;
	}
}

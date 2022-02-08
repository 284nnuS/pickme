package utils;

import static org.mockito.Mockito.*;

import java.beans.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.stream.*;

public class MockResultSet {
	private final Map<String, Integer> columnIndexes;
	private final Object[][] values;
	private int rowIndex = -1;

	public <T> MockResultSet(final List<T> data, final String[] columnNames) {
		this.columnIndexes = IntStream.range(0,
				columnNames.length).boxed()
				.collect(Collectors.toMap(i -> columnNames[i].toLowerCase(), i -> i));

		this.values = data.stream().map(el -> fieldsToArray(el, columnNames, columnIndexes)).toArray(Object[][]::new);
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
			final var columnName = answer.getArgumentAt(0, String.class).toLowerCase();
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
			final var columnName = answer.getArgumentAt(0, String.class).toLowerCase();
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
			final var columnName = answer.getArgumentAt(0, String.class).toLowerCase();
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

	private static Object[] fieldsToArray(Object source, String[] fieldNames,
			Map<String, Integer> fieldIndexes) {
		BeanInfo beanInfo = null;

		try {
			beanInfo = Introspector.getBeanInfo(source.getClass());
		} catch (IntrospectionException e) {
			return null;
		}

		PropertyDescriptor[] pdList = beanInfo.getPropertyDescriptors();
		Object[] fieldArray = new Object[fieldNames.length];

		for (PropertyDescriptor pd : pdList) {
			Method readMethod = pd.getReadMethod();
			String name = pd.getName().toLowerCase();

			if (readMethod == null || !fieldIndexes.containsKey(name))
				continue;

			try {
				Object val = readMethod.invoke(source);
				int index = fieldIndexes.get(name);

				if (val instanceof Character)
					val = Character.toString((Character) val);
				fieldArray[index] = val;
			} catch (IllegalAccessException | InvocationTargetException e) {
			}
		}

		return fieldArray;
	}
}

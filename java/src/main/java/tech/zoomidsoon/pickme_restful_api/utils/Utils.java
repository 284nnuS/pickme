package tech.zoomidsoon.pickme_restful_api.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {
	public static void copyNonNullFields(Object dest, Object source, String... excludes)
			throws IntrospectionException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Set<String> excludeSet = Arrays.stream(excludes).map(el -> el.toLowerCase()).collect(Collectors.toSet());

		BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
		PropertyDescriptor[] pdList = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor pd : pdList) {
			Method writeMethod = pd.getWriteMethod();
			Method readMethod = pd.getReadMethod();

			if (readMethod == null || writeMethod == null) {
				continue;
			}

			Object val = readMethod.invoke(source);

			String fieldName = pd.getName().toLowerCase();

			if (val != null && !excludeSet.contains(fieldName))
				writeMethod.invoke(dest, val);
		}
	}

	public static String getEnv(String name, String defaultValue) {
		try {
			String value = System.getenv(name);
			if (value.isBlank())
				return defaultValue;
			return value;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static <T> boolean equalList(List<T> listone, List<T> listtwo) {
		return listone != null && listtwo != null && listone.size() == listtwo.size() && listone.containsAll(listtwo)
				&& listtwo.containsAll(listone);
	}

	public static <T> T getLastItem(List<T> list) {
		if (list == null || list.isEmpty())
			return null;
		return list.get(list.size() - 1);
	}

	public static <T> void diffList(List<T> oldList, List<T> newList, List<T> add, List<T> remove, List<T> merge) {
		if (oldList == null)
			throw new IllegalArgumentException("Old list cannot be null");

		if (newList != null && !newList.isEmpty())
			merge.addAll(newList);
		else if (!oldList.isEmpty())
			merge.addAll(oldList);

		if (newList == null)
			return;

		if (oldList.isEmpty()) {
			if (newList != null && !newList.isEmpty())
				add.addAll(newList);
			return;
		}

		if (newList != null && newList.isEmpty()) {
			if (!oldList.isEmpty())
				remove.addAll(oldList);
			return;
		}

		add.addAll(newList.stream().filter(el -> !oldList.contains(el)).collect(Collectors.toList()));
		remove.addAll(oldList.stream().filter(el -> !newList.contains(el)).collect(Collectors.toList()));
	}
}

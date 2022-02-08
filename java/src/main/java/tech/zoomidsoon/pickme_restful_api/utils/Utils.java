package tech.zoomidsoon.pickme_restful_api.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {

	public static void copyNonNullFields(Object dest, Object source)
			throws IntrospectionException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
		PropertyDescriptor[] pdList = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor pd : pdList) {
			Method writeMethod = pd.getWriteMethod();
			Method readMethod = pd.getReadMethod();

			if (readMethod == null || writeMethod == null) {
				continue;
			}

			Object val = readMethod.invoke(source);
			writeMethod.invoke(dest, val);
		}
	}

	public static Object[] fieldsToArray(Object source, String[] fieldNames)
			throws IntrospectionException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
		PropertyDescriptor[] pdList = beanInfo.getPropertyDescriptors();
		Object[] fieldArray = new Object[fieldNames.length];

		Map<String, Integer> fieldIndexes = IntStream.range(0,
				fieldNames.length).boxed()
				.collect(Collectors.toMap(i -> fieldNames[i], i -> i));

		System.out.println();

		for (PropertyDescriptor pd : pdList) {
			Method readMethod = pd.getReadMethod();
			String name = pd.getName();

			if (readMethod == null || !fieldIndexes.containsKey(name))
				continue;

			Object val = readMethod.invoke(source);
			int index = fieldIndexes.get(name);

			if (val instanceof Character)
				val = Character.toString((Character) val);
			fieldArray[index] = val;
		}
		return fieldArray;
	}
}

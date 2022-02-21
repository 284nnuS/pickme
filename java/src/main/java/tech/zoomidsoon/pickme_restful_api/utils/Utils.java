package tech.zoomidsoon.pickme_restful_api.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
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
}

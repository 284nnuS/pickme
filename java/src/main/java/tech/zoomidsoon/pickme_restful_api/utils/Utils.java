package tech.zoomidsoon.pickme_restful_api.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

package tech.zoomidsoon.pickme_restful_api.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Utils {

	void copyNonNullFields(Object dest, Object source)
			throws IntrospectionException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
		PropertyDescriptor[] pdList = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor pd : pdList) {
			Method writeMethod = null;
			Method readMethod = null;
			try {
				writeMethod = pd.getWriteMethod();
				readMethod = pd.getReadMethod();
			} catch (Exception e) {
			}

			if (readMethod == null || writeMethod == null) {
				continue;
			}

			Object val = readMethod.invoke(source);
			writeMethod.invoke(dest, val);
		}
	}
}

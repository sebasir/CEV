package net.hpclab.cev.services;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectRetriever implements Serializable {

	private static final long serialVersionUID = 18893253806111254L;
	private static final Logger LOGGER = Logger.getLogger(ObjectRetriever.class.getSimpleName());

	@SuppressWarnings("unchecked")
	public synchronized static <T> T getObjectFromId(Class<?> entityClass, Integer idObject) {
		try {
			DataWarehouse dwh = DataWarehouse.getInstance();
			Class<?> cls = dwh.getClass();
			Field[] fields = cls.getDeclaredFields();
			ParameterizedType subType;
			Object value = null;
			for (Field field : fields) {
				field.setAccessible(true);
				if ("java.util.List".equals(field.getType().getName())) {
					subType = (ParameterizedType) field.getGenericType();
					if (subType.getActualTypeArguments()[0].getTypeName().equals(entityClass.getName())) {
						value = field.get(dwh);
						return getValueFromList((List<T>) value, "id" + entityClass.getSimpleName());
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error extrayendo Objeto de DataWareHouse", e);
		}
		return null;
	}

	private synchronized static <T> T getValueFromList(List<T> list, String idObjectName) {
		Class<?> clsT = null;
		Field[] fields = null;
		for (T t : list) {
			clsT = t.getClass();
			fields = clsT.getDeclaredFields();
			for (Field field : fields) {
				if ("Integer".equals(field.getType().getSimpleName()) && field.getName().equals(idObjectName))
					return t;
			}
		}
		return null;
	}
}

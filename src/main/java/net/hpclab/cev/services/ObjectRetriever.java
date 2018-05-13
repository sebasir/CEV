/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */

package net.hpclab.cev.services;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio diseñado para recuperar un objeto de la lista de dominios cargados
 * en el servicio <tt>DataWarehouse</tt>, de manera que se pueda obtener dada la
 * indexación y tipo de clase a recuperar.
 * 
 * @since 1.0
 * @author Sebasir
 * @see DataWarehouse
 * 
 */
public class ObjectRetriever implements Serializable {

	private static final long serialVersionUID = 18893253806111254L;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(ObjectRetriever.class.getSimpleName());

	/**
	 * Función encargada de recuperar un objeto existente en los dominios del
	 * servicio de <tt>DataWarehouse</tt>.
	 * 
	 * @param <T>
	 *            El tipo de clase a recuperar de las listas de dominio
	 * @param entityClass
	 *            Tipo de clase a recuperar para comparar nombres
	 * @param idObject
	 *            Número de la llave primaria que identifica al objeto dentro de las
	 *            listas
	 * @return El objeto encontrado con la llave primaria, <tt>null</tt> si no lo
	 *         encuentra
	 */
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
						return getValueFromList((List<T>) value, idObject);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error extrayendo Objeto de DataWareHouse", e);
		}
		return null;
	}

	/**
	 * Función encargada de recuperar el valor desde la lista específica encontrada
	 * en los dominios
	 * 
	 * @param list
	 *            Lista de objetos que coinciden con el objeto a buscar.
	 * @param idObject
	 *            Número de la llave primaria que identifica al objeto dentro de las
	 *            listas
	 * @return El objeto encontrado con la llave primaria en la lista pasada en el
	 *         parámetro
	 * @throws IllegalArgumentException
	 *             Cuando el objeto tiene alguna equivocación recuperando los
	 *             parámetros de clase
	 * @throws IllegalAccessException
	 *             Cuando el objeto no tiene acceso a los parámetros de clase,
	 *             <tt>null</tt> si no lo encuentra
	 */
	private synchronized static <T> T getValueFromList(List<T> list, Integer idObject)
			throws IllegalArgumentException, IllegalAccessException {
		Class<?> clsT = null;
		Object value = null;
		Field[] fields = null;
		for (T t : list) {
			clsT = t.getClass();
			fields = clsT.getDeclaredFields();
			for (Field field : fields) {
				if ("Integer".equals(field.getType().getSimpleName()) && field.getName().startsWith("id")) {
					field.setAccessible(true);
					value = field.get(t);
					if (idObject.equals(value))
						return t;
				}
			}
		}
		return null;
	}
}

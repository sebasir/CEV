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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Es un servicio creado para la encapsulación del servicio de persistencia y
 * administración de conexiones a la base de datos, realizando operaciones de
 * consulta, creación, modificación y eliminación, de cualquier entidad que esté
 * atada al modelo de datos mapeados como entidades. Este servicio esta
 * soportado por el servicio de <tt>EntityResourcer</tt> el cual administra las
 * conexiones hacia la base de datos, y además permite centralizar las
 * particularidades del servicio al framework de JPA, extendido por Hibernate,
 * ambos usando las capacidades de transaccionabilidad del motro de JTA del
 * servidor de despliegue.
 *
 * @param <T>
 *            Tipo de clase la cual se extienden todas las operaciones
 *            disponibles desde el motor de persistencia
 * 
 * @since 1.0
 * @author Sebasir
 * 
 */

public class DataBaseService<T> implements Serializable {

	private static final long serialVersionUID = -2261108379261211921L;

	/**
	 * Objeto que soportará las conexiones y traducirá las peticiones en lenguaje
	 * SQL
	 */
	private EntityManager entityManager;

	/**
	 * Enumeración que realiza una diferenciación en el uso de tipos de consulta.
	 */
	private static enum QueryMethod {
		MAP, ENTITY, NAMED_QUERY, QUERY_MAP
	};

	/**
	 * Constante para la palabra clave de SQL
	 */
	private static final String SELECT = "SELECT";

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(DataBaseService.class.getSimpleName());

	/**
	 * Objeto de la sublclase que permite paginar un resultado según un ordenamiento
	 * básico
	 */
	private Pager pager;

	/**
	 * Objeto que permite diferenciar en distintas funciones la manera original de
	 * extracción de información
	 */
	private QueryMethod queryMethod;

	/**
	 * Página actual del conjunto de datos obtenido para una consulta
	 */
	private int currentPage;

	/**
	 * Cantidad de registros obtenido para una consulta
	 */
	private int numberOfResults;

	/**
	 * Máxima cantidad de registros disponibles para un conjunto de resultados
	 * paginados.
	 */
	private int queryMaxResults;

	/**
	 * Objeto de parámetro de tipo parametrizado para una consulta
	 */
	private T entityParam;

	/**
	 * Mapa de objetos indexados como filtros de una consulta
	 */
	private HashMap<String, Object> mapParam;

	/**
	 * Cadena de caracteres el cual contiene la consulta en lenguaje SQL
	 */
	private String queryParam;

	/**
	 * Clase abstracta del tipo de la parametrización
	 */
	protected Class<T> entityClass;

	/**
	 * Sesion de base de datos con el cual el motor de JPA conecta al DS
	 */
	private Session session;

	/**
	 * Transacción obtenida desde la sesión, donde se puede realizar confirmación o
	 * retroceso
	 */
	private Transaction tx;

	/**
	 * Constructor que define las propiedades del número máximo de resultados a
	 * obtener
	 * 
	 * @param entityClass
	 *            Clase abstracta de los resultados a obtener.
	 * @param queryMaxResults
	 *            Número máximo de resultados.
	 * @throws PersistenceException
	 *             En caso de existir un error de tipo de conexión de base de datos.
	 * @throws Exception
	 *             En caso de existir otro error
	 */
	public DataBaseService(Class<T> entityClass, int queryMaxResults) throws PersistenceException, Exception {
		this.entityClass = entityClass;
		this.queryMaxResults = queryMaxResults;
		numberOfResults = 0;
		this.pager = new Pager();
		getEntityManager();
	}

	/**
	 * Constructor que define las propiedades del básicas de una consulta de
	 * resultados ilimitados, desde el servicio de <tt>Constant</tt>
	 * 
	 * @throws PersistenceException
	 *             En caso de existir un error de tipo de conexión de base de datos.
	 * @throws Exception
	 *             En caso de existir otro error
	 */
	public DataBaseService() throws PersistenceException, Exception {
		this.queryMaxResults = Constant.UNLIMITED_QUERY_RESULTS;
		numberOfResults = 0;
		this.pager = new Pager();
		getEntityManager();
	}

	/**
	 * Constructor que define las propiedades del tipo de clase que se obtiene con
	 * una consulta
	 * 
	 * @param entityClass
	 *            Clase abstracta de los resultados a obtener.
	 * @throws PersistenceException
	 *             En caso de existir un error de tipo de conexión de base de datos.
	 * @throws Exception
	 *             En caso de existir otro error
	 */
	public DataBaseService(Class<T> entityClass) throws PersistenceException, Exception {
		this(entityClass, Constant.QUERY_MAX_RESULTS);
	}

	/**
	 * Función que permite consultar un resultado de una consulta, realizandola por
	 * defecto con parámetros de tipo mapa según se definen en la enumeración
	 * <tt>QueryMethod</tt>. Según se defina, el motor añade filtros, o si nó,
	 * realiza una consulta básica
	 * 
	 * @return Lista de resultados de tipo <tt>T</tt>.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	public List<T> getList() throws NoResultException, Exception {
		if (queryMethod == null) {
			queryMethod = QueryMethod.MAP;
		}
		switch (queryMethod) {
		case MAP:
			return getList(mapParam);
		case ENTITY:
			return getList(entityParam);
		case NAMED_QUERY:
			return getList(queryParam);
		case QUERY_MAP:
			return getList(queryParam, mapParam);
		default:
			return getList(new HashMap<String, Object>());
		}
	}

	/**
	 * Obtiene una página determinada por índice a obtener.
	 * 
	 * @param page
	 *            Índice de la página que se espera obtener.
	 * @return Lista de resultados de tipo <tt>T</tt>.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	public List<T> getList(int page) throws NoResultException, Exception {
		currentPage = page;
		return getList();
	}

	/**
	 * Función que permite obtener valores de un objeto parametrizado através de
	 * reflexión de clases, y estos aplicarlos a una consulta que se adjunta hacia
	 * la base de datos, para filtrar una búsqueda.
	 * 
	 * @param obj
	 *            Objeto del cual se extraen los valores de filtros.
	 * @return Un objeto que corresponde al valor del filtro.
	 * @throws Exception
	 *             Cuando se viola algún acceso o tipo de parámetro no existente.
	 */
	private Object getInnerValue(Object obj) throws Exception {
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.getType().getSimpleName().equals("Integer")) {
				return field.get(obj);
			}
		}
		return null;
	}

	/**
	 * Función que extrae de una entidad <tt>T</tt> los filtros necesarios para
	 * crear un mapa de parámetros, los cuales se adjuntan a una consulta hacia la
	 * base de datos.
	 * 
	 * @param entityFilters
	 *            Objeto de tipo <tt>T</tt> con los valores definidos para
	 *            traducirlos a filtros.
	 * @return Mapa de filtros y valores añadidos desde el objeto.
	 * @throws Exception
	 *             Cuando se viola algún acceso o tipo de parámetro no existente.
	 */
	private HashMap<String, Object> getFilterFromEntity(T entityFilters) throws Exception {
		HashMap<String, Object> filters = new HashMap<>();
		if (entityFilters != null) {
			Class<?> cls = entityFilters.getClass();
			Field[] fields = cls.getDeclaredFields();
			Object value;
			for (Field field : fields) {
				field.setAccessible(true);
				value = field.get(entityFilters);
				if (!field.getName().equals("serialVersionUID") && value != null) {
					if (value.getClass().getName().contains("hpclab")) {
						value = getInnerValue(value);
					}
					filters.put(field.getName(), value);
				}
			}
		}
		return filters;
	}

	/**
	 * Función que retorna una lista de resultados a partir de los filtros extraidos
	 * de una entidad de parámetro, haciendo uso de la reflexión de clases, la cual
	 * crea un mapa de parámetros
	 * 
	 * @param entityFilters
	 *            Objeto de tipo <tt>T</tt> con los valores definidos para
	 *            traducirlos a filtros.
	 * @return Lista de resultados de tipo <tt>T</tt>.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	public List<T> getList(T entityFilters) throws NoResultException, Exception {
		getEntityManager();
		restartFilters(QueryMethod.ENTITY);
		entityParam = entityFilters;
		HashMap<String, Object> filters = getFilterFromEntity(entityFilters);
		LOGGER.log(Level.INFO, "Filters: {0}", filters);
		return getList(filters);
	}

	/**
	 * Función que retorna una lista de resultados a partir de los filtros extraidos
	 * de una consulta escrita como cadena de texto en lenguaje SQL
	 * 
	 * @param query
	 *            Cadena con la sentencia SQL
	 * @return Lista de resultados de tipo <tt>T</tt>.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	public List<T> getList(String query) throws NoResultException, Exception {
		restartFilters(QueryMethod.NAMED_QUERY);
		queryParam = query;
		mapParam = null;
		return getList(query, null);
	}

	/**
	 * Función que retorna una lista de resultados a partir de los filtros extraidos
	 * de una consulta escrita como cadena de texto en lenguaje SQL, y
	 * adicionalmente un mapa de parámetros
	 * 
	 * @param query
	 *            Cadena con la sentencia SQL
	 * @param params
	 * @return Lista de resultados de tipo <tt>T</tt>.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	public List<T> getList(String query, HashMap<String, Object> params) throws NoResultException, Exception {
		restartFilters(QueryMethod.QUERY_MAP);
		queryParam = query;
		mapParam = params;
		LOGGER.log(Level.INFO, "Listing {0}, params: '{'{1}'}'",
				new Object[] { entityClass.getSimpleName(), params == null ? "N/A" : params.size() });
		TypedQuery<T> finalQuery;
		if (query.toLowerCase().contains(SELECT)) {
			finalQuery = entityManager.createQuery(query, entityClass);
		} else {
			finalQuery = entityManager.createNamedQuery(query, entityClass);
		}
		if (params != null) {
			for (String param : params.keySet()) {
				finalQuery.setParameter(param, params.get(param));
			}
		}
		List<T> result = getListOfResults(finalQuery);
		LOGGER.log(Level.INFO, "Listing {0}, OK", entityClass.getSimpleName());
		return result;
	}

	/**
	 * Función que retorna una lista de resultados a partir de un tipo de clase la
	 * cual se deriva como un resultado total, independientemente de cuantos
	 * registros se declaren en el contructor.
	 * 
	 * @param entityClass
	 *            Clase abtracta que acota el resultado de búsqueda al tipo de clase
	 *            a buscar.
	 * @return Lista de resultados de tipo <tt>T</tt>.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	public List<T> getList(Class<T> entityClass) throws NoResultException, Exception {
		LOGGER.log(Level.INFO, "Listing (Class<{0}>)", new Object[] { entityClass.getSimpleName() });
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		criteriaQuery.from(entityClass);
		List<T> result = entityManager.createQuery(criteriaQuery).getResultList();
		LOGGER.log(Level.INFO, "Listing {0}, OK", entityClass.getSimpleName());
		return result;
	}

	/**
	 * Función que permite crear un objeto tipo <tt>Query</tt> el cual se añaden
	 * parámetros y permite filtrar una consulta, desde un mapa de filtros
	 * 
	 * @param params
	 *            Mapa de filtros y valores
	 * @return Objeto <tt>Query</tt> con los parámetros traducidos, y listo para
	 *         ejecutar
	 */
	private CriteriaQuery<T> queryFromParams(HashMap<String, Object> params) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		Root<T> root = criteriaQuery.from(entityClass);

		if (params != null && !params.isEmpty()) {
			Predicate predicate = criteriaBuilder.conjunction();
			Predicate auxPredicate;
			Object value;
			Path<T> path;
			String[] attributes;
			for (String param : params.keySet()) {
				value = params.get(param);
				if (value == null || (value instanceof String && value.toString().isEmpty())) {
					continue;
				}
				path = null;
				attributes = param.split(Constant.POINT);
				for (String attribute : attributes) {
					if (path == null) {
						path = root.get(attribute);
					} else {
						path = path.get(attribute);
					}
				}
				if (value != null && !value.toString().isEmpty() && value instanceof String)
					auxPredicate = criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)),
							"%" + value.toString().toUpperCase() + "%");
				else
					auxPredicate = criteriaBuilder.equal(path, value);
				predicate = criteriaBuilder.and(predicate, auxPredicate);
			}
			criteriaQuery.select(root).where(predicate);
		}
		return criteriaQuery;
	}

	/**
	 * Función que retorna una lista de resultados a partir de los filtros extraidos
	 * de un mapa de parámetros
	 * 
	 * @param params
	 * @return Lista de resultados de tipo <tt>T</tt>.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	public List<T> getList(HashMap<String, Object> params) throws NoResultException, Exception {
		restartFilters(QueryMethod.MAP);
		mapParam = params;
		LOGGER.log(Level.INFO, "Listing (CriteriaQuery) {0}, params: '{'{1}'}'",
				new Object[] { entityClass.getSimpleName(), params == null ? "N/A" : params.size() });
		CriteriaQuery<T> criteriaQuery = queryFromParams(params);
		List<T> result = getListOfResults(entityManager.createQuery(criteriaQuery));
		LOGGER.log(Level.INFO, "Listing {0}, OK", entityClass.getSimpleName());
		return result;
	}

	/**
	 * Función que permite contar la cantidad de coincidencias que existen para un
	 * query obtenido, y usando el mapa de filtros definidos a nivel de clase
	 */
	public void getCount() {
		LOGGER.log(Level.INFO, "Counting {0}, OK", entityClass.getSimpleName());
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
		Root<T> root = countQuery.from(entityClass);
		countQuery.select(criteriaBuilder.count(root));
		if (mapParam != null && !mapParam.isEmpty()) {
			Predicate predicate = criteriaBuilder.conjunction();
			Predicate auxPredicate;
			Object value;
			Path<T> path;
			String[] attributes;
			for (String param : mapParam.keySet()) {
				value = mapParam.get(param);
				if (value == null || (value instanceof String && value.toString().isEmpty())) {
					continue;
				}
				path = null;
				attributes = param.split(Constant.POINT);
				for (String attribute : attributes) {
					if (path == null) {
						path = root.get(attribute);
					} else {
						path = path.get(attribute);
					}
				}
				auxPredicate = criteriaBuilder.equal(path, value);
				predicate = criteriaBuilder.and(predicate, auxPredicate);
			}
			countQuery.select(criteriaBuilder.count(root)).where(predicate);
		} else {
			countQuery.select(criteriaBuilder.count(root));
		}
		numberOfResults = entityManager.createQuery(countQuery).getSingleResult().intValue();
		LOGGER.log(Level.INFO, "Count {0}, OK", entityClass.getSimpleName());
	}

	/**
	 * @return El número real de resultados de una consulta.
	 */
	public int getActualNumberOfResults() {
		return numberOfResults;
	}

	/**
	 * @return El número de páginas que se encuentran para una consulta
	 */
	public int getNumberOfPages() {
		return queryMaxResults > 0 ? (int) Math.ceil((double) numberOfResults / queryMaxResults) : 1;
	}

	/**
	 * @return El número de la página actual de la paginación
	 */
	public int getActualCurrentPage() {
		return currentPage;
	}

	/**
	 * Función que retorna una lista de resultados a partir de un <tt>Query</tt>
	 * escrito en lenguaje SQL
	 * 
	 * @param query
	 *            Tipo de objeto que simboliza un <tt>Query</tt>.
	 * @return Lista de resultados de tipo <tt>T</tt>.
	 */
	private List<T> getListOfResults(TypedQuery<T> query) {
		getCount();
		if (currentPage > 0) {
			LOGGER.log(Level.INFO, "Page {0} of {1}", new Object[] { currentPage, getNumberOfPages() });
			query.setFirstResult(currentPage - 1);
		}
		return queryMaxResults > 0 ? query.setMaxResults(queryMaxResults).getResultList() : query.getResultList();
	}

	/**
	 * Función que retorna un resultado único para una consulta a partir de un
	 * objeto tipo <tt>Query</tt>, y un mapa de parámetros que se aplican sobre el
	 * objeto.
	 * 
	 * @param typedQuery
	 *            Objeto a parametrizar con el mapa
	 * @param params
	 *            Parámetros con filtros.
	 * @return Objeto de tipo <tt>T</tt> con el resultado de la búsqueda.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	private T getSingleRecord(TypedQuery<T> typedQuery, HashMap<String, Object> params)
			throws NoResultException, Exception {
		if (params != null) {
			for (String param : params.keySet()) {
				typedQuery.setParameter(param, params.get(param));
			}
		}
		T result = typedQuery.getSingleResult();
		LOGGER.log(Level.INFO, "GetSingleRecord {0}, OK", entityClass.getSimpleName());
		return result;
	}

	/**
	 * Función que retorna un resultado único para una consulta a partir de un
	 * objeto tipo <tt>T</tt> con los filtros definidos.
	 * 
	 * @param entityFilters
	 *            Objeto tipo <tt>T</tt> con todos los filtros aplicados.
	 * @return Objeto de tipo <tt>T</tt> con el resultado de la búsqueda.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	public T getSingleRecord(T entityFilters) throws NoResultException, Exception {
		ensureConnection();
		restartFilters(QueryMethod.ENTITY);
		entityParam = entityFilters;
		HashMap<String, Object> filters = getFilterFromEntity(entityFilters);
		LOGGER.log(Level.INFO, "Filters: {0}", filters);
		return getSingleRecord(filters);
	}

	/**
	 * Función que retorna un resultado único para una consulta a partir de un mapa
	 * de parámetros que se aplican sobre el objeto.
	 * 
	 * @param params
	 *            Parámetros con filtros.
	 * @return Objeto de tipo <tt>T</tt> con el resultado de la búsqueda.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	private T getSingleRecord(HashMap<String, Object> params) throws NoResultException, Exception {
		ensureConnection();
		restartFilters(QueryMethod.MAP);
		mapParam = params;
		LOGGER.log(Level.INFO, "GetSingleRecord (CriteriaQuery) {0}, params: '{'{1}'}'",
				new Object[] { entityClass.getSimpleName(), params == null ? "N/A" : params.size() });
		CriteriaQuery<T> criteriaQuery = queryFromParams(params);
		T result = entityManager.createQuery(criteriaQuery).getSingleResult();
		LOGGER.log(Level.INFO, "GetSingleRecord {0}, OK", entityClass.getSimpleName());
		return result;
	}

	/**
	 * Función que retorna un resultado único para una consulta a partir de un query
	 * en lenguaje SQL, y un mapa de parámetros que se aplican sobre la sentencia.
	 * 
	 * @param query
	 *            Sentencia con el Query a aplicar los filtros, y a ejecutar.
	 * @return Objeto de tipo <tt>T</tt> con el resultado de la búsqueda.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	public T getSingleRecord(String query, HashMap<String, Object> params) throws NoResultException, Exception {
		ensureConnection();
		LOGGER.log(Level.INFO, "GetSingleRecord {0}, params: '{'{1}'}'",
				new Object[] { entityClass.getSimpleName(), params == null ? "N/A" : params.size() });
		if (query.toLowerCase().contains(SELECT)) {
			return getSingleRecord(entityManager.createQuery(query, entityClass), params);
		} else {
			return getSingleRecord(entityManager.createNamedQuery(query, entityClass), params);
		}
	}

	/**
	 * Función que permite actualizar un objeto a partir de un query.
	 * 
	 * @param queryUpdate
	 *            Sentencia de actualización
	 * @param query
	 *            Sentencia de resultado una vez la actualización sea aplicada
	 * @param params
	 *            Mapa de parámetros con el cual se realiza la consulta.
	 * @return Objeto de tipo <tt>T</tt> con el resultado de la búsqueda.
	 * @throws NoResultException
	 *             Excepción específica de cuando no se encuentran resultados.
	 * @throws Exception
	 *             Cuando ocurre otro error
	 */
	public T mergeFromQuery(String queryUpdate, String query, HashMap<String, Object> params)
			throws NoResultException, Exception {
		LOGGER.log(Level.INFO, "Merging {0} from Query: {1}",
				new Object[] { entityClass.getSimpleName(), queryUpdate });
		Query typedQuery = entityManager.createQuery(queryUpdate);
		HashMap<String, Object> nParams = new HashMap<>();
		if (params != null) {
			for (String param : params.keySet()) {
				if (param.contains("new_")) {
					nParams.put(param.replaceAll("new_", ""), params.get(param));
				}
				typedQuery.setParameter(param, params.get(param));
			}
		}
		if (typedQuery.executeUpdate() == 1) {
			LOGGER.log(Level.INFO, "Merged {0}, OK", entityClass.getSimpleName());
			return getSingleRecord(query, nParams);
		} else {
			throw new PersistenceException("No entities Updated");
		}
	}

	/**
	 * Función que permite asegurar la conexión al administrador de persistencia y
	 * este a la base de datos.
	 * 
	 * @throws Exception
	 *             Cuando no es posible asegurar la conexión
	 */
	private void ensureConnection() throws Exception {
		if (!EntityResourcer.getInstance().isConnected())
			EntityResourcer.getInstance().initService();
		if (entityManager == null || !entityManager.isOpen())
			getEntityManager();
	}

	/**
	 * Función que permite la actualización de un objeto tipo <tt>T</tt>.
	 * 
	 * @param entity
	 *            Entidad a actualizar.
	 * @return El objeto actualizado
	 * @throws Exception
	 *             Cuando ocurre un error actualizando
	 */
	public T merge(T entity) throws Exception {
		try {
			ensureConnection();
			startTransaction();
			LOGGER.log(Level.INFO, "Merging {0}", entityClass.getSimpleName());
			entity = entityManager.merge(entity);
			entityManager.flush();
			commitTransaction();
			LOGGER.log(Level.INFO, "Merged {0}, OK", entityClass.getSimpleName());
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Merging {0}, Failure", entityClass.getSimpleName());
			LOGGER.log(Level.INFO, "Detail: ", e);
			rollBackTransaction();
			throw e;
		}
		return entity;
	}

	/**
	 * Función que permite la inserción de un objeto tipo <tt>T</tt>.
	 * 
	 * @param entity
	 *            Entidad a insertar.
	 * @return El objeto insertado con la llave primaria actualizada
	 * @throws Exception
	 *             Cuando ocurre un error insertando
	 */
	public T persist(T entity) throws Exception {
		try {
			ensureConnection();
			startTransaction();
			LOGGER.log(Level.INFO, "Persiting {0}", entityClass.getSimpleName());
			entity = entityManager.merge(entity);
			entityManager.flush();
			commitTransaction();
			LOGGER.log(Level.INFO, "Persist {0}, OK", entityClass.getSimpleName());
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Persisting {0}, Failure", entityClass.getSimpleName());
			LOGGER.log(Level.INFO, "Detail: ", e);
			rollBackTransaction();
			throw e;
		}
		return entity;
	}

	/**
	 * Función que permite eliminar una entidad de la base de datos
	 * 
	 * @param entity
	 *            Enitdad a eliminar
	 * @throws Exception
	 *             Cuando no es posible la eliminación
	 */
	public void delete(T entity) throws Exception {
		try {
			ensureConnection();
			startTransaction();
			LOGGER.log(Level.INFO, "Removing {0}", entityClass.getSimpleName());
			entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
			entityManager.flush();
			commitTransaction();
			LOGGER.log(Level.INFO, "Removed {0}, OK", entityClass.getSimpleName());
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Removing {0}, Failure", entityClass.getSimpleName());
			LOGGER.log(Level.INFO, "Detail: ", e);
			rollBackTransaction();
			throw e;
		}
	}

	/**
	 * Función que permite obtener una sesión de base de datos y de esta, generar
	 * una transacción
	 * 
	 * @throws Exception
	 *             Cuando no es posible obtener una sesión o abrir una transacción
	 *             nueva
	 */
	private void startTransaction() throws Exception {
		LOGGER.log(Level.INFO, "Starting transaction...");
		session = entityManager.unwrap(Session.class);
		session.setCacheMode(CacheMode.IGNORE);
		tx = session.beginTransaction();
	}

	/**
	 * Función que permite realizar confimación de los datos que han sido alterados
	 * 
	 * @throws Exception
	 *             Cuando no es posible confirmar la transacción o bien cerrarla
	 */
	private void commitTransaction() throws Exception {
		tx.commit();
		LOGGER.log(Level.INFO, "Commited...");
		close();
	}

	/**
	 * Función que permite realizar retroceder los datos que han sido alterados
	 * 
	 * @throws Exception
	 *             Cuando no es posible retroceder la transacción o bien cerrarla
	 */
	private void rollBackTransaction() throws Exception {
		tx.rollback();
		LOGGER.log(Level.INFO, "RolledBack...");
		close();
	}

	/**
	 * Función que a través del servicio <tt>EntityResourcer</tt> permite obtener
	 * una conexión a la base de datos.
	 * 
	 * @throws PersistenceException
	 *             Cuando existe un error de conexión a base de datos.
	 * @throws Exception
	 *             Cuando se presenta otro error.
	 */
	private void getEntityManager() throws PersistenceException, Exception {
		entityManager = EntityResourcer.getInstance().getEntityManager();
	}

	/**
	 * Función que permite desconectar una sesión y además permite desconectar un
	 * administrador de entidades.
	 */
	private void close() {
		session.disconnect();
		if (entityManager != null && entityManager.isOpen())
			entityManager.close();
		LOGGER.log(Level.INFO, "entityManager is closed");
	}

	/**
	 * @return Verifiación del estado de una conexión con el administrador de
	 *         entidades. <tt>true</tt> Si esta activa, <tt>false</tt> si no lo
	 *         está'.
	 * @throws Exception
	 */
	public boolean isConnected() throws Exception {
		return EntityResourcer.getInstance().isConnected();
	}

	/**
	 * Función que reinicia los tipos de filtros de la sesión de base de datos.
	 * 
	 * @param queryMethod
	 *            Identificador del tipo de consulta según la enumeración
	 *            <tt>QueryMethod</tt>.
	 */
	private void restartFilters(QueryMethod queryMethod) {
		if (this.queryMethod != queryMethod) {
			this.queryMethod = queryMethod;
			currentPage = 1;
			entityParam = null;
			queryParam = null;
			mapParam = null;
		}
	}

	public Pager getPager() {
		return pager;
	}

	public class Pager {

		public void firstPage() throws Exception {
			getPageResults(1);
		}

		public void lastPage() throws Exception {
			getPageResults(getNumberOfPages());
		}

		public void nextPage() throws Exception {
			if (getCurrentPage() + 1 <= getNumberOfPages()) {
				getPageResults(getCurrentPage() + 1);
			}
		}

		public void previousPage() throws Exception {
			if (getCurrentPage() - 1 > 0) {
				getPageResults(getCurrentPage() - 1);
			}
		}

		public List<T> getPageResults(int page) throws Exception {
			return getList(page);
		}

		public List<Integer> getPages() {
			ArrayList<Integer> pages = new ArrayList<>();
			int bottomIndex = getCurrentPage() - (Constant.MAX_PAGE_INDEX / 2);
			bottomIndex = bottomIndex <= 0 ? 1 : bottomIndex;
			int topIndex = bottomIndex + Constant.MAX_PAGE_INDEX;
			topIndex = topIndex > getNumberOfPages() ? getNumberOfPages() : topIndex;
			for (int i = bottomIndex; i <= topIndex; i++) {
				pages.add(i);
			}
			return pages;
		}

		public int getCurrentPage() {
			return getActualCurrentPage();
		}

		public int getNumberOfResults() {
			return getActualNumberOfResults();
		}
	}
}

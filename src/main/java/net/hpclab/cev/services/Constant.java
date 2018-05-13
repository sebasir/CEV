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

/**
 * Es un servicio creado para el manejo de constantes que se hacen extensivos a
 * través de la aplicación.
 * 
 * @since 1.0
 * @author Sebasir
 */

public class Constant {

	/**
	 * Enumeración local que funciona para administrar la diferencia del módulo de
	 * catálogos y colecciones
	 */
	public enum CollectionClassType {
		INSTITUTION, COLLECTION, CATALOG
	}

	/**
	 * Enumeración local que funciona para administrar el nivel de acceso básico que
	 * contiene algún módulo
	 */
	public enum AccessLevel {
		SELECT(1), INSERT(2), UPDATE(4), DELETE(8);

		private int level;

		private AccessLevel(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}
	}

	/**
	 * Constante para punto
	 */
	public static final String POINT = "\\.";

	/**
	 * Constante para terminación de LOG
	 */
	public static final String LOG = "_LOG";

	/**
	 * Constante para el nombre de la unidad de persistencia
	 */
	public static final String PERSISTENCE_UNIT = "CEV_PU";

	/**
	 * Constante para el juego de caracteres de la aplicación
	 */
	public static final String CHAR_ENCODING = "UTF-8";

	/**
	 * Constante para el algoritmo de encripción de las contraseñas de los usuarios
	 * del CEV
	 */
	public static final String HASH_ALGORITHM = "MD5";

	/**
	 * Constante para la llave de las contraseñas a almacenar.
	 */
	public static final String ENCRYPT_KEY = "_B1cH05";

	/**
	 * Constante para redirección de AJAX-XML
	 */
	public static final String AJAX_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><partial-response><redirect url=\"%s\"></redirect></partial-response>";

	/**
	 * Contante para la identificación de las variables de usuario en las variables
	 * de sesión.
	 */
	public static final String USER_DATA = "USER_DATA";

	/**
	 * Constante para la identificación de las variables de módulos de usuario en
	 * las variables de sesión
	 */
	public static final String USER_MODULES = "USER_MODULES";

	/**
	 * Constante para identificar la página de inicio en caso de redirección.
	 */
	public static final String LOGIN_PAGE = "/admin/login.xhtml";

	/**
	 * Constante para redireccionar sin tener en cuenta ciclo de vida JSF
	 */
	public static final String FACES_REDIRECT = "?faces-redirect=true";

	/**
	 * Constante para identificar la página principal en caso de redirección.
	 */
	public static final String MAIN_PAGE = "/";

	/**
	 * Constante para identificar la página de ingreso a la administración en caso
	 * de redirección.
	 */
	public static final String MAIN_ADMIN_PAGE = "/admin/index.xhtml";

	/**
	 * Constante para la comparación de cadenas de texto con calidad de correo
	 * electrónico.
	 */
	public static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

	/**
	 * Constante para la identificación de la ruta del archivo de propiedades del
	 * sistema.
	 */
	public static final String MESSAGES_FILE = "/WEB-INF/classes/META-INF/messages.properties";

	/**
	 * Constante para la identificación de comandos de CREACION
	 */
	public static final String CREATE_COMMAND = "create";

	/**
	 * Constante para la identificación de comandos de EDICION
	 */
	public static final String EDIT_COMMAND = "edit";

	/**
	 * Constante para la identificación de comandos de DETALLE
	 */
	public static final String DETAIL_COMMAND = "detail";

	/**
	 * Constante para la restauración de la contraseña base de un usuario
	 */
	public static final String RESTART_PASSWORD = "R3st4rt_p4ss";

	/**
	 * Constante para el nombre del archivo base de imagen sin imagen guardada.
	 */
	public static final String DEFAULT_SPECIMEN_IMAGE = "/images/utils/bug-silhouette-dark.png";

	/**
	 * Constante para identificar tipo de menú contextual sin menu de edición
	 */
	public static final String NO_MENU_TYPE = "noMenuType";

	/**
	 * Constante para identificar tipo de menú contextual con menu de edición
	 */
	public static final String MENU_TYPE = "menuType";

	/**
	 * Constante para el razón de aspecto nominal de una imagen cargada
	 */
	public static final double NOMINAL_ASPECT_RATIO = 4d / 3d;

	/**
	 * Constante para el rango de tolerancia de razón de aspecto de una imagen
	 * cargada
	 */
	public static final double TOLERANCE_RANGE_VALUE = 1d / 5d;

	/**
	 * Constante para la identificación de nodos en el arbol de colecciones para las
	 * Instituciones
	 */
	public static final int INSTITUTION_HINT = 10000;

	/**
	 * Constante para la identificación de nodos en el arbol de colecciones para las
	 * Colecciones
	 */
	public static final int COLLECTION_HINT = 20000;

	/**
	 * Constante para la identificación de nodos en el arbol de colecciones para los
	 * catálogos
	 */
	public static final int CATALOG_HINT = 30000;

	/**
	 * Constante para la máxima cantidad de registros recuperados
	 */
	public static final int QUERY_MAX_RESULTS = 10;

	/**
	 * Constante para recuperar registros ilimitados
	 */
	public static final int UNLIMITED_QUERY_RESULTS = -1;

	/**
	 * Constante para la máxima cantidad de registros de ejemplares mostrados en
	 * lista
	 */
	public static final int MAX_SPECIMEN_LIST = 6;

	/**
	 * Constante para la máxima cantidad de indices de páginas en el paginador
	 */
	public static final int MAX_PAGE_INDEX = 4;

	/**
	 * Constante para el nivel del nodo 0 de un árbol
	 */
	public static final int ROOT_LEVEL = 0;

	/**
	 * Constante para el nivel de una institución en el árbol
	 */
	public static final int INSTITUTION_LEVEL = 1;

	/**
	 * Constante para el nivel de una colección en el árbol
	 */
	public static final int COLLECTION_LEVEL = 2;

	/**
	 * Constante para el nivel de un catálogo en el árbol
	 */
	public static final int CATALOG_LEVEL = 3;

	/**
	 * Constante para el tiempo máximo de vida para una sesión en segundos de un
	 * usuario no loggeado
	 */
	public static final int MAX_IDLE_SESSION_NO_LOGGED = 5;

	/**
	 * Constante para el tiempo máximo de vida para una sesión en segundos de un
	 * usuario loggeado
	 */
	public static final int MAX_IDLE_SESSION_LOGGED_IN = 180;
}

package net.hpclab.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

public class Util implements Serializable {

    private static final long serialVersionUID = 1L;
    private static String[] dataBase;
    private static HashMap<String, String> entityNames;

    class Constant {

        public static final String URL = "hibernate.connection.url";
        public static final String DRIVER = "hibernate.connection.driver_class";
        public static final String USER = "hibernate.connection.username";
        public static final String PASS = "hibernate.connection.password";
        public static final String SHOW_SQL = "hibernate.show_sql";
        public static final String DB_REQ_MESS = "Se requiere el nombre de la base de datos a usar";
        public static final String POINT = "\\.";
        public static final String PERSISTENCE_UNIT = "CEV_PU_LOCAL";
        public static final int QUERY_MAX_RESULTS = 20;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isEmpty(Long number) {
        return number == null || number == 0;
    }

    public static boolean isEmpty(Integer number) {
        return number == null || number == 0;
    }

    public static boolean isEmpty(BigDecimal number) {
        return number == null || number.compareTo(BigDecimal.ZERO) == 0;
    }

    public static String[] getDataBase() {
        return dataBase;
    }

    public static void setDataBase(String[] dataBase) {
        Util.dataBase = dataBase;
    }
    
    public static String getEntityNames(String className) {
        return entityNames.get(className);
    }

    public static HashMap<String, String> getEntityNames() {
        return entityNames;
    }

    public static void setEntityNames(HashMap<String, String> aEntityNames) {
        entityNames = aEntityNames;
    }
}

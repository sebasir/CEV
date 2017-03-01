package net.hpclab.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import net.hpclab.entities.Institution;

public class Util implements Serializable {

    private static final long serialVersionUID = 1L;
    private static HashMap<String, String> entityNames;
    private static List<Institution> institutions;

    class Constant {

        public static final String POINT = "\\.";
        public static final String PERSISTENCE_UNIT = "CEV_PU_LOCAL";
        public static final String CHAR_ENCODING = "UTF-8";
        public static final String HASH_ALGORITHM = "MD5";
        public static final String ENCRYPT_KEY = "_B1cH05";
        public static final int QUERY_MAX_RESULTS = 20;
        public static final int ACCESS_LEVEL_SELECT = 1;
        public static final int ACCESS_LEVEL_UPDATE = 2;
        public static final int ACCESS_LEVEL_DELETE = 3;
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

    public static String encrypt(String pass) throws Exception {
        pass += Constant.ENCRYPT_KEY;
        MessageDigest md = MessageDigest.getInstance(Constant.HASH_ALGORITHM);
        md.update(pass.getBytes(Constant.CHAR_ENCODING));
        pass = DatatypeConverter.printBase64Binary(md.digest());
        return pass;
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

    public static List<Institution> getInstitutions() {
        return institutions;
    }

    public static void setInstitutions(List<Institution> aInstitutions) {
        institutions = aInstitutions;
    }
}

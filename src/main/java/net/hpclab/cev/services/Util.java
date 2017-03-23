package net.hpclab.cev.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import net.hpclab.cev.entities.Institution;
import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.enums.ModulesEnum;

public class Util implements Serializable {

    private static final long serialVersionUID = 1L;
    private static HashMap<String, String> entityNames;
    private static HashMap<ModulesEnum, Modules> modules;
    private static List<Institution> institutions;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(Constant.EMAIL_REGEX);

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

    public static boolean checkEmail(final String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static Modules getModule(ModulesEnum moduleName) {
        return modules.get(moduleName);
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

    public static HashMap<ModulesEnum, Modules> getModules() {
        return modules;
    }

    public static void setModules(HashMap<ModulesEnum, Modules> aModules) {
        modules = aModules;
    }

    public static List<Institution> getInstitutions() {
        return institutions;
    }

    public static void setInstitutions(List<Institution> aInstitutions) {
        institutions = aInstitutions;
    }
}

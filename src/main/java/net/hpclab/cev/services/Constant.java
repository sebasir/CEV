package net.hpclab.cev.services;

public class Constant {

    public static final String POINT = "\\.";
    public static final String LOG = "_LOG";
    public static final String PERSISTENCE_UNIT = "CEV_PU";
    public static final String CHAR_ENCODING = "UTF-8";
    public static final String HASH_ALGORITHM = "MD5";
    public static final String ENCRYPT_KEY = "_B1cH05";
    public static final String AJAX_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><partial-response><redirect url=\"%s\"></redirect></partial-response>";
    public static final String USER_DATA = "USER_DATA";
    public static final String LOGIN_PAGE = "/login.xhtml";
    public static final String FACES_REDIRECT = "?faces-redirect=true";
    public static final String MAIN_ADMIN_PAGE = "/admin/index.xhtml";
    public static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static final String MESSAGES_FILE = "/WEB-INF/classes/META-INF/messages.properties";
    public static final String CREATE_COMMAND = "create";
    public static final String EDIT_COMMAND = "edit";
    public static final String DETAIL_COMMAND = "detail";
    public static final int QUERY_MAX_RESULTS = 10;
    public static final int UNLIMITED_QUERY_RESULTS = -1;
    public static final int MAX_SPECIMEN_LIST = 6;
    public static final int MAX_PAGE_INDEX = 4;
    public static final int ACCESS_LEVEL_SELECT = 1;
    public static final int ACCESS_LEVEL_UPDATE = 2;
    public static final int ACCESS_LEVEL_DELETE = 3;
}

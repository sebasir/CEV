package net.hpclab.cev.enums;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

public class StatusEnumConverter implements UserType, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(StatusEnumConverter.class.getSimpleName());
    private static final int[] SQL_TYPES = new int[]{Types.OTHER};

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] strings, SessionImplementor si, Object o) throws HibernateException, SQLException {
        try {
            Object pgObject = rs.getObject(strings[0]);
            Method valueMethod = null;
            String value = null;
            try {
                valueMethod = pgObject.getClass().getMethod("getValue");
            } catch (NoSuchMethodException nsm) {
                try {
                    valueMethod = pgObject.getClass().getMethod("toString");
                } catch (NoSuchMethodException nsm2) {
                    value = "Activo";
                }
            }
            if (valueMethod != null) {
                value = (String) valueMethod.invoke(pgObject);
            }
            return StatusEnum.valueOf(value);
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error: {0}", ex.getMessage());
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement ps, Object o, int i, SessionImplementor si) throws HibernateException, SQLException {
        ps.setObject(i, o, SQL_TYPES[0]);
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        return ((StatusEnum) o).equals((StatusEnum) o1);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return Objects.hash(o);
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        return ((StatusEnum) o);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        return ((Serializable) o);
    }

    @Override
    public Object assemble(Serializable srlzbl, Object o) throws HibernateException {
        return srlzbl;
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        return o;
    }

    @Override
    public Class returnedClass() {
        return StatusEnum.class;
    }
}

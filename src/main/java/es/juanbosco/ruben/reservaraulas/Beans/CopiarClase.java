package es.juanbosco.ruben.reservaraulas.Beans;

import org.apache.commons.beanutils.BeanUtilsBean;
import java.lang.reflect.InvocationTargetException;

public class CopiarClase extends BeanUtilsBean {
    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {

        if (value != null) { // sólo copia si no es null
            super.copyProperty(dest, name, value);
        }
    }
}

package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class SpecimenBean extends UtilsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SpecimenBean.class.getSimpleName());

    @PostConstruct
    public void init() {

    }
}

package net.hpclab.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import net.hpclab.entities.SampleType;

@ManagedBean
@SessionScoped
public class SampleTypeBean extends UtilsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private SampleType sampleType;
    private List<SampleType> allSampleTypes;

    public SampleTypeBean() {
    }

    @PostConstruct
    public void init() {
    }

    public String persist() {
        try {
            //setSampleType(sampleTypeSession.persist(getSampleType()));
            if (getSampleType() != null && getSampleType().getIdSaty() != null) {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(sampleType, Operations.CREATE_SUCCESS));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(sampleType, Operations.CREATE_ERROR));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(sampleType, Operations.CREATE_ERROR));
        }

        return findAllSampleTypes();
    }

    public void delete() {
        try {
            //sampleTypeSession.delete(getSampleType());
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getSampleType(), Operations.DELETE_SUCCESS));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getSampleType(), Operations.DELETE_ERROR));
        }
    }

    public void prepareCreate() {
        setSampleType(new SampleType());
    }

    public void edit() {
        try {
            //setSampleType(sampleTypeSession.merge(getSampleType()));
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getSampleType(), Operations.UPDATE_SUCCESS));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getSampleType(), Operations.UPDATE_ERROR));
        }
    }

    public String displayList() {
        findAllSampleTypes();
        return "specimen";
    }

    public String findAllSampleTypes() {
        //setAllSampleTypes(sampleTypeSession.listAll());
        return null;
    }

    public SampleType getSampleType() {
        return sampleType;
    }

    public void setSampleType(SampleType sampleType) {
        this.sampleType = sampleType;
    }

    public List<SampleType> getAllSampleTypes() {
        return allSampleTypes;
    }

    public void setAllSampleTypes(List<SampleType> allSampleTypes) {
        this.allSampleTypes = allSampleTypes;
    }
}

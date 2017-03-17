package net.hpclab.cev.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.util.Calendar;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.entities.SpecimenContent;
import net.hpclab.cev.entities.Taxonomy;
import net.hpclab.cev.enums.OutcomeEnum;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@Named
@SessionScoped
public class SpecimenContentBean extends UtilsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean publish;
    private Specimen specimen;
    private SpecimenContent specimenContent;
    private String selectedSpecimen;
    private String specimenDetail;
    private UploadedFile contentFile;

    public SpecimenContentBean() {
    }

    @PostConstruct
    public void init() {
        System.out.println("Inicializando lista 'SpecimenContents'");
        if (allSpecimenContents == null) {
            allSpecimenContents = new ArrayList<>();
        }
    }

    public void persist() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            if (contentFile != null) {
                specimenContent.setPublish(publish ? 'S' : 'N');
                if (publish) {
                    specimenContent.setPublishDate(Calendar.getInstance().getTime());
                }
                specimenContent.setFileName(contentFile.getFileName());
                specimenContent.setFileUploadDate(Calendar.getInstance().getTime());
                specimenContent.setIdSpecimen(getSpecimen(selectedSpecimen));
                specimenContent.setFileName(contentFile.getFileName());
                specimenContent.setFileContent(getByteArray(contentFile.getInputstream()));
                //setSpecimenContent(specimenContentSession.persist(getSpecimenContent()));
                if (getSpecimenContent() != null && getSpecimenContent().getIdSpeccont() != null) {
                    showMessage(facesContext, OutcomeEnum.CREATE_SUCCESS, null);
                } else {
                    showMessage(facesContext, OutcomeEnum.CREATE_ERROR, null);
                }
            } else {
                showFileMessage(facesContext, OutcomeEnum.FILE_REQUIRED, "de imagen", "No hay archivo!");
            }
        } catch (IOException e) {
            showFileMessage(facesContext, OutcomeEnum.FILE_UPLOAD_ERROR, "de imagen", e.getMessage());
        }
    }

    public void delete() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            //specimenContentSession.delete(getSpecimenContent());
            showMessage(facesContext, OutcomeEnum.DELETE_SUCCESS, null);
        } catch (Exception e) {
            showMessage(facesContext, OutcomeEnum.DELETE_ERROR, e.getMessage());
        }
    }

    public void prepareCreate() {
        setSpecimenContent(new SpecimenContent());
        selectedSpecimen = null;
        contentFile = null;
        publish = false;
    }

    public void prepareUpdate(SpecimenContent specimenContent) {
        setSpecimenContent(specimenContent);
        publish = specimenContent.getPublish() == 'S';
        selectedSpecimen = specimenContent.getIdSpecimen().getIdSpecimen().toString();
    }

    public void prepareDelete(SpecimenContent specimenContent) {
        setSpecimenContent(specimenContent);
    }

    public void edit() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            //setSpecimenContent(specimenContentSession.merge(getSpecimenContent()));
            showMessage(facesContext, OutcomeEnum.UPDATE_SUCCESS, null);
        } catch (Exception e) {
            showMessage(facesContext, OutcomeEnum.UPDATE_ERROR, null);
        }
    }

    private Specimen getSpecimen(String id) {
        Integer idSpecimen;
        try {
            idSpecimen = new Integer(id);
            for (Specimen s : allSpecimens) {
                if (s.getIdSpecimen().equals(idSpecimen)) {
                    return s;
                }
            }
        } catch (NumberFormatException e) {

        }
        return null;
    }

    public List<Taxonomy> getFamilies() {
        ArrayList<Integer> families = new ArrayList<>();
        ArrayList<Taxonomy> familiesWithContent = new ArrayList<>();
        //allTaxonomys = (List<Taxonomy>) specimenContentSession.findListByQuery("Taxonomy.findAll", Taxonomy.class);
        allTaxonomys = new ArrayList<>();
        for (Taxonomy t : allTaxonomys) {
            if (t.getIdTaxlevel().getTaxlevelRank() == 12) {
                families.add(t.getIdTaxonomy());
            }
        }
        for (SpecimenContent s : allSpecimenContents) {
            for (Integer t : families) {
                if (isFather(t, s.getIdSpecimen())) {
                    familiesWithContent.add(s.getIdSpecimen().getIdTaxonomy());
                }
            }
        }
        return familiesWithContent;
    }

    public StreamedContent getSpecimenThumb() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {
            String id = context.getExternalContext().getRequestParameterMap().get("idSpecimen");
            Integer idSpecimen = Integer.parseInt(id);
            for (SpecimenContent s : allSpecimenContents) {
                if (s.getIdSpecimen().getIdSpecimen().equals(idSpecimen)) {
                    return new DefaultStreamedContent(super.getInputStream(s.getFileContent()), "image/jpeg");
                }
            }
            return null;
        }
    }

    public StreamedContent getFamilyThumb() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {
            String id = context.getExternalContext().getRequestParameterMap().get("idTaxonomy");
            Integer idTaxonomy = Integer.parseInt(id);
            for (SpecimenContent s : allSpecimenContents) {
                if (isFather(idTaxonomy, s.getIdSpecimen())) {
                    return new DefaultStreamedContent(super.getInputStream(s.getFileContent()), "image/jpeg");
                }
            }
            return null;
        }
    }

    private boolean isFather(Integer f, Specimen s) {
        Taxonomy t = s.getIdTaxonomy();
        while (t != null) {
            if (f.equals(t.getIdTaxonomy())) {
                return true;
            }
            t = t.getIdContainer();
        }
        return false;
    }

    public SpecimenContent getSpecimenContent() {
        return specimenContent;
    }

    public void setSpecimenContent(SpecimenContent specimenContent) {
        this.specimenContent = specimenContent;
    }

    public List<SpecimenContent> getAllSpecimenContents() {
        return allSpecimenContents;
    }

    public List<Specimen> getAllSpecimens() {
        return allSpecimens;
    }

    public String getSelectedSpecimen() {
        return selectedSpecimen;
    }

    public void setSelectedSpecimen(String selectedSpecimen) {
        this.selectedSpecimen = selectedSpecimen;
    }

    private void setSpecimenfromId() {
        this.specimen = getSpecimen(specimenDetail);
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public UploadedFile getContentFile() {
        return contentFile;
    }

    public void setContentFile(UploadedFile contentFile) {
        this.contentFile = contentFile;
    }

    public String getHeader() {
        return specimenContent != null && specimenContent.getIdSpeccont() != null ? "Editar el contenido de " + specimenContent.getIdSpecimen().getIdTaxonomy().getTaxonomyName() + " " + specimenContent.getIdSpecimen().getSpecificEpithet() : "Nuevo contenido";
    }

    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    public String getSpecimenDetail() {
        return specimenDetail;
    }

    public void setSpecimenDetail(String specimenDetail) {
        this.specimenDetail = specimenDetail;
        setSpecimenfromId();
    }
}

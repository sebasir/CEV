package net.hpclab.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import net.hpclab.entities.Specimen;
import net.hpclab.entities.SpecimenContent;
import java.util.Calendar;
import javax.faces.event.PhaseId;
import net.hpclab.entities.Taxonomy;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@ManagedBean
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
            allSpecimenContents = new ArrayList<SpecimenContent>();
        }
    }

    public void persist() {
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
                    FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.createSuccess));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.createError));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.fileRequired));
            }
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, showFileMessage(contentFile.getFileName(), Actions.fileUploadError));
        }
    }

    public void delete() {
        try {
            //specimenContentSession.delete(getSpecimenContent());
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.deleteSuccess));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.deleteError));
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
        try {
            //setSpecimenContent(specimenContentSession.merge(getSpecimenContent()));
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.updateSuccess));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.updateError));
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
        ArrayList<Integer> families = new ArrayList<Integer>();
        ArrayList<Taxonomy> familiesWithContent = new ArrayList<Taxonomy>();
        //allTaxonomys = (List<Taxonomy>) specimenContentSession.findListByQuery("Taxonomy.findAll", Taxonomy.class);
        allTaxonomys = new ArrayList<Taxonomy>();
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
            if (f == t.getIdTaxonomy()) {
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

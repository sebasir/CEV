package net.hpclab.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.Specimen;
import net.hpclab.entities.SpecimenContent;
import net.hpclab.sessions.SpecimenContentSession;
import java.util.Calendar;
import javax.faces.event.PhaseId;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@SessionScoped
public class SpecimenContentBean extends Utilsbean implements Serializable {

    @Inject
    private SpecimenContentSession specimenContentSession;

    private static final long serialVersionUID = 1L;
    private SpecimenContent specimenContent;
    private List<SpecimenContent> allSpecimenContents;
    private List<Specimen> allSpecimens;
    private String selectedSpecimen;
    private boolean publish;
    private UploadedFile contentFile;

    public SpecimenContentBean() {
	   specimenContentSession = new SpecimenContentSession();
    }

    @PostConstruct
    public void init() {
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
			 String fileName = contentFile.getFileName();
			 specimenContent.setFileName(fileName);
			 String ext = fileName.substring(fileName.lastIndexOf("."));
			 if (copyFile(selectedSpecimen + ext, contentFile.getInputstream())) {
				setSpecimenContent(specimenContentSession.persist(getSpecimenContent()));
				if (getSpecimenContent() != null && getSpecimenContent().getIdSpeccont() != null) {
				    FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.createSuccess));
				} else {
				    FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.createError));
				}
			 } else {
				FacesContext.getCurrentInstance().addMessage(null, showFileMessage(contentFile.getFileName(), Actions.fileUploadError));
			 }
		  } else {
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.fileRequired));
		  }
	   } catch (IOException e) {
		  FacesContext.getCurrentInstance().addMessage(null, showFileMessage(contentFile.getFileName(), Actions.fileUploadError));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.createError));
	   }
    }

    public void delete() {
	   try {
		  String fileName = specimenContent.getFileName();
		  fileName = fileName.substring(fileName.lastIndexOf("."));
		  fileName = specimenContent.getIdSpecimen().getIdSpecimen() + fileName;
		  if (deleteFile(fileName)) {
			 specimenContentSession.delete(getSpecimenContent());
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.deleteSuccess));
		  } else {
			 FacesContext.getCurrentInstance().addMessage(null, showFileMessage(specimenContent.getFileName(), Actions.fileDeleteError));
		  }
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.deleteError));
	   }
    }

    public void prepareCreate() {
	   setSpecimenContent(new SpecimenContent());
	   selectedSpecimen = null;
	   contentFile = null;
	   publish = false;
	   setAllSpecimens((List<Specimen>) specimenContentSession.findListByQuery("Specimen.findAll", Specimen.class));
    }

    public void prepareUpdate(SpecimenContent specimenContent) {
	   setAllSpecimens((List<Specimen>) specimenContentSession.findListByQuery("Specimen.findAll", Specimen.class));
	   setSpecimenContent(specimenContent);
	   publish = specimenContent.getPublish() == 'S';
	   selectedSpecimen = specimenContent.getIdSpecimen().getIdSpecimen().toString();
    }

    public void prepareDelete(SpecimenContent specimenContent) {
	   setSpecimenContent(specimenContent);
    }

    public void edit() {
	   try {
		  setSpecimenContent(specimenContentSession.merge(getSpecimenContent()));
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.updateSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getSpecimenContent(), Actions.updateError));
	   }
    }

    private Specimen getSpecimen(String id) {
	   Integer idSpecimen = null;
	   try {
		  idSpecimen = new Integer(id);
		  for (Specimen s : allSpecimens) {
			 if (s.getIdSpecimen().equals(idSpecimen)) {
				return s;
			 }
		  }
	   } catch (Exception e) {

	   }
	   return null;
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
				String fileName = s.getFileName();
				fileName = fileName.substring(fileName.lastIndexOf("."));
				return new DefaultStreamedContent(super.getInputStream(idSpecimen + fileName), "image/jpeg");
			 }
		  }
		  return null;
	   }
    }

    public void findAllSpecimenContent() {
	   setAllSpecimenContents(specimenContentSession.listAll());
    }

    public SpecimenContent getSpecimenContent() {
	   return specimenContent;
    }

    public void setSpecimenContent(SpecimenContent specimenContent) {
	   this.specimenContent = specimenContent;
    }

    public List<SpecimenContent> getAllSpecimenContents() {
	   findAllSpecimenContent();
	   return allSpecimenContents;
    }

    public void setAllSpecimenContents(List<SpecimenContent> allSpecimenContents) {
	   this.allSpecimenContents = allSpecimenContents;
    }

    public List<Specimen> getAllSpecimens() {
	   return allSpecimens;
    }

    public void setAllSpecimens(List<Specimen> allSpecimens) {
	   this.allSpecimens = allSpecimens;
    }

    public String getSelectedSpecimen() {
	   return selectedSpecimen;
    }

    public void setSelectedSpecimen(String selectedSpecimen) {
	   this.selectedSpecimen = selectedSpecimen;
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
}

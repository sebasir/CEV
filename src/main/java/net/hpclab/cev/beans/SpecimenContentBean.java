package net.hpclab.cev.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.entities.SpecimenContent;
import net.hpclab.cev.entities.Taxonomy;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;

@ManagedBean
@ViewScoped
public class SpecimenContentBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 7216923042328535541L;
	private DataBaseService<SpecimenContent> specimenContentService;
	private List<SpecimenContent> searchSpecimenContent;
	private List<Specimen> contentSpecimen;
	private boolean publish;
	private Integer idSpecimen;
	private Specimen specimen;
	private SpecimenContent specimenContent;
	private UploadedFile contentFile;

	private static final Logger LOGGER = Logger.getLogger(SpecimenContentBean.class.getName());

	public SpecimenContentBean() {
		try {
			specimenContentService = new DataBaseService<>(SpecimenContent.class);
			limpiarFiltros();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	public void limpiarFiltros() {
		publish = false;
		specimen = new Specimen();
		specimenContent = new SpecimenContent();
		specimenContent.setIdSpecimen(specimen);
	}

	public DataBaseService<SpecimenContent>.Pager getPager() {
		return specimenContentService.getPager();
	}

	public void search() {
		try {
			specimenContent.setPublish(publish ? 'S' : 'N');
			searchSpecimenContent = specimenContentService.getList(specimenContent);
			if (specimenContentService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
	}

	public List<Specimen> specimenQuery(String query) {
		ArrayList<Specimen> specimens = new ArrayList<>();
		String completeName = "";
		for (Specimen s : DataWarehouse.getInstance().allSpecimens) {
			completeName = s.getIdTaxonomy().getTaxonomyName() + s.getSpecificEpithet();
			if (completeName.contains(query.toLowerCase()) && specimens.size() < Constant.QUERY_MAX_RESULTS) {
				specimens.add(s);
			}
		}
		return specimens;
	}

	public void persist() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.CREATE_ERROR;
		String transactionMessage = specimenContent.getIdSpecimen().getCommonName();
		try {
			if (contentFile != null) {
				specimenContent.setPublish('N');
				if (publish) {
					specimenContent.setPublishDate(Calendar.getInstance().getTime());
					specimenContent.setPublish('S');
				}
				specimenContent.setFileName(contentFile.getFileName());
				specimenContent.setFileUploadDate(Calendar.getInstance().getTime());
				specimenContent.setIdSpecimen(new Specimen(idSpecimen));
				specimenContent.setFileContent(getByteArray(contentFile.getInputstream()));
				specimenContentService.persist(specimenContent);
				DataWarehouse.getInstance().allSpecimenContents.add(specimenContent);

				outcomeEnum = OutcomeEnum.CREATE_SUCCESS;
			} else {
				showFileMessage(facesContext, OutcomeEnum.FILE_REQUIRED, "de imagen", "No hay archivo!");
			}
		} catch (IOException e) {
			showFileMessage(facesContext, OutcomeEnum.FILE_UPLOAD_ERROR, "de imagen", e.getMessage());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error persisting", e);
		}
		limpiarFiltros();
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = specimenContent.getIdSpecimen().getCommonName();
		try {
			specimenContentService.delete(specimenContent);
			DataWarehouse.getInstance().allSpecimenContents.remove(specimenContent);
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error deleting", e);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = specimenContent.getIdSpecimen().getCommonName();
		try {
			if (contentFile != null) {
				specimenContent.setFileName(contentFile.getFileName());
				specimenContent.setFileContent(getByteArray(contentFile.getInputstream()));
				specimenContent.setFileUploadDate(Calendar.getInstance().getTime());
			}
			specimenContent.setPublish('N');
			if (publish) {
				specimenContent.setPublishDate(Calendar.getInstance().getTime());
				specimenContent.setPublish('S');
			}
			specimenContent.setIdSpecimen(new Specimen(idSpecimen));

			SpecimenContent tempSpecimen = specimenContentService.merge(specimenContent);
			DataWarehouse.getInstance().allSpecimenContents.remove(specimenContent);
			DataWarehouse.getInstance().allSpecimenContents.add(tempSpecimen);
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error updating", e);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public StreamedContent getSpecimenThumb() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			String id = context.getExternalContext().getRequestParameterMap().get("idSpecimen");
			Integer idSpecimen = Integer.parseInt(id);
			for (SpecimenContent s : DataWarehouse.getInstance().allSpecimenContents) {
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
			for (SpecimenContent s : DataWarehouse.getInstance().allSpecimenContents) {
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
		return DataWarehouse.getInstance().allSpecimenContents;
	}

	public List<Specimen> getAllSpecimens() {
		return DataWarehouse.getInstance().allSpecimens;
	}

	public Integer getIdSpecimen() {
		return idSpecimen;
	}

	public void setIDSpecimen(Integer idSpecimen) {
		this.idSpecimen = idSpecimen;
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
		return specimenContent != null && specimenContent.getIdSpeccont() != null
				? "Editar el contenido de " + specimenContent.getIdSpecimen().getIdTaxonomy().getTaxonomyName() + " "
						+ specimenContent.getIdSpecimen().getSpecificEpithet()
				: "Nuevo contenido";
	}

	public Specimen getSpecimen() {
		return specimen;
	}

	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}

	public List<SpecimenContent> getSearchSpecimenContent() {
		return searchSpecimenContent;
	}

	public void setSearchSpecimenContent(List<SpecimenContent> searchSpecimenContent) {
		this.searchSpecimenContent = searchSpecimenContent;
	}

	public void setIdSpecimen(Integer idSpecimen) {
		this.idSpecimen = idSpecimen;
	}

	public List<Specimen> getContentSpecimen() {
		return contentSpecimen;
	}

	public void setContentSpecimen(List<Specimen> contentSpecimen) {
		this.contentSpecimen = contentSpecimen;
	}
}

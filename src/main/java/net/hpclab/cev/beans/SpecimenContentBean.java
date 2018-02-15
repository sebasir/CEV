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

import org.primefaces.event.FileUploadEvent;
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
import net.hpclab.cev.services.ObjectRetriever;

@ManagedBean
@ViewScoped
public class SpecimenContentBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 7216923042328535541L;
	private DataBaseService<SpecimenContent> specimenContentService;
	private List<SpecimenContent> searchSpecimenContent;
	private List<Specimen> contentSpecimen;
	private Specimen specimen;
	private SpecimenContent specimenContent;
	private SpecimenContent specimenContentSearch;
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
		specimen = new Specimen();
		specimenContent = new SpecimenContent();
		specimenContentSearch = new SpecimenContent();
		specimenContentSearch.setPublish(true);
		specimenContentSearch.setIdSpecimen(specimen);
		searchSpecimenContent = null;
		contentFile = null;
	}

	public DataBaseService<SpecimenContent>.Pager getPager() {
		return specimenContentService.getPager();
	}

	public void search() {
		try {
			searchSpecimenContent = specimenContentService.getList(specimenContentSearch);
			if (specimenContentService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
			if (searchSpecimenContent != null)
				for (SpecimenContent s : searchSpecimenContent)
					s.setIdSpecimen(ObjectRetriever.getObjectFromId(Specimen.class, s.getIdSpecimen().getIdSpecimen()));
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

		if (specimenContent.getIdSpecimen() == null) {
			showMessage(facesContext, outcomeEnum, "El espécimen es obligatorio");
			return;
		}
		specimenContent.setIdSpecimen(getSpecimenFromId(specimenContent.getIdSpecimen().getIdSpecimen()));
		String transactionMessage = specimenContent.getIdSpecimen().getIdTaxonomy().getTaxonomyName()
				+ (specimenContent.getIdSpecimen().getCommonName() != null
						? ", " + specimenContent.getIdSpecimen().getCommonName()
						: "");
		try {
			if (contentFile != null) {
				if (specimenContent.getPublish())
					specimenContent.setPublishDate(Calendar.getInstance().getTime());
				specimenContent.setFileName(contentFile.getFileName());
				specimenContent.setFileUploadDate(Calendar.getInstance().getTime());
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
		search();
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = specimenContent.getIdSpecimen().getIdTaxonomy().getTaxonomyName()
				+ (specimenContent.getIdSpecimen().getCommonName() != null
						? specimenContent.getIdSpecimen().getCommonName()
						: "");
		try {
			specimenContentService.delete(specimenContent);
			DataWarehouse.getInstance().allSpecimenContents.remove(specimenContent);
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error deleting", e);
		}
		limpiarFiltros();
		search();
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = specimenContent.getIdSpecimen().getIdTaxonomy().getTaxonomyName()
				+ (specimenContent.getIdSpecimen().getCommonName() != null
						? specimenContent.getIdSpecimen().getCommonName()
						: "");
		try {
			if (contentFile != null) {
				specimenContent.setFileName(contentFile.getFileName());
				specimenContent.setFileUploadDate(Calendar.getInstance().getTime());
			}
			if (specimenContent.getPublish())
				specimenContent.setPublishDate(Calendar.getInstance().getTime());
			SpecimenContent tempSpecimen = specimenContentService.merge(specimenContent);
			DataWarehouse.getInstance().allSpecimenContents.remove(specimenContent);
			DataWarehouse.getInstance().allSpecimenContents.add(tempSpecimen);
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error updating", e);
		}
		limpiarFiltros();
		search();
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public Specimen getSpecimenFromId(Integer idSpecimen) {
		for (Specimen s : DataWarehouse.getInstance().allSpecimens) {
			if (idSpecimen.equals(s.getIdSpecimen()))
				return s;
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

	public void handleFileUpload(FileUploadEvent event) {
		contentFile = event.getFile();
		specimenContent.setFileContent(contentFile.getContents());
		specimenContent.setFileName(contentFile.getFileName());
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

	public SpecimenContent getSpecimenContentSearch() {
		return specimenContentSearch;
	}

	public void setSpecimenContentSearch(SpecimenContent specimenContentSearch) {
		this.specimenContentSearch = specimenContentSearch;
	}

	public List<SpecimenContent> getAllSpecimenContents() {
		return DataWarehouse.getInstance().allSpecimenContents;
	}

	public List<Specimen> getAllSpecimens() {
		return DataWarehouse.getInstance().allSpecimens;
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

	public List<Specimen> getContentSpecimen() {
		return contentSpecimen;
	}

	public void setContentSpecimen(List<Specimen> contentSpecimen) {
		this.contentSpecimen = contentSpecimen;
	}
}

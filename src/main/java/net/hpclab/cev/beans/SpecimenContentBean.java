/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */

package net.hpclab.cev.beans;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.entities.SpecimenContent;
import net.hpclab.cev.entities.Taxonomy;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.services.AccessService;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;
import net.hpclab.cev.services.ObjectRetriever;
import net.hpclab.cev.services.ParseExceptionService;

/**
 * Este servicio permite la interacción con el servicio de
 * <tt>DataBaseService</tt> para la gestión de contenido gráfico de un
 * espécimen. Principalmente expone métodos de creación, edición, consulta y
 * eliminación, validando la posibilidad de estas operaciones contra el servicio
 * de <tt>AccessesService</tt>, el cual valida el usuario que realiza la
 * operación.
 * 
 * @author Sebasir
 * @since 1.0
 * @see DataBaseService
 * @see SpecimenContent
 * @see Specimen
 * @see Taxonomy
 */

@ManagedBean
@SessionScoped
public class SpecimenContentBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 7216923042328535541L;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>SpecimenContent</tt>, lo cual permite extender todas las operaciones del
	 * servicio para esta clase.
	 */
	private DataBaseService<SpecimenContent> specimenContentService;

	/**
	 * Lista de contenido gráfico para búsquedas
	 */
	private List<SpecimenContent> searchSpecimenContent;

	/**
	 * Lista de contenido gráfico para mostrar en la galería
	 */
	private List<SpecimenContent> contentSpecimen;

	/**
	 * Lista de familias
	 */
	private List<Taxonomy> families;

	/**
	 * Objeto que permite seleccionar un espécimen
	 */
	private Specimen specimen;

	/**
	 * Objeto que permite seleccionar un espécimen en detalle
	 */
	private Specimen specimenContentDetail;

	/**
	 * Objeto que permite editar un contenido gráfico
	 */
	private SpecimenContent specimenContent;

	/**
	 * Objeto que permite consultar contenido gráfico
	 */
	private SpecimenContent specimenContentSearch;

	/**
	 * Archivo que representa a un espécimen
	 */
	private UploadedFile contentFile;

	/**
	 * Cadena de texto que guarda la clave primaria de un espécimen
	 */
	private String specimenDetail;

	/**
	 * Nombre de la familia que se visita
	 */
	private String familyName;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(SpecimenContentBean.class.getName());

	/**
	 * Constructor que permite inicializar los servicios de
	 * <tt>DataBaseService</tt>, y además filtrar los contenidos que son
	 * publicables, así como las familias de grupos
	 */
	public SpecimenContentBean() {
		try {
			specimenContentService = new DataBaseService<>(SpecimenContent.class);
			families = new ArrayList<>();
			Taxonomy t = null;
			for (SpecimenContent sc : DataWarehouse.getInstance().allSpecimenContents) {
				if (!sc.getPublish())
					continue;

				t = sc.getIdSpecimen().getIdTaxonomy();
				t = ObjectRetriever.getObjectFromId(Taxonomy.class, t.getIdTaxonomy());
				while (t.getIdTaxlevel().getTaxlevelRank() != 12)
					t = t.getIdContainer();
				if (!families.contains(t))
					families.add(t);
			}

			limpiarFiltros();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Permite limpiar los objetos
	 */
	public void limpiarFiltros() {
		specimen = new Specimen();
		specimenContent = new SpecimenContent();
		specimenContentSearch = new SpecimenContent();
		specimenContentSearch.setPublish(true);
		specimenContentSearch.setIdSpecimen(specimen);
		searchSpecimenContent = null;
		contentFile = null;
	}

	/**
	 * @return El paginador de la consulta de contenido gráfico
	 */
	public DataBaseService<SpecimenContent>.Pager getPager() {
		return specimenContentService.getPager();
	}

	/**
	 * Muestra un mensaje de selección de especímenes
	 */
	public void showSpecimenSelection() {
		if (specimenContent != null && specimenContent.getIdSpecimen() != null) {
			Specimen spc = ObjectRetriever.getObjectFromId(Specimen.class,
					specimenContent.getIdSpecimen().getIdSpecimen());
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_INFO,
					"Ejemplar " + spc.getIdTaxonomy().getTaxonomyName() + " seleccionado!");
		}
	}

	/**
	 * Permite realizar la búsqueda de contenido gráfico dados unos parámetros de
	 * búsqueda
	 */
	public void search() {
		try {
			List<SpecimenContent> tempList = specimenContentService.getList(specimenContentSearch);
			if (specimenContentService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
			if (tempList != null) {
				searchSpecimenContent = new ArrayList<>();
				for (SpecimenContent s : tempList) {
					s.setIdSpecimen(ObjectRetriever.getObjectFromId(Specimen.class, s.getIdSpecimen().getIdSpecimen()));
					searchSpecimenContent.add(s);
				}
			}
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
	}

	/**
	 * Permite consultar una lista de especímenes dado un texto
	 * 
	 * @param query
	 *            Cadena de texto a buscar en los nombres de los especímenes
	 * @return Lista de especímenes que coiciden los nombres con la cadena de texto
	 */
	public List<Specimen> specimenQuery(String query) {
		ArrayList<Specimen> specimens = new ArrayList<>();
		String completeName = "";
		for (Specimen s : DataWarehouse.getInstance().allSpecimens) {
			completeName = s.getIdTaxonomy().getTaxonomyName() + s.getSpecificEpithet();
			if (completeName.toLowerCase().contains(query.toLowerCase())
					&& specimens.size() < Constant.QUERY_MAX_RESULTS) {
				specimens.add(s);
			}
		}
		return specimens;
	}

	/**
	 * Permite guardar un objeto de contenido gráfico que se haya definido en la
	 * interfáz validando el permiso de escritura
	 */
	public void persist() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.CREATE_ERROR;

		if (specimenContent.getIdSpecimen() == null) {
			showMessage(facesContext, outcomeEnum, "El espécimen es obligatorio");
			return;
		}
		specimenContent.setIdSpecimen(
				ObjectRetriever.getObjectFromId(Specimen.class, specimenContent.getIdSpecimen().getIdSpecimen()));
		String transactionMessage = specimenContent.getIdSpecimen().getIdTaxonomy().getTaxonomyName()
				+ (specimenContent.getIdSpecimen().getCommonName() != null
						? ", " + specimenContent.getIdSpecimen().getCommonName()
						: "");
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.CONTENT, getUsers(facesContext),
					Constant.AccessLevel.INSERT)) {
				showAccessMessage(facesContext, OutcomeEnum.INSERT_NOT_GRANTED);
				return;
			}

			if (contentFile != null) {
				if (specimenContent.getPublish())
					specimenContent.setPublishDate(Calendar.getInstance().getTime());
				specimenContent.setFileName(contentFile.getFileName());
				specimenContent.setFileUploadDate(Calendar.getInstance().getTime());
				specimenContent.setStatus(StatusEnum.ACTIVO.get());
				specimenContentService.persist(specimenContent);
				DataWarehouse.getInstance().allSpecimenContents.add(specimenContent);

				outcomeEnum = OutcomeEnum.CREATE_SUCCESS;
			} else {
				showFileMessage(facesContext, OutcomeEnum.FILE_REQUIRED, "de imagen", "No hay archivo!");
			}
		} catch (IOException e) {
			showFileMessage(facesContext, OutcomeEnum.FILE_UPLOAD_ERROR, "de imagen", e.getMessage());
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error persisting: " + transactionMessage);
		}
		limpiarFiltros();
		search();
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite eliminar un objeto de contenido gráfico validando el permiso de
	 * eliminación
	 */
	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = specimenContent.getIdSpecimen().getIdTaxonomy().getTaxonomyName()
				+ (specimenContent.getIdSpecimen().getCommonName() != null
						? specimenContent.getIdSpecimen().getCommonName()
						: "");
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.CONTENT, getUsers(facesContext),
					Constant.AccessLevel.DELETE)) {
				showAccessMessage(facesContext, OutcomeEnum.DELETE_NOT_GRANTED);
				return;
			}

			specimenContentService.delete(specimenContent);
			DataWarehouse.getInstance().allSpecimenContents.remove(specimenContent);
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error deleting: " + transactionMessage);
		}
		limpiarFiltros();
		search();
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite editar un objeto de contenido gráfico que se haya definido en la
	 * interfáz validando el permiso de modificación
	 */
	public void edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;

		specimenContent.setIdSpecimen(
				ObjectRetriever.getObjectFromId(Specimen.class, specimenContent.getIdSpecimen().getIdSpecimen()));
		String transactionMessage = specimenContent.getIdSpecimen().getIdTaxonomy().getTaxonomyName()
				+ (specimenContent.getIdSpecimen().getCommonName() != null
						? ", " + specimenContent.getIdSpecimen().getCommonName()
						: "");

		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.CONTENT, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

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
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error updating: " + transactionMessage);
		}
		limpiarFiltros();
		search();
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite obtener la imagen del espécimen
	 * 
	 * @return La imagen del espécimen
	 * @throws IOException
	 *             Cuando hubo error trayendo la imagen desde el objeto
	 */
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
			return new DefaultStreamedContent(
					super.getInputStream(Constant.DEFAULT_SPECIMEN_IMAGE, FacesContext.getCurrentInstance()),
					"image/jpeg");
		}
	}

	/**
	 * Permite obtener la imagen de un especímen de alguna familia
	 * 
	 * @return La imagen de representativa de la familia
	 * @throws IOException
	 *             Cuando hubo error trayendo la imagen desde el objeto
	 */
	public StreamedContent getFamilyThumb() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			String id = context.getExternalContext().getRequestParameterMap().get("idTaxonomy");
			Integer idTaxonomy = Integer.parseInt(id);
			for (SpecimenContent s : DataWarehouse.getInstance().allSpecimenContents)
				if (isFather(idTaxonomy, s.getIdSpecimen()) && s.getPublish())
					return new DefaultStreamedContent(super.getInputStream(s.getFileContent()), "image/jpeg");

			return null;
		}
	}

	/**
	 * Permite cargar y validar el archivo que el usuario carga para definir el
	 * contenido gráfico de un espécimen
	 * 
	 * @param event
	 *            Evento del cual se extrae el archivo.
	 */
	public void handleFileUpload(FileUploadEvent event) {
		contentFile = event.getFile();
		if (!contentFile.getContentType().contains("image")) {
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_WARNING,
					"El archivo no corresponde a un archivo de imagen");
			contentFile = null;
			specimenContent.setFileContent(null);
			specimenContent.setFileName("El archivo no corresponde a un archivo de imagen");
			return;
		}
		try {
			BufferedImage image = ImageIO.read(contentFile.getInputstream());
			int height = image.getHeight();
			int width = image.getWidth();
			double imageAspectRatio = (((double) width) / ((double) height));
			if (Math.abs(Constant.NOMINAL_ASPECT_RATIO - imageAspectRatio) > Constant.TOLERANCE_RANGE_VALUE)
				showFileMessage(FacesContext.getCurrentInstance(), OutcomeEnum.FILE_UPLOAD_WARNING,
						contentFile.getFileName(), "No obstante se recomienda cambiar la relación de aspecto (actual: "
								+ imageAspectRatio + ", deseable: " + Constant.NOMINAL_ASPECT_RATIO + ")");
			else
				showFileMessage(FacesContext.getCurrentInstance(), OutcomeEnum.FILE_UPLOAD_SUCCESS,
						contentFile.getFileName(), null);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error leyendo propiedades del archivo", e);
		}

		specimenContent.setFileContent(contentFile.getContents());
		specimenContent.setFileName(contentFile.getFileName());
	}

	/**
	 * Determina si una clasificación es padre de un espécimen
	 * 
	 * @param f
	 *            Clave primaria de la clasificación taxonómica
	 * @param s
	 *            Objeto de espécimen
	 * @return <tt>true</tt> si la clave de la clasificación es padre del espécimen,
	 *         <tt>false</tt> si no
	 */
	private boolean isFather(Integer f, Specimen s) {
		Taxonomy t = s.getIdTaxonomy();
		while (t != null) {
			if (f.equals(t.getIdTaxonomy()))
				return true;
			t = t.getIdContainer();
		}
		return false;
	}

	/**
	 * Permite cargar los ejemplares de una familia
	 * 
	 * @return Dirección de la página a visitar una vez se cargue el contenido.
	 */
	public String loadFamilySpecimens() {
		contentSpecimen = new ArrayList<>();
		String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idTaxonomy");
		Integer idTaxonomy = Integer.parseInt(id);

		Taxonomy tax = ObjectRetriever.getObjectFromId(Taxonomy.class, idTaxonomy);

		if (tax != null)
			familyName = tax.getTaxonomyName();

		for (SpecimenContent s : DataWarehouse.getInstance().allSpecimenContents)
			if (s.getFileContent() != null && isFather(idTaxonomy, s.getIdSpecimen()) && s.getPublish())
				contentSpecimen.add(s);
		return "Collection.xhtml" + Constant.FACES_REDIRECT;
	}

	/**
	 * Permite cargar el objeto espécimen a partir de una cadena de texto con la
	 * clave primaria
	 */
	public void loadDetailContent() {
		if (specimenDetail == null || specimenDetail.isEmpty())
			return;

		specimenContentDetail = ObjectRetriever.getObjectFromId(Specimen.class, Integer.parseInt(specimenDetail));
	}

	/**
	 * @return Objeto que permite editar un contenido gráfico
	 */
	public SpecimenContent getSpecimenContent() {
		return specimenContent;
	}

	/**
	 * @param specimenContent
	 *            Objeto que permite editar un contenido gráfico a definir
	 */
	public void setSpecimenContent(SpecimenContent specimenContent) {
		this.specimenContent = specimenContent;
	}

	/**
	 * @return Objeto que permite consultar contenido gráfico
	 */
	public SpecimenContent getSpecimenContentSearch() {
		return specimenContentSearch;
	}

	/**
	 * @param specimenContentSearch
	 *            Objeto que permite consultar contenido gráfico a definir
	 */
	public void setSpecimenContentSearch(SpecimenContent specimenContentSearch) {
		this.specimenContentSearch = specimenContentSearch;
	}

	/**
	 * @return Permite el acceso a todos los contenidos gráficos de los especímenes
	 */
	public List<SpecimenContent> getAllSpecimenContents() {
		return DataWarehouse.getInstance().allSpecimenContents;
	}

	/**
	 * @return Permite el acceso a todos los especímenes
	 */
	public List<Specimen> getAllSpecimens() {
		return DataWarehouse.getInstance().allSpecimens;
	}

	/**
	 * @return Archivo que representa a un espécimen
	 */
	public UploadedFile getContentFile() {
		return contentFile;
	}

	/**
	 * @param contentFile
	 *            Archivo que representa a un espécimen a definir
	 */
	public void setContentFile(UploadedFile contentFile) {
		this.contentFile = contentFile;
	}

	/**
	 * @return Objeto que permite seleccionar un espécimen
	 */
	public Specimen getSpecimen() {
		return specimen;
	}

	/**
	 * @param specimen
	 *            Objeto que permite seleccionar un espécimen a definir
	 */
	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}

	/**
	 * @return Lista de contenido gráfico para búsquedas
	 */
	public List<SpecimenContent> getSearchSpecimenContent() {
		return searchSpecimenContent;
	}

	/**
	 * @return Lista de familias
	 */
	public List<Taxonomy> getFamilies() {
		return families;
	}

	/**
	 * @param searchSpecimenContent
	 *            Lista de contenido gráfico para búsquedas a definir
	 */
	public void setSearchSpecimenContent(List<SpecimenContent> searchSpecimenContent) {
		this.searchSpecimenContent = searchSpecimenContent;
	}

	/**
	 * @return Lista de contenido gráfico para mostrar en la galería
	 */
	public List<SpecimenContent> getContentSpecimen() {
		return contentSpecimen;
	}

	/**
	 * @return Cadena de texto que guarda la clave primaria de un espécimen
	 */
	public String getSpecimenDetail() {
		return specimenDetail;
	}

	/**
	 * @param specimenDetail
	 *            Cadena de texto que guarda la clave primaria de un espécimen a
	 *            definir
	 */
	public void setSpecimenDetail(String specimenDetail) {
		this.specimenDetail = specimenDetail;
	}

	/**
	 * @return Objeto que permite seleccionar un espécimen en detalle
	 */
	public Specimen getSpecimenContentDetail() {
		return specimenContentDetail;
	}

	/**
	 * @param specimenContentDetail
	 *            Objeto que permite seleccionar un espécimen en detalle a definir
	 */
	public void setSpecimenContentDetail(Specimen specimenContentDetail) {
		this.specimenContentDetail = specimenContentDetail;
	}

	/**
	 * @return Nombre de la familia que se visita
	 */
	public String getFamilyName() {
		return familyName;
	}

	/**
	 * @param familyName
	 *            Nombre de la familia que se visita a definir
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
}
package net.hpclab.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.Author;
import net.hpclab.entities.AuthorRole;
import net.hpclab.entities.Catalog;
import net.hpclab.entities.Collection;
import net.hpclab.entities.Location;
import net.hpclab.entities.RegType;
import net.hpclab.entities.SampleType;
import net.hpclab.entities.Specimen;
import net.hpclab.entities.Taxonomy;
import net.hpclab.sessions.CollectionSession;
import net.hpclab.sessions.SpecimenSession;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.column.Column;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.row.Row;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

@ManagedBean
@SessionScoped
public class SpecimenBean extends Utilsbean implements Serializable {

    @Inject
    private SpecimenSession specimenSession;

    @Inject
    private CollectionSession collectionSession;
    private static final long serialVersionUID = 1L;
    //private static final Logger logger = Logger.getLogger(SpecimenBean.class);
    private Specimen specimen;
    private Location location;
    private Taxonomy taxonomy;
    private RegType regType;
    private SampleType sampleType;
    private Author collector, determiner;
    private List<Specimen> allSpecimens;
    private List<Taxonomy> allTaxonomys;
    private List<Location> allLocations;
    private List<AuthorRole> allDeterminer;
    private List<AuthorRole> allCollector;
    private List<Collection> allCollection;
    private List<Catalog> allCatalog;
    private List<RegType> allRegTypes;
    private List<SampleType> allSampleTypes;
    private PanelGrid specimenForm;
    private String selectedTax;
    private String selectedLoc;
    private String selectedDeterminer;
    private String selectedCollector;
    private String selectedCatalog;
    private String selectedCollection;
    private String selectedRegType;
    private String selectedSampleType;

    public SpecimenBean() {
	   specimenSession = new SpecimenSession();
	   collectionSession = new CollectionSession();
    }

    @PostConstruct
    public void init() {
    }

    public String persist() {
	   specimen.setIdTaxonomy(new Taxonomy(new Integer(selectedTax)));
	   specimen.setIdDeterminer(new AuthorRole(new Integer(selectedDeterminer)));
	   specimen.setIdLocation(new Location(new Integer(selectedLoc)));
	   specimen.setIdCollector(new AuthorRole(new Integer(selectedCollector)));
	   specimen.setIdCatalog(new Catalog(new Integer(selectedCatalog)));
	   specimen.setIdRety(new RegType(new Integer(selectedRegType)));
	   specimen.setIdSaty(new SampleType(new Integer(selectedSampleType)));
	   specimen = specimenSession.persist(specimen);
	   if (specimen != null && specimen.getIdSpecimen() != null) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(specimen, Actions.createSuccess));
	   } else {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(specimen, Actions.createError));
	   }
	   return findAllSpecimens();
    }

    public void delete() {
	   try {
		  specimenSession.delete(specimen);
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(specimen, Actions.deleteSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(specimen, Actions.deleteError));
	   }
    }

    public void prepareCreate() {
	   specimen = new Specimen();
	   setAllTaxonomys((List<Taxonomy>) specimenSession.findByQuery("Taxonomy.findOrdered"));
	   setAllLocations((List<Location>) specimenSession.findByQuery("Location.findOrdered"));
	   setAllCollector((List<AuthorRole>) specimenSession.findByQuery("AuthorRole.findCollectors"));
	   setAllDeterminer((List<AuthorRole>) specimenSession.findByQuery("AuthorRole.findDeterminers"));
	   setAllCollection((List<Collection>) specimenSession.findByQuery("Collection.findAll"));
	   setAllSampleTypes((List<SampleType>) specimenSession.findByQuery("SampleType.findAll"));
	   setAllRegTypes((List<RegType>) specimenSession.findByQuery("RegType.findAll"));
    }

    public void edit() {
	   try {
		  specimen = specimenSession.merge(specimen);
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(specimen, Actions.updateSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(specimen, Actions.updateError));
	   }
    }

    private void createSpecimenForm() {
	   if (specimen == null || specimen.getIdSpecimen() == null) {
		  return;
	   }

	   Taxonomy tax = specimen.getIdTaxonomy();
	   ArrayList<Taxonomy> taxList = new ArrayList<Taxonomy>();
	   while (tax != null) {
		  taxList.add(0, tax);
		  tax = tax.getIdContainer();
	   }

	   Location loc = specimen.getIdLocation();
	   ArrayList<Location> locList = new ArrayList<Location>();
	   while (loc != null) {
		  locList.add(0, loc);
		  loc = loc.getIdContainer();
	   }

	   String user = "";
	   specimenForm = new PanelGrid();
	   addRow(addOut("idenHeader", "Datos de Identificación"), null);
	   addRow(addOut("scientificName", "Nombre Científico:"), addOut("scientificNameVal", specimen.getIdTaxonomy().getTaxonomyName() + " " + specimen.getSpecificEpithet()));

	   for (Taxonomy t : taxList) {
		  addRow(addOut("idTaxLevel" + t.getIdTaxlevel().getIdTaxlevel(), t.getIdTaxlevel().getTaxlevelName() + ":"), addOut("idTaxId" + t.getIdTaxonomy(), t.getTaxonomyName()));
	   }
	   if (!user.equals("")) {
		  addRow(addOut("specificEpithetId", "Epiteto Específico:"), addIn("specificEpithetIn", specimen.getSpecificEpithet()));
		  addRow(addOut("commonNameId", "Nombre Común:"), addIn("commonNameIn", specimen.getCommonName()));
		  addRow(addOut("idenCommentId", "Comentario Identificación"), addArea("#{specimenBean.specimen.idenComment}"));
		  addList("det", "Determinador:", "Selecciona un Determinador", "Debes seleccionar un Determinador!", "author", "#{author.idAuthor.authorName}", "#{author.idAuro}", "#{specimenBean.selectedDeterminer}", "#{specimenBean.allDeterminer}");
		  addRow(addOut("idenDateId", "Fecha Identificación:"), addCal("#{specimenBean.specimen.idenDate}"));
	   } else {
		  addRow(addOut("specificEpithetId", "Epiteto Específico:"), addOut("specificEpithetOut", specimen.getSpecificEpithet()));
		  addRow(addOut("commonNameId", "Nombre Común:"), addOut("commonNameOut", specimen.getCommonName()));
		  addRow(addOut("idenCommentId", "Comentario de Identificación:"), addOut("idenCommentOut", specimen.getIdenComment()));
		  addRow(addOut("determinerId", "Determinador:"), addOut("determinerOut", specimen.getIdDeterminer().getIdAuthor().getAuthorName()));
		  addRow(addOut("idenDateId", "Fecha Identificación:"), addOut("idenDateOut", formatDate(specimen.getIdenDate())));
	   }
	   addRow(addOut("localHeader", "Datos de Localización"), null);
	   for (Location l : locList) {
		  addRow(addOut("idLocLevel" + l.getIdLoclevel().getIdLoclevel(), l.getIdLoclevel().getLoclevelName() + ":"), addOut("idLocId" + l.getIdLocation(), l.getLocationName()));
	   }
	   if (!user.equals("")) {
		  addList("col", "Colector:", "Selecciona un Colector", "Debes seleccionar un Colector!", "author", "#{author.idAuthor.authorName}", "#{author.idAuro}", "#{specimenBean.selectedCollector}", "#{specimenBean.allCollector}");
		  addRow(addOut("collectDateId", "Fecha Colecta:"), addCal("#{specimenBean.specimen.collectDate}"));
		  addRow(addOut("collectCommentId", "Comentario Colecta:"), addArea("#{specimenBean.specimen.collectComment}"));
	   } else {
		  addRow(addOut("collectorId", "Colector:"), addOut("collectorOut", specimen.getIdCollector().getIdAuthor().getAuthorName()));
		  addRow(addOut("collectDateId", "Fecha Colecta:"), addOut("collectDateOut", formatDate(specimen.getCollectDate())));
		  addRow(addOut("collectCommentId", "Comentario Colecta:"), addOut("collectCommenOut", specimen.getCollectComment()));
	   }
	   addRow(addOut("collectionHeader", "Datos de Colección"), null);
	   if (!user.equals("")) {
		  addList("colection", "Colector:", "Selecciona una Colección", "Debes seleccionar una Colección!", "col", "#{col.collectionName} - #{col.companyName}", "#{col.idCollection}", "#{specimenBean.selectedCollection}", "#{specimenBean.allCollection}");
	   } else {

	   }
    }

    private void addRow(UIComponent d, UIComponent v) {
	   Column c1 = new Column();
	   Column c2 = new Column();
	   if (v == null) {
		  c1.setColspan(2);
	   } else {
		  c1.getChildren().add(d);
		  c2.getChildren().add(v);
	   }
	   Row r = new Row();
	   r.getChildren().add(c1);
	   if (v != null) {
		  r.getChildren().add(c2);
	   }
	   specimenForm.getChildren().add(r);
    }

    private ValueExpression getValueExpression(String expression) {
	   ExpressionFactory expressionFactory = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
	   ELContext expressionContext = FacesContext.getCurrentInstance().getELContext();
	   return expressionFactory.createValueExpression(expressionContext, expression, Object.class);
    }

    private void addList(String id, String text, String title, String rMessage, String var, String iLabel, String iValue, String cont, String list) {
	   SelectOneMenu sOne = new SelectOneMenu();
	   sOne.setId("sOne" + id);
	   sOne.setRequired(true);
	   sOne.setRequiredMessage(rMessage);
	   sOne.setEffect("fade");
	   sOne.setRendered(true);
	   sOne.setValueExpression("value", getValueExpression(cont));
	   UISelectItem item = new UISelectItem();
	   item.setItemLabel(title);
	   item.setItemValue("");
	   UISelectItems items = new UISelectItems();
	   items.setValueExpression("var", getValueExpression(var));
	   items.setValueExpression("value", getValueExpression(list));
	   items.setValueExpression("itemLabel", getValueExpression(iLabel));
	   items.setValueExpression("itemValue", getValueExpression(iValue));
	   sOne.getChildren().add(item);
	   sOne.getChildren().add(items);
	   addRow(addOut(id + "outList", text), sOne);
    }

    private Calendar addCal(String value) {
	   Calendar c = new Calendar();
	   c.setEffect("fade");
	   c.setValueExpression("value", getValueExpression(value));
	   c.setPattern("dd-MM-yyyy");
	   return c;
    }

    private OutputLabel addOut(String id, String value) {
	   OutputLabel hOut = new OutputLabel();
	   hOut.setId(id);
	   hOut.setValue(value);
	   return hOut;
    }

    private InputText addIn(String id, String value) {
	   InputText hIn = new InputText();
	   hIn.setId(id);
	   hIn.setValue(value);
	   return hIn;
    }

    private InputTextarea addArea(String value) {
	   InputTextarea iA = new InputTextarea();
	   iA.setRows(5);
	   iA.setCols(30);
	   iA.setValueExpression("value", getValueExpression(value));
	   return iA;
    }

    public String displayList() {
	   findAllSpecimens();
	   return "specimen";
    }

    public void filterCatalog() {
	   if (selectedCollection != null && !selectedCollection.equals("")) {
		  Collection col = collectionSession.findById(new Integer(selectedCollection));
		  setAllCatalog(col.getCatalogList());
	   } else {
		  setAllCatalog(null);
	   }
    }

    public Specimen getSpecimen() {
	   return specimen;
    }

    public void setSpecimen(Specimen specimen) {
	   this.specimen = specimen;
    }

    public Location getLocation() {
	   return location;
    }

    public void setLocation(Location location) {
	   this.location = location;
    }

    public Taxonomy getTaxonomy() {
	   return taxonomy;
    }

    public void setTaxonomy(Taxonomy taxonomy) {
	   this.taxonomy = taxonomy;
    }

    public RegType getRegType() {
	   return regType;
    }

    public void setRegType(RegType regType) {
	   this.regType = regType;
    }

    public SampleType getSampleType() {
	   return sampleType;
    }

    public void setSampleType(SampleType sampleType) {
	   this.sampleType = sampleType;
    }

    public Author getCollector() {
	   return collector;
    }

    public void setCollector(Author collector) {
	   this.collector = collector;
    }

    public Author getDeterminer() {
	   return determiner;
    }

    public void setDeterminer(Author determiner) {
	   this.determiner = determiner;
    }

    public PanelGrid getDetailPanel() {
	   return null;
    }

    public PanelGrid getSpecimenForm() {
	   createSpecimenForm();
	   return specimenForm;
    }

    public void setSpecimenForm(PanelGrid specimenForm) {
	   this.specimenForm = specimenForm;
    }

    public List<Specimen> getAllSpecimens() {
	   findAllSpecimens();
	   return allSpecimens;
    }

    public void setAllSpecimens(List<Specimen> allSpecimens) {
	   this.allSpecimens = allSpecimens;
    }

    public String findAllSpecimens() {
	   allSpecimens = specimenSession.listAll();
	   return null;
    }

    public List<Taxonomy> getAllTaxonomys() {
	   return allTaxonomys;
    }

    public void setAllTaxonomys(List<Taxonomy> allTaxonomys) {
	   this.allTaxonomys = allTaxonomys;
    }

    public List<Location> getAllLocations() {
	   return allLocations;
    }

    public void setAllLocations(List<Location> allLocations) {
	   this.allLocations = allLocations;
    }

    public String getSelectedTax() {
	   return selectedTax;
    }

    public void setSelectedTax(String selectedTax) {
	   this.selectedTax = selectedTax;
    }

    public String getSelectedLoc() {
	   return selectedLoc;
    }

    public void setSelectedLoc(String selectedLoc) {
	   this.selectedLoc = selectedLoc;
    }

    public List<AuthorRole> getAllDeterminer() {
	   return allDeterminer;
    }

    public void setAllDeterminer(List<AuthorRole> allDeterminer) {
	   this.allDeterminer = allDeterminer;
    }

    public List<AuthorRole> getAllCollector() {
	   return allCollector;
    }

    public void setAllCollector(List<AuthorRole> allCollector) {
	   this.allCollector = allCollector;
    }

    public List<Collection> getAllCollection() {
	   return allCollection;
    }

    public void setAllCollection(List<Collection> allCollection) {
	   this.allCollection = allCollection;
    }

    public List<Catalog> getAllCatalog() {
	   return allCatalog;
    }

    public void setAllCatalog(List<Catalog> allCatalog) {
	   this.allCatalog = allCatalog;
    }

    public List<RegType> getAllRegTypes() {
	   return allRegTypes;
    }

    public void setAllRegTypes(List<RegType> allRegTypes) {
	   this.allRegTypes = allRegTypes;
    }

    public List<SampleType> getAllSampleTypes() {
	   return allSampleTypes;
    }

    public void setAllSampleTypes(List<SampleType> allSampleTypes) {
	   this.allSampleTypes = allSampleTypes;
    }

    public String getSelectedDeterminer() {
	   return selectedDeterminer;
    }

    public void setSelectedDeterminer(String selectedDeterminer) {
	   this.selectedDeterminer = selectedDeterminer;
    }

    public String getSelectedCollector() {
	   return selectedCollector;
    }

    public void setSelectedCollector(String selectedCollector) {
	   this.selectedCollector = selectedCollector;
    }

    public String getSelectedCollection() {
	   return selectedCollection;
    }

    public void setSelectedCollection(String selectedCollection) {
	   this.selectedCollection = selectedCollection;
    }

    public String getSelectedRegType() {
	   return selectedRegType;
    }

    public void setSelectedRegType(String selectedRegType) {
	   this.selectedRegType = selectedRegType;
    }

    public String getSelectedSampleType() {
	   return selectedSampleType;
    }

    public void setSelectedSampleType(String selectedSampleType) {
	   this.selectedSampleType = selectedSampleType;
    }

    public String getSelectedCatalog() {
	   return selectedCatalog;
    }

    public void setSelectedCatalog(String selectedCatalog) {
	   this.selectedCatalog = selectedCatalog;
    }

    public void specimensJSON() {
	   try {
		  FacesContext facesContext = FacesContext.getCurrentInstance();
		  ExternalContext externalContext = facesContext.getExternalContext();
		  externalContext.setResponseContentType("application/json");
		  externalContext.setResponseCharacterEncoding("UTF-8");
		  allSpecimens = getAllSpecimens();
		  JSONArray jArray = new JSONArray();
		  JSONObject jObj;
		  for (Specimen s : allSpecimens) {
			 jObj = new JSONObject();
			 jObj.put("id", s.getIdSpecimen().toString());
			 jObj.put("sname", s.getIdTaxonomy().getTaxonomyName() + " " + s.getSpecificEpithet());
			 jObj.put("cname", s.getCommonName());
			 jObj.put("loc", s.getIdLocation().getIdLoclevel().getLoclevelName() + " de " + s.getIdLocation().getLocationName());
			 jArray.put(jObj);
		  }
		  JSONObject salida = new JSONObject();
		  salida.put("collection", jArray);
		  jObj = new JSONObject();
		  jObj.put("slide_width", 952);
		  jObj.put("darkslide_width", 794);
		  jObj.put("specimenSize", allSpecimens.size());
		  salida.put("settings", jObj);
		  externalContext.getResponseOutputWriter().write(salida.toString());
		  facesContext.responseComplete();
	   } catch (Exception e) {
		  System.out.println("ERROR" + e.getLocalizedMessage());
	   }
    }
}

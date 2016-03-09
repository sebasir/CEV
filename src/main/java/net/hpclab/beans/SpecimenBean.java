package net.hpclab.beans;

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
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.AuthorRole;
import net.hpclab.entities.Catalog;
import net.hpclab.entities.Collection;
import net.hpclab.entities.Location;
import net.hpclab.entities.RegType;
import net.hpclab.entities.SampleType;
import net.hpclab.entities.Specimen;
import net.hpclab.entities.Taxonomy;
import net.hpclab.entities.entNaming;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
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
    private Specimen specimen;
    private Location location;
    private LocationBean locationBean;
    private Taxonomy taxonomy;
    private TaxonomyBean taxonomyBean;
    private RegType regType;
    private SampleType sampleType;
    private Catalog catalog;
    private Collection collection;
    private AuthorRole collector, determiner;
    private List<AuthorRole> allDeterminer;
    private List<AuthorRole> allCollector;
    private List<Collection> allCollection;
    private List<Catalog> allCatalog;
    private List<RegType> allRegTypes;
    private List<SampleType> allSampleTypes;
    private PanelGrid specimenForm;
    private boolean create;
    private String specimenDetail;
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
	   System.out.println("Inicializando lista 'Specimens'");
	   if (allSpecimens == null) {
		  allSpecimens = specimenSession.listAll();
	   }
    }

    public void persist() {
	   location = (Location) locationBean.getSelectedNode().getData();
	   taxonomy = (Taxonomy) taxonomyBean.getSelectedNode().getData();
	   specimen.setIdTaxonomy(taxonomy);
	   specimen.setIdLocation(location);
	   specimen.setIdDeterminer(determiner);
	   specimen.setIdCollector(collector);
	   specimen.setIdCatalog(catalog);
	   specimen.setIdRety(regType);
	   specimen.setIdSaty(sampleType);
	   specimen = specimenSession.persist(specimen);
	   if (specimen != null && specimen.getIdSpecimen() != null) {
		  launchMessage(specimen, Actions.createSuccess);
		  allSpecimens.add(specimen);
	   } else {
		  launchMessage(specimen, Actions.createError);
	   }
	   resetForm();
    }

    public void edit() {
	   try {
		  location = (Location) locationBean.getSelectedNode().getData();
		  taxonomy = (Taxonomy) taxonomyBean.getSelectedNode().getData();
		  specimen.setIdTaxonomy(taxonomy);
		  specimen.setIdLocation(location);
		  specimen.setIdDeterminer(determiner);
		  specimen.setIdCollector(collector);
		  specimen.setIdCatalog(catalog);
		  specimen.setIdRety(regType);
		  specimen.setIdSaty(sampleType);
		  specimen = specimenSession.merge(specimen);
		  allSpecimens.remove(specimen);
		  allSpecimens.add(specimen);
		  launchMessage(specimen, Actions.updateSuccess);
	   } catch (Exception e) {
		  launchMessage(specimen, Actions.updateError);
	   }
	   resetForm();
    }

    public void delete() {
	   if (specimenSession.delete(specimen)) {
		  launchMessage(specimen, Actions.deleteSuccess);
		  allSpecimens.remove(specimen);
	   } else {
		  launchMessage(specimen, Actions.deleteError);
	   }
    }

    private void resetForm() {
	   specimen = new Specimen();
	   selectedCatalog = null;
	   selectedCollection = null;
	   selectedCollector = null;
	   selectedDeterminer = null;
	   selectedRegType = null;
	   selectedSampleType = null;
	   taxonomyBean.deselectAll();
	   locationBean.deselectAll();
    }

    private void updateLists() {
	   setAllCollector((List<AuthorRole>) specimenSession.findListByQuery("AuthorRole.findCollectors", AuthorRole.class));
	   setAllDeterminer((List<AuthorRole>) specimenSession.findListByQuery("AuthorRole.findDeterminers", AuthorRole.class));
	   setAllCollection((List<Collection>) specimenSession.findListByQuery("Collection.findAll", Collection.class));
	   setAllSampleTypes((List<SampleType>) specimenSession.findListByQuery("SampleType.findAll", SampleType.class));
	   setAllRegTypes((List<RegType>) specimenSession.findListByQuery("RegType.findAll", RegType.class));
    }

    private void prepareBeans() {
	   FacesContext fCon = FacesContext.getCurrentInstance();
	   locationBean = (LocationBean) fCon.getApplication().getELResolver().getValue(fCon.getELContext(), null, "locationBean");
	   taxonomyBean = (TaxonomyBean) fCon.getApplication().getELResolver().getValue(fCon.getELContext(), null, "taxonomyBean");
    }

    public void prepareCreate() {
	   prepareBeans();
	   resetForm();
	   updateLists();
    }

    public void prepareUpdate(Specimen onEdit) {
	   prepareBeans();
	   RequestContext context = RequestContext.getCurrentInstance();
	   context.reset("specimenWizardForm");
	   specimen = onEdit;
	   selectedCatalog = specimen.getIdCatalog().getIdCatalog().toString();
	   selectedCollection = specimen.getIdCatalog().getIdCollection().getIdCollection().toString();
	   filterCatalog();
	   selectedCollector = specimen.getIdCollector().getIdAuro().toString();
	   selectedDeterminer = specimen.getIdDeterminer().getIdAuro().toString();
	   selectedRegType = specimen.getIdRety().getIdRety().toString();
	   selectedSampleType = specimen.getIdSaty().getIdSaty().toString();
	   updateLists();
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

    public void filterCatalog() {
	   if (selectedCollection != null && !selectedCollection.equals("")) {
		  Collection col = collectionSession.findById(new Integer(selectedCollection));
		  setAllCatalog(col.getCatalogList());
	   } else {
		  setAllCatalog(null);
	   }
    }

    private RegType getRegTypeFromList(String idRegType) {
	   Integer idR;
	   try {
		  idR = new Integer(idRegType);
	   } catch (NumberFormatException e) {
		  return null;
	   }
	   for (RegType r : allRegTypes) {
		  if (r.getIdRety().equals(idR)) {
			 return r;
		  }
	   }
	   return null;
    }

    private SampleType getSamTypeFromList(String idSamType) {
	   Integer idS;
	   try {
		  idS = new Integer(idSamType);
	   } catch (NumberFormatException e) {
		  return null;
	   }
	   for (SampleType s : allSampleTypes) {
		  if (s.getIdSaty().equals(idS)) {
			 return s;
		  }
	   }
	   return null;
    }

    private Catalog getCatalogFromList(String idCatalog) {
	   Integer idC;
	   try {
		  idC = new Integer(idCatalog);
	   } catch (NumberFormatException e) {
		  return null;
	   }
	   for (Catalog c : allCatalog) {
		  if (c.getIdCatalog().equals(idC)) {
			 return c;
		  }
	   }
	   return null;
    }

    private Collection getCollectionFromList(String idCollection) {
	   Integer idC;
	   try {
		  idC = new Integer(idCollection);
	   } catch (NumberFormatException e) {
		  return null;
	   }
	   for (Collection c : allCollection) {
		  if (c.getIdCollection().equals(idC)) {
			 return c;
		  }
	   }
	   return null;
    }

    private AuthorRole getAuthorFromList(String idAuro) {
	   Integer idA;
	   try {
		  idA = new Integer(idAuro);
	   } catch (NumberFormatException e) {
		  return null;
	   }
	   for (AuthorRole ar : allCollector) {
		  if (ar.getIdAuro().equals(idA)) {
			 return ar;
		  }
	   }
	   for (AuthorRole ar : allDeterminer) {
		  if (ar.getIdAuro().equals(idA)) {
			 return ar;
		  }
	   }
	   return null;
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

    public AuthorRole getCollector() {
	   return collector;
    }

    public void setCollector(AuthorRole collector) {
	   this.collector = collector;
    }

    public AuthorRole getDeterminer() {
	   return determiner;
    }

    public void setDeterminer(AuthorRole determiner) {
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
	   return allSpecimens;
    }

    public List<Taxonomy> getAllTaxonomys() {
	   return allTaxonomys;
    }

    public List<Location> getAllLocations() {
	   return allLocations;
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
	   this.determiner = getAuthorFromList(selectedDeterminer);
    }

    public String getSelectedCollector() {
	   return selectedCollector;
    }

    public void setSelectedCollector(String selectedCollector) {
	   this.selectedCollector = selectedCollector;
	   this.collector = getAuthorFromList(selectedCollector);
    }

    public String getSelectedCollection() {
	   return selectedCollection;
    }

    public void setSelectedCollection(String selectedCollection) {
	   this.selectedCollection = selectedCollection;
	   this.collection = getCollectionFromList(selectedCollection);
    }

    public String getSelectedRegType() {
	   return selectedRegType;
    }

    public void setSelectedRegType(String selectedRegType) {
	   this.selectedRegType = selectedRegType;
	   this.regType = getRegTypeFromList(selectedRegType);
    }

    public String getSelectedSampleType() {
	   return selectedSampleType;
    }

    public void setSelectedSampleType(String selectedSampleType) {
	   this.selectedSampleType = selectedSampleType;
	   this.sampleType = getSamTypeFromList(selectedSampleType);
    }

    public String getSelectedCatalog() {
	   return selectedCatalog;
    }

    public void setSelectedCatalog(String selectedCatalog) {
	   this.selectedCatalog = selectedCatalog;
	   this.catalog = getCatalogFromList(selectedCatalog);
    }

    public Catalog getCatalog() {
	   return catalog;
    }

    public void setCatalog(Catalog catalog) {
	   this.catalog = catalog;
    }

    public Collection getCollection() {
	   return collection;
    }

    public void setCollection(Collection collection) {
	   this.collection = collection;
    }

    public String onFlowProcess(FlowEvent event) {
	   return event.getNewStep();
    }

    public String getHeader() {
	   return specimen != null && specimen.getIdSpecimen() != null ? "Editar a " + specimen.getCommonName() : "Registrar nuevo espécimen";
    }

    private void launchMessage(entNaming ent, Actions act) {
	   FacesContext.getCurrentInstance().addMessage(null, showMessage(ent, act));
    }

    public boolean isCreate() {
	   return create;
    }

    public void setCreate(boolean create) {
	   this.create = create;
    }

    private void setSpecimenfromId() {
	   this.specimen = getSpecimen(specimenDetail);
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

    public String getSpecimenDetail() {
	   return specimenDetail;
    }

    public void setSpecimenDetail(String specimenDetail) {
	   this.specimenDetail = specimenDetail;
	   setSpecimenfromId();
    }

    public String printJSON() {
	   JSONObject specimenInfo = new JSONObject();
	   if (specimen == null || specimen.getIdSpecimen() == null) {
		  specimenInfo.put("error", "No hay espécimen seleccionado");
	   } else {
		  JSONArray taxArray = new JSONArray();
		  JSONArray locArray = new JSONArray();
		  JSONObject taxData = new JSONObject();
		  JSONObject locData = new JSONObject();
		  JSONObject colData = new JSONObject();
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
		  JSONObject data;
		  for (Taxonomy t : taxList) {
			 data = new JSONObject();
			 data.put("label", t.getIdTaxlevel().getTaxlevelName());
			 data.put("value", t.getTaxonomyName());
			 taxArray.put(data);
		  }

		  for (Location l : locList) {
			 data = new JSONObject();
			 data.put("label", l.getIdLoclevel().getLoclevelName());
			 data.put("value", l.getLocationName());
			 locArray.put(data);
		  }

		  taxData.put("specificEpithet", specimen.getSpecificEpithet() == null ? "" : specimen.getSpecificEpithet());
		  taxData.put("commonName", specimen.getCommonName());
		  taxData.put("idenComment", specimen.getIdenComment() == null ? "" : specimen.getIdenComment());
		  taxData.put("determiner", specimen.getIdDeterminer().getIdAuthor().getAuthorName());
		  taxData.put("idenDate", formatDate(specimen.getIdenDate()));
		  locData.put("lat", specimen.getIdLocation().getLatitude());
		  locData.put("lon", specimen.getIdLocation().getLongitude());
		  locData.put("alt", specimen.getIdLocation().getAltitude());
		  locData.put("collector", specimen.getIdCollector().getIdAuthor().getAuthorName());
		  locData.put("collectDate", formatDate(specimen.getCollectDate()));
		  locData.put("collectComment", specimen.getCollectComment() == null ? "" : specimen.getCollectComment());
		  colData.put("companyName", specimen.getIdCatalog().getIdCollection().getCompanyName());
		  colData.put("regType", specimen.getIdRety().getRetyName());
		  colData.put("samType", specimen.getIdSaty().getSatyName());
		  colData.put("collectionName", specimen.getIdCatalog().getIdCollection().getCollectionName());
		  colData.put("idBioreg", specimen.getIdBioreg());
		  colData.put("catalogName", specimen.getIdCatalog().getCatalogName());
		  specimenInfo.put("taxArray", taxArray);
		  specimenInfo.put("locArray", locArray);
		  specimenInfo.put("taxData", taxData);
		  specimenInfo.put("locData", locData);
		  specimenInfo.put("colData", colData);
	   }
	   return specimenInfo.toString();
    }
}

package net.hpclab.cev.beans;

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
import net.hpclab.cev.entities.Catalog;
import net.hpclab.cev.entities.Collection;
import net.hpclab.cev.entities.Location;
import net.hpclab.cev.entities.RegType;
import net.hpclab.cev.entities.SampleType;
import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.entities.Taxonomy;
import net.hpclab.cev.services.DataBaseService;
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
public class SpecimenBean extends UtilsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private DataBaseService<Specimen> specimenService;
    private Specimen specimen;
    private Location location;
    private Taxonomy taxonomy;
    private RegType regType;
    private SampleType sampleType;
    private Catalog catalog;
    private Collection collection;
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
        try {
            specimenService = new DataBaseService<>(Specimen.class);
        } catch (Exception e) {

        }
    }

    @PostConstruct
    public void init() {
        try {
            if (allSpecimens == null) {
                allSpecimens = specimenService.getList("Specimen.findAll");
            }
        } catch (Exception e) {

        }
    }

    public void persist() {
        try {
            specimen.setIdTaxonomy(taxonomy);
            specimen.setIdLocation(location);
            specimen.setIdCatalog(catalog);
            specimen.setIdRety(regType);
            specimen.setIdSaty(sampleType);
            specimen = specimenService.persist(specimen);
            if (specimen != null && specimen.getIdSpecimen() != null) {
                allSpecimens.add(specimen);
            }
            resetForm();
        } catch (Exception ex) {

        }
    }

    public void edit() {
        try {
            specimen.setIdTaxonomy(taxonomy);
            specimen.setIdLocation(location);
            specimen.setIdCatalog(catalog);
            specimen.setIdRety(regType);
            specimen.setIdSaty(sampleType);
            specimen = specimenService.merge(specimen);
            allSpecimens.remove(specimen);
            allSpecimens.add(specimen);
        } catch (Exception e) {

        }
        resetForm();
    }

    public void delete() {
        try {
            specimenService.delete(specimen);
            allSpecimens.remove(specimen);
        } catch (Exception e) {

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
    }

    private void updateLists() {
    }

    private void prepareBeans() {

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

        selectedRegType = specimen.getIdRety().getIdRety().toString();
        selectedSampleType = specimen.getIdSaty().getIdSaty().toString();
        updateLists();
    }

    private void createSpecimenForm() {
        if (specimen == null || specimen.getIdSpecimen() == null) {
            return;
        }

        Taxonomy tax = specimen.getIdTaxonomy();
        ArrayList<Taxonomy> taxList = new ArrayList<>();
        while (tax != null) {
            taxList.add(0, tax);
            tax = tax.getIdContainer();
        }

        Location loc = specimen.getIdLocation();
        ArrayList<Location> locList = new ArrayList<>();
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

    }

    private RegType getRegTypeFromList(String idRegType) {
        Integer idR;
        try {
            idR = new Integer(idRegType);
        } catch (NumberFormatException e) {
            return null;
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
        return null;
    }

    private Catalog getCatalogFromList(String idCatalog) {
        Integer idC;
        try {
            idC = new Integer(idCatalog);
        } catch (NumberFormatException e) {
            return null;
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
            ArrayList<Taxonomy> taxList = new ArrayList<>();
            while (tax != null) {
                taxList.add(0, tax);
                tax = tax.getIdContainer();
            }

            Location loc = specimen.getIdLocation();
            ArrayList<Location> locList = new ArrayList<>();
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
            taxData.put("idenDate", formatDate(specimen.getIdenDate()));
            locData.put("lat", specimen.getIdLocation().getLatitude());
            locData.put("lon", specimen.getIdLocation().getLongitude());
            locData.put("alt", specimen.getIdLocation().getAltitude());
            locData.put("collectDate", formatDate(specimen.getCollectDate()));
            locData.put("collectComment", specimen.getCollectComment() == null ? "" : specimen.getCollectComment());
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

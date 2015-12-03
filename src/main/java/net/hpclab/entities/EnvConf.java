package net.hpclab.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "env_conf")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EnvConf.findAll", query = "SELECT e FROM EnvConf e"),
    @NamedQuery(name = "EnvConf.findByIdConfig", query = "SELECT e FROM EnvConf e WHERE e.idConfig = :idConfig"),
    @NamedQuery(name = "EnvConf.findByConfigName", query = "SELECT e FROM EnvConf e WHERE e.configName = :configName"),
    @NamedQuery(name = "EnvConf.findByConfigValue", query = "SELECT e FROM EnvConf e WHERE e.configValue = :configValue"),
    @NamedQuery(name = "EnvConf.findByConfigDesc", query = "SELECT e FROM EnvConf e WHERE e.configDesc = :configDesc")})
public class EnvConf implements entNaming, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_config")
    private Integer idConfig;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "config_name")
    private String configName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "config_value")
    private String configValue;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 512)
    @Column(name = "config_desc")
    private String configDesc;

    public EnvConf() {
    }

    public EnvConf(Integer idConfig) {
	   this.idConfig = idConfig;
    }

    public EnvConf(Integer idConfig, String configName, String configValue, String configDesc) {
	   this.idConfig = idConfig;
	   this.configName = configName;
	   this.configValue = configValue;
	   this.configDesc = configDesc;
    }

    public Integer getIdConfig() {
	   return idConfig;
    }

    public void setIdConfig(Integer idConfig) {
	   this.idConfig = idConfig;
    }

    public String getConfigName() {
	   return configName;
    }

    public void setConfigName(String configName) {
	   this.configName = configName;
    }

    public String getConfigValue() {
	   return configValue;
    }

    public void setConfigValue(String configValue) {
	   this.configValue = configValue;
    }

    public String getConfigDesc() {
	   return configDesc;
    }

    public void setConfigDesc(String configDesc) {
	   this.configDesc = configDesc;
    }

    @Override
    public int hashCode() {
	   int hash = 0;
	   hash += (idConfig != null ? idConfig.hashCode() : 0);
	   return hash;
    }

    @Override
    public boolean equals(Object object) {
	   if (!(object instanceof EnvConf)) {
		  return false;
	   }
	   EnvConf other = (EnvConf) object;
	   return (this.idConfig != null || other.idConfig == null) && (this.idConfig == null || this.idConfig.equals(other.idConfig));
    }

    @Override
    public String toString() {
	   return "net.hpclab.entities.EnvConf[ idConfig=" + idConfig + " ]";
    }

    @Override
    public String getEntityName() {
	   return "Configuración de Ambiente";
    }

    @Override
    public String getDescription() {
	   return "la variable " + configName + " con valor " + configValue;
    }
    
}


package modelo;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author JuanM
 */
@Entity
@Table(name = "PRUEBA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Prueba.findAll", query = "SELECT p FROM Prueba p"),
    @NamedQuery(name = "Prueba.findById", query = "SELECT p FROM Prueba p WHERE p.id = :id"),
    @NamedQuery(name = "Prueba.findByNombre", query = "SELECT p FROM Prueba p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Prueba.findByTipo", query = "SELECT p FROM Prueba p WHERE p.tipo = :tipo"),
    @NamedQuery(name = "Prueba.findByTiporesultado", query = "SELECT p FROM Prueba p WHERE p.tiporesultado = :tiporesultado"),

    @NamedQuery(name = "Prueba.findByNombreCompeticion", query = "SELECT p FROM Prueba p WHERE p.nombre = :nombre and p IN (SELECT c.pruebaId FROM Compuesta c WHERE c.competicionId.id = :competicionid)"),
    @NamedQuery(name = "Prueba.findByCompeticionId", query = "SELECT p FROM Prueba p JOIN p.compuestaCollection c WHERE c.competicionId.id = :id"),
    @NamedQuery(name = "Prueba.findNombresByCompeticionId", query = "SELECT p.nombre FROM Prueba p JOIN p.compuestaCollection c WHERE c.competicionId.id = :id"),
    @NamedQuery(name = "Prueba.findByNotCompeticionId", query = "SELECT p FROM Prueba p WHERE p NOT IN (SELECT p FROM Prueba p JOIN p.compuestaCollection c WHERE c.competicionId.id = :id)"),
    @NamedQuery(name = "Prueba.countRegistrosByPrueba", query = "SELECT COUNT(r) FROM Registro r WHERE r.pruebaId.id = :pruebaid"),
})
public class Prueba implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "NOMBRE")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "TIPO")
    private String tipo;
    @Basic(optional = false)
    @Column(name = "TIPORESULTADO")
    private String tiporesultado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pruebaId")
    private Collection<Compuesta> compuestaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pruebaasignada")
    private Collection<Participante> participanteCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pruebaId")
    private Collection<Registro> registroCollection;

    public Prueba() {
    }

    public Prueba(Integer id) {
        this.id = id;
    }

    public Prueba(Integer id, String nombre, String tipo, String tiporesultado) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.tiporesultado = tiporesultado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTiporesultado() {
        return tiporesultado;
    }

    public void setTiporesultado(String tiporesultado) {
        this.tiporesultado = tiporesultado;
    }

    @XmlTransient
    public Collection<Compuesta> getCompuestaCollection() {
        return compuestaCollection;
    }

    public void setCompuestaCollection(Collection<Compuesta> compuestaCollection) {
        this.compuestaCollection = compuestaCollection;
    }

    @XmlTransient
    public Collection<Participante> getParticipanteCollection() {
        return participanteCollection;
    }

    public void setParticipanteCollection(Collection<Participante> participanteCollection) {
        this.participanteCollection = participanteCollection;
    }

    @XmlTransient
    public Collection<Registro> getRegistroCollection() {
        return registroCollection;
    }

    public void setRegistroCollection(Collection<Registro> registroCollection) {
        this.registroCollection = registroCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prueba)) {
            return false;
        }
        Prueba other = (Prueba) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Prueba[ id=" + id + " ]";
    }
    
}


package modelo.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author JuanM
 */
@Entity
@Table(name = "COMPUESTA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Compuesta.findAll", query = "SELECT c FROM Compuesta c"),
    @NamedQuery(name = "Compuesta.findById", query = "SELECT c FROM Compuesta c WHERE c.id = :id"),
    @NamedQuery(name = "Compuesta.findByOrden", query = "SELECT c FROM Compuesta c WHERE c.orden = :orden"),
    
    @NamedQuery(name = "Compuesta.findByCompeticion", query = "SELECT c FROM Compuesta c WHERE c.competicionId.id = :competicionid"),
    @NamedQuery(name = "Compuesta.findByPrueba_Competicion", query = "SELECT c FROM Compuesta c WHERE c.pruebaId.id = :pruebaid and c.competicionId.id = :competicionid")})
public class Compuesta implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "ORDEN")
    private int orden;
    @JoinColumn(name = "PRUEBA_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Prueba pruebaId;
    @JoinColumn(name = "COMPETICION_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Competicion competicionId;

    public Compuesta() {
    }

    public Compuesta(Integer id) {
        this.id = id;
    }

    public Compuesta(Integer id, int orden) {
        this.id = id;
        this.orden = orden;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public Prueba getPruebaId() {
        return pruebaId;
    }

    public void setPruebaId(Prueba pruebaId) {
        this.pruebaId = pruebaId;
    }

    public Competicion getCompeticionId() {
        return competicionId;
    }

    public void setCompeticionId(Competicion competicionId) {
        this.competicionId = competicionId;
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
        if (!(object instanceof Compuesta)) {
            return false;
        }
        Compuesta other = (Compuesta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Compuesta[ id=" + id + " ]";
    }
    
}


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
@Table(name = "ADMINISTRADO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Administrado.findAll", query = "SELECT a FROM Administrado a"),
    @NamedQuery(name = "Administrado.findByCompeticion", query = "SELECT a FROM Administrado a WHERE a.competicionId = :compid"),
    @NamedQuery(name = "Administrado.findById", query = "SELECT a FROM Administrado a WHERE a.id = :id"),
    @NamedQuery(name = "Administrado.findCompeticionesByUser", query = "SELECT a.competicionId.nombre FROM Administrado a WHERE a.usuarioId.id = :userid"),
    @NamedQuery(name = "Administrado.findByUsuario", query = "SELECT a FROM Administrado a WHERE a.usuarioId.id = :usuarioid"),
    @NamedQuery(name = "Administrado.findByCompeticionAndUsuario", query = "SELECT a FROM Administrado a WHERE a.competicionId.nombre = :nombrecompeticion AND a.usuarioId.id = :usuarioid")
    })
public class Administrado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "USUARIO_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario usuarioId;
    @JoinColumn(name = "COMPETICION_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Competicion competicionId;

    public Administrado() {
    }

    public Administrado(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Usuario usuarioId) {
        this.usuarioId = usuarioId;
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
        if (!(object instanceof Administrado)) {
            return false;
        }
        Administrado other = (Administrado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Administrado[ id=" + id + " ]";
    }
    
}

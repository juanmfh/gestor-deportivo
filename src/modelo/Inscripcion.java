/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author JuanM
 */
@Entity
@Table(name = "INSCRIPCION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Inscripcion.findAll", query = "SELECT i FROM Inscripcion i"),
    @NamedQuery(name = "Inscripcion.findById", query = "SELECT i FROM Inscripcion i WHERE i.id = :id"),
    @NamedQuery(name = "Inscripcion.findByFecha", query = "SELECT i FROM Inscripcion i WHERE i.fecha = :fecha"),
    @NamedQuery(name = "Inscripcion.findByCompeticion", query = "SELECT i FROM Inscripcion i WHERE i.competicionId.id = :id"),
    @NamedQuery(name = "Inscripcion.findByCompeticionByGrupo", query = "SELECT i FROM Inscripcion i WHERE i.competicionId.id = :competicionid AND i.grupoId.id = :grupoid"),
    @NamedQuery(name = "Inscripcion.findByCompeticionByNombreGrupo", query = "SELECT i FROM Inscripcion i WHERE i.competicionId.id = :competicionid AND i.grupoId.nombre = :nombregrupo"),
    })
public class Inscripcion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @JoinColumn(name = "GRUPO_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Grupo grupoId;
    @JoinColumn(name = "COMPETICION_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Competicion competicionId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inscripcionId")
    private Collection<Registro> registroCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inscripcionId")
    private Collection<Compite> compiteCollection;

    public Inscripcion() {
    }

    public Inscripcion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Grupo getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Grupo grupoId) {
        this.grupoId = grupoId;
    }

    public Competicion getCompeticionId() {
        return competicionId;
    }

    public void setCompeticionId(Competicion competicionId) {
        this.competicionId = competicionId;
    }

    @XmlTransient
    public Collection<Registro> getRegistroCollection() {
        return registroCollection;
    }

    public void setRegistroCollection(Collection<Registro> registroCollection) {
        this.registroCollection = registroCollection;
    }

    @XmlTransient
    public Collection<Compite> getCompiteCollection() {
        return compiteCollection;
    }

    public void setCompiteCollection(Collection<Compite> compiteCollection) {
        this.compiteCollection = compiteCollection;
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
        if (!(object instanceof Inscripcion)) {
            return false;
        }
        Inscripcion other = (Inscripcion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pruebadatabase.model.Inscripcion[ id=" + id + " ]";
    }
    
}

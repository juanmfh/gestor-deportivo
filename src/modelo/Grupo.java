/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "GRUPO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Grupo.findAll", query = "SELECT g FROM Grupo g"),
    @NamedQuery(name = "Grupo.findById", query = "SELECT g FROM Grupo g WHERE g.id = :id"),
    @NamedQuery(name = "Grupo.findByNombre", query = "SELECT g FROM Grupo g WHERE g.nombre = :nombre"),
    @NamedQuery(name = "Grupo.findByDescripcion", query = "SELECT g FROM Grupo g WHERE g.descripcion = :descripcion"),
    @NamedQuery(name = "Grupo.findByCompeticion", query = "SELECT g FROM Grupo g JOIN g.inscripcionCollection i WHERE i.competicionId.id = :id"),   
    @NamedQuery(name = "Grupo.findByGrupoId", query = "SELECT g FROM Grupo g WHERE g.grupoId.id = :id"),
    @NamedQuery(name = "Grupo.findByNombreAndCompeticion", query = "SELECT g FROM Grupo g JOIN g.inscripcionCollection i WHERE g.nombre = :nombre and i.competicionId.id = :competicionid "),
    @NamedQuery(name = "Grupo.findByPersonaCompeticion", query = "SELECT g FROM Grupo g JOIN g.participaCollection p WHERE p IN (SELECT p2 FROM Participa p2 WHERE p2.participanteId.personaId.id = :personaid) AND g IN (SELECT g FROM Grupo g JOIN g.inscripcionCollection i WHERE i.competicionId.id = :competicionid )"),
    @NamedQuery(name = "Grupo.findByEquipoCompeticion", query = "SELECT g FROM Grupo g JOIN g.participaCollection p WHERE p IN (SELECT p2 FROM Participa p2 WHERE p2.participanteId.equipoId.id = :equipoid) AND g IN (SELECT g FROM Grupo g JOIN g.inscripcionCollection i WHERE i.competicionId.id = :competicionid )")
    
})
public class Grupo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "grupoId")
    private Collection<Participa> participaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "grupoId")
    private Collection<Acceso> accesoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "grupoId")
    private Collection<Inscripcion> inscripcionCollection;
    @OneToMany(mappedBy = "grupoId")
    private Collection<Grupo> grupoCollection;
    @JoinColumn(name = "GRUPO_ID", referencedColumnName = "ID")
    @ManyToOne
    private Grupo grupoId;

    public Grupo() {
    }

    public Grupo(Integer id) {
        this.id = id;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public Collection<Participa> getParticipaCollection() {
        return participaCollection;
    }

    public void setParticipaCollection(Collection<Participa> participaCollection) {
        this.participaCollection = participaCollection;
    }

    @XmlTransient
    public Collection<Acceso> getAccesoCollection() {
        return accesoCollection;
    }

    public void setAccesoCollection(Collection<Acceso> accesoCollection) {
        this.accesoCollection = accesoCollection;
    }

    @XmlTransient
    public Collection<Inscripcion> getInscripcionCollection() {
        return inscripcionCollection;
    }

    public void setInscripcionCollection(Collection<Inscripcion> inscripcionCollection) {
        this.inscripcionCollection = inscripcionCollection;
    }

    @XmlTransient
    public Collection<Grupo> getGrupoCollection() {
        return grupoCollection;
    }

    public void setGrupoCollection(Collection<Grupo> grupoCollection) {
        this.grupoCollection = grupoCollection;
    }

    public Grupo getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Grupo grupoId) {
        this.grupoId = grupoId;
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
        if (!(object instanceof Grupo)) {
            return false;
        }
        Grupo other = (Grupo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pruebadatabase.model.Grupo[ id=" + id + " ]";
    }
    
}

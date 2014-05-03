/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modelo;

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
@Table(name = "MIEMBRO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Miembro.findAll", query = "SELECT m FROM Miembro m"),
    @NamedQuery(name = "Miembro.findById", query = "SELECT m FROM Miembro m WHERE m.id = :id"),
    @NamedQuery(name = "Miembro.findByEquipo", query = "SELECT m FROM Miembro m WHERE m.equipoId.id = :id"),
    @NamedQuery(name = "Miembro.findByPersona", query = "SELECT m FROM Miembro m WHERE m.personaId.id = :personaid"),
    @NamedQuery(name = "Miembro.findByPersonaGrupo", query = "SELECT m FROM Miembro m WHERE m.personaId.id = :personaid and m.equipoId in (SELECT e FROM Equipo e JOIN e.participanteCollection p WHERE p IN (SELECT par.participanteId FROM Participa par WHERE par.grupoId.id = :grupoid))")
})
public class Miembro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "PERSONA_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Persona personaId;
    @JoinColumn(name = "EQUIPO_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Equipo equipoId;

    public Miembro() {
    }

    public Miembro(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Persona getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Persona personaId) {
        this.personaId = personaId;
    }

    public Equipo getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Equipo equipoId) {
        this.equipoId = equipoId;
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
        if (!(object instanceof Miembro)) {
            return false;
        }
        Miembro other = (Miembro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pruebadatabase.model.Miembro[ id=" + id + " ]";
    }
    
}

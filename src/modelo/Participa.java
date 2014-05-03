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
@Table(name = "PARTICIPA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Participa.findAll", query = "SELECT p FROM Participa p"),
    @NamedQuery(name = "Participa.findById", query = "SELECT p FROM Participa p WHERE p.id = :id"),
    @NamedQuery (name = "Participa.findByGrupo", query = "SELECT p FROM Participa p WHERE p.grupoId.id = :grupoid"),
    @NamedQuery (name = "Participa.findEquiposByGrupo", query = "SELECT p FROM Participa p WHERE p.grupoId.id = :grupoid and p.participanteId.equipoId IS NOT NULL"),
    @NamedQuery (name = "Participa.findPersonasByGrupo", query = "SELECT p FROM Participa p WHERE p.grupoId.id = :grupoid and p.participanteId.personaId IS NOT NULL"),
    @NamedQuery(name = "Participa.findByCompeticionAndParticipante", query = "SELECT p FROM Participa p WHERE p.participanteId.id = :participanteid and p.grupoId IN (SELECT g FROM Grupo g JOIN g.inscripcionCollection i WHERE i.competicionId.id = :competicionid)"),
    @NamedQuery(name = "Participa.countByParticipante", query = "SELECT COUNT(p) FROM Participa p WHERE p.participanteId.id = :participanteid"),
    @NamedQuery (name = "Participa.findByEquipoCompeticion", query = "SELECT p FROM Participa p WHERE p.grupoId IN (SELECT g FROM Grupo g JOIN g.inscripcionCollection i WHERE i.competicionId.id = :competicionid) and p.participanteId IS NOT NULL and p.participanteId.equipoId.id = :equipoid")
    })
public class Participa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "PARTICIPANTE_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Participante participanteId;
    @JoinColumn(name = "GRUPO_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Grupo grupoId;

    public Participa() {
    }

    public Participa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Participante getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Participante participanteId) {
        this.participanteId = participanteId;
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
        if (!(object instanceof Participa)) {
            return false;
        }
        Participa other = (Participa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pruebadatabase.model.Participa[ id=" + id + " ]";
    }
    
}

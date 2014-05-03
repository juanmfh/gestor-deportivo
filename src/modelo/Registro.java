/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modelo;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author JuanM
 */
@Entity
@Table(name = "REGISTRO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Registro.findAll", query = "SELECT r FROM Registro r"),
    @NamedQuery(name = "Registro.findById", query = "SELECT r FROM Registro r WHERE r.id = :id"),
    @NamedQuery(name = "Registro.findByTiempo", query = "SELECT r FROM Registro r WHERE r.tiempo = :tiempo"),
    @NamedQuery(name = "Registro.findByNum", query = "SELECT r FROM Registro r WHERE r.num = :num"),
    @NamedQuery(name = "Registro.findByNumIntento", query = "SELECT r FROM Registro r WHERE r.numIntento = :numIntento"),
    @NamedQuery(name = "Registro.findBySorteo", query = "SELECT r FROM Registro r WHERE r.sorteo = :sorteo"),
    @NamedQuery(name = "Registro.findByParticipante", query = "SELECT r FROM Registro r WHERE r.participanteId.id = :participanteid"),
    @NamedQuery(name = "Registro.findByInscripcion", query = "SELECT r FROM Registro r WHERE r.inscripcionId.id = :inscripcionid"),
    @NamedQuery(name = "Registro.findByPrueba", query = "SELECT r FROM Registro r WHERE r.pruebaId.id = :pruebaid"),
    
    @NamedQuery(name = "Registro.findByCompeticion", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid"),
    @NamedQuery(name = "Registro.findByCompeticionIndividual", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.participanteId.equipoId IS NULL"),
    @NamedQuery(name = "Registro.findByCompeticionEquipo", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.participanteId.personaId IS NULL"),
    
    @NamedQuery(name = "Registro.findByCompeticionPrueba", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.pruebaId.id = :pruebaid"),
    @NamedQuery(name = "Registro.findByCompeticionPruebaIndividual", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.pruebaId.id = :pruebaid  AND r.participanteId.equipoId is NULL"),
    @NamedQuery(name = "Registro.findByCompeticionPruebaEquipo", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.pruebaId.id = :pruebaid AND r.participanteId.personaId is NULL"),
    
    @NamedQuery(name = "Registro.findByCompeticionGrupo", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.inscripcionId.grupoId.id = :grupoid"),
    @NamedQuery(name = "Registro.findByCompeticionGrupoIndividual", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.inscripcionId.grupoId.id = :grupoid AND r.participanteId.equipoId is NULL"),
    @NamedQuery(name = "Registro.findByCompeticionGrupoEquipo", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.inscripcionId.grupoId.id = :grupoid AND r.participanteId.personaId is NULL"),
    
    @NamedQuery(name = "Registro.findByCompeticionGrupoPrueba", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.inscripcionId.grupoId.id = :grupoid AND r.pruebaId.id = :pruebaid"),
    @NamedQuery(name = "Registro.findByCompeticionGrupoPruebaIndividual", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.inscripcionId.grupoId.id = :grupoid AND r.pruebaId.id = :pruebaid AND r.participanteId.equipoId is NULL"),
    @NamedQuery(name = "Registro.findByCompeticionGrupoPruebaEquipo", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.inscripcionId.grupoId.id = :grupoid AND r.pruebaId.id = :pruebaid AND r.participanteId.personaId is NULL"),
    
    @NamedQuery(name = "Registro.findMinNumIntentoByInscripcionPruebaParticipante", query = "SELECT MAX(r.numIntento) FROM Registro r WHERE r.inscripcionId.id = :inscripcionid AND r.participanteId.id = :participanteid AND r.pruebaId.id = :pruebaid"),
    
    @NamedQuery(name = "Registro.findParticipantesConRegistrosNum", query = "SELECT DISTINCT r.participanteId FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.pruebaId.id = :pruebaid AND r.num = (SELECT MAX(r2.num) FROM Registro r2 WHERE r2.participanteId = r.participanteId AND r2.inscripcionId = r.inscripcionId AND r2.pruebaId = r.pruebaId) ORDER BY r.num DESC  ") ,
    @NamedQuery(name = "Registro.findParticipantesConRegistrosTiempo", query = "SELECT DISTINCT r.participanteId FROM Registro r WHERE r.inscripcionId.competicionId.id =:competicionid AND r.pruebaId.id = :pruebaid AND r.tiempo = (SELECT MIN(r2.tiempo) FROM Registro r2 WHERE r2.participanteId = r.participanteId AND r2.inscripcionId = r.inscripcionId AND r2.pruebaId = r.pruebaId) ORDER BY r.tiempo "),
    
    @NamedQuery(name = "Registro.findRegistroByParticipantePruebaCompeticionOrderByNumIntento", query = "SELECT r FROM Registro r WHERE r.inscripcionId.competicionId.id = :competicionid AND r.pruebaId.id = :pruebaid AND r.participanteId.id = :participanteid ORDER BY r.numIntento"),
    
    @NamedQuery(name = "Registro.findMaxRegistroByParticipantePruebaCompeticion", query = "SELECT MAX(r.num) FROM Registro r WHERE r.participanteId.id = :participanteid AND r.inscripcionId.competicionId.id = :competicionid AND r.pruebaId.id = :pruebaid"),
    @NamedQuery(name = "Registro.findMinRegistroByParticipantePruebaCompeticion", query = "SELECT MIN(r.tiempo) FROM Registro r WHERE r.participanteId.id = :participanteid AND r.inscripcionId.competicionId.id = :competicionid AND r.pruebaId.id = :pruebaid"),
        
})
public class Registro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "TIEMPO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tiempo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "NUM")
    private Double num;
    @Basic(optional = false)
    @Column(name = "NUM_INTENTO")
    private int numIntento;
    @Basic(optional = false)
    @Column(name = "SORTEO")
    private int sorteo;
    @JoinColumn(name = "PRUEBA_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Prueba pruebaId;
    @JoinColumn(name = "PERSONA_ID", referencedColumnName = "ID")
    @ManyToOne
    private Persona personaId;
    @JoinColumn(name = "PARTICIPANTE_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Participante participanteId;
    @JoinColumn(name = "INSCRIPCION_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Inscripcion inscripcionId;

    public Registro() {
    }

    public Registro(Integer id) {
        this.id = id;
    }

    public Registro(Integer id, int numIntento, int sorteo) {
        this.id = id;
        this.numIntento = numIntento;
        this.sorteo = sorteo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTiempo() {
        return tiempo;
    }

    public void setTiempo(Date tiempo) {
        this.tiempo = tiempo;
    }

    public Double getNum() {
        return num;
    }

    public void setNum(Double num) {
        this.num = num;
    }

    public int getNumIntento() {
        return numIntento;
    }

    public void setNumIntento(int numIntento) {
        this.numIntento = numIntento;
    }

    public int getSorteo() {
        return sorteo;
    }

    public void setSorteo(int sorteo) {
        this.sorteo = sorteo;
    }

    public Prueba getPruebaId() {
        return pruebaId;
    }

    public void setPruebaId(Prueba pruebaId) {
        this.pruebaId = pruebaId;
    }

    public Persona getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Persona personaId) {
        this.personaId = personaId;
    }

    public Participante getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Participante participanteId) {
        this.participanteId = participanteId;
    }

    public Inscripcion getInscripcionId() {
        return inscripcionId;
    }

    public void setInscripcionId(Inscripcion inscripcionId) {
        this.inscripcionId = inscripcionId;
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
        if (!(object instanceof Registro)) {
            return false;
        }
        Registro other = (Registro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pruebadatabase.model.Registro[ id=" + id + " ]";
    }
    
}

package br.gov.mj.side.entidades.programa.inscricao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.programa.ProgramaCriterioElegibilidade;

@Entity
@Table(name = "tb_ipe_inscricao_programa_criterio_elegibilidade", schema = "side")
public class InscricaoProgramaCriterioElegibilidade extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ipe_id_inscricao_programa_criterio_elegibilidade")
    @SequenceGenerator(name = "tb_ipe_inscricao_programa_criterio_elegibilidade_generator", sequenceName = "side.seq_tb_ipe_inscricao_programa_criterio_elegibilidade", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ipe_inscricao_programa_criterio_elegibilidade_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ipe_fk_ipg_id_inscricao_programa")
    @NotNull
    private InscricaoPrograma inscricaoPrograma;

    @ManyToOne
    @JoinColumn(name = "ipe_fk_pce_id_programa_criterio_elegibilidade")
    @NotNull
    private ProgramaCriterioElegibilidade programaCriterioElegibilidade;

    @Column(name = "ipe_bo_atende_criterio_elegibilidade")
    private Boolean atendeCriterioElegibilidade;

    @Column(name = "ipe_bo_aceita_criterio_elegibilidade")
    private Boolean aceitaCriterioElegibilidade;

    @Column(name = "ipe_ds_descricao_motivo")
    private String descricaoMotivo;

    @OneToMany(mappedBy = "inscricaoProgramaCriterioElegibilidade", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<InscricaoAnexoElegibilidade> anexos = new ArrayList<InscricaoAnexoElegibilidade>();

    public InscricaoProgramaCriterioElegibilidade() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InscricaoPrograma getInscricaoPrograma() {
        return inscricaoPrograma;
    }

    public void setInscricaoPrograma(InscricaoPrograma inscricaoPrograma) {
        this.inscricaoPrograma = inscricaoPrograma;
    }

    public ProgramaCriterioElegibilidade getProgramaCriterioElegibilidade() {
        return programaCriterioElegibilidade;
    }

    public void setProgramaCriterioElegibilidade(ProgramaCriterioElegibilidade programaCriterioElegibilidade) {
        this.programaCriterioElegibilidade = programaCriterioElegibilidade;
    }

    public Boolean getAtendeCriterioElegibilidade() {
        return atendeCriterioElegibilidade;
    }

    public void setAtendeCriterioElegibilidade(Boolean atendeCriterioElegibilidade) {
        this.atendeCriterioElegibilidade = atendeCriterioElegibilidade;
    }

    public List<InscricaoAnexoElegibilidade> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<InscricaoAnexoElegibilidade> anexos) {
        this.anexos = anexos;
    }

    public Boolean getAceitaCriterioElegibilidade() {
        return aceitaCriterioElegibilidade;
    }

    public void setAceitaCriterioElegibilidade(Boolean aceitaCriterioElegibilidade) {
        this.aceitaCriterioElegibilidade = aceitaCriterioElegibilidade;
    }

    public String getDescricaoMotivo() {
        return descricaoMotivo;
    }

    public void setDescricaoMotivo(String descricaoMotivo) {
        this.descricaoMotivo = descricaoMotivo;
    }

}
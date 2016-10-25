package br.gov.mj.side.entidades.programa.inscricao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.programa.ProgramaBem;

@Entity
@Table(name = "tb_ipb_inscricao_programa_bem", schema = "side")
public class InscricaoProgramaBem extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ipb_id_inscricao_programa_bem")
    @SequenceGenerator(name = "tb_ipb_inscricao_programa_bem_generator", sequenceName = "side.seq_tb_ipb_inscricao_programa_bem", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ipb_inscricao_programa_bem_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ipb_fk_prb_id_programa_bem")
    @NotNull
    private ProgramaBem programaBem;

    @ManyToOne
    @JoinColumn(name = "ipb_fk_ipg_id_inscricao_programa")
    @NotNull
    private InscricaoPrograma inscricaoPrograma;

    @Column(name = "ipb_qt_quantidade")
    @NotNull
    private Integer quantidade;

    public InscricaoProgramaBem() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgramaBem getProgramaBem() {
        return programaBem;
    }

    public void setProgramaBem(ProgramaBem programaBem) {
        this.programaBem = programaBem;
    }

    public InscricaoPrograma getInscricaoPrograma() {
        return inscricaoPrograma;
    }

    public void setInscricaoPrograma(InscricaoPrograma inscricaoPrograma) {
        this.inscricaoPrograma = inscricaoPrograma;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

}
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
import br.gov.mj.side.entidades.programa.ProgramaKit;

@Entity
@Table(name = "tb_ipk_inscricao_programa_kit", schema = "side")
public class InscricaoProgramaKit extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ipk_id_inscricao_programa_kit")
    @SequenceGenerator(name = "tb_ipk_inscricao_programa_kit_generator", sequenceName = "side.seq_tb_ipk_inscricao_programa_kit", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ipk_inscricao_programa_kit_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ipk_fk_prk_id_programa_kit")
    @NotNull
    private ProgramaKit programaKit;

    @ManyToOne
    @JoinColumn(name = "ipk_fk_ipg_id_inscricao_programa")
    @NotNull
    private InscricaoPrograma inscricaoPrograma;

    @Column(name = "ipk_qt_quantidade")
    @NotNull
    private Integer quantidade;

    public InscricaoProgramaKit() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgramaKit getProgramaKit() {
        return programaKit;
    }

    public void setProgramaKit(ProgramaKit programaKit) {
        this.programaKit = programaKit;
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
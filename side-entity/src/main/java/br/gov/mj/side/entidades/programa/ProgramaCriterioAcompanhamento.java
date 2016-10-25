package br.gov.mj.side.entidades.programa;

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

@Entity
@Table(name = "tb_pca_programa_criterio_acompanhamento", schema = "side")
public class ProgramaCriterioAcompanhamento extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pca_id_programa_criterio_acompanhamento")
    @SequenceGenerator(name = "tb_pca_programa_criterio_acompanhamento_generator", sequenceName = "side.seq_tb_pca_programa_criterio_acompanhamento", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_pca_programa_criterio_acompanhamento_generator")
    @NotNull
    private Long id;

    @Column(name = "pca_no_nome_criterio_acompanhamento")
    @NotNull
    private String nomeCriterioAcompanhamento;

    @Column(name = "pca_ds_descricao_criterio_acompanhamento")
    @NotNull
    private String descricaoCriterioAcompanhamento;

    @Column(name = "pca_ds_forma_acompanhamento")
    @NotNull
    private String formaAcompanhamento;

    @ManyToOne
    @JoinColumn(name = "pca_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    public ProgramaCriterioAcompanhamento() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCriterioAcompanhamento() {
        return nomeCriterioAcompanhamento;
    }

    public void setNomeCriterioAcompanhamento(String nomeCriterioAcompanhamento) {
        this.nomeCriterioAcompanhamento = nomeCriterioAcompanhamento;
    }

    public String getDescricaoCriterioAcompanhamento() {
        return descricaoCriterioAcompanhamento;
    }

    public void setDescricaoCriterioAcompanhamento(String descricaoCriterioAcompanhamento) {
        this.descricaoCriterioAcompanhamento = descricaoCriterioAcompanhamento;
    }

    public String getFormaAcompanhamento() {
        return formaAcompanhamento;
    }

    public void setFormaAcompanhamento(String formaAcompanhamento) {
        this.formaAcompanhamento = formaAcompanhamento;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

}
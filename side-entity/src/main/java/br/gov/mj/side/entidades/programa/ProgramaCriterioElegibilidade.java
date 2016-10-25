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
@Table(name = "tb_pce_programa_criterio_elegibilidade", schema = "side")
public class ProgramaCriterioElegibilidade extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pce_id_programa_criterio_elegibilidade")
    @SequenceGenerator(name = "tb_pce_programa_criterio_elegibilidade_generator", sequenceName = "side.seq_tb_pce_programa_criterio_elegibilidade", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_pce_programa_criterio_elegibilidade_generator")
    @NotNull
    private Long id;

    @Column(name = "pce_no_nome_criterio_elegibilidade")
    @NotNull
    private String nomeCriterioElegibilidade;

    @Column(name = "pce_ds_descricao_criterio_elegibilidade")
    @NotNull
    private String descricaoCriterioElegibilidade;

    @Column(name = "pce_ds_forma_verificacao")
    @NotNull
    private String formaVerificacao;

    @ManyToOne
    @JoinColumn(name = "pce_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @Column(name = "pce_bo_possui_obrigatoriedade_anexo")
    @NotNull
    private Boolean possuiObrigatoriedadeDeAnexo;

    public ProgramaCriterioElegibilidade() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCriterioElegibilidade() {
        return nomeCriterioElegibilidade;
    }

    public void setNomeCriterioElegibilidade(String nomeCriterioElegibilidade) {
        this.nomeCriterioElegibilidade = nomeCriterioElegibilidade;
    }

    public String getDescricaoCriterioElegibilidade() {
        return descricaoCriterioElegibilidade;
    }

    public void setDescricaoCriterioElegibilidade(String descricaoCriterioElegibilidade) {
        this.descricaoCriterioElegibilidade = descricaoCriterioElegibilidade;
    }

    public String getFormaVerificacao() {
        return formaVerificacao;
    }

    public void setFormaVerificacao(String formaVerificacao) {
        this.formaVerificacao = formaVerificacao;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public Boolean getPossuiObrigatoriedadeDeAnexo() {
        return possuiObrigatoriedadeDeAnexo;
    }

    public void setPossuiObrigatoriedadeDeAnexo(Boolean possuiObrigatoriedadeDeAnexo) {
        this.possuiObrigatoriedadeDeAnexo = possuiObrigatoriedadeDeAnexo;
    }

}
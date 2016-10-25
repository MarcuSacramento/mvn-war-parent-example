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
@Table(name = "tb_car_criterio_avaliacao_opcao_resposta", schema = "side")
public class ProgramaCriterioAvaliacaoOpcaoResposta extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "car_id_criterio_avaliacao_opcao_resposta")
    @SequenceGenerator(name = "tb_car_criterio_avaliacao_opcao_resposta_generator", sequenceName = "side.seq_tb_car_criterio_avaliacao_opcao_resposta", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_car_criterio_avaliacao_opcao_resposta_generator")
    @NotNull
    private Long id;

    @Column(name = "car_ds_descricao_opcao_resposta")
    @NotNull
    private String descricaoOpcaoResposta;

    @Column(name = "car_nt_nota_opcao_resposta")
    @NotNull
    private Integer notaOpcaoResposta;

    @ManyToOne
    @JoinColumn(name = "car_fk_pcv_id_programa_criterio_avaliacao")
    @NotNull
    private ProgramaCriterioAvaliacao criterioAvaliacao;

    public ProgramaCriterioAvaliacaoOpcaoResposta() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricaoOpcaoResposta() {
        return descricaoOpcaoResposta;
    }

    public void setDescricaoOpcaoResposta(String descricaoOpcaoResposta) {
        this.descricaoOpcaoResposta = descricaoOpcaoResposta;
    }

    public Integer getNotaOpcaoResposta() {
        return notaOpcaoResposta;
    }

    public void setNotaOpcaoResposta(Integer notaOpcaoResposta) {
        this.notaOpcaoResposta = notaOpcaoResposta;
    }

    public ProgramaCriterioAvaliacao getCriterioAvaliacao() {
        return criterioAvaliacao;
    }

    public void setCriterioAvaliacao(ProgramaCriterioAvaliacao criterioAvaliacao) {
        this.criterioAvaliacao = criterioAvaliacao;
    }

}
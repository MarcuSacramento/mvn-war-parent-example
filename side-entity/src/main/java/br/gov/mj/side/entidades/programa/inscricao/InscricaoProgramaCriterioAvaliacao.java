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
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacao;

@Entity
@Table(name = "tb_ipa_inscricao_programa_criterio_avaliacao", schema = "side")
public class InscricaoProgramaCriterioAvaliacao extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ipa_id_inscricao_programa_criterio_avaliacao")
    @SequenceGenerator(name = "tb_ipa_inscricao_programa_criterio_avaliacao_generator", sequenceName = "side.seq_tb_ipa_inscricao_programa_criterio_avaliacao", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ipa_inscricao_programa_criterio_avaliacao_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ipa_fk_ipg_id_inscricao_programa")
    @NotNull
    private InscricaoPrograma inscricaoPrograma;

    @ManyToOne
    @JoinColumn(name = "ipa_fk_pcv_id_programa_criterio_avaliacao")
    @NotNull
    private ProgramaCriterioAvaliacao programaCriterioAvaliacao;

    @Column(name = "ipa_ds_descricao_resposta")
    private String descricaoResposta;

    @Column(name = "ipa_bo_aceita_criterio_avaliacao")
    private Boolean aceitaCriterioAvaliacao;

    @Column(name = "ipa_ds_descricao_motivo")
    private String descricaoMotivo;

    @Column(name = "ipa_vl_valor_nota")
    private Integer notaCriterio;

    @OneToMany(mappedBy = "inscricaoProgramaCriterioAvaliacao", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<InscricaoAnexoAvaliacao> anexos = new ArrayList<InscricaoAnexoAvaliacao>();

    public InscricaoProgramaCriterioAvaliacao() {
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

    public ProgramaCriterioAvaliacao getProgramaCriterioAvaliacao() {
        return programaCriterioAvaliacao;
    }

    public void setProgramaCriterioAvaliacao(ProgramaCriterioAvaliacao programaCriterioAvaliacao) {
        this.programaCriterioAvaliacao = programaCriterioAvaliacao;
    }

    public String getDescricaoResposta() {
        return descricaoResposta;
    }

    public void setDescricaoResposta(String descricaoResposta) {
        this.descricaoResposta = descricaoResposta;
    }

    public List<InscricaoAnexoAvaliacao> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<InscricaoAnexoAvaliacao> anexos) {
        this.anexos = anexos;
    }

    public Boolean getAceitaCriterioAvaliacao() {
        return aceitaCriterioAvaliacao;
    }

    public void setAceitaCriterioAvaliacao(Boolean aceitaCriterioAvaliacao) {
        this.aceitaCriterioAvaliacao = aceitaCriterioAvaliacao;
    }

    public String getDescricaoMotivo() {
        return descricaoMotivo;
    }

    public void setDescricaoMotivo(String descricaoMotivo) {
        this.descricaoMotivo = descricaoMotivo;
    }

    public Integer getNotaCriterio() {
        return notaCriterio;
    }

    public void setNotaCriterio(Integer notaCriterio) {
        this.notaCriterio = notaCriterio;
    }

}
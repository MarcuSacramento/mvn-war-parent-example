package br.gov.mj.side.entidades.programa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumTipoResposta;

@Entity
@Table(name = "tb_pcv_programa_criterio_avaliacao", schema = "side")
public class ProgramaCriterioAvaliacao extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pcv_id_programa_criterio_avaliacao")
    @SequenceGenerator(name = "tb_pcv_programa_criterio_avaliacao_generator", sequenceName = "side.seq_tb_pcv_programa_criterio_avaliacao", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_pcv_programa_criterio_avaliacao_generator")
    @NotNull
    private Long id;

    @Column(name = "pcv_no_nome_criterio_avaliacao")
    @NotNull
    private String nomeCriterioAvaliacao;

    @Column(name = "pcv_ds_descricao_criterio_avaliacao")
    @NotNull
    private String descricaoCriterioAvaliacao;

    @Column(name = "pcv_ds_forma_verificacao")
    @NotNull
    private String formaVerificacao;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoResposta", name = "enumClassName"))
    @Column(name = "pcv_tp_tipo_resposta")
    @NotNull
    private EnumTipoResposta tipoResposta;

    @Column(name = "pcv_vl_peso_resposta")
    @NotNull
    private Integer pesoResposta;

    @Column(name = "pcv_bo_anexo_obrigatorio")
    @NotNull
    private Boolean possuiObrigatoriedadeDeAnexo;

    @Column(name = "pcv_bo_criterio_desempate")
    @NotNull
    private Boolean utilizadoParaCriterioDesempate;

    @ManyToOne
    @JoinColumn(name = "pcv_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @OneToMany(fetch=FetchType.EAGER,mappedBy = "criterioAvaliacao", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ProgramaCriterioAvaliacaoOpcaoResposta> criteriosAvaliacaoOpcaoResposta = new ArrayList<ProgramaCriterioAvaliacaoOpcaoResposta>();

    public ProgramaCriterioAvaliacao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCriterioAvaliacao() {
        return nomeCriterioAvaliacao;
    }

    public void setNomeCriterioAvaliacao(String nomeCriterioAvaliacao) {
        this.nomeCriterioAvaliacao = nomeCriterioAvaliacao;
    }

    public String getDescricaoCriterioAvaliacao() {
        return descricaoCriterioAvaliacao;
    }

    public void setDescricaoCriterioAvaliacao(String descricaoCriterioAvaliacao) {
        this.descricaoCriterioAvaliacao = descricaoCriterioAvaliacao;
    }

    public String getFormaVerificacao() {
        return formaVerificacao;
    }

    public void setFormaVerificacao(String formaVerificacao) {
        this.formaVerificacao = formaVerificacao;
    }

    public EnumTipoResposta getTipoResposta() {
        return tipoResposta;
    }

    public void setTipoResposta(EnumTipoResposta tipoResposta) {
        this.tipoResposta = tipoResposta;
    }

    public Integer getPesoResposta() {
        return pesoResposta;
    }

    public void setPesoResposta(Integer pesoResposta) {
        this.pesoResposta = pesoResposta;
    }

    public Boolean getPossuiObrigatoriedadeDeAnexo() {
        return possuiObrigatoriedadeDeAnexo;
    }

    public void setPossuiObrigatoriedadeDeAnexo(Boolean possuiObrigatoriedadeDeAnexo) {
        this.possuiObrigatoriedadeDeAnexo = possuiObrigatoriedadeDeAnexo;
    }

    public Boolean getUtilizadoParaCriterioDesempate() {
        return utilizadoParaCriterioDesempate;
    }

    public void setUtilizadoParaCriterioDesempate(Boolean utilizadoParaCriterioDesempate) {
        this.utilizadoParaCriterioDesempate = utilizadoParaCriterioDesempate;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public List<ProgramaCriterioAvaliacaoOpcaoResposta> getCriteriosAvaliacaoOpcaoResposta() {
        return criteriosAvaliacaoOpcaoResposta;
    }

    public void setCriteriosAvaliacaoOpcaoResposta(List<ProgramaCriterioAvaliacaoOpcaoResposta> criteriosAvaliacaoOpcaoResposta) {
        this.criteriosAvaliacaoOpcaoResposta = criteriosAvaliacaoOpcaoResposta;
    }

}
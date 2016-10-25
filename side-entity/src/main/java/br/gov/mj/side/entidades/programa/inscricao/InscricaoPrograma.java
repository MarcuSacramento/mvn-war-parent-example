package br.gov.mj.side.entidades.programa.inscricao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.apoio.entidades.MotivoAnalise;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumResultadoFinalAnaliseElegibilidade;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.inscricao.analise.HistoricoAnaliseAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.analise.HistoricoAnaliseElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.analise.InscricaoAnexoAnalise;

@Entity
@Table(name = "tb_ipg_inscricao_progama", schema = "side")
public class InscricaoPrograma extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ipg_id_inscricao_programa")
    @SequenceGenerator(name = "tb_ipg_inscricao_progama_generator", sequenceName = "side.seq_tb_ipg_inscricao_progama", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ipg_inscricao_progama_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ipg_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @ManyToOne
    @JoinColumn(name = "ipg_fk_pse_id_pessoa_entidade")
    @NotNull
    private PessoaEntidade pessoaEntidade;

    @ManyToOne
    @JoinColumn(name = "ipg_fk_hpb_id_historico_publicizacao")
    @NotNull
    private ProgramaHistoricoPublicizacao historicoPublicizacao;

    @Column(name = "ipg_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @Column(name = "ipg_no_usuario_alteracao")
    private String usuarioAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "ipg_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "ipg_dt_data_alteracao")
    private LocalDateTime dataAlteracao;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusInscricao", name = "enumClassName"))
    @Column(name = "ipg_st_status_inscricao")
    @NotNull
    private EnumStatusInscricao statusInscricao;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumResultadoFinalAnaliseElegibilidade", name = "enumClassName"))
    @Column(name = "ipg_tp_resultado_final_analise_elegibilidade")
    private EnumResultadoFinalAnaliseElegibilidade resultadoFinalAnaliseElegibilidade;

    @Column(name = "ipg_ds_descricao_justificativa_elegibilidade")
    private String descricaoJustificativaElegibilidade;

    @ManyToOne
    @JoinColumn(name = "ipg_fk_mae_id_motivo_analise_elegibilidade")
    private MotivoAnalise motivoAnaliseElegibilidade;

    @Column(name = "ipg_bo_esta_fase_recurso_elegibilidade")
    private Boolean estaEmFaseRecursoElegibilidade;

    @Column(name = "ipg_bo_finalizado_fase_recurso_elegibilidade")
    private Boolean finalizadoRecursoElegibilidade;

    @Column(name = "ipg_vl_pontuacao_final_analise_avaliacao")
    private Integer pontuacaoFinal;

    @Column(name = "ipg_ds_descricao_justificativa_avaliacao")
    private String descricaoJustificativaAvaliacao;

    @ManyToOne
    @JoinColumn(name = "ipg_fk_mae_id_motivo_analise_avaliacao")
    private MotivoAnalise motivoAnaliseAvaliacao;

    @Column(name = "ipg_bo_esta_fase_recurso_avaliacao")
    private Boolean estaEmFaseRecursoAvaliacao;

    @Column(name = "ipg_bo_finalizado_fase_recurso_avaliacao")
    private Boolean finalizadoRecursoAvaliacao;

    @Column(name = "ipg_nu_numero_processo_sei_recurso_elegibilidade")
    private String numeroProcessoSEIRecursoElegibilidade;

    @Column(name = "ipg_nu_numero_processo_sei_recurso_avaliacao")
    private String numeroProcessoSEIRecursoAvaliacao;

    @OneToMany(mappedBy = "inscricaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<InscricaoProgramaBem> programasBem = new ArrayList<InscricaoProgramaBem>();

    @OneToMany(mappedBy = "inscricaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<InscricaoProgramaKit> programasKit = new ArrayList<InscricaoProgramaKit>();

    @OneToMany(mappedBy = "inscricaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<InscricaoProgramaCriterioAvaliacao> programasCriterioAvaliacao = new ArrayList<InscricaoProgramaCriterioAvaliacao>();

    @OneToMany(mappedBy = "inscricaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<InscricaoProgramaCriterioElegibilidade> programasCriterioElegibilidade = new ArrayList<InscricaoProgramaCriterioElegibilidade>();

    @OneToMany(mappedBy = "inscricaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<InscricaoAnexoAnalise> anexos = new ArrayList<InscricaoAnexoAnalise>();

    @OneToMany(mappedBy = "inscricaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<HistoricoAnaliseElegibilidade> historicoAnalisesElegibilidade = new ArrayList<HistoricoAnaliseElegibilidade>();

    @OneToMany(mappedBy = "inscricaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<HistoricoAnaliseAvaliacao> historicoAnalisesAvaliacao = new ArrayList<HistoricoAnaliseAvaliacao>();

    @OneToMany(mappedBy = "inscricaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<InscricaoLocalEntrega> locaisEntregaInscricao = new ArrayList<InscricaoLocalEntrega>();

    @OneToMany(mappedBy = "inscricaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ComissaoRecebimento> comissaoRecebimento = new ArrayList<ComissaoRecebimento>();

    @OneToMany(mappedBy = "inscricaoPrograma", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ComissaoAnexo> comissaoAnexos = new ArrayList<ComissaoAnexo>();

    @Transient
    private boolean classificado;

    @Transient
    private Long colocacao;

    public InscricaoPrograma() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public PessoaEntidade getPessoaEntidade() {
        return pessoaEntidade;
    }

    public void setPessoaEntidade(PessoaEntidade pessoaEntidade) {
        this.pessoaEntidade = pessoaEntidade;
    }

    public ProgramaHistoricoPublicizacao getHistoricoPublicizacao() {
        return historicoPublicizacao;
    }

    public void setHistoricoPublicizacao(ProgramaHistoricoPublicizacao historicoPublicizacao) {
        this.historicoPublicizacao = historicoPublicizacao;
    }

    public List<InscricaoProgramaBem> getProgramasBem() {
        return programasBem;
    }

    public void setProgramasBem(List<InscricaoProgramaBem> programasBem) {
        this.programasBem = programasBem;
    }

    public List<InscricaoProgramaKit> getProgramasKit() {
        return programasKit;
    }

    public void setProgramasKit(List<InscricaoProgramaKit> programasKit) {
        this.programasKit = programasKit;
    }

    public List<InscricaoProgramaCriterioAvaliacao> getProgramasCriterioAvaliacao() {
        return programasCriterioAvaliacao;
    }

    public void setProgramasCriterioAvaliacao(List<InscricaoProgramaCriterioAvaliacao> programasCriterioAvaliacao) {
        this.programasCriterioAvaliacao = programasCriterioAvaliacao;
    }

    public List<InscricaoProgramaCriterioElegibilidade> getProgramasCriterioElegibilidade() {
        return programasCriterioElegibilidade;
    }

    public void setProgramasCriterioElegibilidade(List<InscricaoProgramaCriterioElegibilidade> programasCriterioElegibilidade) {
        this.programasCriterioElegibilidade = programasCriterioElegibilidade;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public String getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(String usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public EnumStatusInscricao getStatusInscricao() {
        return statusInscricao;
    }

    public void setStatusInscricao(EnumStatusInscricao statusInscricao) {
        this.statusInscricao = statusInscricao;
    }

    @Transient
    public BigDecimal getTotalUtilizado() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(getTotalValorUtilizadoBens());
        total = total.add(getTotalValorUtilizadoKits());

        return total;
    }

    @Transient
    public BigDecimal getTotalValorUtilizadoBens() {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalParcial;

        for (InscricaoProgramaBem bemUtilizado : getProgramasBem()) {
            if (bemUtilizado.getQuantidade() != null) {
                totalParcial = BigDecimal.ZERO;
                totalParcial = bemUtilizado.getProgramaBem().getBem().getValorEstimadoBem().multiply(new BigDecimal(bemUtilizado.getQuantidade()));
                total = total.add(totalParcial);
            }
        }

        return total;
    }

    @Transient
    public BigDecimal getTotalValorUtilizadoKits() {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalParcial;

        for (InscricaoProgramaKit kitUtilizado : getProgramasKit()) {
            if (kitUtilizado.getQuantidade() != null) {
                totalParcial = BigDecimal.ZERO;
                totalParcial = kitUtilizado.getProgramaKit().getKit().getValorEstimado().multiply(new BigDecimal(kitUtilizado.getQuantidade()));
                total = total.add(totalParcial);
            }
        }

        return total;
    }

    public List<InscricaoAnexoAnalise> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<InscricaoAnexoAnalise> anexos) {
        this.anexos = anexos;
    }

    public List<HistoricoAnaliseElegibilidade> getHistoricoAnalisesElegibilidade() {
        return historicoAnalisesElegibilidade;
    }

    public void setHistoricoAnalisesElegibilidade(List<HistoricoAnaliseElegibilidade> historicoAnalisesElegibilidade) {
        this.historicoAnalisesElegibilidade = historicoAnalisesElegibilidade;
    }

    public List<HistoricoAnaliseAvaliacao> getHistoricoAnalisesAvaliacao() {
        return historicoAnalisesAvaliacao;
    }

    public void setHistoricoAnalisesAvaliacao(List<HistoricoAnaliseAvaliacao> historicoAnalisesAvaliacao) {
        this.historicoAnalisesAvaliacao = historicoAnalisesAvaliacao;
    }

    public EnumResultadoFinalAnaliseElegibilidade getResultadoFinalAnaliseElegibilidade() {
        return resultadoFinalAnaliseElegibilidade;
    }

    public void setResultadoFinalAnaliseElegibilidade(EnumResultadoFinalAnaliseElegibilidade resultadoFinalAnaliseElegibilidade) {
        this.resultadoFinalAnaliseElegibilidade = resultadoFinalAnaliseElegibilidade;
    }

    public String getDescricaoJustificativaElegibilidade() {
        return descricaoJustificativaElegibilidade;
    }

    public void setDescricaoJustificativaElegibilidade(String descricaoJustificativaElegibilidade) {
        this.descricaoJustificativaElegibilidade = descricaoJustificativaElegibilidade;
    }

    public MotivoAnalise getMotivoAnaliseElegibilidade() {
        return motivoAnaliseElegibilidade;
    }

    public void setMotivoAnaliseElegibilidade(MotivoAnalise motivoAnaliseElegibilidade) {
        this.motivoAnaliseElegibilidade = motivoAnaliseElegibilidade;
    }

    public Boolean getEstaEmFaseRecursoElegibilidade() {
        return estaEmFaseRecursoElegibilidade;
    }

    public void setEstaEmFaseRecursoElegibilidade(Boolean estaEmFaseRecursoElegibilidade) {
        this.estaEmFaseRecursoElegibilidade = estaEmFaseRecursoElegibilidade;
    }

    public Boolean getFinalizadoRecursoElegibilidade() {
        return finalizadoRecursoElegibilidade;
    }

    public void setFinalizadoRecursoElegibilidade(Boolean finalizadoRecursoElegibilidade) {
        this.finalizadoRecursoElegibilidade = finalizadoRecursoElegibilidade;
    }

    public Integer getPontuacaoFinal() {
        return pontuacaoFinal;
    }

    public void setPontuacaoFinal(Integer pontuacaoFinal) {
        this.pontuacaoFinal = pontuacaoFinal;
    }

    public String getDescricaoJustificativaAvaliacao() {
        return descricaoJustificativaAvaliacao;
    }

    public void setDescricaoJustificativaAvaliacao(String descricaoJustificativaAvaliacao) {
        this.descricaoJustificativaAvaliacao = descricaoJustificativaAvaliacao;
    }

    public MotivoAnalise getMotivoAnaliseAvaliacao() {
        return motivoAnaliseAvaliacao;
    }

    public void setMotivoAnaliseAvaliacao(MotivoAnalise motivoAnaliseAvaliacao) {
        this.motivoAnaliseAvaliacao = motivoAnaliseAvaliacao;
    }

    public Boolean getEstaEmFaseRecursoAvaliacao() {
        return estaEmFaseRecursoAvaliacao;
    }

    public void setEstaEmFaseRecursoAvaliacao(Boolean estaEmFaseRecursoAvaliacao) {
        this.estaEmFaseRecursoAvaliacao = estaEmFaseRecursoAvaliacao;
    }

    public Boolean getFinalizadoRecursoAvaliacao() {
        return finalizadoRecursoAvaliacao;
    }

    public void setFinalizadoRecursoAvaliacao(Boolean finalizadoRecursoAvaliacao) {
        this.finalizadoRecursoAvaliacao = finalizadoRecursoAvaliacao;
    }

    public boolean isClassificado() {
        return classificado;
    }

    public void setClassificado(boolean classificado) {
        this.classificado = classificado;
    }

    public String getNumeroProcessoSEIRecursoElegibilidade() {
        return numeroProcessoSEIRecursoElegibilidade;
    }

    public void setNumeroProcessoSEIRecursoElegibilidade(String numeroProcessoSEIRecursoElegibilidade) {
        this.numeroProcessoSEIRecursoElegibilidade = numeroProcessoSEIRecursoElegibilidade;
    }

    public String getNumeroProcessoSEIRecursoAvaliacao() {
        return numeroProcessoSEIRecursoAvaliacao;
    }

    public void setNumeroProcessoSEIRecursoAvaliacao(String numeroProcessoSEIRecursoAvaliacao) {
        this.numeroProcessoSEIRecursoAvaliacao = numeroProcessoSEIRecursoAvaliacao;
    }

    public Long getColocacao() {
        return colocacao;
    }

    public void setColocacao(Long colocacao) {
        this.colocacao = colocacao;
    }

    public List<InscricaoLocalEntrega> getLocaisEntregaInscricao() {
        return locaisEntregaInscricao;
    }

    public void setLocaisEntregaInscricao(List<InscricaoLocalEntrega> locaisEntregaInscricao) {
        this.locaisEntregaInscricao = locaisEntregaInscricao;
    }

    public List<ComissaoRecebimento> getComissaoRecebimento() {
        return comissaoRecebimento;
    }

    public void setComissaoRecebimento(List<ComissaoRecebimento> comissaoRecebimento) {
        this.comissaoRecebimento = comissaoRecebimento;
    }

    public List<ComissaoAnexo> getComissaoAnexos() {
        return comissaoAnexos;
    }

    public void setComissaoAnexos(List<ComissaoAnexo> comissaoAnexos) {
        this.comissaoAnexos = comissaoAnexos;
    }
}
package br.gov.mj.side.entidades.programa.licitacao.contrato;

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

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.enums.EnumAnaliseFinalItem;
import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.enums.EnumSituacaoAvaliacaoPreliminarPreenchimentoItem;
import br.gov.mj.side.entidades.enums.EnumSituacaoBem;
import br.gov.mj.side.entidades.enums.EnumSituacaoGeracaoTermos;
import br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoBeneficiario;
import br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoFornecedor;
import br.gov.mj.side.entidades.enums.EnumTipoPatrimonio;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.TermoRecebimentoDefinitivo;
import br.gov.mj.side.entidades.programa.patrimoniamento.PatrimonioObjetoFornecimento;

@Entity
@Table(name = "tb_ofo_objeto_fornecimento_contrato", schema = "side")
public class ObjetoFornecimentoContrato extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ofo_id_objeto_fornecimento_contrato")
    @SequenceGenerator(name = "seq_tb_ofo_objeto_fornecimento_contrato_generator", sequenceName = "side.seq_tb_ofo_objeto_fornecimento_contrato", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_ofo_objeto_fornecimento_contrato_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ofo_fk_ofc_id_ordem_fornecimento_contrato")
    @NotNull
    private OrdemFornecimentoContrato ordemFornecimento;
    
    @ManyToOne
    @JoinColumn(name = "ofo_fk_nrc_id_nota_remessa_ordem_fornecimento_contrato")
    private NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato;

    @Column(name = "ofo_id_objeto_fornecimento_contrato_pai")
    private Long objetoFornecimentoContratoPai;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "ofo_dt_data_devolucao_item")
    private LocalDateTime dataDevolucaoItem;
    
    @Column(name = "ofo_bo_objeto_devolvido")
    private Boolean objetoDevolvido;    

    @ManyToOne
    @JoinColumn(name = "ofo_fk_bem_id_bem")
    @NotNull
    private Bem item;

    @ManyToOne
    @JoinColumn(name = "ofo_fk_lee_id_local_entrega_entidade")
    @NotNull
    private LocalEntregaEntidade localEntrega;

    @Column(name = "ofo_qt_quantidade")
    private Integer quantidade;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao", name = "enumClassName"))
    @Column(name = "ofo_tp_tipo_forma_verificacao_formatacao")
    @NotNull
    private EnumFormaVerificacaoFormatacao formaVerificacao;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoFornecedor", name = "enumClassName"))
    @Column(name = "ofo_st_situacao_preenchimento_item_formatacao_fornecedor")
    @NotNull
    private EnumSituacaoPreenchimentoItemFormatacaoFornecedor situacaoPreenchimentoFornecedor;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumSituacaoPreenchimentoItemFormatacaoBeneficiario", name = "enumClassName"))
    @Column(name = "ofo_st_situacao_preenchimento_item_formatacao_beneficiario")
    @NotNull
    private EnumSituacaoPreenchimentoItemFormatacaoBeneficiario situacaoPreenchimentoBeneficiario;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumSituacaoAvaliacaoPreliminarPreenchimentoItem", name = "enumClassName"))
    @Column(name = "ofo_st_situacao_avaliacao_preliminar_item")
    private EnumSituacaoAvaliacaoPreliminarPreenchimentoItem situacaoAvaliacaoPreliminarPreenchimentoItem;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumAnaliseFinalItem", name = "enumClassName"))
    @Column(name = "ofo_st_analise_final_item")
    private EnumAnaliseFinalItem analiseFinalItem;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumSituacaoGeracaoTermos", name = "enumClassName"))
    @Column(name = "ofo_st_situacao_geracao_termo")
    private EnumSituacaoGeracaoTermos situacaoGeracaoTermos;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumSituacaoBem", name = "enumClassName"))
    @Column(name = "ofo_st_situacao_bem")
    private EnumSituacaoBem situacaoBem;

    @Column(name = "ofo_qt_quantidade_quesitos_fornecedor")
    private Integer quantidadeQuesitosFornecedor;

    @Column(name = "ofo_qt_quantidade_quesitos_preenchidos_fornecedor_op")
    private Integer quantidadeQuesitosOpcionaisPreenchidosFornecedor;

    @Column(name = "ofo_qt_quantidade_quesitos_beneficiario")
    private Integer quantidadeQuesitosBeneficiario;

    @Column(name = "ofo_qt_quantidade_quesitos_preenchidos_beneficiario_op")
    private Integer quantidadeQuesitosOpcionaisPreenchidosBeneficiario;

    @Column(name = "ofo_bo_estado_novo")
    private Boolean estadoDeNovo;

    @Column(name = "ofo_bo_funcionando_acordo")
    private Boolean funcionandoDeAcordo;

    @Column(name = "ofo_bo_configurado_acordo")
    private Boolean configuradoDeAcordo;

    @Column(name = "ofo_ds_descricao_nao_configurado_acordo")
    private String descricaoNaoConfiguradoDeAcordo;

    @Column(name = "ofo_ds_descricao_nao_funcionando_acordo")
    private String descricaoNaoFuncionandoDeAcordo;

    @Column(name = "ofo_qt_quantidade_quesitos_preenchidos_fornecedor_ob")
    private Integer quantidadeQuesitosObrigatoriosPreenchidosFornecedor;

    @Column(name = "ofo_qt_quantidade_quesitos_preenchidos_beneficiario_ob")
    private Integer quantidadeQuesitosObrigatoriosPreenchidosBeneficiario;

    // /////

    @Column(name = "ofo_qt_quantidade_quesitos_beneficiario_ob")
    private Integer quantidadeQuesitosObrigatoriosBeneficiario;

    @Column(name = "ofo_qt_quantidade_quesitos_beneficiario_op")
    private Integer quantidadeQuesitosOpcionaisBeneficiario;

    @Column(name = "ofo_qt_quantidade_quesitos_fornecedor_ob")
    private Integer quantidadeQuesitosObrigatoriosFornecedor;

    @Column(name = "ofo_qt_quantidade_quesitos_fornecedor_op")
    private Integer quantidadeQuesitosOpcionaisFornecedor;

    @OneToMany(mappedBy = "objetoFornecimento", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<FormatacaoObjetoFornecimento> listaFormatacaoObjetoFornecimento = new ArrayList<FormatacaoObjetoFornecimento>();

    @ManyToOne
    @JoinColumn(name = "ofo_fk_trd_id_termo_recebimento_definitivo")
    private TermoRecebimentoDefinitivo termoRecebimentoDefinitivo;

    @ManyToOne
    @JoinColumn(name = "ofo_fk_trd_id_termo_doacao")
    private TermoDoacao termoDoacao;

    @Column(name = "ofo_ds_motivo")
    private String motivo;

    @Column(name = "ofo_ds_texto_nao_conformidade")
    private String motivoNaoConformidade;

    @Column(name = "ofo_bo_item_patrimoniavel")
    private Boolean itemPatrimoniavel;

    @Column(name = "ofo_no_motivo_item_nao_patrimoniavel")
    private String motivoItemNaoPatrimoniavel;

    @OneToMany(mappedBy = "objetoFornecimentoContrato", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<PatrimonioObjetoFornecimento> listaPatrimonio = new ArrayList<PatrimonioObjetoFornecimento>();

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoPatrimonio", name = "enumClassName"))
    @Column(name = "ofo_tp_tipo_patrimonio")
    private EnumTipoPatrimonio tipoPatrimonio;
    
    @Transient
    private Boolean ItemSelecionado;
    
    @Transient
    private Boolean mostrarItem;
    
    @Override
    public Long getId() {
        return id;
    }

    public Bem getItem() {
        return item;
    }

    public void setItem(Bem item) {
        this.item = item;
    }

    public LocalEntregaEntidade getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(LocalEntregaEntidade localEntrega) {
        this.localEntrega = localEntrega;
    }

    public OrdemFornecimentoContrato getOrdemFornecimento() {
        return ordemFornecimento;
    }

    public void setOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimento) {
        this.ordemFornecimento = ordemFornecimento;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public EnumFormaVerificacaoFormatacao getFormaVerificacao() {
        return formaVerificacao;
    }

    public void setFormaVerificacao(EnumFormaVerificacaoFormatacao formaVerificacao) {
        this.formaVerificacao = formaVerificacao;
    }

    public List<FormatacaoObjetoFornecimento> getListaFormatacaoObjetoFornecimento() {
        return listaFormatacaoObjetoFornecimento;
    }

    public void setListaFormatacaoObjetoFornecimento(List<FormatacaoObjetoFornecimento> listaFormatacaoObjetoFornecimento) {
        this.listaFormatacaoObjetoFornecimento = listaFormatacaoObjetoFornecimento;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumSituacaoPreenchimentoItemFormatacaoFornecedor getSituacaoPreenchimentoFornecedor() {
        return situacaoPreenchimentoFornecedor;
    }

    public void setSituacaoPreenchimentoFornecedor(EnumSituacaoPreenchimentoItemFormatacaoFornecedor situacaoPreenchimentoFornecedor) {
        this.situacaoPreenchimentoFornecedor = situacaoPreenchimentoFornecedor;
    }

    public EnumSituacaoPreenchimentoItemFormatacaoBeneficiario getSituacaoPreenchimentoBeneficiario() {
        return situacaoPreenchimentoBeneficiario;
    }

    public void setSituacaoPreenchimentoBeneficiario(EnumSituacaoPreenchimentoItemFormatacaoBeneficiario situacaoPreenchimentoBeneficiario) {
        this.situacaoPreenchimentoBeneficiario = situacaoPreenchimentoBeneficiario;
    }

    public Boolean getEstadoDeNovo() {
        return estadoDeNovo;
    }

    public void setEstadoDeNovo(Boolean estadoDeNovo) {
        this.estadoDeNovo = estadoDeNovo;
    }

    public Boolean getFuncionandoDeAcordo() {
        return funcionandoDeAcordo;
    }

    public void setFuncionandoDeAcordo(Boolean funcionandoDeAcordo) {
        this.funcionandoDeAcordo = funcionandoDeAcordo;
    }

    public Boolean getConfiguradoDeAcordo() {
        return configuradoDeAcordo;
    }

    public void setConfiguradoDeAcordo(Boolean configuradoDeAcordo) {
        this.configuradoDeAcordo = configuradoDeAcordo;
    }

    public Integer getQuantidadeQuesitosFornecedor() {
        return quantidadeQuesitosFornecedor;
    }

    public void setQuantidadeQuesitosFornecedor(Integer quantidadeQuesitosFornecedor) {
        this.quantidadeQuesitosFornecedor = quantidadeQuesitosFornecedor;
    }

    public Integer getQuantidadeQuesitosBeneficiario() {
        return quantidadeQuesitosBeneficiario;
    }

    public void setQuantidadeQuesitosBeneficiario(Integer quantidadeQuesitosBeneficiario) {
        this.quantidadeQuesitosBeneficiario = quantidadeQuesitosBeneficiario;
    }

    public String getDescricaoNaoConfiguradoDeAcordo() {
        return descricaoNaoConfiguradoDeAcordo;
    }

    public void setDescricaoNaoConfiguradoDeAcordo(String descricaoNaoConfiguradoDeAcordo) {
        this.descricaoNaoConfiguradoDeAcordo = descricaoNaoConfiguradoDeAcordo;
    }

    public String getDescricaoNaoFuncionandoDeAcordo() {
        return descricaoNaoFuncionandoDeAcordo;
    }

    public void setDescricaoNaoFuncionandoDeAcordo(String descricaoNaoFuncionandoDeAcordo) {
        this.descricaoNaoFuncionandoDeAcordo = descricaoNaoFuncionandoDeAcordo;
    }

    public Integer getQuantidadeQuesitosObrigatoriosPreenchidosFornecedor() {
        return quantidadeQuesitosObrigatoriosPreenchidosFornecedor;
    }

    public void setQuantidadeQuesitosObrigatoriosPreenchidosFornecedor(Integer quantidadeQuesitosObrigatoriosPreenchidosFornecedor) {
        this.quantidadeQuesitosObrigatoriosPreenchidosFornecedor = quantidadeQuesitosObrigatoriosPreenchidosFornecedor;
    }

    public Integer getQuantidadeQuesitosObrigatoriosPreenchidosBeneficiario() {
        return quantidadeQuesitosObrigatoriosPreenchidosBeneficiario;
    }

    public void setQuantidadeQuesitosObrigatoriosPreenchidosBeneficiario(Integer quantidadeQuesitosObrigatoriosPreenchidosBeneficiario) {
        this.quantidadeQuesitosObrigatoriosPreenchidosBeneficiario = quantidadeQuesitosObrigatoriosPreenchidosBeneficiario;
    }

    public Integer getQuantidadeQuesitosOpcionaisPreenchidosFornecedor() {
        return quantidadeQuesitosOpcionaisPreenchidosFornecedor;
    }

    public void setQuantidadeQuesitosOpcionaisPreenchidosFornecedor(Integer quantidadeQuesitosOpcionaisPreenchidosFornecedor) {
        this.quantidadeQuesitosOpcionaisPreenchidosFornecedor = quantidadeQuesitosOpcionaisPreenchidosFornecedor;
    }

    public Integer getQuantidadeQuesitosOpcionaisPreenchidosBeneficiario() {
        return quantidadeQuesitosOpcionaisPreenchidosBeneficiario;
    }

    public void setQuantidadeQuesitosOpcionaisPreenchidosBeneficiario(Integer quantidadeQuesitosOpcionaisPreenchidosBeneficiario) {
        this.quantidadeQuesitosOpcionaisPreenchidosBeneficiario = quantidadeQuesitosOpcionaisPreenchidosBeneficiario;
    }

    public Integer getQuantidadeQuesitosObrigatoriosBeneficiario() {
        return quantidadeQuesitosObrigatoriosBeneficiario;
    }

    public void setQuantidadeQuesitosObrigatoriosBeneficiario(Integer quantidadeQuesitosObrigatoriosBeneficiario) {
        this.quantidadeQuesitosObrigatoriosBeneficiario = quantidadeQuesitosObrigatoriosBeneficiario;
    }

    public Integer getQuantidadeQuesitosOpcionaisBeneficiario() {
        return quantidadeQuesitosOpcionaisBeneficiario;
    }

    public void setQuantidadeQuesitosOpcionaisBeneficiario(Integer quantidadeQuesitosOpcionaisBeneficiario) {
        this.quantidadeQuesitosOpcionaisBeneficiario = quantidadeQuesitosOpcionaisBeneficiario;
    }

    public Integer getQuantidadeQuesitosObrigatoriosFornecedor() {
        return quantidadeQuesitosObrigatoriosFornecedor;
    }

    public void setQuantidadeQuesitosObrigatoriosFornecedor(Integer quantidadeQuesitosObrigatoriosFornecedor) {
        this.quantidadeQuesitosObrigatoriosFornecedor = quantidadeQuesitosObrigatoriosFornecedor;
    }

    public Integer getQuantidadeQuesitosOpcionaisFornecedor() {
        return quantidadeQuesitosOpcionaisFornecedor;
    }

    public void setQuantidadeQuesitosOpcionaisFornecedor(Integer quantidadeQuesitosOpcionaisFornecedor) {
        this.quantidadeQuesitosOpcionaisFornecedor = quantidadeQuesitosOpcionaisFornecedor;
    }

    public EnumSituacaoAvaliacaoPreliminarPreenchimentoItem getSituacaoAvaliacaoPreliminarPreenchimentoItem() {
        return situacaoAvaliacaoPreliminarPreenchimentoItem;
    }

    public void setSituacaoAvaliacaoPreliminarPreenchimentoItem(EnumSituacaoAvaliacaoPreliminarPreenchimentoItem situacaoAvaliacaoPreliminarPreenchimentoItem) {
        this.situacaoAvaliacaoPreliminarPreenchimentoItem = situacaoAvaliacaoPreliminarPreenchimentoItem;
    }

    public EnumAnaliseFinalItem getAnaliseFinalItem() {
        return analiseFinalItem;
    }

    public void setAnaliseFinalItem(EnumAnaliseFinalItem analiseFinalItem) {
        this.analiseFinalItem = analiseFinalItem;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Boolean getItemSelecionado() {
        return ItemSelecionado;
    }

    public void setItemSelecionado(Boolean itemSelecionado) {
        ItemSelecionado = itemSelecionado;
    }

    public String getMotivoNaoConformidade() {
        return motivoNaoConformidade;
    }

    public void setMotivoNaoConformidade(String motivoNaoConformidade) {
        this.motivoNaoConformidade = motivoNaoConformidade;
    }

    public TermoRecebimentoDefinitivo getTermoRecebimentoDefinitivo() {
        return termoRecebimentoDefinitivo;
    }

    public void setTermoRecebimentoDefinitivo(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo) {
        this.termoRecebimentoDefinitivo = termoRecebimentoDefinitivo;
    }

    public TermoDoacao getTermoDoacao() {
        return termoDoacao;
    }

    public void setTermoDoacao(TermoDoacao termoDoacao) {
        this.termoDoacao = termoDoacao;
    }

    public EnumSituacaoGeracaoTermos getSituacaoGeracaoTermos() {
        return situacaoGeracaoTermos;
    }

    public void setSituacaoGeracaoTermos(EnumSituacaoGeracaoTermos situacaoGeracaoTermos) {
        this.situacaoGeracaoTermos = situacaoGeracaoTermos;
    }

    public EnumTipoPatrimonio getTipoPatrimonio() {
        return tipoPatrimonio;
    }

    public void setTipoPatrimonio(EnumTipoPatrimonio tipoPatrimonio) {
        this.tipoPatrimonio = tipoPatrimonio;
    }

    public List<PatrimonioObjetoFornecimento> getListaPatrimonio() {
        return listaPatrimonio;
    }

    public void setListaPatrimonio(List<PatrimonioObjetoFornecimento> listaPatrimonio) {
        this.listaPatrimonio = listaPatrimonio;
    }

    public Boolean getItemPatrimoniavel() {
        return itemPatrimoniavel;
    }

    public void setItemPatrimoniavel(Boolean itemPatrimoniavel) {
        this.itemPatrimoniavel = itemPatrimoniavel;
    }

    public String getMotivoItemNaoPatrimoniavel() {
        return motivoItemNaoPatrimoniavel;
    }

    public void setMotivoItemNaoPatrimoniavel(String motivoItemNaoPatrimoniavel) {
        this.motivoItemNaoPatrimoniavel = motivoItemNaoPatrimoniavel;
    }

    public EnumSituacaoBem getSituacaoBem() {
        return situacaoBem;
    }

    public void setSituacaoBem(EnumSituacaoBem situacaoBem) {
        this.situacaoBem = situacaoBem;
    }

    public Long getObjetoFornecimentoContratoPai() {
        return objetoFornecimentoContratoPai;
    }

    public void setObjetoFornecimentoContratoPai(Long objetoFornecimentoContratoPai) {
        this.objetoFornecimentoContratoPai = objetoFornecimentoContratoPai;
    }

    public LocalDateTime getDataDevolucaoItem() {
        return dataDevolucaoItem;
    }

    public void setDataDevolucaoItem(LocalDateTime dataDevolucaoItem) {
        this.dataDevolucaoItem = dataDevolucaoItem;
    }

    public Boolean getObjetoDevolvido() {
        return objetoDevolvido;
    }

    public void setObjetoDevolvido(Boolean objetoDevolvido) {
        this.objetoDevolvido = objetoDevolvido;
    }

    public NotaRemessaOrdemFornecimentoContrato getNotaRemessaOrdemFornecimentoContrato() {
        return notaRemessaOrdemFornecimentoContrato;
    }

    public void setNotaRemessaOrdemFornecimentoContrato(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        this.notaRemessaOrdemFornecimentoContrato = notaRemessaOrdemFornecimentoContrato;
    }

    public Boolean getMostrarItem() {
        return mostrarItem;
    }

    public void setMostrarItem(Boolean mostrarItem) {
        this.mostrarItem = mostrarItem;
    }
}

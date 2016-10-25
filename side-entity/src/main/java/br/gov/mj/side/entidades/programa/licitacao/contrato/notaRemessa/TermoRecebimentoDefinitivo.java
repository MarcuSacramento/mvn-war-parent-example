package br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa;

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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumStatusGeracaoTermoDoacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;

@Entity
@Table(name = "tb_trd_termo_recebimento_definitivo", schema = "side")
public class TermoRecebimentoDefinitivo extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "trd_id_termo_recebimento_definitivo")
    @SequenceGenerator(name = "seq_tb_trd_termo_recebimento_definitivo_generator", sequenceName = "side.seq_tb_trd_termo_recebimento_definitivo", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_trd_termo_recebimento_definitivo_generator")
    @NotNull
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "trd_fk_nrc_id_nota_remessa_ordem_fornecimento_contrato")
    @NotNull
    private NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato;
    
    @OneToOne
    @JoinColumn(name = "trd_fk_ent_id_entidade")
    private Entidade entidade;

    @Column(name = "trd_no_nome_anexo")
    private String nomeAnexo;
    
    @Column(name = "trd_no_nome_beneficiario")
    private String nomeBeneficiario;
    
    @Column(name = "trd_no_usuario_criacao_termo")
    private String usuarioCriacao;
    
    @Column(name = "trd_no_usuario_ultima_alteracao")
    private String usuarioAlteracao;
    
    @Column(name = "trd_nu_numero_processo_sei")
    private String numeroProcessoSEI;
    
    @Column(name = "trd_nu_numero_documento_sei")
    private String numeroDocumentoSei;
    
    @Column(name = "trd_nu_numero_nota_fiscal")
    private String numeroNotaFiscal;
    
    @Column(name = "trd_no_nome_anexo_nota_fiscal")
    private String nomeAnexoNotaFiscal;
    
    @Column(name = "trd_vl_tamanho_anexo_nota_fiscal")
    private Long tamanhoAnexoNotaFiscal;
    
    @Column(name = "trd_ct_termo_nota_fiscal")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] notaFiscal;
    
    @Column(name = "trd_ct_termo_recebimento_definitivo_gerado")
    @Type(type = "org.hibernate.type.BinaryType")
    @NotNull
    private byte[] termoRecebimentoDefinitivoGerado;
    
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "trd_dt_data_geracao_documento")
    @NotNull
    private LocalDateTime dataGeracao;    
    
    @OneToMany(mappedBy = "termoRecebimentoDefinitivo", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ItensNotaRemessaOrdemFornecimentoContrato> itensNotaRemessaOrdemFornecimentoContrato = new ArrayList<ItensNotaRemessaOrdemFornecimentoContrato>();
    
    @OneToMany(mappedBy = "termoRecebimentoDefinitivo", cascade = { CascadeType.MERGE }, orphanRemoval = true)
    private List<ObjetoFornecimentoContrato> objetosFornecimentoContrato = new ArrayList<ObjetoFornecimentoContrato>();
    
    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusGeracaoTermoDoacao", name = "enumClassName"))
    @Column(name = "trd_st_status_termo_recebimento")
    private EnumStatusGeracaoTermoDoacao statusTermoRecebimento;
    
    @Transient
    private Boolean trdSelecionado;
    
    @Override
    public Long getId() {
        return id;
    }


    public String getNomeAnexo() {
        return nomeAnexo;
    }


    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }


    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }


    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }


    public List<ItensNotaRemessaOrdemFornecimentoContrato> getItensNotaRemessaOrdemFornecimentoContrato() {
        return itensNotaRemessaOrdemFornecimentoContrato;
    }


    public void setItensNotaRemessaOrdemFornecimentoContrato(List<ItensNotaRemessaOrdemFornecimentoContrato> itensNotaRemessaOrdemFornecimentoContrato) {
        this.itensNotaRemessaOrdemFornecimentoContrato = itensNotaRemessaOrdemFornecimentoContrato;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public NotaRemessaOrdemFornecimentoContrato getNotaRemessaOrdemFornecimentoContrato() {
        return notaRemessaOrdemFornecimentoContrato;
    }


    public void setNotaRemessaOrdemFornecimentoContrato(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        this.notaRemessaOrdemFornecimentoContrato = notaRemessaOrdemFornecimentoContrato;
    }


    public byte[] getTermoRecebimentoDefinitivoGerado() {
        return termoRecebimentoDefinitivoGerado;
    }


    public void setTermoRecebimentoDefinitivoGerado(byte[] termoRecebimentoDefinitivoGerado) {
        this.termoRecebimentoDefinitivoGerado = termoRecebimentoDefinitivoGerado;
    }


    public String getUsuarioAlteracao() {
        return usuarioAlteracao;
    }


    public void setUsuarioAlteracao(String usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }


    public String getUsuarioCriacao() {
        return usuarioCriacao;
    }


    public void setUsuarioCriacao(String usuarioCriacao) {
        this.usuarioCriacao = usuarioCriacao;
    }


    public List<ObjetoFornecimentoContrato> getObjetosFornecimentoContrato() {
        return objetosFornecimentoContrato;
    }


    public void setObjetosFornecimentoContrato(List<ObjetoFornecimentoContrato> objetosFornecimentoContrato) {
        this.objetosFornecimentoContrato = objetosFornecimentoContrato;
    }


    public String getNomeBeneficiario() {
        return nomeBeneficiario;
    }


    public void setNomeBeneficiario(String nomeBeneficiario) {
        this.nomeBeneficiario = nomeBeneficiario;
    }

    public Boolean getTrdSelecionado() {
        return trdSelecionado;
    }


    public void setTrdSelecionado(Boolean trdSelecionado) {
        this.trdSelecionado = trdSelecionado;
    }


    public EnumStatusGeracaoTermoDoacao getStatusTermoRecebimento() {
        return statusTermoRecebimento;
    }


    public void setStatusTermoRecebimento(EnumStatusGeracaoTermoDoacao statusTermoRecebimento) {
        this.statusTermoRecebimento = statusTermoRecebimento;
    }


    public String getNumeroProcessoSEI() {
        return numeroProcessoSEI;
    }


    public void setNumeroProcessoSEI(String numeroProcessoSEI) {
        this.numeroProcessoSEI = numeroProcessoSEI;
    }


    public String getNumeroDocumentoSei() {
        return numeroDocumentoSei;
    }


    public void setNumeroDocumentoSei(String numeroDocumentoSei) {
        this.numeroDocumentoSei = numeroDocumentoSei;
    }


    public Entidade getEntidade() {
        return entidade;
    }


    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }


    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }


    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }


    public byte[] getNotaFiscal() {
        return notaFiscal;
    }


    public void setNotaFiscal(byte[] notaFiscal) {
        this.notaFiscal = notaFiscal;
    }


    public String getNomeAnexoNotaFiscal() {
        return nomeAnexoNotaFiscal;
    }


    public void setNomeAnexoNotaFiscal(String nomeAnexoNotaFiscal) {
        this.nomeAnexoNotaFiscal = nomeAnexoNotaFiscal;
    }


    public Long getTamanhoAnexoNotaFiscal() {
        return tamanhoAnexoNotaFiscal;
    }


    public void setTamanhoAnexoNotaFiscal(Long tamanhoAnexoNotaFiscal) {
        this.tamanhoAnexoNotaFiscal = tamanhoAnexoNotaFiscal;
    }

}

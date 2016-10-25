package br.gov.mj.side.entidades.programa.licitacao.contrato;

import java.time.LocalDateTime;

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

import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_hco_historico_geracao_comunicacao_of_contrato", schema = "side")
public class HistoricoComunicacaoGeracaoOrdemFornecimentoContrato extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "hco_id_historico_geracao_comunicacao_of_contrato")
    @SequenceGenerator(name = "seq_tb_hco_historico_geracao_comunicacao_of_contrato_generator", sequenceName = "side.seq_tb_hco_historico_geracao_comunicacao_of_contrato", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_hco_historico_geracao_comunicacao_of_contrato_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hco_fk_ofc_id_ordem_fornecimento_contrato")
    @NotNull
    private OrdemFornecimentoContrato ordemFornecimento;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "hco_dt_data_comunicacao")
    private LocalDateTime dataComunicacao;

    @Column(name = "hco_bo_possui_comunicado")
    @NotNull
    private Boolean possuiComunicado;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "hco_dt_data_geracao")
    @NotNull
    private LocalDateTime dataGeracao;

    @Column(name = "hco_ct_conteudo_minuta_gerada")
    @Type(type = "org.hibernate.type.BinaryType")
    @NotNull
    private byte[] minutaGerada;

    @Column(name = "hco_bo_possui_cancelamento")
    @NotNull
    private Boolean possuiCancelamento;

    @Column(name = "hco_nu_numero_documento_sei")
    private String numeroDocumentoSei;

    @Column(name = "hco_ds_motivo_cancelamento")
    private String motivoCancelamento;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "hco_dt_data_cancelamento")
    private LocalDateTime dataCancelamento;

    @Override
    public Long getId() {
        return id;
    }

    public OrdemFornecimentoContrato getOrdemFornecimento() {
        return ordemFornecimento;
    }

    public void setOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimento) {
        this.ordemFornecimento = ordemFornecimento;
    }

    public LocalDateTime getDataComunicacao() {
        return dataComunicacao;
    }

    public void setDataComunicacao(LocalDateTime dataComunicacao) {
        this.dataComunicacao = dataComunicacao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public byte[] getMinutaGerada() {
        return minutaGerada;
    }

    public void setMinutaGerada(byte[] minutaGerada) {
        this.minutaGerada = minutaGerada;
    }

    public Boolean getPossuiComunicado() {
        return possuiComunicado;
    }

    public void setPossuiComunicado(Boolean possuiComunicado) {
        this.possuiComunicado = possuiComunicado;
    }

    public Boolean getPossuiCancelamento() {
        return possuiCancelamento;
    }

    public void setPossuiCancelamento(Boolean possuiCancelamento) {
        this.possuiCancelamento = possuiCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getNumeroDocumentoSei() {
        return numeroDocumentoSei;
    }

    public void setNumeroDocumentoSei(String numeroDocumentoSei) {
        this.numeroDocumentoSei = numeroDocumentoSei;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(LocalDateTime dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

}

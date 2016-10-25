package br.gov.mj.side.entidades.programa.licitacao.contrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumStatusFormatacao;
import br.gov.mj.side.entidades.programa.patrimoniamento.ArquivoUnico;

@Entity
@Table(name = "tb_fir_formatacao_itens_contrato_resposta", schema = "side")
public class FormatacaoItensContratoResposta extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "fir_id_formatacao_itens_contrato_resposta")
    @SequenceGenerator(name = "seq_tb_fir_formatacao_itens_contrato_resposta_generator", sequenceName = "side.seq_tb_fir_formatacao_itens_contrato_resposta", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_fir_formatacao_itens_contrato_resposta_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fir_fk_fic_id_formatacao_itens_contrato")
    @NotNull
    private FormatacaoItensContrato formatacao;

    @Column(name = "fir_ds_resposta_alfa_numerico")
    private String respostaAlfanumerico;

    @Column(name = "fir_ct_conteudo_anexo")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] conteudo;

    @Column(name = "fir_no_nome_anexo")
    private String nomeAnexo;

    @Column(name = "fir_bo_resposta_booleano")
    private Boolean respostaBooleana;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "fir_dt_resposta_data")
    private LocalDate respostaData;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "fir_dt_data_foto")
    private LocalDateTime dataFoto;

    @Column(name = "fir_ds_latitude_longitude_foto")
    private String latitudeLongitudeFoto;

    @Column(name = "fir_ds_resposta_texto")
    private String respostaTexto;

    @Column(name = "fir_vl_tamanho_anexo")
    private Long tamanho;
    
    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusFormatacao", name = "enumClassName"))
    @Column(name = "fir_st_status_formatacao")
    private EnumStatusFormatacao statusFormatacao;
    
    @Column(name = "fir_ds_motivo_nao_conformidade")
    private String motivoNaoConformidade;
    
    @OneToOne(cascade=CascadeType.REMOVE)
    @JoinColumn(name = "fir_fk_arq_id_codigo_unico_arquivo_cadastrado")
    private ArquivoUnico arquivoUnico; 
    
    @Transient
    private String codigoUnicoFoto;

    @Override
    public Long getId() {
        return id;
    }

    public FormatacaoItensContrato getFormatacao() {
        return formatacao;
    }

    public void setFormatacao(FormatacaoItensContrato formatacao) {
        this.formatacao = formatacao;
    }

    public String getRespostaAlfanumerico() {
        return respostaAlfanumerico;
    }

    public void setRespostaAlfanumerico(String respostaAlfanumerico) {
        this.respostaAlfanumerico = respostaAlfanumerico;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    public Boolean getRespostaBooleana() {
        return respostaBooleana;
    }

    public void setRespostaBooleana(Boolean respostaBooleana) {
        this.respostaBooleana = respostaBooleana;
    }

    public LocalDate getRespostaData() {
        return respostaData;
    }

    public void setRespostaData(LocalDate respostaData) {
        this.respostaData = respostaData;
    }

    public LocalDateTime getDataFoto() {
        return dataFoto;
    }

    public void setDataFoto(LocalDateTime dataFoto) {
        this.dataFoto = dataFoto;
    }

    public String getLatitudeLongitudeFoto() {
        return latitudeLongitudeFoto;
    }

    public void setLatitudeLongitudeFoto(String latitudeLongitudeFoto) {
        this.latitudeLongitudeFoto = latitudeLongitudeFoto;
    }

    public String getRespostaTexto() {
        return respostaTexto;
    }

    public void setRespostaTexto(String respostaTexto) {
        this.respostaTexto = respostaTexto;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public BigDecimal getTamanhoArquivoEmMB() {
        Long tamanhoArquivo = new Long("0");
        if (this.tamanho != null) {
            tamanhoArquivo = this.tamanho;
        } else if (this.conteudo != null) {
            tamanhoArquivo = new Long(this.conteudo.length);
        }
        BigDecimal valor = new BigDecimal(String.valueOf(tamanhoArquivo));
        BigDecimal mega = new BigDecimal("1024");
        valor = valor.divide(mega).divide(mega);
        return valor.setScale(2, BigDecimal.ROUND_UP);
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

    public EnumStatusFormatacao getStatusFormatacao() {
        return statusFormatacao;
    }

    public void setStatusFormatacao(EnumStatusFormatacao statusFormatacao) {
        this.statusFormatacao = statusFormatacao;
    }

    public String getMotivoNaoConformidade() {
        return motivoNaoConformidade;
    }

    public void setMotivoNaoConformidade(String motivoNaoConformidade) {
        this.motivoNaoConformidade = motivoNaoConformidade;
    }

    public String getCodigoUnicoFoto() {
        return codigoUnicoFoto;
    }

    public void setCodigoUnicoFoto(String codigoUnicoFoto) {
        this.codigoUnicoFoto = codigoUnicoFoto;
    }

    public ArquivoUnico getArquivoUnico() {
        return arquivoUnico;
    }

    public void setArquivoUnico(ArquivoUnico arquivoUnico) {
        this.arquivoUnico = arquivoUnico;
    }

}

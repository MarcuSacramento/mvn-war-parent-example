package br.gov.mj.side.web.dto.formatacaoObjetoFornecimento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.gov.mj.side.entidades.enums.EnumStatusFormatacao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.patrimoniamento.ArquivoUnico;

public class FormatacaoItensContratoRespostaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Programa programa;
    private FormatacaoItensContratoDtot formatacao;
    private String respostaAlfanumerico;
    private byte[] conteudo;
    private String nomeAnexo;
    private Boolean respostaBooleana;
    private LocalDate respostaData;
    private LocalDateTime dataFoto;
    private String latitudeLongitudeFoto;
    private String respostaTexto;
    private Long tamanho;
    private EnumStatusFormatacao statusFormatacaoResposta;
    private String motivoNaoConformidade;
    private ArquivoUnico arquivoUnico;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FormatacaoItensContratoDtot getFormatacao() {
        return formatacao;
    }

    public void setFormatacao(FormatacaoItensContratoDtot formatacao) {
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

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

    public EnumStatusFormatacao getStatusFormatacaoResposta() {
        return statusFormatacaoResposta;
    }

    public void setStatusFormatacaoResposta(EnumStatusFormatacao statusFormatacaoResposta) {
        this.statusFormatacaoResposta = statusFormatacaoResposta;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public String getMotivoNaoConformidade() {
        return motivoNaoConformidade;
    }

    public void setMotivoNaoConformidade(String motivoNaoConformidade) {
        this.motivoNaoConformidade = motivoNaoConformidade;
    }

    public ArquivoUnico getArquivoUnico() {
        return arquivoUnico;
    }

    public void setArquivoUnico(ArquivoUnico arquivoUnico) {
        this.arquivoUnico = arquivoUnico;
    }
}

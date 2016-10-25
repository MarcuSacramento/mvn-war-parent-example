package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.enums.EnumStatusFormatacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContrato;

public class FormatacaoItensContratoRespostaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private FormatacaoItensContrato formatacao;
    private String respostaAlfanumerico;
    private String nomeAnexo;
    private Boolean respostaBooleana;
    private LocalDate respostaData;
    private LocalDateTime dataFoto;
    private String latitudeLongitudeFoto;
    private String respostaTexto;
    private Long tamanho;
    private EnumStatusFormatacao statusFormatacao;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public EnumStatusFormatacao getStatusFormatacao() {
        return statusFormatacao;
    }
    public void setStatusFormatacao(EnumStatusFormatacao statusFormatacao) {
        this.statusFormatacao = statusFormatacao;
    }
    

}

package br.gov.mj.side.web.dto.formatacaoObjetoFornecimento;

import java.io.Serializable;

import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.enums.EnumResponsavelPreencherFormatacaoItem;
import br.gov.mj.side.entidades.enums.EnumTipoCampoFormatacao;

public class FormatacaoItensContratoDtot implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private EnumFormaVerificacaoFormatacao formaVerificacao;
    private EnumTipoCampoFormatacao tipoCampo;
    private String tituloQuesito;
    private String orientacaoFornecedores;
    private Boolean possuiIdentificadorUnico;
    private Boolean possuiInformacaoOpcional;
    private Boolean possuiDispositivoMovel;
    private Boolean possuiGPS;
    private Boolean possuiData;
    private EnumResponsavelPreencherFormatacaoItem responsavelFormatacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumFormaVerificacaoFormatacao getFormaVerificacao() {
        return formaVerificacao;
    }

    public void setFormaVerificacao(EnumFormaVerificacaoFormatacao formaVerificacao) {
        this.formaVerificacao = formaVerificacao;
    }

    public EnumTipoCampoFormatacao getTipoCampo() {
        return tipoCampo;
    }

    public void setTipoCampo(EnumTipoCampoFormatacao tipoCampo) {
        this.tipoCampo = tipoCampo;
    }

    public String getTituloQuesito() {
        return tituloQuesito;
    }

    public void setTituloQuesito(String tituloQuesito) {
        this.tituloQuesito = tituloQuesito;
    }

    public String getOrientacaoFornecedores() {
        return orientacaoFornecedores;
    }

    public void setOrientacaoFornecedores(String orientacaoFornecedores) {
        this.orientacaoFornecedores = orientacaoFornecedores;
    }

    public Boolean getPossuiIdentificadorUnico() {
        return possuiIdentificadorUnico;
    }

    public void setPossuiIdentificadorUnico(Boolean possuiIdentificadorUnico) {
        this.possuiIdentificadorUnico = possuiIdentificadorUnico;
    }

    public Boolean getPossuiInformacaoOpcional() {
        return possuiInformacaoOpcional;
    }

    public void setPossuiInformacaoOpcional(Boolean possuiInformacaoOpcional) {
        this.possuiInformacaoOpcional = possuiInformacaoOpcional;
    }

    public Boolean getPossuiDispositivoMovel() {
        return possuiDispositivoMovel;
    }

    public void setPossuiDispositivoMovel(Boolean possuiDispositivoMovel) {
        this.possuiDispositivoMovel = possuiDispositivoMovel;
    }

    public Boolean getPossuiGPS() {
        return possuiGPS;
    }

    public void setPossuiGPS(Boolean possuiGPS) {
        this.possuiGPS = possuiGPS;
    }

    public Boolean getPossuiData() {
        return possuiData;
    }

    public void setPossuiData(Boolean possuiData) {
        this.possuiData = possuiData;
    }

    public EnumResponsavelPreencherFormatacaoItem getResponsavelFormatacao() {
        return responsavelFormatacao;
    }

    public void setResponsavelFormatacao(EnumResponsavelPreencherFormatacaoItem responsavelFormatacao) {
        this.responsavelFormatacao = responsavelFormatacao;
    }

}

package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumFormaVerificacaoFormatacao;
import br.gov.mj.side.entidades.enums.EnumSituacaoBem;
import br.gov.mj.side.entidades.enums.EnumSituacaoPesquisaPatrimoniamento;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;

public class PatrimonioObjetoFornecimentoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private ObjetoFornecimentoContrato objetoFornecimentoContrato;
    private String numeroPatrimonio;
    private String nomeItem;
    private LocalDateTime dataCadastro;
    private Boolean itemPatrimoniavel;
    private String motivoItemNaoPatrimoniavel;
    private Entidade entidade;
    private Programa programa;
    private String identificadorUnico;
    private Long numeroQrCode;
    private EnumSituacaoPesquisaPatrimoniamento situacaoPesquisaPatrimoniamento;
    private EnumSituacaoBem situacaoBem;
    private String nomeBem;
    private EnumFormaVerificacaoFormatacao formaVerificacao;
    private byte[] conteudo;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public ObjetoFornecimentoContrato getObjetoFornecimentoContrato() {
        return objetoFornecimentoContrato;
    }
    public void setObjetoFornecimentoContrato(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        this.objetoFornecimentoContrato = objetoFornecimentoContrato;
    }
    public String getNumeroPatrimonio() {
        return numeroPatrimonio;
    }
    public void setNumeroPatrimonio(String numeroPatrimonio) {
        this.numeroPatrimonio = numeroPatrimonio;
    }
    public String getNomeItem() {
        return nomeItem;
    }
    public void setNomeItem(String nomeItem) {
        this.nomeItem = nomeItem;
    }
    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
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
    public byte[] getConteudo() {
        return conteudo;
    }
    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }
    public Entidade getEntidade() {
        return entidade;
    }
    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }
    public Programa getPrograma() {
        return programa;
    }
    public void setPrograma(Programa programa) {
        this.programa = programa;
    }
    public String getNomeBem() {
        return nomeBem;
    }
    public void setNomeBem(String nomeBem) {
        this.nomeBem = nomeBem;
    }
    public Long getNumeroQrCode() {
        return numeroQrCode;
    }
    public void setNumeroQrCode(Long numeroQrCode) {
        this.numeroQrCode = numeroQrCode;
    }
    public String getIdentificadorUnico() {
        return identificadorUnico;
    }
    public void setIdentificadorUnico(String identificadorUnico) {
        this.identificadorUnico = identificadorUnico;
    }
    public EnumSituacaoPesquisaPatrimoniamento getSituacaoPesquisaPatrimoniamento() {
        return situacaoPesquisaPatrimoniamento;
    }
    public void setSituacaoPesquisaPatrimoniamento(EnumSituacaoPesquisaPatrimoniamento situacaoPesquisaPatrimoniamento) {
        this.situacaoPesquisaPatrimoniamento = situacaoPesquisaPatrimoniamento;
    }
    public EnumSituacaoBem getSituacaoBem() {
        return situacaoBem;
    }
    public void setSituacaoBem(EnumSituacaoBem situacaoBem) {
        this.situacaoBem = situacaoBem;
    }
    public EnumFormaVerificacaoFormatacao getFormaVerificacao() {
        return formaVerificacao;
    }
    public void setFormaVerificacao(EnumFormaVerificacaoFormatacao formaVerificacao) {
        this.formaVerificacao = formaVerificacao;
    }
}

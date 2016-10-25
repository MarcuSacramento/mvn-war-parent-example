package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class QrCodeOrdemFornecedorDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String item;
    private String nomeEntidade;
    private String numeroCnpj;
    private String localEntrega;
    private String formaVerificacao;
    private Integer quantidade;
    private Boolean visualizar;
    private String link;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getNomeEntidade() {
        return nomeEntidade;
    }

    public void setNomeEntidade(String nomeEntidade) {
        this.nomeEntidade = nomeEntidade;
    }

    public String getNumeroCnpj() {
        return numeroCnpj;
    }

    public void setNumeroCnpj(String numeroCnpj) {
        this.numeroCnpj = numeroCnpj;
    }

    public String getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(String localEntrega) {
        this.localEntrega = localEntrega;
    }

    public String getFormaVerificacao() {
        return formaVerificacao;
    }

    public void setFormaVerificacao(String formaVerificacao) {
        this.formaVerificacao = formaVerificacao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getVisualizar() {
        return visualizar;
    }

    public void setVisualizar(Boolean visualizar) {
        this.visualizar = visualizar;
    }
}

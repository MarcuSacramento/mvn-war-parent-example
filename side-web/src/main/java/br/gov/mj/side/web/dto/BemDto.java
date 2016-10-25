package br.gov.mj.side.web.dto;

import br.gov.mj.side.entidades.Bem;

public class BemDto {

    private String nomeBem;
    private String descricaoBem;
    private String quantidade;
    private String quantidadeMaxPorProposta;

    // itens para formatação
    private Boolean itemSelecionadoFormatacao;
    private Bem bem;

    public String getNomeBem() {
        return nomeBem;
    }

    public void setNomeBem(String nomeBem) {
        this.nomeBem = nomeBem;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getQuantidadeMaxPorProposta() {
        return quantidadeMaxPorProposta;
    }

    public void setQuantidadeMaxPorProposta(String quantidadeMaxPorProposta) {
        this.quantidadeMaxPorProposta = quantidadeMaxPorProposta;
    }

    public Boolean getItemSelecionadoFormatacao() {
        return itemSelecionadoFormatacao;
    }

    public void setItemSelecionadoFormatacao(Boolean itemSelecionadoFormatacao) {
        this.itemSelecionadoFormatacao = itemSelecionadoFormatacao;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

}

package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumPerfilEntidade implements BaseEnum<String> {

    BENEFICIARIO("B", "Beneficiario"), FORNECEDOR("F", "Fornecedor");

    private String valor;
    private String descricao;

    private EnumPerfilEntidade(String t, String d) {
        valor = t;
        descricao = d;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}

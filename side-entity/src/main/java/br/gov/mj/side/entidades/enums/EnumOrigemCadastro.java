package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumOrigemCadastro implements BaseEnum<String> {

    CADASTRO_INTERNO("I", "Cadastro Interno"), CADASTRO_EXTERNO("E", "Cadastro Externo");

    private String valor;
    private String descricao;

    private EnumOrigemCadastro(String t, String d) {
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

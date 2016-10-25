package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumPerfilUsuario implements BaseEnum<String> {

    REPRESENTANTE("2", "REPRESENTANTE"), GESTOR("1", "GESTOR"), TITULAR("3", "TITULAR"), ADMINISTRADOR("4", "ADMINISTRADOR"), MEMBRO_COMISSAO("5", "MEMBRO_COMISSAO"), PREPOSTO("6", "PREPOSTO");

    private String valor;
    private String descricao;

    private EnumPerfilUsuario(String t, String d) {
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

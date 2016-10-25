package br.gov.mj.seg.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoUsuario implements BaseEnum<String> {
    INTERNO("I", "Interno"), EXTERNO("E", "Externo");

    private String valor;
    private String descricao;

    private EnumTipoUsuario(String valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    @Override
    public String getValor() {
        return this.valor;
    }

    public String getDescricao() {
        return this.descricao;
    }

}

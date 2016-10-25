package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoRetornoObjeto implements BaseEnum<String> {
    TODOS("TODO", "Sim"), SOMENTE_DESTA_NOTA("NOTA", "Não");

    private String valor;
    private String descricao;

    private EnumTipoRetornoObjeto(String valor, String descricao) {
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

package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusEntidade implements BaseEnum<String> {
    ATIVA("A", "Ativa"), INATIVA("I", "Inativa");

    private String valor;
    private String descricao;

    private EnumStatusEntidade(String valor, String descricao) {
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

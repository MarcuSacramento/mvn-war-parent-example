package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusTitular implements BaseEnum<String> {
    ATIVA("A", "Ativo"), INATIVO("I", "Inativo"), EXPIRADO("E", "Expirado");

    private String valor;
    private String descricao;

    private EnumStatusTitular(String valor, String descricao) {
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

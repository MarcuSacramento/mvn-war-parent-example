package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumConfirme implements BaseEnum<Boolean> {
    SIM(true, "Sim"), NAO(false, "Não");

    private Boolean valor;
    private String descricao;

    private EnumConfirme(boolean valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    @Override
    public Boolean getValor() {
        return this.valor;
    }

    public String getDescricao() {
        return this.descricao;
    }
}

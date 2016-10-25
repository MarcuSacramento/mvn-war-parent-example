package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;


public enum EnumStatusLocalEntrega implements BaseEnum<String> {
    HABILITADO("H", "Habilitado"), DESABILITADO("D", "Desabilitado");

    private String valor;
    private String descricao;

    private EnumStatusLocalEntrega(String valor, String descricao) {
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

package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusComunicacaoOrdemFornecimento implements BaseEnum<String> {
    NAO_COMUNICADA("NAOC", "Não comunicada"), COMUNICADA("COMU", "Comunicada"), CANCELADA("CANC","Cancelada");

    private String valor;
    private String descricao;

    private EnumStatusComunicacaoOrdemFornecimento(String valor, String descricao) {
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

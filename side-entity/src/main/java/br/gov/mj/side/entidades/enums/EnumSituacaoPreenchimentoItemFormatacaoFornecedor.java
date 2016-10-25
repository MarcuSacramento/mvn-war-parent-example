package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumSituacaoPreenchimentoItemFormatacaoFornecedor implements BaseEnum<String> {

    PREENCHIDO("P", "Preenchido"), NAO_PREENCHIDO("N", "Não preenchido"), PREENCHIDO_INCOMPLETO("I", "Preenchido incompleto"),PREENCHIDO_COM_PENDENCIA("E", "Preenchido com pendência");

    private String valor;
    private String descricao;

    private EnumSituacaoPreenchimentoItemFormatacaoFornecedor(String valor, String descricao) {
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

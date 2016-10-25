package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoArquivoTermoEntrega implements BaseEnum<String> {
    NOTA_REMESSA("NR", "Nota de remessa"), NOTA_FISCAL("N", "Nota fiscal"), TERMO_ENTREGA_ASSINADO("T", "Termo de entrega assinado"), RELATORIO_RECEBIMENTO_ASSINADO("R", "Relatório de recebimento assinado");

    private String valor;
    private String descricao;

    private EnumTipoArquivoTermoEntrega(String valor, String descricao) {
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

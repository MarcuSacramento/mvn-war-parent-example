package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumBotaoClicadoNotaRemessa implements BaseEnum<String> {
    REGISTRAR_ENTREGA("REGISTRAR", "Registrar Entrega"), EXECUTAR_ENTREGA("EXECUTAR", "Executar Entrega"),TERMO_RECEBIMENTO_ENVIADO("TERMO_RECEBIMENTO_ENVIADO", "Termo de recebimento enviado");

    private String valor;
    private String descricao;

    private EnumBotaoClicadoNotaRemessa(String valor, String descricao) {
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

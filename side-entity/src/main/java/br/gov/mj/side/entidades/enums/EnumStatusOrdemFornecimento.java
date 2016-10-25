package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusOrdemFornecimento implements BaseEnum<String> {
    NAO_COMUNICADA("NAOC", "Não comunicada"), EMITIDA("EMIT", "Emitida"), EXECUTADA("EXEC", "Aguardando entrega"), ENTREGUE("ENTR", "Entregue"), RECEBIDA("RECE", "Recebida"), COM_PENDENCIA("PEND", "Com pendência"), DEVOLVIDA("DEVO", "Devolvida"),CONCLUIDA("CONC", "Concluida");

    private String valor;
    private String descricao;

    private EnumStatusOrdemFornecimento(String valor, String descricao) {
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

    public void setValor(String valor) {
        this.valor = valor;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}

package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusExecucaoNotaRemessaFornecedor implements BaseEnum<String> {
    EM_PREPARACAO("PREP", "Em preparação"), EMITIDA("EMIT", "Emitida"), ENTREGUE("ENTR", "Entregue"), CONCLUIDA("CONC", "Concluida");

    private String valor;
    private String descricao;

    private EnumStatusExecucaoNotaRemessaFornecedor(String valor, String descricao) {
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

package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusExecucaoNotaRemessaBeneficiario implements BaseEnum<String> {
    NAO_EMITIDA("NAOE", "Não emitida pelo fornecedor"), EMITIDA("EMIT", "Emitida"), RECEBIDO("RECE", "Recebido"), EM_ANALISE("ANAL", "Em análise"), EM_ATRASO("ATRA", "Em atraso"), INADIPLENTE("INAD", "Inadimplente"), ENVIADO("ENVI", "Enviado");

    private String valor;
    private String descricao;

    private EnumStatusExecucaoNotaRemessaBeneficiario(String valor, String descricao) {
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

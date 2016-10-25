package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusFormatacao implements BaseEnum<String> {

    NAO_PREENCHIDO("NAOP","Não Preenchido"),CONFORMIDADE("CONF", "Em conformidade"), NAO_CONFORMIDADE("NCON", "Não conformidade");

    private String valor;
    private String descricao;

    private EnumStatusFormatacao(String t, String d) {
        valor = t;
        descricao = d;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}

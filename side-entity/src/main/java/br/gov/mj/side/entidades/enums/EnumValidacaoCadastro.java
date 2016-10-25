package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumValidacaoCadastro implements BaseEnum<String> {

    VALIDADO("V", "Validado"), RECUSADO("R", "Recusado"),NAO_ANALISADO("N", "Não Analisado"),EXPURGO("E", "Entidade para expurgo");

    private String valor;
    private String descricao;

    private EnumValidacaoCadastro(String t, String d) {
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

package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusGeracaoTermoDoacao implements BaseEnum<String> {

    TERMO_DOACAO_NAO_GERADO("NAOG", "Termo de doação não gerado"), 
    TERMO_DOACAO_GERADO("GERA", "Termo de doação gerado");
    
    private String valor;
    private String descricao;

    private EnumStatusGeracaoTermoDoacao(String valor, String descricao) {
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

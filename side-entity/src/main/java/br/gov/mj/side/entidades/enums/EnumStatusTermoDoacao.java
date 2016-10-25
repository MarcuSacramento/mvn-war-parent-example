package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumStatusTermoDoacao implements BaseEnum<String> {

    DOACAO_NAO_CONCLUIDA("NAOC", "Doação não concluída"), 
    DOACAO_CONCLUIDA("CONC", "Doação concluída");
    
    private String valor;
    private String descricao;

    private EnumStatusTermoDoacao(String valor, String descricao) {
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

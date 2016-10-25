package br.gov.mj.side.web.view.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoItemEntrega implements BaseEnum<String>{
    BEM("BEM","Bem"),
    KIT("KIT","Kit");

    private String valor;
    private String descricao;
    
    private EnumTipoItemEntrega(String valor,String descricao) {
        this.descricao = descricao;
        this.valor = valor;
    }
    
    @Override
    public String getValor() {
        return this.valor;
    }
    
    public String getDescricao(){
        return this.descricao;
    }

}

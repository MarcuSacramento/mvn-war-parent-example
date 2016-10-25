package br.gov.mj.side.entidades.enums;

import br.gov.mj.infra.negocio.persistencia.BaseEnum;

public enum EnumTipoPrograma implements BaseEnum<String> {

    EQUIPAGEM("E", "Equipagem"), 
    REGISTRO_PRECO("R", "Registro de preço"), 
    REGISTRO_CONVENIO("C", "Registro / Convênio"), 
    DESFAZIMENTO("D", "Desfazimento"), 
    DESFAZIMENTO_APREENSAO("A", "Desfazimento Apreensão");
    
    private String valor;
    private String descricao;

    private EnumTipoPrograma(String valor, String descricao) {
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

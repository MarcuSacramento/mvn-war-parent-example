package br.gov.mj.side.web.dto.minuta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.Bem;

public class BemEspecificacaoDto implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private List<Bem> listaDeBens = new ArrayList<Bem>();

    public List<Bem> getListaDeBens() {
        return listaDeBens;
    }

    public void setListaDeBens(List<Bem> listaDeBens) {
        this.listaDeBens = listaDeBens;
    }
}

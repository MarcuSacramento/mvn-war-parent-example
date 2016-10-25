package br.gov.mj.side.web.dto;

import java.util.List;

public class MinutaTrRecursoFinanceiro {
    private String recurso;
    private List<MinutaTrEmendas> minutaTrList;

    public String getRecurso() {
        return recurso;
    }

    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }

    public List<MinutaTrEmendas> getMinutaTrList() {
        return minutaTrList;
    }

    public void setMinutaTrList(List<MinutaTrEmendas> minutaTrList) {
        this.minutaTrList = minutaTrList;
    }
}
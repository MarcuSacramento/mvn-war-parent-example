package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.Comparator;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;

public class BemUfDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private SelecaoItem selecaoItem = null;
    private Boolean selecionado;
    private Bem bem;
    private Uf uf;
    private Long quantidade;

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Uf getUf() {
        return uf;
    }

    public void setUf(Uf uf) {
        this.uf = uf;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public static Comparator<BemUfDto> getComparator(int order, String coluna) {
        return new Comparator<BemUfDto>() {
            @Override
            public int compare(BemUfDto o1, BemUfDto o2) {
                int valor = 0;

                if ("nome".equalsIgnoreCase(coluna)) {
                    valor = o1.getBem().getNomeBem().toUpperCase().compareTo(o2.getBem().getNomeBem().toUpperCase()) * order;
                } else if ("regiao".equalsIgnoreCase(coluna)) {
                    valor = o1.getUf().getRegiao().getNomeRegiao().compareTo(o2.getUf().getRegiao().getNomeRegiao()) * order;
                } else if ("uf".equalsIgnoreCase(coluna)) {
                    valor = o1.getUf().getSiglaUf().compareTo(o2.getUf().getSiglaUf()) * order;
                } else if ("quantidade".equalsIgnoreCase(coluna)) {
                    valor = o1.getQuantidade().compareTo(o2.getQuantidade()) * order;
                }
                return valor;
            }
        };
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public SelecaoItem getSelecaoItem() {
        return selecaoItem;
    }

    public void setSelecaoItem(SelecaoItem selecaoItem) {
        this.selecaoItem = selecaoItem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

package br.gov.mj.side.entidades;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_kit_kit", schema = "side")
public class Kit extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "kit_id_kit")
    @SequenceGenerator(name = "TB_KIT_KIT_KITIDKIT_GENERATOR", sequenceName = "SIDE.SEQ_TB_KIT_KIT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_KIT_KIT_KITIDKIT_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "kit_no_kit")
    @NotNull
    private String nomeKit;

    @Column(name = "kit_ds_kit")
    @NotNull
    private String descricaoKit;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "kit", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<KitBem> kitsBens = new ArrayList<KitBem>();

    public Kit() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeKit() {
        return nomeKit;
    }

    public void setNomeKit(String nomeKit) {
        this.nomeKit = nomeKit;
    }

    public List<KitBem> getKitsBens() {
        return kitsBens;
    }

    public void setKitsBens(List<KitBem> kitsBens) {
        this.kitsBens = kitsBens;
    }

    public String getDescricaoKit() {
        return descricaoKit;
    }

    public void setDescricaoKit(String descricaoKit) {
        this.descricaoKit = descricaoKit;
    }

    public BigDecimal getValorEstimado() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (KitBem kitBem : this.kitsBens) {
            BigDecimal quantidadeEmBigDecimal = new BigDecimal(String.valueOf(kitBem.getQuantidade()));
            retorno = retorno.add(kitBem.getBem().getValorEstimadoBem().multiply(quantidadeEmBigDecimal));
        }
        return retorno;
    }

    public static Comparator<Kit> getComparator(int order, String coluna) {
        return new Comparator<Kit>() {
            @Override
            public int compare(Kit o1, Kit o2) {
                int valor = 0;

                if ("nome".equalsIgnoreCase(coluna)) {
                    valor = o1.getNomeKit().toUpperCase().compareTo(o2.getNomeKit().toUpperCase()) * order;
                } else if ("descricao".equalsIgnoreCase(coluna)) {
                    valor = o1.getDescricaoKit().toUpperCase().compareTo(o2.getDescricaoKit().toUpperCase()) * order;
                } else if ("valor".equalsIgnoreCase(coluna)) {
                    valor = o1.getValorEstimado().compareTo(o2.getValorEstimado()) * order;
                }
                return valor;
            }
        };
    }

}
package br.gov.mj.side.entidades;

import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_kib_kit_bem", schema = "side")
public class KitBem extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "kib_id_kit_bem")
    @SequenceGenerator(name = "TB_KIB_KIT_BEM_KIBIDKITBEM_GENERATOR", sequenceName = "SIDE.SEQ_TB_KIB_KIT_BEM", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_KIB_KIT_BEM_KIBIDKITBEM_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "kib_qt_quantidade")
    @NotNull
    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "kib_fk_bem_id_bem")
    @NotNull
    private Bem bem;

    @ManyToOne
    @JoinColumn(name = "kib_fk_kit_id_kit")
    @NotNull
    private Kit kit;

    public KitBem() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    @Transient
    public static Comparator<KitBem> getComparator(int order, String coluna) {
        return new Comparator<KitBem>() {
            @Override
            public int compare(KitBem o1, KitBem o2) {
                int valor = 0;

                if ("nome".equalsIgnoreCase(coluna)) {
                    valor = o1.getBem().getNomeBem().toUpperCase().compareTo(o2.getBem().getNomeBem().toUpperCase()) * order;
                } else if ("descricao".equalsIgnoreCase(coluna)) {
                    valor = o1.getBem().getDescricaoBem().toUpperCase().compareTo(o2.getBem().getDescricaoBem().toUpperCase()) * order;
                }
                return valor;
            }
        };
    }

}
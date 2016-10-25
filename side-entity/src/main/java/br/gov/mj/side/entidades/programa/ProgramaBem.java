package br.gov.mj.side.entidades.programa;

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
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.Bem;

@Entity
@Table(name = "tb_prb_programa_bem", schema = "side")
public class ProgramaBem extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "prb_id_programa_bem")
    @SequenceGenerator(name = "tb_prb_programa_bem_generator", sequenceName = "side.seq_tb_prb_programa_bem", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_prb_programa_bem_generator")
    @NotNull
    private Long id;

    @Column(name = "prb_qt_quantidade")
    @NotNull
    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "prb_fk_bem_id_bem")
    @NotNull
    private Bem bem;

    @ManyToOne
    @JoinColumn(name = "prb_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @Column(name = "prb_qt_quantidade_proposta")
    @NotNull
    private Integer quantidadePorProposta;

    public ProgramaBem() {
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

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public static Comparator<ProgramaBem> getComparator(int order, String coluna) {
        return new Comparator<ProgramaBem>() {
            @Override
            public int compare(ProgramaBem o1, ProgramaBem o2) {
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

    public Integer getQuantidadePorProposta() {
        return quantidadePorProposta;
    }

    public void setQuantidadePorProposta(Integer quantidadePorProposta) {
        this.quantidadePorProposta = quantidadePorProposta;
    }

}
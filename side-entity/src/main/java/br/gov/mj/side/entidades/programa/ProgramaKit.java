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
import br.gov.mj.side.entidades.Kit;

@Entity
@Table(name = "tb_prk_programa_kit", schema = "side")
public class ProgramaKit extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "prk_id_programa_kit")
    @SequenceGenerator(name = "tb_prk_programa_kit_generator", sequenceName = "side.seq_tb_prk_programa_kit", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_prk_programa_kit_generator")
    @NotNull
    private Long id;

    @Column(name = "prk_qt_quantidade")
    @NotNull
    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "prk_fk_kit_id_kit")
    @NotNull
    private Kit kit;

    @ManyToOne
    @JoinColumn(name = "prk_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @Column(name = "prk_qt_quantidade_proposta")
    @NotNull
    private Integer quantidadePorProposta;

    public ProgramaKit() {
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

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public static Comparator<ProgramaKit> getComparator(int order, String coluna) {
        return new Comparator<ProgramaKit>() {
            @Override
            public int compare(ProgramaKit o1, ProgramaKit o2) {
                int valor = 0;

                if ("nome".equalsIgnoreCase(coluna)) {
                    valor = o1.getKit().getNomeKit().toUpperCase().compareTo(o2.getKit().getNomeKit().toUpperCase()) * order;
                } else if ("descricao".equalsIgnoreCase(coluna)) {
                    valor = o1.getKit().getDescricaoKit().toUpperCase().compareTo(o2.getKit().getDescricaoKit().toUpperCase()) * order;
                } else if ("valor".equalsIgnoreCase(coluna)) {
                    valor = o1.getKit().getValorEstimado().compareTo(o2.getKit().getValorEstimado()) * order;
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
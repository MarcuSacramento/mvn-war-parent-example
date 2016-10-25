package br.gov.mj.side.entidades.programa.licitacao;

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

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.Bem;

@Entity
@Table(name = "tb_buf_bem_uf", schema = "side")
public class BemUf extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "buf_id_bem_uf")
    @SequenceGenerator(name = "tb_buf_bem_uf_generator", sequenceName = "side.seq_tb_buf_bem_uf", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_buf_bem_uf_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buf_fk_sit_id_selecao_item")
    @NotNull
    private SelecaoItem selecaoItem;

    @ManyToOne
    @JoinColumn(name = "buf_fk_bem_id_bem")
    @NotNull
    private Bem bem;

    @ManyToOne
    @JoinColumn(name = "buf_fk_euf_id_estado")
    @NotNull
    private Uf uf;

    @Column(name = "buf_qt_quantidade")
    @NotNull
    private Long quantidade;

    public BemUf() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public SelecaoItem getSelecaoItem() {
        return selecaoItem;
    }

    public void setSelecaoItem(SelecaoItem selecaoItem) {
        this.selecaoItem = selecaoItem;
    }

}
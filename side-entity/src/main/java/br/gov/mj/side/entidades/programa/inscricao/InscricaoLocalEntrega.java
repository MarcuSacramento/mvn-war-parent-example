package br.gov.mj.side.entidades.programa.inscricao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;

@Entity
@Table(name = "tb_ile_inscricao_programa_local_entrega", schema = "side")
public class InscricaoLocalEntrega extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ile_id_inscricao_programa_local_entrega")
    @SequenceGenerator(name = "tb_ile_inscricao_programa_local_entrega_generator", sequenceName = "side.seq_tb_ile_inscricao_programa_local_entrega", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ile_inscricao_programa_local_entrega_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ile_fk_ipg_id_inscricao_programa")
    @NotNull
    private InscricaoPrograma inscricaoPrograma;

    @ManyToOne
    @JoinColumn(name = "ile_fk_lee_id_local_entrega_entidade")
    @NotNull
    private LocalEntregaEntidade localEntregaEntidade;

    @OneToMany(mappedBy = "inscricaoLocalEntrega", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<InscricaoLocalEntregaBem> bensEntrega = new ArrayList<InscricaoLocalEntregaBem>();

    @OneToMany(mappedBy = "inscricaoLocalEntrega", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<InscricaoLocalEntregaKit> kitsEntrega = new ArrayList<InscricaoLocalEntregaKit>();

    public InscricaoLocalEntrega() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InscricaoPrograma getInscricaoPrograma() {
        return inscricaoPrograma;
    }

    public void setInscricaoPrograma(InscricaoPrograma inscricaoPrograma) {
        this.inscricaoPrograma = inscricaoPrograma;
    }

    public LocalEntregaEntidade getLocalEntregaEntidade() {
        return localEntregaEntidade;
    }

    public void setLocalEntregaEntidade(LocalEntregaEntidade localEntregaEntidade) {
        this.localEntregaEntidade = localEntregaEntidade;
    }

    public List<InscricaoLocalEntregaBem> getBensEntrega() {
        return bensEntrega;
    }

    public void setBensEntrega(List<InscricaoLocalEntregaBem> bensEntrega) {
        this.bensEntrega = bensEntrega;
    }

    public List<InscricaoLocalEntregaKit> getKitsEntrega() {
        return kitsEntrega;
    }

    public void setKitsEntrega(List<InscricaoLocalEntregaKit> kitsEntrega) {
        this.kitsEntrega = kitsEntrega;
    }

}
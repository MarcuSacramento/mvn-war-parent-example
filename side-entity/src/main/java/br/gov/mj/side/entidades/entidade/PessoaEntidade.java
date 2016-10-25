package br.gov.mj.side.entidades.entidade;

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

@Entity
@Table(name = "tb_pse_pessoa_entidade", schema = "side")
public class PessoaEntidade extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pse_id_pessoa_entidade")
    @SequenceGenerator(name = "tb_pse_pessoa_entidade_generator", sequenceName = "side.seq_tb_pse_pessoa_entidade", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_pse_pessoa_entidade_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pse_fk_ent_id_entidade")
    @NotNull
    private Entidade entidade;

    @ManyToOne
    @JoinColumn(name = "pse_fk_pso_id_pessoa")
    @NotNull
    private Pessoa pessoa;

    public PessoaEntidade() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

}
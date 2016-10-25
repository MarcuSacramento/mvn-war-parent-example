package br.gov.mj.apoio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_ten_tipo_entidade", schema = "apoio")
public class TipoEntidade extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ten_id_tipo_entidade")
    @NotNull
    private Long id;

    @Column(name = "ten_ds_descricao_tipo_entidade")
    @NotNull
    private String descricaoTipoEntidade;

    public TipoEntidade() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricaoTipoEntidade() {
        return descricaoTipoEntidade;
    }

    public void setDescricaoTipoEntidade(String descricaoTipoEntidade) {
        this.descricaoTipoEntidade = descricaoTipoEntidade;
    }

}
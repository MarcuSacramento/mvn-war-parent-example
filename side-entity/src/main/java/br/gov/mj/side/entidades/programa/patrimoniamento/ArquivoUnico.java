package br.gov.mj.side.entidades.programa.patrimoniamento;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumOrigemArquivo;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;

@Entity
@Table(name = "tb_arq_codigo_unico_arquivo_cadastrado", schema = "side")
public class ArquivoUnico extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "arq_id_codigo_unico_arquivo_cadastrado")
    @SequenceGenerator(name = "seq_tb_arq_codigo_unico_arquivo_cadastrado_generator", sequenceName = "side.seq_tb_arq_codigo_unico_arquivo_cadastrado", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_arq_codigo_unico_arquivo_cadastrado_generator")
    @NotNull
    private Long id;

    @Column(name = "arq_no_codigo_unico_imagem")
    private String codigoUnico;
    
    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumOrigemArquivo", name = "enumClassName"))
    @Column(name = "arq_tp_origem_arquivo")
    private EnumOrigemArquivo origemArquivo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public EnumOrigemArquivo getOrigemArquivo() {
        return origemArquivo;
    }

    public void setOrigemArquivo(EnumOrigemArquivo origemArquivo) {
        this.origemArquivo = origemArquivo;
    }
}

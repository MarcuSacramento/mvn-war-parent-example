package br.gov.mj.side.web.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.gov.mj.side.entidades.CodigoVerificacao;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class CodigoVerificacaoDAO {

    @Inject
    private EntityManager em;

    public CodigoVerificacao incluir(String descricaoCodigoVerificacao) {
        CodigoVerificacao codigoVerificacao = new CodigoVerificacao();
        codigoVerificacao.setDescricaoCodigoVerificacao(descricaoCodigoVerificacao);
        em.persist(codigoVerificacao);
        return codigoVerificacao;
    }

}

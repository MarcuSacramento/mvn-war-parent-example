package br.gov.mj.side.web.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.apoio.entidades.TipoEntidade;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TipoEntidadeDAO {

    private static final String ATRIBUTO_ORDENACAO_PADRAO = "descricaoTipoEntidade";
    @Inject
    private EntityManager em;

    public List<TipoEntidade> buscarTodos() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TipoEntidade> criteriaQuery = criteriaBuilder.createQuery(TipoEntidade.class);
        Root<TipoEntidade> root = criteriaQuery.from(TipoEntidade.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ATRIBUTO_ORDENACAO_PADRAO)));
        TypedQuery<TipoEntidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<TipoEntidade> buscar(TipoEntidade tipoEntidade) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TipoEntidade> criteriaQuery = criteriaBuilder.createQuery(TipoEntidade.class);
        Root<TipoEntidade> root = criteriaQuery.from(TipoEntidade.class);

        Predicate[] predicates = extractPredicates(tipoEntidade, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ATRIBUTO_ORDENACAO_PADRAO)));

        TypedQuery<TipoEntidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(TipoEntidade tipoEntidade, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (tipoEntidade.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), tipoEntidade.getId()));
        }
        if (StringUtils.isNotBlank(tipoEntidade.getDescricaoTipoEntidade())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get(ATRIBUTO_ORDENACAO_PADRAO))), "%" + UtilDAO.removerAcentos(tipoEntidade.getDescricaoTipoEntidade().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

}

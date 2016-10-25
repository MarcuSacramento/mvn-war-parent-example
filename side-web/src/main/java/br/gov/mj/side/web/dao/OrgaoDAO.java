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

import br.gov.mj.apoio.entidades.Orgao;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class OrgaoDAO {

    private static final String ATRIBUTO_ORDENACAO_PADRAO = "nomeOrgao";
    @Inject
    private EntityManager em;

    public List<Orgao> buscarTodos() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Orgao> criteriaQuery = criteriaBuilder.createQuery(Orgao.class);
        Root<Orgao> root = criteriaQuery.from(Orgao.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ATRIBUTO_ORDENACAO_PADRAO)));
        TypedQuery<Orgao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Orgao> buscar(Orgao orgao) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Orgao> criteriaQuery = criteriaBuilder.createQuery(Orgao.class);
        Root<Orgao> root = criteriaQuery.from(Orgao.class);

        Predicate[] predicates = extractPredicates(orgao, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(ATRIBUTO_ORDENACAO_PADRAO)));

        TypedQuery<Orgao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(Orgao orgao, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (orgao.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), orgao.getId()));
        }
        if (StringUtils.isNotBlank(orgao.getNomeOrgao())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("nomeOrgao"))), "%" + UtilDAO.removerAcentos(orgao.getNomeOrgao().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

}

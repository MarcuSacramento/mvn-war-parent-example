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

import br.gov.mj.apoio.entidades.Funcao;
import br.gov.mj.apoio.entidades.SubFuncao;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class SubFuncaoDAO {

    private static final String NOME_SUB_FUNCAO = "nomeSubFuncao";
    @Inject
    private EntityManager em;

    public List<SubFuncao> buscar(SubFuncao subFuncao) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<SubFuncao> criteriaQuery = criteriaBuilder.createQuery(SubFuncao.class);
        Root<SubFuncao> root = criteriaQuery.from(SubFuncao.class);

        Predicate[] predicates = extractPredicates(subFuncao, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_SUB_FUNCAO)));

        TypedQuery<SubFuncao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<SubFuncao> buscarPelaFuncaoId(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<SubFuncao> criteriaQuery = criteriaBuilder.createQuery(SubFuncao.class);
        Root<SubFuncao> root = criteriaQuery.from(SubFuncao.class);

        Funcao fun = new Funcao();
        fun.setId(id);
        SubFuncao suf = new SubFuncao();
        suf.setFuncao(fun);

        Predicate[] predicates = extractPredicates(suf, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_SUB_FUNCAO)));

        TypedQuery<SubFuncao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(SubFuncao subFuncao, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (subFuncao.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), subFuncao.getId()));
        }
        if (StringUtils.isNotBlank(subFuncao.getNomeSubFuncao())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get(NOME_SUB_FUNCAO))), "%" + UtilDAO.removerAcentos(subFuncao.getNomeSubFuncao().toLowerCase()) + "%"));
        }

        if (subFuncao.getFuncao() != null && subFuncao.getFuncao().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("funcao").get("id"), subFuncao.getFuncao().getId()));
        }

        if (subFuncao.getFuncao() != null && StringUtils.isNotBlank(subFuncao.getFuncao().getNomeFuncao())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO,String.class,criteriaBuilder.lower(root.get("funcao").get("nomeFuncao"))), "%" + UtilDAO.removerAcentos(subFuncao.getFuncao().getNomeFuncao().toLowerCase()) + "%"));
        }

        return predicates.toArray(new Predicate[] {});

    }

}

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

import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.patrimoniamento.ArquivoUnico;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ArquivoUnicoDAO {

    @Inject
    private EntityManager em;

    // Só é possivel usar 1 foto em toda a aplicação, cada foto terá um codigo especifico e unico
    public boolean verificarSeAFotoJafoiUtilizadaNoSistema(String hashImagem) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<ArquivoUnico> root = criteriaQuery.from(ArquivoUnico.class);

        Predicate predHash = criteriaBuilder.equal(root.get("codigoUnico"), hashImagem);
        criteriaQuery.multiselect(root.get("codigoUnico")).where(criteriaBuilder.and(predHash));

        TypedQuery<String> query = em.createQuery(criteriaQuery);
        List<String> listaRetornada = query.getResultList();

        if (listaRetornada != null && listaRetornada.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public List<ArquivoUnico> buscarSemPaginacao(ArquivoUnico arquivoUnico) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ArquivoUnico> criteriaQuery = criteriaBuilder.createQuery(ArquivoUnico.class);
        Root<ArquivoUnico> root = criteriaQuery.from(ArquivoUnico.class);

        Predicate[] predicates = extractPredicates(arquivoUnico, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        TypedQuery<ArquivoUnico> query = em.createQuery(criteriaQuery);
        List<ArquivoUnico> listaRetornada = query.getResultList();
        return listaRetornada;
    }
    
    private Predicate[] extractPredicates(ArquivoUnico arquivoUnico, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(arquivoUnico.getCodigoUnico() != null){
            predicates.add(criteriaBuilder.equal(root.get("codigoUnico"),arquivoUnico.getCodigoUnico()));;
        }
        
        if(arquivoUnico.getOrigemArquivo() != null){
            predicates.add(criteriaBuilder.equal(root.get("origemArquivo"),arquivoUnico.getOrigemArquivo()));;
        }
        return predicates.toArray(new Predicate[] {});
    }

    
    
    public ArquivoUnico buscarPeloId(Long id){
        ArquivoUnico retornar = em.find(ArquivoUnico.class, id);
        return retornar;
    }

    public ArquivoUnico incluir(ArquivoUnico arquivosCadastrados) {
        em.persist(arquivosCadastrados);
        return arquivosCadastrados;
    }
    
    public void excluir(ArquivoUnico arquivosCadastrados){
        em.remove(arquivosCadastrados);
    }
    
    public ArquivoUnico alterar(ArquivoUnico arquivosCadastrados){
        return em.merge(arquivosCadastrados);
    }
}

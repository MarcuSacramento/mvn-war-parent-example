package br.gov.mj.side.web.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.gov.mj.side.entidades.enums.EnumTipoObjeto;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.dto.ObjetoFornecimentoContratoDto;
import br.gov.mj.side.web.service.OrdemFornecimentoContratoService;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ObjetoFornecimentoContratoDAO {

    @Inject
    private EntityManager em;

    @Inject
    private OrdemFornecimentoContratoService ordemFornecimentoContratoService;

    public void devolverParaCorrecao(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        ordemFornecimentoContratoService.inserirObjetoFornecimentoEnviadoParaCorrecao(objetoFornecimentoContrato);
    }

    public void setarNotaRemessaNoObjetoFornecimento(ObjetoFornecimentoContrato objetoFornecimentoContrato, NotaRemessaOrdemFornecimentoContrato notaRemessa){
        String stringQuery = " UPDATE side.tb_ofo_objeto_fornecimento_contrato "+
                " SET "+
                " ofo_fk_nrc_id_nota_remessa_ordem_fornecimento_contrato=:idNotaRemessa "+
                " WHERE "+
                " ofo_id_objeto_fornecimento_contrato=:idObjetoFornecimento ";
        Query query = em.createNativeQuery(stringQuery);
        query.setParameter("idNotaRemessa", notaRemessa.getId());
        query.setParameter("idObjetoFornecimento", objetoFornecimentoContrato.getId());
        query.executeUpdate();
    }
    
    public void retirarNotaRemessaNoObjetoFornecimento(ObjetoFornecimentoContrato objetoFornecimentoContrato){
        String stringQuery = " UPDATE side.tb_ofo_objeto_fornecimento_contrato "+
                " SET "+
                " ofo_fk_nrc_id_nota_remessa_ordem_fornecimento_contrato=null "+
                " WHERE "+
                " ofo_id_objeto_fornecimento_contrato=:idObjetoFornecimento ";
        Query query = em.createNativeQuery(stringQuery);
        query.setParameter("idObjetoFornecimento", objetoFornecimentoContrato.getId());
        query.executeUpdate();
    }
    
    public List<ObjetoFornecimentoContrato> buscarSemPaginacao(ObjetoFornecimentoContratoDto objetoDto){
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ObjetoFornecimentoContrato> criteriaQuery = criteriaBuilder.createQuery(ObjetoFornecimentoContrato.class);
        Root<ObjetoFornecimentoContrato> root = criteriaQuery.from(ObjetoFornecimentoContrato.class);

        Predicate[] predicates = extractPredicates(objetoDto, criteriaBuilder, root);
               
        criteriaQuery.select(criteriaQuery.getSelection()).where(criteriaBuilder.and(predicates));

        TypedQuery<ObjetoFornecimentoContrato> query = em.createQuery(criteriaQuery);
        
        List<ObjetoFornecimentoContrato> lista = new ArrayList<ObjetoFornecimentoContrato>();
        lista = query.getResultList();
        return lista;
    }
    
    private Predicate[] extractPredicates(ObjetoFornecimentoContratoDto objetoFornecimentoContratoDto, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (objetoFornecimentoContratoDto.getObjetoFornecimentoContrato() != null) {
            if (objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getItem() != null) {
                predicates.add(criteriaBuilder.equal(root.get("item").get("id"), objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getItem().getId()));
            }

            if (objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getLocalEntrega() != null) {
                predicates.add(criteriaBuilder.equal(root.get("localEntrega").get("id"), objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getLocalEntrega().getId()));
            }

            if (objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getOrdemFornecimento() != null) {
                predicates.add(criteriaBuilder.equal(root.get("ordemFornecimento").get("id"), objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getOrdemFornecimento().getId()));
            }

            if (objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getFormaVerificacao() != null) {
                predicates.add(criteriaBuilder.equal(root.get("formaVerificacao"), objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getFormaVerificacao()));
            }
            
            if(objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getObjetoDevolvido() != null){
                predicates.add(criteriaBuilder.equal(root.get("objetoDevolvido"), objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getObjetoDevolvido()));
            }
            
            if(objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getNotaRemessaOrdemFornecimentoContrato() != null){
                predicates.add(criteriaBuilder.equal(root.get("notaRemessaOrdemFornecimentoContrato"), objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getNotaRemessaOrdemFornecimentoContrato().getId()));
            }else{
                predicates.add(criteriaBuilder.isNull(root.get("notaRemessaOrdemFornecimentoContrato")));
            }
            
            if(objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getFormaVerificacao() != null){
                predicates.add(criteriaBuilder.equal(root.get("formaVerificacao"), objetoFornecimentoContratoDto.getObjetoFornecimentoContrato().getFormaVerificacao()));
            }
        }
        
        if(objetoFornecimentoContratoDto.getTipoObjeto() != null){
            if(objetoFornecimentoContratoDto.getTipoObjeto() == EnumTipoObjeto.ORIGINAIS){
                predicates.add(criteriaBuilder.isNull(root.get("objetoFornecimentoContratoPai")));
            }else{
                if(objetoFornecimentoContratoDto.getTipoObjeto() == EnumTipoObjeto.TODOS_DEVOLVIDOS){
                    predicates.add(criteriaBuilder.isNotNull(root.get("objetoFornecimentoContratoPai")));
                }else{
                    if(objetoFornecimentoContratoDto.getTipoObjeto() == EnumTipoObjeto.DEVOLVIDOS_SEM_VINCULO_COM_NOTA_REMESSA){
                        predicates.add(criteriaBuilder.isNotNull(root.get("objetoFornecimentoContratoPai")));
                        predicates.add(criteriaBuilder.isNull(root.get("notaRemessaOrdemFornecimentoContrato")));
                    }
                }
            }
        }
        return predicates.toArray(new Predicate[] {});
    }
}

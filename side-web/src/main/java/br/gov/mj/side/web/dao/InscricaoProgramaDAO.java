package br.gov.mj.side.web.dao;

import java.time.LocalDateTime;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoAnexo;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoRecebimento;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoAnexoAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoAnexoElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntrega;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntregaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntregaKit;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaKit;
import br.gov.mj.side.web.dto.BensVinculadosEntidadeDto;
import br.gov.mj.side.web.dto.BensVinculadosLocaisEntregaDto;
import br.gov.mj.side.web.dto.BensVinculadosTotalDto;
import br.gov.mj.side.web.dto.KitsVinculadosTotalDto;
import br.gov.mj.side.web.util.CnpjUtil;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class InscricaoProgramaDAO {

    @Inject
    private EntityManager em;

    private static final String INSCRICAO_PROGRAMA = "inscricaoPrograma";

    public List<ComissaoRecebimento> buscarComissaoRecebimento(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ComissaoRecebimento> criteriaQuery = criteriaBuilder.createQuery(ComissaoRecebimento.class);
        Root<ComissaoRecebimento> root = criteriaQuery.from(ComissaoRecebimento.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(INSCRICAO_PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<ComissaoRecebimento> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<InscricaoPrograma> buscarInscricaoProgramaPeloPrograma(Programa programa) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoPrograma> criteriaQuery = criteriaBuilder.createQuery(InscricaoPrograma.class);
        Root<InscricaoPrograma> root = criteriaQuery.from(InscricaoPrograma.class);
        List<Predicate> predicates = new ArrayList<>();

        if (programa != null && programa.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("programa").get("id"), programa.getId()));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoPrograma> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }
    
    public List<BensVinculadosEntidadeDto> buscarBensVinculadosAEnderecos(Programa programa){
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        Root<InscricaoPrograma> root = criteriaQuery.from(InscricaoPrograma.class);
        
        Join<InscricaoPrograma,PessoaEntidade> joinPessoaEntidade = root.join("pessoaEntidade");
        Join<PessoaEntidade,Entidade> joinEntidade = joinPessoaEntidade.join("entidade");
        Join<InscricaoPrograma,Programa> joinPrograma = root.join("programa");
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("programa").get("id"), programa.getId()));

        criteriaQuery.multiselect(joinEntidade.get("nomeEntidade"),joinPrograma.get("nomePrograma"),root.get("id")).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        
        List<BensVinculadosEntidadeDto> listaEntidade = new ArrayList<BensVinculadosEntidadeDto>();
        
        List<Object> resultado = query.getResultList();
        listaEntidade = montarListaDeBensVinculados(resultado);
        return listaEntidade;
    }
    
    //Metodo utilizado para montar o DTO que será usado para mostrar os itens que já foram vinculados a locais de entrega
    private List<BensVinculadosEntidadeDto> montarListaDeBensVinculados(List<Object> resultado){
        
        List<BensVinculadosEntidadeDto> listaEntidade = new ArrayList<BensVinculadosEntidadeDto>();
        for(Object object:resultado){
            Object[] o = (Object[]) object;            
            List<InscricaoProgramaBem> listaProgramaBem = buscarInscricaoProgramaBem((Long)o[2]);
            List<InscricaoProgramaKit> listaProgramaKit = buscarInscricaoProgramaKit((Long)o[2]);
            List<InscricaoLocalEntrega> listaLocaisEntrega = buscarInscricaoLocalEntrega((Long)o[2]);
            
            BensVinculadosEntidadeDto bemBensVinculadosEntidadeDto = new BensVinculadosEntidadeDto();
            bemBensVinculadosEntidadeDto.setEntidade(listaProgramaBem.get(0).getInscricaoPrograma().getPessoaEntidade().getEntidade());
            
            montarListaLocaisEntregaBem(listaProgramaBem,listaLocaisEntrega,bemBensVinculadosEntidadeDto);
            montarListaLocaisEntregaKit(listaProgramaKit, listaLocaisEntrega, bemBensVinculadosEntidadeDto);
                      
            listaEntidade.add(bemBensVinculadosEntidadeDto);
        }
        return listaEntidade;
    }
    
    //Metodo utilizado para montar o DTO que será usado para mostrar os itens que já foram vinculados a locais de entrega
    private void montarListaLocaisEntregaBem(List<InscricaoProgramaBem> listaProgramaBem,List<InscricaoLocalEntrega> listaLocaisEntrega,BensVinculadosEntidadeDto bemBensVinculadosEntidadeDto){
        
        List<BensVinculadosTotalDto> listaBensVinculadosTotal = new ArrayList<BensVinculadosTotalDto>();
        for(InscricaoProgramaBem programaBem:listaProgramaBem){
            BensVinculadosTotalDto bemTotal = new BensVinculadosTotalDto();
            bemTotal.setBem(programaBem.getProgramaBem().getBem());
            bemTotal.setQuantidade(programaBem.getQuantidade());
            int quantidadeRestante = programaBem.getQuantidade();
            
            List<BensVinculadosLocaisEntregaDto> listaLocais = new ArrayList<BensVinculadosLocaisEntregaDto>();
            for(InscricaoLocalEntrega ile:listaLocaisEntrega){
                for(InscricaoLocalEntregaBem ilb:ile.getBensEntrega()){
                    if(ilb.getInscricaoProgramaBem().getProgramaBem().getBem().getId().longValue()==programaBem.getProgramaBem().getBem().getId().longValue()){
                        BensVinculadosLocaisEntregaDto bemVinculado = new BensVinculadosLocaisEntregaDto();
                        bemVinculado.setLocalEntrega(ile.getLocalEntregaEntidade());
                        bemVinculado.setQuantidade(ilb.getQuantidade());
                        listaLocais.add(bemVinculado);
                        quantidadeRestante -= ilb.getQuantidade();
                    }
                }
            }
            bemTotal.setRestantes(quantidadeRestante);
            bemTotal.setListaLocaisEntrega(listaLocais);
            listaBensVinculadosTotal.add(bemTotal);
            bemBensVinculadosEntidadeDto.setListaBensVinculadosTotal(listaBensVinculadosTotal);
        }
    }
    
    //Metodo utilizado para montar o DTO que será usado para mostrar os itens que já foram vinculados a locais de entrega
    private void montarListaLocaisEntregaKit(List<InscricaoProgramaKit> listaProgramaKit,List<InscricaoLocalEntrega> listaLocaisEntrega,BensVinculadosEntidadeDto bemBensVinculadosEntidadeDto){
        
        List<KitsVinculadosTotalDto> listaKitsVinculadosTotal = new ArrayList<KitsVinculadosTotalDto>();
        for(InscricaoProgramaKit programaKit:listaProgramaKit){
            KitsVinculadosTotalDto kitTotal = new KitsVinculadosTotalDto();
            kitTotal.setKit(programaKit.getProgramaKit().getKit());
            kitTotal.setQuantidade(programaKit.getQuantidade());
            int quantidadeRestante = programaKit.getQuantidade();
            
            List<BensVinculadosLocaisEntregaDto> listaLocais = new ArrayList<BensVinculadosLocaisEntregaDto>();
            for(InscricaoLocalEntrega ile:listaLocaisEntrega){
                for(InscricaoLocalEntregaKit ilb:ile.getKitsEntrega()){
                    if(ilb.getInscricaoProgramaKit().getProgramaKit().getKit().getId().longValue()==programaKit.getProgramaKit().getKit().getId().longValue()){
                        BensVinculadosLocaisEntregaDto kitVinculado = new BensVinculadosLocaisEntregaDto();
                        kitVinculado.setLocalEntrega(ile.getLocalEntregaEntidade());
                        kitVinculado.setQuantidade(ilb.getQuantidade());
                        listaLocais.add(kitVinculado);
                        quantidadeRestante -= ilb.getQuantidade();
                    }
                }
            }
            kitTotal.setRestantes(quantidadeRestante);
            kitTotal.setListaLocaisEntrega(listaLocais);
            listaKitsVinculadosTotal.add(kitTotal);
            bemBensVinculadosEntidadeDto.setListaKitsVinculadosTotal(listaKitsVinculadosTotal);
        }  
    }

    public List<InscricaoPrograma> buscarInscricaoProgramaPeloProgramaEEntidade(Programa programa, Entidade entidade) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoPrograma> criteriaQuery = criteriaBuilder.createQuery(InscricaoPrograma.class);
        Root<InscricaoPrograma> root = criteriaQuery.from(InscricaoPrograma.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.equal(root.get("programa").get("id"), programa.getId()));
        predicates.add(criteriaBuilder.equal(root.get("pessoaEntidade").get("entidade").get("id"), entidade.getId()));

        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoPrograma> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<InscricaoLocalEntregaBem> buscarInscricaoLocalEntregaBem(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoLocalEntregaBem> criteriaQuery = criteriaBuilder.createQuery(InscricaoLocalEntregaBem.class);
        Root<InscricaoLocalEntregaBem> root = criteriaQuery.from(InscricaoLocalEntregaBem.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("inscricaoLocalEntrega").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoLocalEntregaBem> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<InscricaoLocalEntregaKit> buscarInscricaoLocalEntregaKit(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoLocalEntregaKit> criteriaQuery = criteriaBuilder.createQuery(InscricaoLocalEntregaKit.class);
        Root<InscricaoLocalEntregaKit> root = criteriaQuery.from(InscricaoLocalEntregaKit.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("inscricaoLocalEntrega").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoLocalEntregaKit> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<InscricaoLocalEntrega> buscarInscricaoLocalEntregaEntidade(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoLocalEntrega> criteriaQuery = criteriaBuilder.createQuery(InscricaoLocalEntrega.class);
        Root<InscricaoLocalEntrega> root = criteriaQuery.from(InscricaoLocalEntrega.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("localEntregaEntidade").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoLocalEntrega> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<InscricaoLocalEntrega> buscarInscricaoLocalEntrega(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoLocalEntrega> criteriaQuery = criteriaBuilder.createQuery(InscricaoLocalEntrega.class);
        Root<InscricaoLocalEntrega> root = criteriaQuery.from(InscricaoLocalEntrega.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(INSCRICAO_PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoLocalEntrega> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<InscricaoProgramaBem> buscarInscricaoProgramaBem(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoProgramaBem> criteriaQuery = criteriaBuilder.createQuery(InscricaoProgramaBem.class);
        Root<InscricaoProgramaBem> root = criteriaQuery.from(InscricaoProgramaBem.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(INSCRICAO_PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoProgramaBem> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<InscricaoProgramaKit> buscarInscricaoProgramaKit(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoProgramaKit> criteriaQuery = criteriaBuilder.createQuery(InscricaoProgramaKit.class);
        Root<InscricaoProgramaKit> root = criteriaQuery.from(InscricaoProgramaKit.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(INSCRICAO_PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoProgramaKit> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<InscricaoProgramaCriterioAvaliacao> buscarInscricaoProgramaCriterioAvaliacao(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoProgramaCriterioAvaliacao> criteriaQuery = criteriaBuilder.createQuery(InscricaoProgramaCriterioAvaliacao.class);
        Root<InscricaoProgramaCriterioAvaliacao> root = criteriaQuery.from(InscricaoProgramaCriterioAvaliacao.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(INSCRICAO_PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoProgramaCriterioAvaliacao> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<InscricaoProgramaCriterioElegibilidade> buscarInscricaoProgramaCriterioElegibilidade(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoProgramaCriterioElegibilidade> criteriaQuery = criteriaBuilder.createQuery(InscricaoProgramaCriterioElegibilidade.class);
        Root<InscricaoProgramaCriterioElegibilidade> root = criteriaQuery.from(InscricaoProgramaCriterioElegibilidade.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(INSCRICAO_PROGRAMA).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoProgramaCriterioElegibilidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<InscricaoPrograma> buscarPaginado(InscricaoPrograma inscricaoPrograma, int first, int size, EnumOrder order, String propertyOrder) {

        List<InscricaoPrograma> lista1 = buscarSemPaginacao(inscricaoPrograma, order, propertyOrder);

        // filtra paginado
        List<InscricaoPrograma> listaRetorno = new ArrayList<InscricaoPrograma>();
        if (!lista1.isEmpty()) {
            int inicio = first;
            int fim = first + size;

            if (fim > lista1.size()) {
                fim = lista1.size();
            }
            for (int i = inicio; i < fim; i++) {
                listaRetorno.add(lista1.get(i));
            }
        }

        return listaRetorno;

    }

    public List<InscricaoPrograma> paginar(List<InscricaoPrograma> lista, int first, int size) {
        // filtra paginado
        List<InscricaoPrograma> listaRetorno = new ArrayList<InscricaoPrograma>();
        if (!lista.isEmpty()) {
            int inicio = first;
            int fim = first + size;

            if (fim > lista.size()) {
                fim = lista.size();
            }
            for (int i = inicio; i < fim; i++) {
                listaRetorno.add(lista.get(i));
            }
        }
        return listaRetorno;
    }

    private List<InscricaoPrograma> buscarSemPaginacao(InscricaoPrograma inscricaoPrograma, EnumOrder order, String propertyOrder) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoPrograma> criteriaQuery = criteriaBuilder.createQuery(InscricaoPrograma.class);
        Root<InscricaoPrograma> root = criteriaQuery.from(InscricaoPrograma.class);

        Predicate[] predicates = extractPredicates(inscricaoPrograma, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<InscricaoPrograma> query = em.createQuery(criteriaQuery);
        return retornaListaFiltrada(inscricaoPrograma, query);
    }

    private List<InscricaoPrograma> retornaListaFiltrada(InscricaoPrograma inscricao, TypedQuery<InscricaoPrograma> query) {
        List<InscricaoPrograma> inscricaoFiltradosPorPredicates = query.getResultList();
        return retornaListaFiltrada(inscricaoFiltradosPorPredicates, inscricao);
    }

    private List<InscricaoPrograma> retornaListaFiltrada(List<InscricaoPrograma> inscricaoFiltradaPorPredicates, InscricaoPrograma inscricao) {
        List<InscricaoPrograma> inscricoesFiltradasFinal = new ArrayList<InscricaoPrograma>();
        boolean possuiId = false;
        boolean possuiCodigo = false;
        boolean possuiNome = false;
        boolean possuiAno = false;
        boolean possuiOrgao = false;
        boolean possuiEntidade = false;
        boolean possuiCnpj = false;
        boolean possuiEstado = false;

        if (inscricao.getPrograma() != null || inscricao.getPessoaEntidade() != null) {
            for (InscricaoPrograma inscricaoLista : inscricaoFiltradaPorPredicates) {

                // Possui codigo inscricao
                if (inscricao.getId() == null || existeCodigoInscricao(inscricao, inscricaoLista)) {
                    possuiId = true;
                }

                // Código do programa
                if (inscricao.getPrograma() == null || inscricao.getPrograma().getCodigoIdentificadorProgramaPublicadoTemp() == null || existeCodigoPrograma(inscricao, inscricaoLista)) {
                    possuiCodigo = true;
                }

                // Nome do programa
                if (inscricao.getPrograma() == null || inscricao.getPrograma().getNomePrograma() == null || existeNomePrograma(inscricao, inscricaoLista)) {
                    possuiNome = true;
                }

                // Ano
                if (inscricao.getPrograma() == null || inscricao.getPrograma().getAnoPrograma() == null || existeAnoPrograma(inscricao, inscricaoLista)) {
                    possuiAno = true;
                }

                // Orgao
                if (inscricao.getPrograma() == null || inscricao.getPrograma().getUnidadeExecutora() == null || inscricao.getPrograma().getUnidadeExecutora().getOrgao() == null || existeUnidadeExecutoraPrograma(inscricao, inscricaoLista)) {
                    possuiOrgao = true;
                }

                // Nome entidade Proponente
                if (inscricao.getPessoaEntidade() == null || inscricao.getPessoaEntidade().getEntidade() == null || inscricao.getPessoaEntidade().getEntidade().getNomeEntidade() == null || existeNomeEntidade(inscricao, inscricaoLista)) {
                    possuiEntidade = true;
                }

                // Cnpj
                if (inscricao.getPessoaEntidade() == null || inscricao.getPessoaEntidade().getEntidade() == null || inscricao.getPessoaEntidade().getEntidade().getNumeroCnpj() == null || existeCnpjEntidade(inscricao, inscricaoLista)) {
                    possuiCnpj = true;
                }

                // Estado
                if (inscricao.getPessoaEntidade() == null || inscricao.getPessoaEntidade().getEntidade() == null || inscricao.getPessoaEntidade().getEntidade().getMunicipio() == null || inscricao.getPessoaEntidade().getEntidade().getMunicipio().getUf() == null
                        || existeEstadoEntidade(inscricao, inscricaoLista)) {
                    possuiEstado = true;
                }

                if (possuiId && possuiCodigo && possuiNome && possuiAno && possuiOrgao && possuiEntidade && possuiCnpj && possuiEstado) {
                    inscricoesFiltradasFinal.add(inscricaoLista);
                }

                possuiId = false;
                possuiCodigo = false;
                possuiNome = false;
                possuiAno = false;
                possuiOrgao = false;
                possuiEntidade = false;
                possuiCnpj = false;
                possuiEstado = false;

            }
        } else {
            return inscricaoFiltradaPorPredicates;
        }
        return inscricoesFiltradasFinal;
    }

    private boolean existeCodigoInscricao(InscricaoPrograma inscricao, InscricaoPrograma inscricaoLista) {
        if (inscricao.getId().intValue() == inscricaoLista.getId().intValue()) {
            return true;
        }
        return false;
    }

    private boolean existeCodigoPrograma(InscricaoPrograma inscricao, InscricaoPrograma inscricaoLista) {
        String codigo = UtilDAO.removerAcentos(inscricao.getPrograma().getCodigoIdentificadorProgramaPublicadoTemp().toUpperCase());
        String codigoLista = UtilDAO.removerAcentos(inscricaoLista.getPrograma().getCodigoIdentificadorProgramaPublicado().toUpperCase());
        if (codigoLista.toUpperCase().contains(codigo)) {
            return true;
        }
        return false;
    }

    private boolean existeNomePrograma(InscricaoPrograma inscricao, InscricaoPrograma inscricaoLista) {
        if (UtilDAO.removerAcentos(inscricaoLista.getPrograma().getNomePrograma().toUpperCase()).contains(UtilDAO.removerAcentos(inscricao.getPrograma().getNomePrograma().toUpperCase()))) {
            return true;
        }
        return false;
    }

    private boolean existeAnoPrograma(InscricaoPrograma inscricao, InscricaoPrograma inscricaoLista) {
        if (inscricaoLista.getPrograma().getAnoPrograma().intValue() == inscricao.getPrograma().getAnoPrograma().intValue()) {
            return true;
        }
        return false;
    }

    private boolean existeUnidadeExecutoraPrograma(InscricaoPrograma inscricao, InscricaoPrograma inscricaoLista) {
        if (inscricao.getPrograma().getUnidadeExecutora().getOrgao().getSiglaOrgao().equalsIgnoreCase(inscricaoLista.getPrograma().getUnidadeExecutora().getOrgao().getSiglaOrgao())) {
            return true;
        }
        return false;
    }

    private boolean existeNomeEntidade(InscricaoPrograma inscricao, InscricaoPrograma inscricaoLista) {
        if (UtilDAO.removerAcentos(inscricaoLista.getPessoaEntidade().getEntidade().getNomeEntidade().toUpperCase()).contains(UtilDAO.removerAcentos(inscricao.getPessoaEntidade().getEntidade().getNomeEntidade().toUpperCase()))) {
            return true;
        }
        return false;
    }

    private boolean existeCnpjEntidade(InscricaoPrograma inscricao, InscricaoPrograma inscricaoLista) {
        CnpjUtil cnpj = new CnpjUtil();
        if (cnpj.limparCnpj(inscricaoLista.getPessoaEntidade().getEntidade().getNumeroCnpj()).equalsIgnoreCase(cnpj.limparCnpj(inscricao.getPessoaEntidade().getEntidade().getNumeroCnpj()))) {
            return true;
        }
        return false;
    }

    private boolean existeEstadoEntidade(InscricaoPrograma inscricao, InscricaoPrograma inscricaoLista) {
        if (inscricao.getPessoaEntidade().getEntidade().getMunicipio().getUf().getId().intValue() == inscricaoLista.getPessoaEntidade().getEntidade().getMunicipio().getUf().getId().intValue()) {
            return true;
        }
        return false;
    }

    private Predicate[] extractPredicates(InscricaoPrograma inscricaoPrograma, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        // código
        if (inscricaoPrograma != null && inscricaoPrograma.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), inscricaoPrograma.getId()));
        }

        // programa id
        if (inscricaoPrograma != null && inscricaoPrograma.getPrograma() != null && inscricaoPrograma.getPrograma().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("programa").get("id"), inscricaoPrograma.getPrograma().getId()));
        }

        // pessoa id
        if (inscricaoPrograma != null && inscricaoPrograma.getPessoaEntidade() != null && inscricaoPrograma.getPessoaEntidade().getPessoa() != null && inscricaoPrograma.getPessoaEntidade().getPessoa().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("pessoaEntidade").get("pessoa").get("id"), inscricaoPrograma.getPessoaEntidade().getPessoa().getId()));
        }

        // entidade id
        if (inscricaoPrograma != null && inscricaoPrograma.getPessoaEntidade() != null && inscricaoPrograma.getPessoaEntidade().getEntidade() != null && inscricaoPrograma.getPessoaEntidade().getEntidade().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("pessoaEntidade").get("entidade").get("id"), inscricaoPrograma.getPessoaEntidade().getEntidade().getId()));
        }

        // status
        if (inscricaoPrograma != null && inscricaoPrograma.getStatusInscricao() != null) {
            predicates.add(criteriaBuilder.equal(root.get("statusInscricao"), inscricaoPrograma.getStatusInscricao()));
        }

        // resultadoFinalAnaliseElegibilidade
        if (inscricaoPrograma != null && inscricaoPrograma.getResultadoFinalAnaliseElegibilidade() != null) {
            predicates.add(criteriaBuilder.equal(root.get("resultadoFinalAnaliseElegibilidade"), inscricaoPrograma.getResultadoFinalAnaliseElegibilidade()));
        }

        return predicates.toArray(new Predicate[] {});

    }

    public List<InscricaoPrograma> buscarSemPaginacao(InscricaoPrograma inscricaoPrograma) {
        return buscarSemPaginacao(inscricaoPrograma, EnumOrder.ASC, "id");
    }

    public List<InscricaoPrograma> buscarSemPaginacaoOrdenado(InscricaoPrograma inscricaoPrograma, EnumOrder order, String propertyOrder) {
        return buscarSemPaginacao(inscricaoPrograma, order, propertyOrder);
    }

    public Long contarPaginado(InscricaoPrograma inscricaoPrograma) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<InscricaoPrograma> root = criteriaQuery.from(InscricaoPrograma.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        Predicate[] predicates = extractPredicates(inscricaoPrograma, criteriaBuilder, root);
        criteriaQuery.where(predicates);

        TypedQuery<Long> query = em.createQuery(criteriaQuery);

        return query.getSingleResult();

    }

    public List<InscricaoPrograma> buscar(InscricaoPrograma inscricaoPrograma) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoPrograma> criteriaQuery = criteriaBuilder.createQuery(InscricaoPrograma.class);
        Root<InscricaoPrograma> root = criteriaQuery.from(InscricaoPrograma.class);

        Predicate[] predicates = extractPredicates(inscricaoPrograma, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));

        TypedQuery<InscricaoPrograma> query = em.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public InscricaoPrograma buscarPeloId(Long id) {

        return em.find(InscricaoPrograma.class, id);
    }

    public void excluir(InscricaoPrograma inscricaoPrograma) {
        em.remove(inscricaoPrograma);

    }

    public InscricaoPrograma incluir(InscricaoPrograma inscricaoPrograma, String usuarioLogado) {

        /* seta data de cadastro, usuario logado e status inicial */
        inscricaoPrograma.setUsuarioCadastro(usuarioLogado);
        inscricaoPrograma.setDataCadastro(LocalDateTime.now());

        inscricaoPrograma.setEstaEmFaseRecursoElegibilidade(Boolean.FALSE);
        inscricaoPrograma.setFinalizadoRecursoElegibilidade(Boolean.FALSE);

        inscricaoPrograma.setEstaEmFaseRecursoAvaliacao(Boolean.FALSE);
        inscricaoPrograma.setFinalizadoRecursoAvaliacao(Boolean.FALSE);

        /* atribuindo tipoEntidade e tipoEndereco com contexto transacional */
        inscricaoPrograma.setHistoricoPublicizacao(em.find(ProgramaHistoricoPublicizacao.class, inscricaoPrograma.getHistoricoPublicizacao().getId()));
        inscricaoPrograma.setPessoaEntidade(em.find(PessoaEntidade.class, inscricaoPrograma.getPessoaEntidade().getId()));
        inscricaoPrograma.setPrograma(em.find(Programa.class, inscricaoPrograma.getPrograma().getId()));

        /*
         * Setar InscricaoPrograma dentro de cada inscricaoBem para resolver a
         * questão da referência bidirecional
         */
        List<InscricaoProgramaBem> listaInscricaoProgramaBem = new ArrayList<InscricaoProgramaBem>();
        for (InscricaoProgramaBem inscricaoProgramaBem : inscricaoPrograma.getProgramasBem()) {
            inscricaoProgramaBem.setInscricaoPrograma(inscricaoPrograma);
            listaInscricaoProgramaBem.add(inscricaoProgramaBem);
        }
        inscricaoPrograma.setProgramasBem(listaInscricaoProgramaBem);

        /*
         * Setar InscricaoPrograma dentro de cada inscricaoKit para resolver a
         * questão da referência bidirecional
         */
        List<InscricaoProgramaKit> listaInscricaoProgramaKit = new ArrayList<InscricaoProgramaKit>();
        for (InscricaoProgramaKit inscricaoProgramaKit : inscricaoPrograma.getProgramasKit()) {
            inscricaoProgramaKit.setInscricaoPrograma(inscricaoPrograma);
            listaInscricaoProgramaKit.add(inscricaoProgramaKit);
        }
        inscricaoPrograma.setProgramasKit(listaInscricaoProgramaKit);

        /*
         * Setar InscricaoPrograma dentro de cada inscricaoCriterioAvaliacao
         * para resolver a questão da referência bidirecional
         */
        List<InscricaoProgramaCriterioAvaliacao> listaInscricaoProgramaCriterioAvaliacao = new ArrayList<InscricaoProgramaCriterioAvaliacao>();
        for (InscricaoProgramaCriterioAvaliacao inscricaoProgramaCriterioAvaliacao : inscricaoPrograma.getProgramasCriterioAvaliacao()) {
            inscricaoProgramaCriterioAvaliacao.setInscricaoPrograma(inscricaoPrograma);

            /*
             * Setar InscricaoProgramaCriterioAvaliacao dentro de cada
             * inscricaoAnexoAvaliacao para resolver a questão da referência
             * bidirecional
             */
            List<InscricaoAnexoAvaliacao> listaInscricaoAnexoAvaliacao = new ArrayList<InscricaoAnexoAvaliacao>();
            for (InscricaoAnexoAvaliacao inscricaoAnexoAvaliacao : inscricaoProgramaCriterioAvaliacao.getAnexos()) {
                inscricaoAnexoAvaliacao.setInscricaoProgramaCriterioAvaliacao(inscricaoProgramaCriterioAvaliacao);
                inscricaoAnexoAvaliacao.setDataCadastro(LocalDateTime.now());
                inscricaoAnexoAvaliacao.setTamanho(new Long(inscricaoAnexoAvaliacao.getConteudo().length));
                listaInscricaoAnexoAvaliacao.add(inscricaoAnexoAvaliacao);
            }
            inscricaoProgramaCriterioAvaliacao.setAnexos(listaInscricaoAnexoAvaliacao);

            listaInscricaoProgramaCriterioAvaliacao.add(inscricaoProgramaCriterioAvaliacao);

        }
        inscricaoPrograma.setProgramasCriterioAvaliacao(listaInscricaoProgramaCriterioAvaliacao);

        /*
         * Setar InscricaoPrograma dentro de cada inscricaoCriterioElegibilidade
         * para resolver a questão da referência bidirecional
         */
        List<InscricaoProgramaCriterioElegibilidade> listaInscricaoProgramaCriterioElegibilidade = new ArrayList<InscricaoProgramaCriterioElegibilidade>();
        for (InscricaoProgramaCriterioElegibilidade inscricaoProgramaCriterioElegibilidade : inscricaoPrograma.getProgramasCriterioElegibilidade()) {
            inscricaoProgramaCriterioElegibilidade.setInscricaoPrograma(inscricaoPrograma);

            /*
             * Setar InscricaoProgramaCriterioElegibilidade dentro de cada
             * inscricaoAnexoElegibilidade para resolver a questão da referência
             * bidirecional
             */
            List<InscricaoAnexoElegibilidade> listaInscricaoAnexoElegibilidade = new ArrayList<InscricaoAnexoElegibilidade>();
            for (InscricaoAnexoElegibilidade inscricaoAnexoElegibilidade : inscricaoProgramaCriterioElegibilidade.getAnexos()) {
                inscricaoAnexoElegibilidade.setInscricaoProgramaCriterioElegibilidade(inscricaoProgramaCriterioElegibilidade);
                inscricaoAnexoElegibilidade.setDataCadastro(LocalDateTime.now());
                inscricaoAnexoElegibilidade.setTamanho(new Long(inscricaoAnexoElegibilidade.getConteudo().length));
                listaInscricaoAnexoElegibilidade.add(inscricaoAnexoElegibilidade);
            }
            inscricaoProgramaCriterioElegibilidade.setAnexos(listaInscricaoAnexoElegibilidade);

            listaInscricaoProgramaCriterioElegibilidade.add(inscricaoProgramaCriterioElegibilidade);

        }
        inscricaoPrograma.setProgramasCriterioElegibilidade(listaInscricaoProgramaCriterioElegibilidade);

        em.persist(inscricaoPrograma);

        return inscricaoPrograma;
    }

    public InscricaoPrograma alterar(InscricaoPrograma inscricaoPrograma, String usuarioLogado) {

        InscricaoPrograma inscricaoProgramaParaMerge = buscarPeloId(inscricaoPrograma.getId());

        inscricaoProgramaParaMerge.setUsuarioAlteracao(usuarioLogado);
        inscricaoProgramaParaMerge.setDataAlteracao(LocalDateTime.now());
        inscricaoProgramaParaMerge.setPessoaEntidade(em.find(PessoaEntidade.class, inscricaoPrograma.getPessoaEntidade().getId()));
        inscricaoProgramaParaMerge.setStatusInscricao(inscricaoPrograma.getStatusInscricao());

        inscricaoPrograma.setEstaEmFaseRecursoElegibilidade(inscricaoPrograma.getEstaEmFaseRecursoElegibilidade());
        inscricaoPrograma.setFinalizadoRecursoElegibilidade(inscricaoPrograma.getFinalizadoRecursoElegibilidade());

        inscricaoPrograma.setEstaEmFaseRecursoAvaliacao(inscricaoPrograma.getEstaEmFaseRecursoAvaliacao());
        inscricaoPrograma.setFinalizadoRecursoAvaliacao(inscricaoPrograma.getFinalizadoRecursoAvaliacao());

        sincronizarInscricaoBens(inscricaoPrograma.getProgramasBem(), inscricaoProgramaParaMerge);
        sincronizarInscricaoKits(inscricaoPrograma.getProgramasKit(), inscricaoProgramaParaMerge);
        sincronizarInscricaoAvaliacoes(inscricaoPrograma.getProgramasCriterioAvaliacao(), inscricaoProgramaParaMerge);
        sincronizarInscricaoElegibilidades(inscricaoPrograma.getProgramasCriterioElegibilidade(), inscricaoProgramaParaMerge);

        em.merge(inscricaoProgramaParaMerge);
        return inscricaoProgramaParaMerge;
    }

    public InscricaoPrograma sincronizarLocaisEntrega(InscricaoPrograma inscricaoPrograma, String usuarioLogado) {

        InscricaoPrograma inscricaoProgramaParaMerge = buscarPeloId(inscricaoPrograma.getId());
        inscricaoProgramaParaMerge.setUsuarioAlteracao(usuarioLogado);
        inscricaoProgramaParaMerge.setDataAlteracao(LocalDateTime.now());
        sincronizarLocaisEntrega(inscricaoPrograma.getLocaisEntregaInscricao(), inscricaoProgramaParaMerge);
        em.merge(inscricaoProgramaParaMerge);
        return inscricaoProgramaParaMerge;
    }

    public InscricaoPrograma sincronizarComissaoRecebimento(InscricaoPrograma inscricaoPrograma, String usuarioLogado) {
        InscricaoPrograma inscricaoProgramaParaMerge = buscarPeloId(inscricaoPrograma.getId());
        inscricaoProgramaParaMerge.setUsuarioAlteracao(usuarioLogado);
        inscricaoProgramaParaMerge.setDataAlteracao(LocalDateTime.now());
        sincronizarComissaoRecebimento(inscricaoPrograma.getComissaoRecebimento(), inscricaoProgramaParaMerge);
        sincronizarComissaoAnexo(inscricaoPrograma.getComissaoAnexos(), inscricaoProgramaParaMerge);
        em.merge(inscricaoProgramaParaMerge);
        return inscricaoProgramaParaMerge;
    }

    private void sincronizarComissaoRecebimento(List<ComissaoRecebimento> comissaoDaInscricao, InscricaoPrograma inscricaoAtual) {

        List<ComissaoRecebimento> listaComissaoAtualParaAtualizar = new ArrayList<ComissaoRecebimento>();
        List<ComissaoRecebimento> listaComissaoAdicionar = new ArrayList<ComissaoRecebimento>();

        /*
         * seleciona apenas os Comissao que foram mantidas na lista vinda do
         * service
         */
        for (ComissaoRecebimento comissaoAtual : inscricaoAtual.getComissaoRecebimento()) {
            if (comissaoDaInscricao.contains(comissaoAtual)) {
                listaComissaoAtualParaAtualizar.add(comissaoAtual);
            }

        }

        /* remove a lista atual de comissao */
        inscricaoAtual.getComissaoRecebimento().clear();

        /* adiciona os novos na lista de comissao */
        for (ComissaoRecebimento comissaoNovo : comissaoDaInscricao) {
            if (comissaoNovo.getId() == null) {
                /* atender referencia bidirecional */
                comissaoNovo.setInscricaoPrograma(inscricaoAtual);
                comissaoNovo.setDataCadastro(LocalDateTime.now());
                inscricaoAtual.getComissaoRecebimento().add(comissaoNovo);
            }
        }

        /* atualiza atributos nos locaisEntrega vindos do service para persistir */
        for (ComissaoRecebimento comissaoParaAtualizar : listaComissaoAtualParaAtualizar) {
            for (ComissaoRecebimento comDaInscricao : comissaoDaInscricao) {
                if (comDaInscricao.getId() != null && comDaInscricao.getId().equals(comissaoParaAtualizar.getId())) {
                    listaComissaoAdicionar.add(comissaoParaAtualizar);
                }
            }
        }
        // adiciona os locaisEntrega atualizados
        inscricaoAtual.getComissaoRecebimento().addAll(listaComissaoAdicionar);
    }

    private void sincronizarComissaoAnexo(List<ComissaoAnexo> anexos, InscricaoPrograma entityAtual) {
        // remover os excluidos
        List<ComissaoAnexo> anexosAux = new ArrayList<ComissaoAnexo>(entityAtual.getComissaoAnexos());
        for (ComissaoAnexo anexoAtual : anexosAux) {
            if (!anexos.contains(anexoAtual)) {
                entityAtual.getComissaoAnexos().remove(anexoAtual);
            }
        }

        // adiciona os novos
        for (ComissaoAnexo anexoNovo : anexos) {
            if (anexoNovo.getId() == null) {
                anexoNovo.setInscricaoPrograma(entityAtual);
                anexoNovo.setDataCadastro(LocalDateTime.now());
                anexoNovo.setTamanho(new Long(anexoNovo.getConteudo().length));
                entityAtual.getComissaoAnexos().add(anexoNovo);
            }
        }

    }

    private void sincronizarLocaisEntrega(List<InscricaoLocalEntrega> locaisEntregaDaInscricao, InscricaoPrograma inscricaoAtual) {

        List<InscricaoLocalEntrega> listalocaisEntregaAtualParaAtualizar = new ArrayList<InscricaoLocalEntrega>();
        List<InscricaoLocalEntrega> listalocaisEntregaAdicionar = new ArrayList<InscricaoLocalEntrega>();

        /*
         * seleciona apenas os locaisEntrega que foram mantidas na lista vinda
         * do service
         */
        for (InscricaoLocalEntrega inscricaoLocalAtual : inscricaoAtual.getLocaisEntregaInscricao()) {
            if (locaisEntregaDaInscricao.contains(inscricaoLocalAtual)) {
                listalocaisEntregaAtualParaAtualizar.add(inscricaoLocalAtual);
            }

        }

        /* remove a lista atual de locaisEntrega */
        inscricaoAtual.getLocaisEntregaInscricao().clear();

        /* adiciona os novos na lista de locaisEntrega */
        for (InscricaoLocalEntrega inscricaoLocalNovo : locaisEntregaDaInscricao) {
            if (inscricaoLocalNovo.getId() == null) {
                /* atender referencia bidirecional */
                inscricaoLocalNovo.setInscricaoPrograma(inscricaoAtual);
                inscricaoAtual.getLocaisEntregaInscricao().add(inscricaoLocalNovo);
            }
        }

        /* atualiza atributos nos locaisEntrega vindos do service para persistir */
        for (InscricaoLocalEntrega inscricaoLocalParaAtualizar : listalocaisEntregaAtualParaAtualizar) {
            for (InscricaoLocalEntrega inscricaoLocalDaInscricao : locaisEntregaDaInscricao) {
                if (inscricaoLocalDaInscricao.getId() != null && inscricaoLocalDaInscricao.getId().equals(inscricaoLocalParaAtualizar.getId())) {

                    sincronizarLocaisEntregaBens(inscricaoLocalDaInscricao.getBensEntrega(), inscricaoLocalParaAtualizar);
                    sincronizarLocaisEntregaKits(inscricaoLocalDaInscricao.getKitsEntrega(), inscricaoLocalParaAtualizar);

                    listalocaisEntregaAdicionar.add(inscricaoLocalParaAtualizar);
                }
            }
        }
        // adiciona os locaisEntrega atualizados
        inscricaoAtual.getLocaisEntregaInscricao().addAll(listalocaisEntregaAdicionar);
    }

    private void sincronizarLocaisEntregaBens(List<InscricaoLocalEntregaBem> locaisEntregaDaInscricaoBem, InscricaoLocalEntrega inscricaoEntregaAtual) {

        List<InscricaoLocalEntregaBem> listalocaisEntregaBemAtualParaAtualizar = new ArrayList<InscricaoLocalEntregaBem>();
        List<InscricaoLocalEntregaBem> listalocaisEntregaBemAdicionar = new ArrayList<InscricaoLocalEntregaBem>();

        /*
         * seleciona apenas os locaisEntregaBem que foram mantidas na lista
         * vinda do service
         */
        for (InscricaoLocalEntregaBem inscricaoLocalAtual : inscricaoEntregaAtual.getBensEntrega()) {
            if (locaisEntregaDaInscricaoBem.contains(inscricaoLocalAtual)) {
                listalocaisEntregaBemAtualParaAtualizar.add(inscricaoLocalAtual);
            }

        }

        /* remove a lista atual de locaisEntregaBem */
        inscricaoEntregaAtual.getBensEntrega().clear();

        /* adiciona os novos na lista de locaisEntregaBem */
        for (InscricaoLocalEntregaBem inscricaoLocalNovo : locaisEntregaDaInscricaoBem) {
            if (inscricaoLocalNovo.getId() == null) {
                /* atender referencia bidirecional */
                inscricaoLocalNovo.setInscricaoLocalEntrega(inscricaoEntregaAtual);
                inscricaoEntregaAtual.getBensEntrega().add(inscricaoLocalNovo);
            }
        }

        /*
         * atualiza atributos nos locaisEntregaBem vindos do service para
         * persistir
         */
        for (InscricaoLocalEntregaBem inscricaoLocalParaAtualizar : listalocaisEntregaBemAtualParaAtualizar) {
            for (InscricaoLocalEntregaBem inscricaoLocalDaInscricao : locaisEntregaDaInscricaoBem) {
                if (inscricaoLocalDaInscricao.getId() != null && inscricaoLocalDaInscricao.getId().equals(inscricaoLocalParaAtualizar.getId())) {
                    inscricaoLocalParaAtualizar.setQuantidade(inscricaoLocalDaInscricao.getQuantidade());
                    listalocaisEntregaBemAdicionar.add(inscricaoLocalParaAtualizar);
                }
            }
        }
        // adiciona os locaisEntregaBem atualizados
        inscricaoEntregaAtual.getBensEntrega().addAll(listalocaisEntregaBemAdicionar);

    }

    private void sincronizarLocaisEntregaKits(List<InscricaoLocalEntregaKit> locaisEntregaDaInscricaoKit, InscricaoLocalEntrega inscricaoEntregaAtual) {

        List<InscricaoLocalEntregaKit> listalocaisEntregaKitAtualParaAtualizar = new ArrayList<InscricaoLocalEntregaKit>();
        List<InscricaoLocalEntregaKit> listalocaisEntregaKitAdicionar = new ArrayList<InscricaoLocalEntregaKit>();

        /*
         * seleciona apenas os locaisEntregaKit que foram mantidas na lista
         * vinda do service
         */
        for (InscricaoLocalEntregaKit inscricaoLocalAtual : inscricaoEntregaAtual.getKitsEntrega()) {
            if (locaisEntregaDaInscricaoKit.contains(inscricaoLocalAtual)) {
                listalocaisEntregaKitAtualParaAtualizar.add(inscricaoLocalAtual);
            }

        }

        /* remove a lista atual de locaisEntregaKit */
        inscricaoEntregaAtual.getKitsEntrega().clear();

        /* adiciona os novos na lista de locaisEntregaKit */
        for (InscricaoLocalEntregaKit inscricaoLocalNovo : locaisEntregaDaInscricaoKit) {
            if (inscricaoLocalNovo.getId() == null) {
                /* atender referencia bidirecional */
                inscricaoLocalNovo.setInscricaoLocalEntrega(inscricaoEntregaAtual);
                inscricaoEntregaAtual.getKitsEntrega().add(inscricaoLocalNovo);
            }
        }

        /*
         * atualiza atributos nos locaisEntregaBem vindos do service para
         * persistir
         */
        for (InscricaoLocalEntregaKit inscricaoLocalParaAtualizar : listalocaisEntregaKitAtualParaAtualizar) {
            for (InscricaoLocalEntregaKit inscricaoLocalDaInscricao : locaisEntregaDaInscricaoKit) {
                if (inscricaoLocalDaInscricao.getId() != null && inscricaoLocalDaInscricao.getId().equals(inscricaoLocalParaAtualizar.getId())) {
                    inscricaoLocalParaAtualizar.setQuantidade(inscricaoLocalDaInscricao.getQuantidade());
                    listalocaisEntregaKitAdicionar.add(inscricaoLocalParaAtualizar);
                }
            }
        }
        // adiciona os locaisEntregaKit atualizados
        inscricaoEntregaAtual.getKitsEntrega().addAll(listalocaisEntregaKitAdicionar);

    }

    private void sincronizarInscricaoBens(List<InscricaoProgramaBem> bensDaInscricao, InscricaoPrograma inscricaoAtual) {

        List<InscricaoProgramaBem> listaBensAtualParaAtualizar = new ArrayList<InscricaoProgramaBem>();
        List<InscricaoProgramaBem> listaBensAdicionar = new ArrayList<InscricaoProgramaBem>();

        /*
         * seleciona apenas os Bens que foram mantidas na lista vinda do service
         */
        for (InscricaoProgramaBem bemAtual : inscricaoAtual.getProgramasBem()) {
            if (bensDaInscricao.contains(bemAtual)) {
                listaBensAtualParaAtualizar.add(bemAtual);
            }

        }

        /* remove a lista atual de Bens */
        inscricaoAtual.getProgramasBem().clear();

        /* adiciona os novos na lista de Bens */
        for (InscricaoProgramaBem bemNovo : bensDaInscricao) {
            if (bemNovo.getId() == null) {
                /* atender referencia bidirecional */
                bemNovo.setInscricaoPrograma(inscricaoAtual);
                inscricaoAtual.getProgramasBem().add(bemNovo);
            }
        }

        /* atualiza atributos nos bens vindos do service para persistir */
        for (InscricaoProgramaBem bemParaAtualizar : listaBensAtualParaAtualizar) {
            for (InscricaoProgramaBem bemDaInscricao : bensDaInscricao) {
                if (bemDaInscricao.getId() != null && bemDaInscricao.getId().equals(bemParaAtualizar.getId())) {
                    bemParaAtualizar.setQuantidade(bemDaInscricao.getQuantidade());
                    listaBensAdicionar.add(bemParaAtualizar);
                }
            }
        }
        // adiciona os Bens atualizados
        inscricaoAtual.getProgramasBem().addAll(listaBensAdicionar);
    }

    private void sincronizarInscricaoKits(List<InscricaoProgramaKit> kitsDaInscricao, InscricaoPrograma inscricaoAtual) {

        List<InscricaoProgramaKit> listaKitsAtualParaAtualizar = new ArrayList<InscricaoProgramaKit>();
        List<InscricaoProgramaKit> listaKitsAdicionar = new ArrayList<InscricaoProgramaKit>();

        /*
         * seleciona apenas os Kits que foram mantidas na lista vinda do service
         */
        for (InscricaoProgramaKit kitAtual : inscricaoAtual.getProgramasKit()) {
            if (kitsDaInscricao.contains(kitAtual)) {
                listaKitsAtualParaAtualizar.add(kitAtual);
            }

        }

        /* remove a lista atual de Kits */
        inscricaoAtual.getProgramasKit().clear();

        /* adiciona os novos na lista de Kits */
        for (InscricaoProgramaKit kitNovo : kitsDaInscricao) {
            if (kitNovo.getId() == null) {
                /* atender referencia bidirecional */
                kitNovo.setInscricaoPrograma(inscricaoAtual);
                inscricaoAtual.getProgramasKit().add(kitNovo);
            }
        }

        /* atualiza atributos nos kits vindos do service para persistir */
        for (InscricaoProgramaKit kitParaAtualizar : listaKitsAtualParaAtualizar) {
            for (InscricaoProgramaKit kitDoPrograma : kitsDaInscricao) {
                if (kitDoPrograma.getId() != null && kitDoPrograma.getId().equals(kitParaAtualizar.getId())) {
                    kitParaAtualizar.setQuantidade(kitDoPrograma.getQuantidade());
                    listaKitsAdicionar.add(kitParaAtualizar);
                }
            }
        }
        // adiciona os Kits atualizados
        inscricaoAtual.getProgramasKit().addAll(listaKitsAdicionar);
    }

    private void sincronizarInscricaoAvaliacoes(List<InscricaoProgramaCriterioAvaliacao> avaliacoesDaInscricao, InscricaoPrograma inscricaoAtual) {

        List<InscricaoProgramaCriterioAvaliacao> listaAvaliacoesAtualParaAtualizar = new ArrayList<InscricaoProgramaCriterioAvaliacao>();
        List<InscricaoProgramaCriterioAvaliacao> listaAvaliacoesAdicionar = new ArrayList<InscricaoProgramaCriterioAvaliacao>();

        /*
         * seleciona apenas os Avaliacoes que foram mantidas na lista vinda do
         * service
         */
        for (InscricaoProgramaCriterioAvaliacao avaliacaoAtual : inscricaoAtual.getProgramasCriterioAvaliacao()) {
            if (avaliacoesDaInscricao.contains(avaliacaoAtual)) {
                listaAvaliacoesAtualParaAtualizar.add(avaliacaoAtual);
            }

        }

        /* remove a lista atual de Avaliacoes */
        inscricaoAtual.getProgramasCriterioAvaliacao().clear();

        /* adiciona os novos na lista de Avaliacoes */
        for (InscricaoProgramaCriterioAvaliacao avaliacaoNovo : avaliacoesDaInscricao) {
            if (avaliacaoNovo.getId() == null) {
                /* atender referencia bidirecional */
                avaliacaoNovo.setInscricaoPrograma(inscricaoAtual);
                inscricaoAtual.getProgramasCriterioAvaliacao().add(avaliacaoNovo);
            }
        }

        /* atualiza atributos nos Avaliacoes vindos do service para persistir */
        for (InscricaoProgramaCriterioAvaliacao avaliacaoParaAtualizar : listaAvaliacoesAtualParaAtualizar) {
            for (InscricaoProgramaCriterioAvaliacao avaliacaoDoPrograma : avaliacoesDaInscricao) {
                if (avaliacaoDoPrograma.getId() != null && avaliacaoDoPrograma.getId().equals(avaliacaoParaAtualizar.getId())) {
                    avaliacaoParaAtualizar.setDescricaoResposta(avaliacaoDoPrograma.getDescricaoResposta());
                    sincronizarAnexosAvaliacao(avaliacaoDoPrograma.getAnexos(), avaliacaoParaAtualizar);
                    listaAvaliacoesAdicionar.add(avaliacaoParaAtualizar);
                }
            }
        }
        // adiciona os Avaliacoes atualizados
        inscricaoAtual.getProgramasCriterioAvaliacao().addAll(listaAvaliacoesAdicionar);
    }

    private void sincronizarInscricaoElegibilidades(List<InscricaoProgramaCriterioElegibilidade> elegibilidadesDaInscricao, InscricaoPrograma inscricaoAtual) {

        List<InscricaoProgramaCriterioElegibilidade> listaElegibilidadesAtualParaAtualizar = new ArrayList<InscricaoProgramaCriterioElegibilidade>();
        List<InscricaoProgramaCriterioElegibilidade> listaElegibilidadesAdicionar = new ArrayList<InscricaoProgramaCriterioElegibilidade>();

        /*
         * seleciona apenas os Elegibilidades que foram mantidas na lista vinda
         * do service
         */
        for (InscricaoProgramaCriterioElegibilidade elegibilidadeAtual : inscricaoAtual.getProgramasCriterioElegibilidade()) {
            if (elegibilidadesDaInscricao.contains(elegibilidadeAtual)) {
                listaElegibilidadesAtualParaAtualizar.add(elegibilidadeAtual);
            }

        }

        /* remove a lista atual de Elegibilidades */
        inscricaoAtual.getProgramasCriterioElegibilidade().clear();

        /* adiciona os novos na lista de Elegibilidades */
        for (InscricaoProgramaCriterioElegibilidade elegibilidadeNovo : elegibilidadesDaInscricao) {
            if (elegibilidadeNovo.getId() == null) {
                /* atender referencia bidirecional */
                elegibilidadeNovo.setInscricaoPrograma(inscricaoAtual);
                inscricaoAtual.getProgramasCriterioElegibilidade().add(elegibilidadeNovo);
            }
        }

        /*
         * atualiza atributos nos Elegibilidades vindos do service para
         * persistir
         */
        for (InscricaoProgramaCriterioElegibilidade elegibilidadeParaAtualizar : listaElegibilidadesAtualParaAtualizar) {
            for (InscricaoProgramaCriterioElegibilidade elegibilidadeDoPrograma : elegibilidadesDaInscricao) {
                if (elegibilidadeDoPrograma.getId() != null && elegibilidadeDoPrograma.getId().equals(elegibilidadeParaAtualizar.getId())) {
                    elegibilidadeParaAtualizar.setAtendeCriterioElegibilidade(elegibilidadeDoPrograma.getAtendeCriterioElegibilidade());
                    sincronizarAnexosElegibilidade(elegibilidadeDoPrograma.getAnexos(), elegibilidadeParaAtualizar);
                    listaElegibilidadesAdicionar.add(elegibilidadeParaAtualizar);
                }
            }
        }
        // adiciona os Elegibilidades atualizados
        inscricaoAtual.getProgramasCriterioElegibilidade().addAll(listaElegibilidadesAdicionar);
    }

    private void sincronizarAnexosElegibilidade(List<InscricaoAnexoElegibilidade> anexos, InscricaoProgramaCriterioElegibilidade entityAtual) {
        // remover os excluidos
        List<InscricaoAnexoElegibilidade> anexosAux = new ArrayList<InscricaoAnexoElegibilidade>(entityAtual.getAnexos());
        for (InscricaoAnexoElegibilidade anexoAtual : anexosAux) {
            if (!anexos.contains(anexoAtual)) {
                entityAtual.getAnexos().remove(anexoAtual);
            }
        }

        // adiciona os novos
        for (InscricaoAnexoElegibilidade anexoNovo : anexos) {
            if (anexoNovo.getId() == null) {
                anexoNovo.setInscricaoProgramaCriterioElegibilidade(entityAtual);
                anexoNovo.setDataCadastro(LocalDateTime.now());
                anexoNovo.setTamanho(new Long(anexoNovo.getConteudo().length));
                entityAtual.getAnexos().add(anexoNovo);
            }
        }

    }

    private void sincronizarAnexosAvaliacao(List<InscricaoAnexoAvaliacao> anexos, InscricaoProgramaCriterioAvaliacao entityAtual) {
        // remover os excluidos
        List<InscricaoAnexoAvaliacao> anexosAux = new ArrayList<InscricaoAnexoAvaliacao>(entityAtual.getAnexos());
        for (InscricaoAnexoAvaliacao anexoAtual : anexosAux) {
            if (!anexos.contains(anexoAtual)) {
                entityAtual.getAnexos().remove(anexoAtual);
            }
        }

        // adiciona os novos
        for (InscricaoAnexoAvaliacao anexoNovo : anexos) {
            if (anexoNovo.getId() == null) {
                anexoNovo.setInscricaoProgramaCriterioAvaliacao(entityAtual);
                anexoNovo.setDataCadastro(LocalDateTime.now());
                anexoNovo.setTamanho(new Long(anexoNovo.getConteudo().length));
                entityAtual.getAnexos().add(anexoNovo);
            }
        }

    }

}

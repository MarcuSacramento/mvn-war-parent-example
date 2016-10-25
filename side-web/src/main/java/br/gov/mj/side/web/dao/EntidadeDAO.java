package br.gov.mj.side.web.dao;

import java.time.LocalDate;
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

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.apoio.entidades.TipoEntidade;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumOrigemCadastro;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusLocalEntrega;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.enums.EnumValidacaoCadastro;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.BemUf;
import br.gov.mj.side.entidades.programa.licitacao.SelecaoItem;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.web.dto.EntidadePesquisaDto;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.UtilDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class EntidadeDAO {

    private static final int LIMITE_ENVIO_EMAIL_ENTIDADE_NAO_ANALISADA_MESES = 2;
    private static final String ENTIDADE = "entidade";
    private static final String PESSOA = "pessoa";
    private static final String NOME_ENTIDADE = "nomeEntidade";
    @Inject
    private EntityManager em;

    @Inject
    private IGenericPersister genericPersister;

    public List<LocalEntregaEntidade> buscarLocaisEntrega(Long id, EnumStatusLocalEntrega statusLocalEntrega) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<LocalEntregaEntidade> criteriaQuery = criteriaBuilder.createQuery(LocalEntregaEntidade.class);
        Root<LocalEntregaEntidade> root = criteriaQuery.from(LocalEntregaEntidade.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(ENTIDADE).get("id"), id));
        }
        if (statusLocalEntrega != null) {
            predicates.add(criteriaBuilder.equal(root.get("statusLocalEntrega"), statusLocalEntrega));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<LocalEntregaEntidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<PessoaEntidade> buscarEntidadesDoUsuario(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PessoaEntidade> criteriaQuery = criteriaBuilder.createQuery(PessoaEntidade.class);
        Root<PessoaEntidade> root = criteriaQuery.from(PessoaEntidade.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(PESSOA).get("usuario").get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<PessoaEntidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<PessoaEntidade> buscarEntidade(Long id) {
        return buscarEntidade(id, null);
    }

    public List<PessoaEntidade> buscarEntidade(Long id, EnumPerfilEntidade perfil) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PessoaEntidade> criteriaQuery = criteriaBuilder.createQuery(PessoaEntidade.class);
        Root<PessoaEntidade> root = criteriaQuery.from(PessoaEntidade.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(PESSOA).get("id"), id));
        }
        if (perfil != null) {
            predicates.add(criteriaBuilder.equal(root.get(ENTIDADE).get("perfilEntidade"), perfil.getValor()));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<PessoaEntidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<PessoaEntidade> buscarPessoa(Long id) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PessoaEntidade> criteriaQuery = criteriaBuilder.createQuery(PessoaEntidade.class);
        Root<PessoaEntidade> root = criteriaQuery.from(PessoaEntidade.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get(ENTIDADE).get("id"), id));
        }
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<PessoaEntidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<PessoaEntidade> buscarTitularEntidade(Long id) {

        List<PessoaEntidade> lista = new ArrayList<PessoaEntidade>();
        for (PessoaEntidade pessoa : buscarPessoa(id)) {
            if (pessoa.getPessoa().isTitular()) {
                lista.add(pessoa);
            }
        }
        return lista;
    }

    public List<PessoaEntidade> buscarRepresentanteFornecedor(Long id) {

        List<PessoaEntidade> lista = new ArrayList<PessoaEntidade>();
        for (PessoaEntidade pessoa : buscarPessoa(id)) {
            if (pessoa.getPessoa().isRepresentanteFornecedor()) {
                lista.add(pessoa);
            }
        }
        return lista;
    }

    public List<PessoaEntidade> buscarPrepostosFornecedor(Long id) {

        List<PessoaEntidade> lista = new ArrayList<PessoaEntidade>();
        for (PessoaEntidade pessoa : buscarPessoa(id)) {
            if (pessoa.getPessoa().isPreposto()) {
                lista.add(pessoa);
            }
        }
        return lista;
    }

    public List<PessoaEntidade> buscarRepresentanteEntidade(Long id, boolean somenteAtivos) {

        List<PessoaEntidade> lista = new ArrayList<PessoaEntidade>();
        for (PessoaEntidade pessoa : buscarPessoa(id)) {
            if (pessoa.getPessoa().isRepresentante()) {
                if (somenteAtivos) {
                    if (pessoa.getPessoa().getStatusPessoa().equals(EnumStatusPessoa.ATIVO)) {
                        lista.add(pessoa);
                    }
                } else {
                    lista.add(pessoa);
                }

            }
        }
        return lista;
    }

    public List<Entidade> buscarPaginado(EntidadePesquisaDto entidadePesquisaDto, int first, int size, EnumOrder order, String propertyOrder) {

        List<Entidade> lista1 = buscarSemPaginacao(entidadePesquisaDto, order, propertyOrder);

        // filtra paginado
        List<Entidade> listaRetorno = new ArrayList<Entidade>();
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

    private List<Entidade> buscarSemPaginacao(EntidadePesquisaDto entidadePesquisaDto, EnumOrder order, String propertyOrder) {

        List<PessoaEntidade> listaPessoaEntidades = new ArrayList<PessoaEntidade>();
        if (entidadePesquisaDto.getUsuarioLogado() != null) {
            listaPessoaEntidades = pegarEntidadesDoUsuarioExternoLogado(entidadePesquisaDto);
        }

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Entidade> criteriaQuery = criteriaBuilder.createQuery(Entidade.class);
        Root<Entidade> root = criteriaQuery.from(Entidade.class);

        Predicate[] predicates = extractPredicates(entidadePesquisaDto.getValidacaoCadastro(), entidadePesquisaDto.getOrigemCadastro(), entidadePesquisaDto.getTipoPerfil(), entidadePesquisaDto.getEntidade(), listaPessoaEntidades, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        if (order.isAscOrder()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(UtilDAO.extractObjectOrder(propertyOrder, root)));
        }

        TypedQuery<Entidade> query = em.createQuery(criteriaQuery);
        return retornaListaFiltrada(entidadePesquisaDto, query);
    }

    private List<PessoaEntidade> pegarEntidadesDoUsuarioExternoLogado(EntidadePesquisaDto entidadePesquisaDto) {

        List<PessoaEntidade> listaRetorno = new ArrayList<PessoaEntidade>();

        // caso seja usuario externo(representante) trazer apenas as entidades
        // que ele pode manipular
        Pessoa pessoa = null;
        if (entidadePesquisaDto.getUsuarioLogado().getTipoUsuario().equals(EnumTipoUsuario.EXTERNO)) {
            Usuario usuarioLogado = em.find(Usuario.class, entidadePesquisaDto.getUsuarioLogado().getId());
            List<Pessoa> listaPessoas = genericPersister.findByProperty(Pessoa.class, "usuario", usuarioLogado);
            if (!listaPessoas.isEmpty()) {
                pessoa = listaPessoas.get(0);
            }
            if (pessoa != null) {
                listaRetorno = buscarEntidade(pessoa.getId());
            }
        }

        return listaRetorno;
    }

    public List<Entidade> buscarSemPaginacao(EntidadePesquisaDto entidadePesquisaDto) {
        return buscarSemPaginacao(entidadePesquisaDto, EnumOrder.ASC, NOME_ENTIDADE);
    }

    public List<Entidade> buscarSemPaginacaoOrdenado(EntidadePesquisaDto entidadePesquisaDto, EnumOrder order, String propertyOrder) {
        return buscarSemPaginacao(entidadePesquisaDto, order, propertyOrder);
    }

    private List<Entidade> retornaListaFiltrada(EntidadePesquisaDto entidadePesquisaDto, TypedQuery<Entidade> query) {
        List<Entidade> entidadesFiltradosPorPredicates = query.getResultList();
        return retornaListaFiltrada(entidadesFiltradosPorPredicates, entidadePesquisaDto);

    }

    private List<Entidade> retornaListaFiltrada(List<Entidade> entidadesFiltradosPorPredicates, EntidadePesquisaDto entidadePesquisaDto) {

        List<Entidade> entidadesFiltradosFinal = new ArrayList<Entidade>();

        boolean possuiNumeroNup = false;
        boolean possuiEmailEntidade = false;

        boolean possuiStatusRepresentante = false;
        boolean possuiNomeRepresentante = false;
        boolean possuiNumeroCpfRepresentante = false;
        boolean possuiDescricaoCargoRepresentante = false;
        boolean possuiNumeroTelefoneRepresentante = false;
        boolean possuiEmailRepresentante = false;
        boolean possuiEnderecoCorrespondenciaRepresentante = false;
        boolean possuiTipoRepresentante = false;
        boolean possuiFuncaoRepresentanteRepresentante = false;

        boolean possuiStatusTitular = false;
        boolean possuiNomeTitular = false;
        boolean possuiNumeroCpfTitular = false;
        boolean possuiDescricaoCargoTitular = false;
        boolean possuiNumeroTelefoneTitular = false;
        boolean possuiEmailTitular = false;
        boolean possuiEnderecoCorrespondenciaTitular = false;
        boolean possuiTipoTitular = false;
        boolean possuiFuncaoRepresentanteTitular = false;

        boolean possuiNumeroCpfBuscandoTodos = false;
        boolean possuiContrato = false;
        boolean possuiBem = false;
        boolean possuiPrograma = false;
        boolean possuiEmailBuscandoTodos = false;

        /*
         * Como o perfil vale tanto para Beneficiario quanto Fornecedor o
         * resultado da busca só poderá trazer tipos iguais.
         */
        boolean mesmoPerfil = false;

        if (entidadePesquisaDto.getBem() != null || entidadePesquisaDto.getContrato() != null || entidadePesquisaDto.getEntidade() != null || entidadePesquisaDto.getRepresentante() != null || entidadePesquisaDto.getPrograma() != null || entidadePesquisaDto.getTitular() != null
                || entidadePesquisaDto.getTodos() != null) {
            for (Entidade entidade : entidadesFiltradosPorPredicates) {

                if (entidadePesquisaDto.getEntidade() == null || existeEmailEntidade(entidade, entidadePesquisaDto.getEntidade())) {
                    possuiEmailEntidade = true;
                }

                if (entidadePesquisaDto.getEntidade() == null || entidadePesquisaDto.getEntidade().getNumeroProcessoSEI() == null || existeNumeroNupEntidade(entidade, entidadePesquisaDto.getEntidade())) {
                    possuiNumeroNup = true;
                }

                // Status Representante
                if (entidadePesquisaDto.getRepresentante() == null || existeStatusPessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getRepresentante())) {
                    possuiStatusRepresentante = true;
                }

                // Nome Representante
                if (entidadePesquisaDto.getRepresentante() == null || existeNomePessoaRepresentanteNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getRepresentante())) {
                    possuiNomeRepresentante = true;
                }

                // Numero Cpf Representante
                if (entidadePesquisaDto.getRepresentante() == null || existeNumeroCpfPessoaRepresentanteNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getRepresentante())) {
                    possuiNumeroCpfRepresentante = true;
                }

                // Numero Cpf Titular
                if (entidadePesquisaDto.getTitular() == null || existeNumeroCpfPessoaTitularNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getTitular())) {
                    possuiNumeroCpfTitular = true;
                }

                // Irá buscar Todos os Cpfs independente do perfil
                if (entidadePesquisaDto.getTodos() == null || existeNumeroCpf(entidade.getPessoas(), entidadePesquisaDto.getTodos())) {
                    possuiNumeroCpfBuscandoTodos = true;
                }

                // Irá buscar Todos os Emails independente do perfil
                if (entidadePesquisaDto.getTodos() == null || existeEmail(entidade.getPessoas(), entidadePesquisaDto.getTodos())) {
                    possuiEmailBuscandoTodos = true;
                }

                // Descricao cargo Representante
                if (entidadePesquisaDto.getRepresentante() == null || existeDescricaoCargoPessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getRepresentante())) {
                    possuiDescricaoCargoRepresentante = true;
                }

                // Numero telefone Representante
                if (entidadePesquisaDto.getRepresentante() == null || existeNumeroTelefonePessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getRepresentante())) {
                    possuiNumeroTelefoneRepresentante = true;
                }

                // Email Representante
                if (entidadePesquisaDto.getRepresentante() == null || existeEmailPessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getRepresentante())) {
                    possuiEmailRepresentante = true;
                }

                // Endereco Correspondencia Representante
                if (entidadePesquisaDto.getRepresentante() == null || existeEnderecoCorrespondenciaPessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getRepresentante())) {
                    possuiEnderecoCorrespondenciaRepresentante = true;
                }

                // Funcao Tipo Representante
                if (entidadePesquisaDto.getRepresentante() == null || existeTipoPessoaNaEntidade(entidade.getPessoas(), EnumTipoPessoa.REPRESENTANTE_ENTIDADE)) {
                    possuiTipoRepresentante = true;
                }

                // Funcao Representante Representante
                if (entidadePesquisaDto.getRepresentante() == null || existeFuncaoRepresentantePessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getRepresentante())) {
                    possuiFuncaoRepresentanteRepresentante = true;
                }

                // Status Titular
                if (entidadePesquisaDto.getTitular() == null || existeStatusPessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getTitular())) {
                    possuiStatusTitular = true;
                }

                // Nome Titular
                if (entidadePesquisaDto.getTitular() == null || existeNomePessoaTitularNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getTitular())) {
                    possuiNomeTitular = true;
                }

                // Descricao cargo Titular
                if (entidadePesquisaDto.getTitular() == null || existeDescricaoCargoPessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getTitular())) {
                    possuiDescricaoCargoTitular = true;
                }

                // Numero telefone Titular
                if (entidadePesquisaDto.getTitular() == null || existeNumeroTelefonePessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getTitular())) {
                    possuiNumeroTelefoneTitular = true;
                }

                // Email Titular
                if (entidadePesquisaDto.getTitular() == null || existeEmailPessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getTitular())) {
                    possuiEmailTitular = true;
                }

                // Endereco Correspondencia Titular
                if (entidadePesquisaDto.getTitular() == null || existeEnderecoCorrespondenciaPessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getTitular())) {
                    possuiEnderecoCorrespondenciaTitular = true;
                }

                // Funcao Tipo Titular
                if (entidadePesquisaDto.getTitular() == null || existeTipoPessoaNaEntidade(entidade.getPessoas(), EnumTipoPessoa.TITULAR)) {
                    possuiTipoTitular = true;
                }

                // Funcao Representante Titular
                if (entidadePesquisaDto.getTitular() == null || existeFuncaoRepresentantePessoaNaEntidade(entidade.getPessoas(), entidadePesquisaDto.getTitular())) {
                    possuiFuncaoRepresentanteTitular = true;
                }

                // Possui perfil
                if (entidadePesquisaDto.getTipoPerfil() == null || entidadePesquisaDto.getTipoPerfil() == entidade.getPerfilEntidade()) {
                    mesmoPerfil = true;
                }

                // Possui contrato
                if (entidadePesquisaDto.getContrato() == null || existeContrato(entidade.getContratosEntidade(), entidadePesquisaDto.getContrato())) {
                    possuiContrato = true;
                }

                // Possui o bem informado
                if (entidadePesquisaDto.getBem() == null || existeBem(entidade.getContratosEntidade(), entidadePesquisaDto.getBem())) {
                    possuiBem = true;
                }

                // Possui o programa informado
                if (entidadePesquisaDto.getPrograma() == null || existePrograma(entidade.getContratosEntidade(), entidadePesquisaDto.getPrograma())) {
                    possuiPrograma = true;
                }
                if (possuiEmailEntidade && possuiNumeroNup && possuiStatusRepresentante && possuiNomeRepresentante && possuiNumeroCpfRepresentante && possuiDescricaoCargoRepresentante && possuiNumeroTelefoneRepresentante && possuiEmailRepresentante && possuiEnderecoCorrespondenciaRepresentante
                        && possuiTipoRepresentante && possuiFuncaoRepresentanteRepresentante && possuiStatusTitular && possuiNomeTitular && possuiNumeroCpfTitular && possuiDescricaoCargoTitular && possuiNumeroTelefoneTitular && possuiEmailTitular && possuiEnderecoCorrespondenciaTitular
                        && possuiTipoTitular && possuiFuncaoRepresentanteTitular && possuiContrato && possuiBem && possuiPrograma && mesmoPerfil && possuiNumeroCpfBuscandoTodos && possuiEmailBuscandoTodos) {
                    entidadesFiltradosFinal.add(entidade);
                }

                possuiEmailEntidade = false;
                possuiNumeroNup = false;

                possuiStatusRepresentante = false;
                possuiNomeRepresentante = false;
                possuiNumeroCpfRepresentante = false;
                possuiDescricaoCargoRepresentante = false;
                possuiNumeroTelefoneRepresentante = false;
                possuiEmailRepresentante = false;
                possuiEnderecoCorrespondenciaRepresentante = false;
                possuiTipoRepresentante = false;
                possuiFuncaoRepresentanteRepresentante = false;

                possuiStatusTitular = false;
                possuiNomeTitular = false;
                possuiNumeroCpfTitular = false;
                possuiDescricaoCargoTitular = false;
                possuiNumeroTelefoneTitular = false;
                possuiEmailTitular = false;
                possuiEnderecoCorrespondenciaTitular = false;
                possuiTipoTitular = false;
                possuiFuncaoRepresentanteTitular = false;

                mesmoPerfil = false;
                possuiNumeroCpfBuscandoTodos = false;
                possuiEmailBuscandoTodos = false;

                possuiContrato = false;
                possuiBem = false;
                possuiPrograma = false;

            }
        } else {
            return entidadesFiltradosPorPredicates;
        }

        return entidadesFiltradosFinal;

    }

    private boolean existeNumeroNupEntidade(Entidade entidadeBusca, Entidade entidadeDto) {
        if (entidadeBusca.getPerfilEntidade() == EnumPerfilEntidade.BENEFICIARIO) {
            if (entidadeDto.getNumeroProcessoSEI() != null && entidadeBusca.getNumeroProcessoSEI() != null) {
                if (entidadeDto.getNumeroProcessoSEI() == null || entidadeBusca.getNumeroProcessoSEI().equalsIgnoreCase(entidadeDto.getNumeroProcessoSEI())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private boolean existeEmailEntidade(Entidade entidadeBusca, Entidade entidadeDto) {
        if (entidadeDto.getEmail() == null || entidadeBusca.getEmail().equalsIgnoreCase(entidadeDto.getEmail())) {
            return true;
        }
        return false;
    }

    private boolean existeStatusPessoaNaEntidade(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getStatusPessoa() == null || (pessoa.getStatusPessoa() != null && objPessoa.getPessoa().getStatusPessoa().getValor().equals(pessoa.getStatusPessoa().getValor()))) {
                return true;
            }
        }
        return false;
    }

    private boolean existeNomePessoaTitularNaEntidade(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getNomePessoa() == null || (pessoa.getNomePessoa() != null && UtilDAO.removerAcentos(objPessoa.getPessoa().getNomePessoa().toUpperCase()).contains(UtilDAO.removerAcentos(pessoa.getNomePessoa().toUpperCase())) && objPessoa.getPessoa().isTitular())) {
                return true;
            }
        }
        return false;
    }

    private boolean existeNomePessoaRepresentanteNaEntidade(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {

            if (pessoa.getNomePessoa() == null || (pessoa.getNomePessoa() != null && UtilDAO.removerAcentos(objPessoa.getPessoa().getNomePessoa().toUpperCase()).contains(UtilDAO.removerAcentos(pessoa.getNomePessoa().toUpperCase())) && objPessoa.getPessoa().isRepresentante())) {
                return true;
            }
        }
        return false;
    }

    private boolean existeNumeroCpfPessoaRepresentanteNaEntidade(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getNumeroCpf() == null || (pessoa.getNumeroCpf() != null && UtilDAO.removerAcentos(objPessoa.getPessoa().getNumeroCpf().toUpperCase()).contains(UtilDAO.removerAcentos(limparStringComMascara(pessoa.getNumeroCpf()).toUpperCase())) && objPessoa.getPessoa().isRepresentante())) {
                return true;
            }
        }
        return false;
    }

    private boolean existeNumeroCpfPessoaTitularNaEntidade(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getNumeroCpf() == null || (pessoa.getNumeroCpf() != null && UtilDAO.removerAcentos(objPessoa.getPessoa().getNumeroCpf().toUpperCase()).contains(UtilDAO.removerAcentos(limparStringComMascara(pessoa.getNumeroCpf()).toUpperCase())) && objPessoa.getPessoa().isTitular())) {
                return true;
            }
        }
        return false;
    }

    private boolean existeNumeroCpf(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getNumeroCpf() == null || (pessoa.getNumeroCpf() != null && UtilDAO.removerAcentos(objPessoa.getPessoa().getNumeroCpf().toUpperCase()).contains(UtilDAO.removerAcentos(limparStringComMascara(pessoa.getNumeroCpf()).toUpperCase())))) {
                return true;
            }
        }
        return false;
    }

    private boolean existeNumeroTelefonePessoaNaEntidade(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getNumeroTelefone() == null || (pessoa.getNumeroTelefone() != null && UtilDAO.removerAcentos(objPessoa.getPessoa().getNumeroTelefone().toUpperCase()).contains(UtilDAO.removerAcentos(limparStringComMascara(pessoa.getNumeroTelefone()).toUpperCase())))) {
                return true;
            }
        }
        return false;
    }

    private boolean existeDescricaoCargoPessoaNaEntidade(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getDescricaoCargo() == null || (pessoa.getDescricaoCargo() != null && UtilDAO.removerAcentos(objPessoa.getPessoa().getDescricaoCargo().toUpperCase()).contains(UtilDAO.removerAcentos(pessoa.getDescricaoCargo().toUpperCase())))) {
                return true;
            }
        }
        return false;
    }

    private boolean existeEmailPessoaNaEntidade(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getEmail() == null || (pessoa.getEmail() != null && UtilDAO.removerAcentos(objPessoa.getPessoa().getEmail().toUpperCase()).contains(UtilDAO.removerAcentos(pessoa.getEmail().toUpperCase())))) {
                return true;
            }
        }
        return false;
    }

    private boolean existeEmail(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getEmail() == null || (pessoa.getEmail() != null && objPessoa.getPessoa().getEmail().toUpperCase().equalsIgnoreCase(pessoa.getEmail().toUpperCase()))) {
                return true;
            }
        }
        return false;
    }

    private boolean existeEnderecoCorrespondenciaPessoaNaEntidade(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getEnderecoCorrespondencia() == null || (pessoa.getEnderecoCorrespondencia() != null && UtilDAO.removerAcentos(objPessoa.getPessoa().getEnderecoCorrespondencia().toUpperCase()).contains(UtilDAO.removerAcentos(pessoa.getEnderecoCorrespondencia().toUpperCase())))) {
                return true;
            }
        }
        return false;
    }

    private boolean existeTipoPessoaNaEntidade(List<PessoaEntidade> listaPessoa, EnumTipoPessoa tipoPessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (tipoPessoa == null || (tipoPessoa != null && objPessoa.getPessoa().getTipoPessoa().getValor().equals(tipoPessoa.getValor()))) {
                return true;
            }
        }
        return false;
    }

    private boolean existeFuncaoRepresentantePessoaNaEntidade(List<PessoaEntidade> listaPessoa, Pessoa pessoa) {
        for (PessoaEntidade objPessoa : listaPessoa) {
            if (pessoa.getPossuiFuncaoDeRepresentante() == null || (pessoa.getPossuiFuncaoDeRepresentante() != null && objPessoa.getPessoa().getPossuiFuncaoDeRepresentante().equals(pessoa.getPossuiFuncaoDeRepresentante()))) {
                return true;
            }
        }
        return false;
    }

    private boolean existeContrato(List<Contrato> contratos, Contrato contrato) {
        if (contratos != null) {
            for (Contrato c : contratos) {
                if (contrato != null && UtilDAO.removerAcentos(c.getNumeroContrato().toLowerCase()).contains(UtilDAO.removerAcentos(contrato.getNumeroContrato().toLowerCase()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeBem(List<Contrato> contratos, Bem bem) {

        if (contratos != null) {
            for (Contrato c : contratos) {
                for (AgrupamentoLicitacao a : c.getListaAgrupamentosLicitacao()) {
                    for (SelecaoItem s : a.getListaSelecaoItem()) {
                        for (BemUf bu : s.getListaBemUf()) {
                            if (bem != null && bu.getBem().getId().equals(bem.getId())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean existePrograma(List<Contrato> contratos, Programa programa) {
        if (contratos != null) {
            for (Contrato c : contratos) {
                if (c.getPrograma().getId().equals(programa.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Long contarPaginado(EntidadePesquisaDto entidadePesquisaDto) {

        List<PessoaEntidade> listaPessoaEntidades = new ArrayList<PessoaEntidade>();
        if (entidadePesquisaDto.getUsuarioLogado() != null) {
            listaPessoaEntidades = pegarEntidadesDoUsuarioExternoLogado(entidadePesquisaDto);
        }

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Entidade> root = criteriaQuery.from(Entidade.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        Predicate[] predicates = extractPredicates(entidadePesquisaDto.getValidacaoCadastro(), entidadePesquisaDto.getOrigemCadastro(), entidadePesquisaDto.getTipoPerfil(), entidadePesquisaDto.getEntidade(), listaPessoaEntidades, criteriaBuilder, root);
        criteriaQuery.where(predicates);

        TypedQuery<Long> query = em.createQuery(criteriaQuery);

        return retornarCountListaFiltrada(entidadePesquisaDto, query);

    }

    private Predicate[] extractPredicates(EnumValidacaoCadastro validacaoCadastro, EnumOrigemCadastro origemCadastro, EnumPerfilEntidade perfilEntidade, Entidade entidade, List<PessoaEntidade> listaPessoaEntidades, CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();

        // entidades de Usuario Externo Logado

        if (!listaPessoaEntidades.isEmpty()) {
            List<Long> lista = new ArrayList<Long>();
            for (PessoaEntidade pessoaEntidade : listaPessoaEntidades) {
                lista.add(pessoaEntidade.getEntidade().getId());
            }

            predicates.add(root.get("id").in(lista));
        }

        // código
        if (entidade != null && entidade.getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), entidade.getId()));
        }

        // status
        if (entidade != null && entidade.getStatusEntidade() != null) {
            predicates.add(criteriaBuilder.equal(root.get("statusEntidade"), entidade.getStatusEntidade()));
        }

        // numeroCnpj
        if (entidade != null && entidade.getNumeroCnpj() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("numeroCnpj"))), "%" + UtilDAO.removerAcentos(entidade.getNumeroCnpj().toLowerCase()) + "%"));
        }

        // tipoEntidade
        if (entidade != null && entidade.getTipoEntidade() != null && entidade.getTipoEntidade().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("tipoEntidade").get("id"), entidade.getTipoEntidade().getId()));
        }

        // nomeEntidade
        if (entidade != null && StringUtils.isNotBlank(entidade.getNomeEntidade())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get(NOME_ENTIDADE))), "%" + UtilDAO.removerAcentos(entidade.getNomeEntidade().toLowerCase()) + "%"));
        }

        // personalidadeJuridica
        if (entidade != null && entidade.getPersonalidadeJuridica() != null) {
            predicates.add(criteriaBuilder.equal(root.get("personalidadeJuridica"), entidade.getPersonalidadeJuridica()));
        }

        // UF
        if (entidade != null && entidade.getMunicipio() != null && entidade.getMunicipio().getUf() != null && entidade.getMunicipio().getUf().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("municipio").get("uf").get("id"), entidade.getMunicipio().getUf().getId()));
        }

        // municipio
        if (entidade != null && entidade.getMunicipio() != null && entidade.getMunicipio().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("municipio").get("id"), entidade.getMunicipio().getId()));
        }

        // tipoEndereco
        if (entidade != null && entidade.getTipoEndereco() != null && entidade.getTipoEndereco().getId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("tipoEndereco").get("id"), entidade.getTipoEndereco().getId()));
        }

        // descricaoEndereco
        if (entidade != null && StringUtils.isNotBlank(entidade.getDescricaoEndereco())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("descricaoEndereco"))), "%" + UtilDAO.removerAcentos(entidade.getDescricaoEndereco().toLowerCase()) + "%"));
        }

        // numeroEndereco
        if (entidade != null && StringUtils.isNotBlank(entidade.getNumeroEndereco())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("numeroEndereco"))), "%" + UtilDAO.removerAcentos(entidade.getNumeroEndereco().toLowerCase()) + "%"));
        }

        // complementoEndereco
        if (entidade != null && StringUtils.isNotBlank(entidade.getComplementoEndereco())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("complementoEndereco"))), "%" + UtilDAO.removerAcentos(entidade.getComplementoEndereco().toLowerCase()) + "%"));
        }

        // bairro
        if (entidade != null && StringUtils.isNotBlank(entidade.getBairro())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("bairro"))), "%" + UtilDAO.removerAcentos(entidade.getBairro().toLowerCase()) + "%"));
        }

        // numeroCep
        if (entidade != null && StringUtils.isNotBlank(entidade.getNumeroCep())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("numeroCep"))), "%" + UtilDAO.removerAcentos(entidade.getNumeroCep().toLowerCase()) + "%"));
        }

        // numeroTelefone
        if (entidade != null && StringUtils.isNotBlank(entidade.getNumeroTelefone())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("numeroTelefone"))), "%" + UtilDAO.removerAcentos(entidade.getNumeroTelefone().toLowerCase()) + "%"));
        }

        // numeroFoneFax
        if (entidade != null && StringUtils.isNotBlank(entidade.getNumeroFoneFax())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("numeroFoneFax"))), "%" + UtilDAO.removerAcentos(entidade.getNumeroFoneFax().toLowerCase()) + "%"));
        }

        // email
        if (entidade != null && StringUtils.isNotBlank(entidade.getEmail())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.function(Constants.FUNCAO_RETIRA_ACENTO, String.class, criteriaBuilder.lower(root.get("email"))), "%" + UtilDAO.removerAcentos(entidade.getEmail().toLowerCase()) + "%"));
        }

        // perfil entidade: Beneficiario ou Fornecedor
        if (perfilEntidade != null) {
            predicates.add(criteriaBuilder.equal(root.get("perfilEntidade"), perfilEntidade));
        }

        // origemCadastro: Interno ou externo
        if (origemCadastro != null) {
            predicates.add(criteriaBuilder.equal(root.get("origemCadastro"), origemCadastro));
        }

        // validacaoCadastro: Validado ou Não validado
        if (entidade != null && entidade.getValidacaoCadastro() != null) {
            predicates.add(criteriaBuilder.equal(root.get("validacaoCadastro"), entidade.getValidacaoCadastro()));
        }

        // Irá buscar as entidades externas que escolheram este programa no
        // momento do cadastro.
        if (entidade != null && entidade.getProgramaPreferencial() != null) {
            predicates.add(criteriaBuilder.equal(root.get("programaPreferencial").get("id"), entidade.getProgramaPreferencial().getId()));
        }

        return predicates.toArray(new Predicate[] {});

    }

    private Long retornarCountListaFiltrada(EntidadePesquisaDto entidadePesquisaDto, TypedQuery<Long> query) {

        if (entidadePesquisaDto.getRepresentante() != null || entidadePesquisaDto.getTitular() != null) {
            List<Entidade> lista = buscar(entidadePesquisaDto);
            return (long) retornaListaFiltrada(lista, entidadePesquisaDto).size();

        } else {
            return query.getSingleResult();
        }
    }

    public List<Entidade> buscar(EntidadePesquisaDto entidadePesquisaDto) {

        List<PessoaEntidade> listaPessoaEntidades = new ArrayList<PessoaEntidade>();
        if (entidadePesquisaDto.getUsuarioLogado() != null) {
            listaPessoaEntidades = pegarEntidadesDoUsuarioExternoLogado(entidadePesquisaDto);
        }

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Entidade> criteriaQuery = criteriaBuilder.createQuery(Entidade.class);
        Root<Entidade> root = criteriaQuery.from(Entidade.class);

        Predicate[] predicates = extractPredicates(entidadePesquisaDto.getValidacaoCadastro(), entidadePesquisaDto.getOrigemCadastro(), entidadePesquisaDto.getTipoPerfil(), entidadePesquisaDto.getEntidade(), listaPessoaEntidades, criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(NOME_ENTIDADE)));

        TypedQuery<Entidade> query = em.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public List<Entidade> buscarPorPerfil(EntidadePesquisaDto entidadePesquisaDto, EnumPerfilEntidade enumPerfilEntidade) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Entidade> criteriaQuery = criteriaBuilder.createQuery(Entidade.class);
        Root<Entidade> root = criteriaQuery.from(Entidade.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.equal(criteriaBuilder.upper(root.get("perfilEntidade")), enumPerfilEntidade));

        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<Entidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public Entidade buscarPeloId(Long id) {

        return em.find(Entidade.class, id);
    }
    
    public List<Entidade> buscarTodosBeneficiariosPeloPrograma(Programa programa) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        
        Root<InscricaoPrograma> rootInscricao = criteriaQuery.from(InscricaoPrograma.class);
        
        Join<InscricaoPrograma,Programa> joinPrograma = rootInscricao.join("programa");
        Join<InscricaoPrograma,PessoaEntidade> joinPessoaEntidade = rootInscricao.join("pessoaEntidade");
        Join<PessoaEntidade,Entidade> joinEntidade = joinPessoaEntidade.join("entidade");
        Join<Entidade,Municipio> joinMunicipio = joinEntidade.join("municipio");
        Join<Municipio,Uf> joinUf = joinMunicipio.join("uf");
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(joinPrograma.get("id"), programa.getId()));

        criteriaQuery.multiselect(
                joinEntidade.get("id"),joinEntidade.get("numeroCnpj"),joinEntidade.get("nomeEntidade"),
                joinMunicipio.get("id"),joinMunicipio.get("nomeMunicipio"),
                joinUf.get("id"),joinUf.get("nomeUf"),joinUf.get("siglaUf")
                ).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<Object> query = em.createQuery(criteriaQuery);
        List<Object> lista = query.getResultList();
        return montarListaDeBeneficiarios(lista);
        
    }
    
    public List<Programa> buscarTodosProgramasDaEntidade(Entidade entidade) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InscricaoPrograma> criteriaQuery = criteriaBuilder.createQuery(InscricaoPrograma.class);
        
        Root<InscricaoPrograma> rootInscricao = criteriaQuery.from(InscricaoPrograma.class);
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(rootInscricao.get("pessoaEntidade").get("entidade").get("id"), entidade.getId()));
        
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<InscricaoPrograma> query = em.createQuery(criteriaQuery);
        List<InscricaoPrograma> lista = query.getResultList();
        
        List<Programa> listaProgramasRetornar = new ArrayList<Programa>();
        for(InscricaoPrograma inc:lista){
            listaProgramasRetornar.add(inc.getPrograma());
        }
        
        return listaProgramasRetornar;
    }

    private List<Entidade> montarListaDeBeneficiarios(List<Object> listaObj){
        
        List<Entidade> listaEntidade = new ArrayList<Entidade>();
        for(Object o:listaObj){
            Object[] object = (Object[]) o;
            Entidade entidade = new Entidade();
            
            entidade.setId((Long)object[0]);
            entidade.setNumeroCnpj((String)object[1]);
            entidade.setNomeEntidade((String)object[2]);
            
            Municipio municipio = new Municipio();
            municipio.setId((Long)object[3]);
            municipio.setNomeMunicipio((String)object[4]);
            
            Uf uf=new Uf();
            uf.setId((Long)object[5]);
            uf.setNomeUf((String)object[6]);
            uf.setSiglaUf((String)object[7]);
            
            municipio.setUf(uf);
            entidade.setMunicipio(municipio);
            listaEntidade.add(entidade);
        }
        return listaEntidade;
    }

    public void excluir(Entidade entidade) {
        em.remove(entidade);

    }

    private String limparStringComMascara(String valor) {
        String value = "";
        if (valor != null) {
            value = valor;
            value = value.replace(".", "");
            value = value.replace("/", "");
            value = value.replace("-", "");
            value = value.replace("(", "");
            value = value.replace(")", "");
        }
        return value;
    }

    public void incluirMembroComissao(PessoaEntidade pessoaEntidade, String usuarioLogado) {
        Entidade entidadeParaMerge = buscarPeloId(pessoaEntidade.getEntidade().getId());
        pessoaEntidade.getPessoa().setDataCadastro(LocalDateTime.now());
        pessoaEntidade.getPessoa().setUsuarioCadastro(usuarioLogado);
        pessoaEntidade.getPessoa().setPossuiFuncaoDeRepresentante(Boolean.FALSE);
        pessoaEntidade.getPessoa().setDescricaoCargo(EnumTipoPessoa.MEMBRO_COMISSAO_RECEBIMENTO.getDescricao());
        pessoaEntidade.getPessoa().setDataInicioExercicio(LocalDate.now());

        em.persist(pessoaEntidade.getPessoa());
        pessoaEntidade.setEntidade(entidadeParaMerge);
        entidadeParaMerge.getPessoas().add(pessoaEntidade);
        em.flush();
        em.merge(entidadeParaMerge);
    }

    public void alterarMembroComissao(PessoaEntidade pessoaEntidade, String usuarioLogado) {

        Pessoa pessoa = em.find(Pessoa.class, pessoaEntidade.getPessoa().getId());

        pessoa.setDataAlteracao(LocalDateTime.now());
        pessoa.setUsuarioAlteracao(usuarioLogado);

        pessoa.setNomePessoa(pessoaEntidade.getPessoa().getNomePessoa());
        pessoa.setEmail(pessoaEntidade.getPessoa().getEmail());
        pessoa.setNumeroTelefone(pessoaEntidade.getPessoa().getNumeroTelefone());
        em.merge(pessoa);
    }

    public Entidade incluir(Entidade entidade, String usuarioLogado) {

        /* seta data de cadastro, usuario logado e status inicial */
        entidade.setUsuarioCadastro(usuarioLogado);
        entidade.setDataCadastro(LocalDateTime.now());

        /* atribuindo tipoEntidade e tipoEndereco com contexto transacional */
        entidade.setTipoEndereco(em.find(TipoEndereco.class, entidade.getTipoEndereco().getId()));
        if (entidade.getTipoEntidade() != null) {
            entidade.setTipoEntidade(em.find(TipoEntidade.class, entidade.getTipoEntidade().getId()));
        }

        /*
         * Setar entidade dentro de cada anexo para resolver a questão da
         * referência bidirecional
         */
        List<EntidadeAnexo> listaEntidadeAnexo = new ArrayList<EntidadeAnexo>();
        for (EntidadeAnexo entidadeAnexo : entidade.getAnexos()) {
            entidadeAnexo.setEntidade(entidade);
            entidadeAnexo.setDataCadastro(LocalDateTime.now());
            entidadeAnexo.setTamanho(new Long(entidadeAnexo.getConteudo().length));
            listaEntidadeAnexo.add(entidadeAnexo);
        }
        entidade.setAnexos(listaEntidadeAnexo);

        // Inclui Entidades
        List<PessoaEntidade> listaPessoas = new ArrayList<PessoaEntidade>(entidade.getPessoas());
        entidade.setPessoas(null);
        em.persist(entidade);

        // Inclui Pessoas e vincula
        for (PessoaEntidade pessoaEntidade : listaPessoas) {

            if (pessoaEntidade.getPessoa().getId() == null) {
                pessoaEntidade.getPessoa().setUsuarioCadastro(usuarioLogado);
                pessoaEntidade.getPessoa().setDataCadastro(LocalDateTime.now());
                em.persist(pessoaEntidade.getPessoa());
            } else {

                if (pessoaEntidade.getPessoa().getUsuario() != null && pessoaEntidade.getPessoa().getUsuario().getId() != null) {
                    Usuario usuarioParaMerge = em.find(Usuario.class, pessoaEntidade.getPessoa().getUsuario().getId());
                    usuarioParaMerge.setDataAlteracao(LocalDateTime.now());
                    usuarioParaMerge.setUsuarioAlteracao(usuarioLogado);
                    usuarioParaMerge.setEmail(pessoaEntidade.getPessoa().getEmail());
                    usuarioParaMerge.setSituacaoUsuario(pessoaEntidade.getPessoa().getStatusPessoa());
                    usuarioParaMerge.setNomeCompleto(pessoaEntidade.getPessoa().getNomePessoa());
                    usuarioParaMerge.setPrimeiroNome(pessoaEntidade.getPessoa().getNomePessoa().split(" ")[0]);
                    em.merge(usuarioParaMerge);
                }

                Pessoa pessoaParaMerge = em.find(Pessoa.class, pessoaEntidade.getPessoa().getId());

                pessoaParaMerge.setDataAlteracao(LocalDateTime.now());
                pessoaParaMerge.setUsuarioAlteracao(usuarioLogado);

                pessoaParaMerge.setStatusPessoa(pessoaEntidade.getPessoa().getStatusPessoa());
                pessoaParaMerge.setNomePessoa(pessoaEntidade.getPessoa().getNomePessoa());
                pessoaParaMerge.setNumeroCpf(pessoaEntidade.getPessoa().getNumeroCpf());
                pessoaParaMerge.setDescricaoCargo(pessoaEntidade.getPessoa().getDescricaoCargo());
                pessoaParaMerge.setNumeroTelefone(pessoaEntidade.getPessoa().getNumeroTelefone());
                pessoaParaMerge.setEmail(pessoaEntidade.getPessoa().getEmail());
                pessoaParaMerge.setDataInicioExercicio(pessoaEntidade.getPessoa().getDataInicioExercicio());
                pessoaParaMerge.setDataFimExercicio(pessoaEntidade.getPessoa().getDataFimExercicio());
                pessoaParaMerge.setEnderecoCorrespondencia(pessoaEntidade.getPessoa().getEnderecoCorrespondencia());
                pessoaParaMerge.setTipoPessoa(pessoaEntidade.getPessoa().getTipoPessoa());
                pessoaParaMerge.setPossuiFuncaoDeRepresentante(pessoaEntidade.getPessoa().getPossuiFuncaoDeRepresentante());
                em.merge(pessoaParaMerge);
                pessoaEntidade.setPessoa(pessoaParaMerge);
            }

            pessoaEntidade.setEntidade(entidade);
            em.persist(pessoaEntidade);
        }

        return entidade;
    }

    public Entidade alterar(Entidade entidade, String usuarioLogado, boolean resetDataSenhas) {

        Entidade entidadeParaMerge = buscarPeloId(entidade.getId());

        entidadeParaMerge.setUsuarioAlteracao(usuarioLogado);
        entidadeParaMerge.setDataAlteracao(LocalDateTime.now());

        sincronizarAnexos(entidade.getAnexos(), entidadeParaMerge);
        entidadeParaMerge.setStatusEntidade(entidade.getStatusEntidade());
        entidadeParaMerge.setNumeroCnpj(entidade.getNumeroCnpj());

        if (entidade.getTipoEntidade() != null) {
            entidadeParaMerge.setTipoEntidade(em.find(TipoEntidade.class, entidade.getTipoEntidade().getId()));
        }

        entidadeParaMerge.setNomeEntidade(entidade.getNomeEntidade());
        entidadeParaMerge.setNomeContato(entidade.getNomeContato());
        entidadeParaMerge.setPersonalidadeJuridica(entidade.getPersonalidadeJuridica());
        entidadeParaMerge.setMunicipio(entidade.getMunicipio());
        entidadeParaMerge.setTipoEndereco(em.find(TipoEndereco.class, entidade.getTipoEndereco().getId()));
        entidadeParaMerge.setDescricaoEndereco(entidade.getDescricaoEndereco());
        entidadeParaMerge.setNumeroEndereco(entidade.getNumeroEndereco());
        entidadeParaMerge.setComplementoEndereco(entidade.getComplementoEndereco());
        entidadeParaMerge.setBairro(entidade.getBairro());
        entidadeParaMerge.setNumeroCep(entidade.getNumeroCep());
        entidadeParaMerge.setNumeroTelefone(entidade.getNumeroTelefone());
        entidadeParaMerge.setNumeroFoneFax(entidade.getNumeroFoneFax());
        entidadeParaMerge.setEmail(entidade.getEmail());
        entidadeParaMerge.setNumeroProcessoSEI(entidade.getNumeroProcessoSEI());
        entidadeParaMerge.setObservacoes(entidade.getObservacoes());
        entidadeParaMerge.setValidacaoCadastro(entidade.getValidacaoCadastro());
        entidadeParaMerge.setMotivoValidacao(entidade.getMotivoValidacao());
        em.merge(entidadeParaMerge);

        sincronizarPessoas(entidade.getPessoas(), entidadeParaMerge, usuarioLogado, resetDataSenhas);

        return entidadeParaMerge;
    }

    public Entidade sincronizarLocaisDeEntrega(Entidade entidade, String usuarioLogado) {

        Entidade entidadeParaMerge = buscarPeloId(entidade.getId());
        entidadeParaMerge.setUsuarioAlteracao(usuarioLogado);
        entidadeParaMerge.setDataAlteracao(LocalDateTime.now());
        sincronizarLocaisDeEntrega(entidade.getLocaisEntregaEntidade(), entidadeParaMerge, usuarioLogado);
        em.merge(entidadeParaMerge);

        return entidadeParaMerge;
    }

    private void sincronizarAnexos(List<EntidadeAnexo> anexos, Entidade entityAtual) {
        // remover os excluidos
        List<EntidadeAnexo> anexosAux = new ArrayList<EntidadeAnexo>(entityAtual.getAnexos());
        for (EntidadeAnexo anexoAtual : anexosAux) {
            if (!anexos.contains(anexoAtual)) {
                entityAtual.getAnexos().remove(anexoAtual);
            }
        }

        // adiciona os novos
        for (EntidadeAnexo anexoNovo : anexos) {
            if (anexoNovo.getId() == null) {
                anexoNovo.setEntidade(entityAtual);
                anexoNovo.setDataCadastro(LocalDateTime.now());
                anexoNovo.setTamanho(new Long(anexoNovo.getConteudo().length));
                entityAtual.getAnexos().add(anexoNovo);
            }
        }

    }

    private void sincronizarLocaisDeEntrega(List<LocalEntregaEntidade> locaisEntregaDaEntidade, Entidade entidadeAtual, String usuarioLogado) {

        List<LocalEntregaEntidade> listaLocalEntregaEntidadeAtualParaAtualizar = new ArrayList<LocalEntregaEntidade>();
        List<LocalEntregaEntidade> listaLocalEntregaEntidadeAdicionar = new ArrayList<LocalEntregaEntidade>();

        /*
         * seleciona apenas os Locais que foram mantidas na lista vinda do
         * service
         */
        for (LocalEntregaEntidade localEntregaEntidadeAtual : entidadeAtual.getLocaisEntregaEntidade()) {
            if (locaisEntregaDaEntidade.contains(localEntregaEntidadeAtual)) {
                listaLocalEntregaEntidadeAtualParaAtualizar.add(localEntregaEntidadeAtual);
            }

        }

        /* remove a lista atual de Locais */
        entidadeAtual.getLocaisEntregaEntidade().clear();

        /* adiciona os novos na lista de Criterio de Acompanhamento */
        for (LocalEntregaEntidade localEntregaEntidadeNovo : locaisEntregaDaEntidade) {
            if (localEntregaEntidadeNovo.getId() == null) {
                /* atender referencia bidirecional */
                localEntregaEntidadeNovo.setEntidade(entidadeAtual);
                localEntregaEntidadeNovo.setUsuarioCadastro(usuarioLogado);
                localEntregaEntidadeNovo.setDataCadastro(LocalDateTime.now());
                entidadeAtual.getLocaisEntregaEntidade().add(localEntregaEntidadeNovo);
            }
        }

        /*
         * atualiza atributos nos Locais de Entrega vindos do service para
         * persistir
         */
        for (LocalEntregaEntidade localEntregaEntidadeParaAtualizar : listaLocalEntregaEntidadeAtualParaAtualizar) {
            for (LocalEntregaEntidade localEntregaEntidade : locaisEntregaDaEntidade) {
                if (localEntregaEntidade.getId() != null && localEntregaEntidade.getId().equals(localEntregaEntidadeParaAtualizar.getId())) {

                    localEntregaEntidadeParaAtualizar.setNomeEndereco(localEntregaEntidade.getNomeEndereco());
                    localEntregaEntidadeParaAtualizar.setMunicipio(localEntregaEntidade.getMunicipio());
                    localEntregaEntidadeParaAtualizar.setTipoEndereco(em.find(TipoEndereco.class, localEntregaEntidade.getTipoEndereco().getId()));
                    localEntregaEntidadeParaAtualizar.setDescricaoEndereco(localEntregaEntidade.getDescricaoEndereco());
                    localEntregaEntidadeParaAtualizar.setNumeroEndereco(localEntregaEntidade.getNumeroEndereco());
                    localEntregaEntidadeParaAtualizar.setComplementoEndereco(localEntregaEntidade.getComplementoEndereco());
                    localEntregaEntidadeParaAtualizar.setBairro(localEntregaEntidade.getBairro());
                    localEntregaEntidadeParaAtualizar.setNumeroCep(localEntregaEntidade.getNumeroCep());
                    localEntregaEntidadeParaAtualizar.setNumeroTelefone(localEntregaEntidade.getNumeroTelefone());
                    localEntregaEntidadeParaAtualizar.setNumeroFoneFax(localEntregaEntidade.getNumeroFoneFax());
                    localEntregaEntidadeParaAtualizar.setUsuarioAlteracao(usuarioLogado);
                    localEntregaEntidadeParaAtualizar.setDataAlteracao(LocalDateTime.now());
                    localEntregaEntidadeParaAtualizar.setStatusLocalEntrega(localEntregaEntidade.getStatusLocalEntrega());

                    listaLocalEntregaEntidadeAdicionar.add(localEntregaEntidadeParaAtualizar);
                }
            }
        }
        /* adiciona os Locais de enttrega atualizados */
        entidadeAtual.getLocaisEntregaEntidade().addAll(listaLocalEntregaEntidadeAdicionar);
    }

    public void sincronizarPessoas(List<PessoaEntidade> pessoasDaEntidade, Entidade entidadeAtual, String usuarioLogado, boolean resetDataSenhas) {

        List<PessoaEntidade> listaPessoaAtualParaAtualizar = new ArrayList<PessoaEntidade>();

        /*
         * seleciona apenas as Pessoas que foram mantidas na lista vinda do
         * service
         */
        for (PessoaEntidade pessoaAtual : entidadeAtual.getPessoas()) {
            if (pessoasDaEntidade.contains(pessoaAtual)) {
                listaPessoaAtualParaAtualizar.add(pessoaAtual);
            }

        }

        /* adiciona os novos na lista de Pessoas */
        for (PessoaEntidade pessoaNovo : pessoasDaEntidade) {
            if (pessoaNovo.getId() == null) {
                if (pessoaNovo.getPessoa().getId() == null) {
                    pessoaNovo.getPessoa().setUsuarioCadastro(usuarioLogado);
                    pessoaNovo.getPessoa().setDataCadastro(LocalDateTime.now());
                    em.persist(pessoaNovo.getPessoa());

                } else {

                    if (pessoaNovo.getPessoa().getUsuario() != null && pessoaNovo.getPessoa().getUsuario().getId() != null) {
                        Usuario usuarioParaMerge = em.find(Usuario.class, pessoaNovo.getPessoa().getUsuario().getId());
                        usuarioParaMerge.setDataAlteracao(LocalDateTime.now());
                        usuarioParaMerge.setUsuarioAlteracao(usuarioLogado);
                        usuarioParaMerge.setEmail(pessoaNovo.getPessoa().getEmail());
                        usuarioParaMerge.setSituacaoUsuario(pessoaNovo.getPessoa().getStatusPessoa());
                        usuarioParaMerge.setNomeCompleto(pessoaNovo.getPessoa().getNomePessoa());
                        usuarioParaMerge.setPrimeiroNome(pessoaNovo.getPessoa().getNomePessoa().split(" ")[0]);
                        usuarioParaMerge.setDataExpiracaoAlteraEntidade(pessoaNovo.getPessoa().getUsuario().getDataExpiracaoAlteraEntidade());
                        em.merge(usuarioParaMerge);
                    }

                    Pessoa pessoaParaMerge = em.find(Pessoa.class, pessoaNovo.getPessoa().getId());

                    pessoaParaMerge.setDataAlteracao(LocalDateTime.now());
                    pessoaParaMerge.setUsuarioAlteracao(usuarioLogado);

                    pessoaParaMerge.setStatusPessoa(pessoaNovo.getPessoa().getStatusPessoa());
                    pessoaParaMerge.setNomePessoa(pessoaNovo.getPessoa().getNomePessoa());
                    pessoaParaMerge.setNumeroCpf(pessoaNovo.getPessoa().getNumeroCpf());
                    pessoaParaMerge.setDescricaoCargo(pessoaNovo.getPessoa().getDescricaoCargo());
                    pessoaParaMerge.setNumeroTelefone(pessoaNovo.getPessoa().getNumeroTelefone());
                    pessoaParaMerge.setEmail(pessoaNovo.getPessoa().getEmail());
                    pessoaParaMerge.setDataInicioExercicio(pessoaNovo.getPessoa().getDataInicioExercicio());
                    pessoaParaMerge.setDataFimExercicio(pessoaNovo.getPessoa().getDataFimExercicio());
                    pessoaParaMerge.setEnderecoCorrespondencia(pessoaNovo.getPessoa().getEnderecoCorrespondencia());
                    pessoaParaMerge.setTipoPessoa(pessoaNovo.getPessoa().getTipoPessoa());
                    pessoaParaMerge.setPossuiFuncaoDeRepresentante(pessoaNovo.getPessoa().getPossuiFuncaoDeRepresentante());
                    em.merge(pessoaParaMerge);
                    pessoaNovo.setPessoa(pessoaParaMerge);

                }

                /* atender referencia bidirecional */
                pessoaNovo.setEntidade(entidadeAtual);
                em.persist(pessoaNovo);
            }
        }

        /*
         * atualiza atributos nas Pessoas vindas do service para persistir
         */
        for (PessoaEntidade pessoaParaAtualizar : listaPessoaAtualParaAtualizar) {
            for (PessoaEntidade objPessoa : pessoasDaEntidade) {
                if (objPessoa.getId() != null && objPessoa.getId().equals(pessoaParaAtualizar.getId())) {

                    if (objPessoa.getPessoa().getUsuario() != null && objPessoa.getPessoa().getUsuario().getId() != null) {
                        Usuario usuarioParaMerge = em.find(Usuario.class, objPessoa.getPessoa().getUsuario().getId());
                        usuarioParaMerge.setDataAlteracao(LocalDateTime.now());
                        usuarioParaMerge.setUsuarioAlteracao(usuarioLogado);
                        usuarioParaMerge.setEmail(objPessoa.getPessoa().getEmail());
                        usuarioParaMerge.setSituacaoUsuario(objPessoa.getPessoa().getStatusPessoa());
                        usuarioParaMerge.setNomeCompleto(objPessoa.getPessoa().getNomePessoa());
                        usuarioParaMerge.setPrimeiroNome(objPessoa.getPessoa().getNomePessoa().split(" ")[0]);
                        if (resetDataSenhas) {
                            usuarioParaMerge.setDataLimiteTrocaSenha(objPessoa.getPessoa().getUsuario().getDataLimiteTrocaSenha());
                            usuarioParaMerge.setDataExpiracaoSenha(objPessoa.getPessoa().getUsuario().getDataExpiracaoSenha());
                        }
                        em.merge(usuarioParaMerge);
                    }

                    pessoaParaAtualizar.getPessoa().setDataAlteracao(LocalDateTime.now());
                    pessoaParaAtualizar.getPessoa().setUsuarioAlteracao(usuarioLogado);

                    pessoaParaAtualizar.getPessoa().setStatusPessoa(objPessoa.getPessoa().getStatusPessoa());
                    pessoaParaAtualizar.getPessoa().setNomePessoa(objPessoa.getPessoa().getNomePessoa());
                    pessoaParaAtualizar.getPessoa().setNumeroCpf(objPessoa.getPessoa().getNumeroCpf());
                    pessoaParaAtualizar.getPessoa().setDescricaoCargo(objPessoa.getPessoa().getDescricaoCargo());
                    pessoaParaAtualizar.getPessoa().setNumeroTelefone(objPessoa.getPessoa().getNumeroTelefone());
                    pessoaParaAtualizar.getPessoa().setEmail(objPessoa.getPessoa().getEmail());
                    pessoaParaAtualizar.getPessoa().setDataInicioExercicio(objPessoa.getPessoa().getDataInicioExercicio());
                    pessoaParaAtualizar.getPessoa().setDataFimExercicio(objPessoa.getPessoa().getDataFimExercicio());
                    pessoaParaAtualizar.getPessoa().setEnderecoCorrespondencia(objPessoa.getPessoa().getEnderecoCorrespondencia());
                    pessoaParaAtualizar.getPessoa().setTipoPessoa(objPessoa.getPessoa().getTipoPessoa());
                    pessoaParaAtualizar.getPessoa().setPossuiFuncaoDeRepresentante(objPessoa.getPessoa().getPossuiFuncaoDeRepresentante());
                    em.merge(pessoaParaAtualizar.getPessoa());
                }
            }
        }
    }

    public Pessoa alterarStatusPessoa(Pessoa pessoa) {

        Pessoa pessoaParaMerge = em.find(Pessoa.class, pessoa.getId());
        pessoaParaMerge.setStatusPessoa(pessoa.getStatusPessoa());

        // Se esta pessoa não tiver usuário para logar no sistema então será
        // ignorada a linha abaixo
        if (pessoaParaMerge.getUsuario() != null) {
            Usuario usuarioParaMerge = em.find(Usuario.class, pessoaParaMerge.getUsuario().getId());
            usuarioParaMerge.setSituacaoUsuario(pessoa.getStatusPessoa());
            em.merge(usuarioParaMerge);
        }
        return em.merge(pessoaParaMerge);
    }

    public void atualizarStatusPessoas() {
        List<Pessoa> lista = buscarPessoasPassiveisDeAlteracaoDeStatus();
        for (Pessoa pessoa : lista) {
            definirStatusPessoa(pessoa);
        }
        em.flush();
    }

    public List<Entidade> atualizarInscricoesNaoAnalizadasNoPrazo() {
        List<Entidade> lista = buscarEntidadesPassiveisDeNaoAnalise();
        for (Entidade entidade : lista) {
            atualizarInscricoesNaoAnalizadasNoPrazo(entidade);
        }
        em.flush();
        return lista;
    }

    private void atualizarInscricoesNaoAnalizadasNoPrazo(Entidade entidade) {
        entidade.setValidacaoCadastro(EnumValidacaoCadastro.EXPURGO);
    }

    private void definirStatusPessoa(Pessoa pessoa) {
        pessoa.setStatusPessoa(EnumStatusPessoa.INATIVO);
        if (pessoa.getUsuario() != null && pessoa.getUsuario().getId() != null) {
            pessoa.getUsuario().setSituacaoUsuario(EnumStatusPessoa.INATIVO);
        }

    }

    private List<Pessoa> buscarPessoasPassiveisDeAlteracaoDeStatus() {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Pessoa> criteriaQuery = criteriaBuilder.createQuery(Pessoa.class);
        Root<Pessoa> root = criteriaQuery.from(Pessoa.class);

        Predicate[] predicates = extractPredicates(criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        TypedQuery<Pessoa> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private List<Entidade> buscarEntidadesPassiveisDeNaoAnalise() {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Entidade> criteriaQuery = criteriaBuilder.createQuery(Entidade.class);
        Root<Entidade> root = criteriaQuery.from(Entidade.class);

        Predicate[] predicates = extractPredicatesEntidadeNaoAvaliada(criteriaBuilder, root);
        criteriaQuery.select(criteriaQuery.getSelection()).where(predicates);

        TypedQuery<Entidade> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private Predicate[] extractPredicates(CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("statusPessoa"), EnumStatusPessoa.ATIVO));
        predicates.add(criteriaBuilder.isNotNull(root.get("dataFimExercicio")));
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataFimExercicio"), LocalDate.now()));
        return predicates.toArray(new Predicate[] {});

    }

    private Predicate[] extractPredicatesEntidadeNaoAvaliada(CriteriaBuilder criteriaBuilder, Root<?> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("validacaoCadastro"), EnumValidacaoCadastro.NAO_ANALISADO));
        predicates.add(criteriaBuilder.equal(root.get("origemCadastro"), EnumOrigemCadastro.CADASTRO_EXTERNO));
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataCadastro"), LocalDateTime.now().minusMonths(LIMITE_ENVIO_EMAIL_ENTIDADE_NAO_ANALISADA_MESES)));
        return predicates.toArray(new Predicate[] {});

    }

    public Pessoa alterarPessoa(Pessoa pessoa) {
        return em.merge(pessoa);
    }
}

package br.gov.mj.side.web.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.KitBem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumResultadoFinalAnaliseElegibilidade;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.programa.PotencialBeneficiarioMunicipio;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoRecebimento;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntrega;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntregaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoLocalEntregaKit;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaKit;
import br.gov.mj.side.web.dao.InscricaoProgramaDAO;
import br.gov.mj.side.web.dto.BemUfDto;
import br.gov.mj.side.web.dto.BensVinculadosEntidadeDto;
import br.gov.mj.side.web.dto.ItemDto;
import br.gov.mj.side.web.dto.PermissaoProgramaDto;
import br.gov.mj.side.web.dto.RetornoPermiteInscricaoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InscricaoProgramaService {

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private InscricaoProgramaDAO inscricaoProgramaDAO;

    @Inject
    private PublicizacaoService publicizacaoService;

    @Inject
    private ProgramaService programaService;

    public List<ComissaoRecebimento> buscarComissaoRecebimento(InscricaoPrograma inscricaoPrograma) {
        if (inscricaoPrograma == null || inscricaoPrograma.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return inscricaoProgramaDAO.buscarComissaoRecebimento(inscricaoPrograma.getId());
    }

    public List<InscricaoPrograma> gerarListaClassificacaoAvaliacaoPaginado(Programa programa, int first, int size) {
        return inscricaoProgramaDAO.paginar(gerarListaClassificacaoAvaliacaoSemPaginacao(programa), first, size);
    }

    public List<InscricaoPrograma> gerarListaClassificacaoAvaliacaoSemPaginacao(Programa programa) {

        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        InscricaoPrograma ip = new InscricaoPrograma();
        ip.setPrograma(programa);
        ip.setStatusInscricao(EnumStatusInscricao.CONCLUIDA_ANALISE_AVALIACAO);

        List<InscricaoPrograma> lista = inscricaoProgramaDAO.buscarSemPaginacaoOrdenado(ip, EnumOrder.DESC, "pontuacaoFinal");
        List<InscricaoPrograma> listaFinal = new ArrayList<InscricaoPrograma>();

        BigDecimal total = buscarTodoRecursoFinanceiroDoPrograma(programa);

        Long colocacao = 0L;
        for (InscricaoPrograma inscricaoPrograma : lista) {
            total = total.subtract(inscricaoPrograma.getTotalUtilizado());
            if (BigDecimal.ZERO.compareTo(total) <= 0) {
                inscricaoPrograma.setClassificado(true);
            } else {
                inscricaoPrograma.setClassificado(false);
            }
            inscricaoPrograma.setColocacao(++colocacao);
            listaFinal.add(inscricaoPrograma);
        }

        return listaFinal;
    }

    public List<InscricaoPrograma> buscarListaClassificados(Programa programa) {

        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        List<InscricaoPrograma> lista = gerarListaClassificacaoAvaliacaoSemPaginacao(programa);

        List<InscricaoPrograma> listaClassificados = new ArrayList<InscricaoPrograma>();

        for (InscricaoPrograma inscricaoPrograma : lista) {
            if (inscricaoPrograma.isClassificado()) {
                listaClassificados.add(inscricaoPrograma);
            }
        }
        return listaClassificados;
    }

    public List<BemUfDto> buscarListaBemUfDto(Programa programa) {

        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        List<InscricaoPrograma> listaClassificados = buscarListaClassificados(programa);
        Map<String, BemUfDto> mapa = new HashMap<String, BemUfDto>();

        for (InscricaoPrograma inscricaoPrograma : listaClassificados) {
            for (InscricaoLocalEntrega inscricaoLocalEntrega : inscricaoPrograma.getLocaisEntregaInscricao()) {

                for (InscricaoLocalEntregaBem inscricaoLocalEntregaBem : inscricaoLocalEntrega.getBensEntrega()) {

                    populaMap(mapa, inscricaoLocalEntregaBem.getInscricaoProgramaBem().getProgramaBem().getBem(), inscricaoLocalEntregaBem.getInscricaoLocalEntrega().getLocalEntregaEntidade().getMunicipio().getUf(), Long.parseLong(inscricaoLocalEntregaBem.getQuantidade().toString()));
                }
                for (InscricaoLocalEntregaKit inscricaoLocalEntregaKit : inscricaoLocalEntrega.getKitsEntrega()) {
                    for (KitBem kitBem : inscricaoLocalEntregaKit.getInscricaoProgramaKit().getProgramaKit().getKit().getKitsBens()) {
                        Long quantidade = Long.parseLong(inscricaoLocalEntregaKit.getQuantidade().toString()) * Long.parseLong(kitBem.getQuantidade().toString());
                        populaMap(mapa, kitBem.getBem(), inscricaoLocalEntregaKit.getInscricaoLocalEntrega().getLocalEntregaEntidade().getMunicipio().getUf(), quantidade);
                    }

                }
            }

        }
        return transformaMapEmListaBemUfDto(mapa);
    }

    private void populaMap(Map<String, BemUfDto> mapa, Bem bem, Uf uf, Long quantidade) {

        String chaveIdBem = bem.getId().toString();
        String chaveIdUf = uf.getSiglaUf();
        String chave = chaveIdBem + "-" + chaveIdUf;

        if (mapa.get(chave) != null) {
            Long qtd = mapa.get(chave).getQuantidade() + quantidade;
            mapa.get(chave).setQuantidade(qtd);

        } else {
            BemUfDto dto = new BemUfDto();
            dto.setBem(bem);
            dto.setUf(uf);
            dto.setQuantidade(quantidade);
            mapa.put(chave, dto);
        }
    }

    private List<BemUfDto> transformaMapEmListaBemUfDto(Map<String, BemUfDto> mapa) {
        List<BemUfDto> lista = new ArrayList<BemUfDto>();
        lista.addAll(mapa.values());
        return lista;
    }

    public Long countClassificacaoAvaliacao(Programa programa) {
        InscricaoPrograma ip = new InscricaoPrograma();
        ip.setPrograma(programa);
        ip.setStatusInscricao(EnumStatusInscricao.CONCLUIDA_ANALISE_AVALIACAO);
        return inscricaoProgramaDAO.contarPaginado(ip);
    }

    private BigDecimal buscarTodoRecursoFinanceiroDoPrograma(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        List<ProgramaRecursoFinanceiro> lista = programaService.buscarProgramaRecursoFinanceiroPeloIdPrograma(programa);
        BigDecimal total = BigDecimal.ZERO;
        for (ProgramaRecursoFinanceiro programaRecursoFinanceiro : lista) {
            total = total.add(programaRecursoFinanceiro.getTotal());
        }
        return total;
    }

    public List<InscricaoPrograma> gerarListaClassificacaoElegibilidadePaginado(Programa programa, EnumStatusInscricao statusBuscar, int first, int size) {
        InscricaoPrograma ip = new InscricaoPrograma();
        ip.setPrograma(programa);
        ip.setResultadoFinalAnaliseElegibilidade(EnumResultadoFinalAnaliseElegibilidade.ELEGIVEL);

        if (statusBuscar != null) {
            ip.setStatusInscricao(EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE);
        }
        return inscricaoProgramaDAO.buscarPaginado(ip, first, size, EnumOrder.ASC, "pessoaEntidade.entidade.nomeEntidade");
    }

    public List<InscricaoPrograma> gerarListaClassificacaoElegibilidadeSemPaginacao(Programa programa, EnumStatusInscricao statusBuscar) {
        InscricaoPrograma ip = new InscricaoPrograma();
        ip.setPrograma(programa);
        ip.setResultadoFinalAnaliseElegibilidade(EnumResultadoFinalAnaliseElegibilidade.ELEGIVEL);

        if (statusBuscar != null) {
            ip.setStatusInscricao(EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE);
        }
        return inscricaoProgramaDAO.buscarSemPaginacaoOrdenado(ip, EnumOrder.ASC, "pessoaEntidade.entidade.nomeEntidade");
    }

    public Long countClassificacaoElegibilidade(Programa programa, EnumStatusInscricao statusBuscar) {
        InscricaoPrograma ip = new InscricaoPrograma();
        ip.setPrograma(programa);
        ip.setResultadoFinalAnaliseElegibilidade(EnumResultadoFinalAnaliseElegibilidade.ELEGIVEL);

        if (statusBuscar != null) {
            ip.setStatusInscricao(EnumStatusInscricao.CONCLUIDA_ANALISE_ELEGIBILIDADE);
        }
        return inscricaoProgramaDAO.contarPaginado(ip);
    }
    
    public List<InscricaoPrograma> buscarInscricaoProgramaPeloProgramaEEntidade(Programa programa,Entidade entidade) {
        
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id do Programa não pode ser null");
        }
        
        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id da Entidade não pode ser null");
        }
        return inscricaoProgramaDAO.buscarInscricaoProgramaPeloProgramaEEntidade(programa, entidade);
    }
    

    public List<InscricaoPrograma> buscarProgramasInscritosParaEntidade(Entidade entidade) {

        if (entidade == null || entidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        InscricaoPrograma ip = new InscricaoPrograma();
        PessoaEntidade pe = new PessoaEntidade();
        pe.setEntidade(entidade);
        ip.setPessoaEntidade(pe);
        return inscricaoProgramaDAO.buscar(ip);
    }

    public boolean possuiProgramaInscritoParaEntidade(Programa programa, Entidade entidade) {
        InscricaoPrograma ip = new InscricaoPrograma();
        PessoaEntidade pe = new PessoaEntidade();
        pe.setEntidade(entidade);
        ip.setPessoaEntidade(pe);
        ip.setPrograma(programa);

        List<InscricaoPrograma> lista = inscricaoProgramaDAO.buscar(ip);

        if (!lista.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public List<InscricaoLocalEntregaBem> buscarInscricaoLocalEntregaBem(InscricaoLocalEntrega inscricaoLocalEntrega) {

        if (inscricaoLocalEntrega == null || inscricaoLocalEntrega.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        return inscricaoProgramaDAO.buscarInscricaoLocalEntregaBem(inscricaoLocalEntrega.getId());
    }

    public List<InscricaoLocalEntregaKit> buscarInscricaoLocalEntregaKit(InscricaoLocalEntrega inscricaoLocalEntrega) {
        if (inscricaoLocalEntrega == null || inscricaoLocalEntrega.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return inscricaoProgramaDAO.buscarInscricaoLocalEntregaKit(inscricaoLocalEntrega.getId());
    }

    public List<InscricaoLocalEntrega> buscarInscricaoLocalEntregaEntidade(LocalEntregaEntidade localEntregaEntidade) {
        if (localEntregaEntidade == null || localEntregaEntidade.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return inscricaoProgramaDAO.buscarInscricaoLocalEntregaEntidade(localEntregaEntidade.getId());
    }

    public List<InscricaoLocalEntrega> buscarInscricaoLocalEntrega(InscricaoPrograma inscricaoPrograma) {
        if (inscricaoPrograma == null || inscricaoPrograma.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        return inscricaoProgramaDAO.buscarInscricaoLocalEntrega(inscricaoPrograma.getId());
    }

    public List<InscricaoProgramaBem> buscarInscricaoProgramaBem(InscricaoPrograma inscricaoPrograma) {
        if (inscricaoPrograma == null || inscricaoPrograma.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return inscricaoProgramaDAO.buscarInscricaoProgramaBem(inscricaoPrograma.getId());
    }

    public List<InscricaoProgramaKit> buscarInscricaoProgramaKit(InscricaoPrograma inscricaoPrograma) {
        if (inscricaoPrograma == null || inscricaoPrograma.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return inscricaoProgramaDAO.buscarInscricaoProgramaKit(inscricaoPrograma.getId());
    }

    public List<InscricaoProgramaCriterioAvaliacao> buscarInscricaoProgramaCriterioAvaliacao(InscricaoPrograma inscricaoPrograma) {
        if (inscricaoPrograma == null || inscricaoPrograma.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return inscricaoProgramaDAO.buscarInscricaoProgramaCriterioAvaliacao(inscricaoPrograma.getId());
    }

    public List<InscricaoProgramaCriterioElegibilidade> buscarInscricaoProgramaCriterioElegibilidade(InscricaoPrograma inscricaoPrograma) {
        if (inscricaoPrograma == null || inscricaoPrograma.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return inscricaoProgramaDAO.buscarInscricaoProgramaCriterioElegibilidade(inscricaoPrograma.getId());
    }

    public List<InscricaoPrograma> buscarPaginado(InscricaoPrograma inscricaoPrograma, int first, int size, EnumOrder order, String propertyOrder) {
        return inscricaoProgramaDAO.buscarPaginado(inscricaoPrograma, first, size, order, propertyOrder);
    }

    public List<InscricaoPrograma> buscarSemPaginacao(InscricaoPrograma inscricaoPrograma) {
        return inscricaoProgramaDAO.buscarSemPaginacao(inscricaoPrograma);
    }

    public List<InscricaoPrograma> buscarSemPaginacaoOrdenado(InscricaoPrograma inscricaoPrograma, EnumOrder order, String propertyOrder) {
        return inscricaoProgramaDAO.buscarSemPaginacaoOrdenado(inscricaoPrograma, order, propertyOrder);
    }

    public List<InscricaoPrograma> buscar(InscricaoPrograma inscricaoPrograma) {
        return inscricaoProgramaDAO.buscar(inscricaoPrograma);
    }

    public Long contarPaginado(InscricaoPrograma inscricaoPrograma) {
        return inscricaoProgramaDAO.contarPaginado(inscricaoPrograma);
    }

    public InscricaoPrograma buscarPeloId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe Inscricao com esse id cadastrado */

        InscricaoPrograma i = inscricaoProgramaDAO.buscarPeloId(id);

        if (i == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN044", id);
            throw ex;
        }
        return i;
    }

    public InscricaoPrograma incluirAlterar(InscricaoPrograma inscricaoPrograma, String usuarioLogado) {

        InscricaoPrograma inscricaoProgramaRetorno = null;

        validarObjetoNulo(inscricaoPrograma);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório.");
            throw ex;
        }

        if (inscricaoPrograma.getPessoaEntidade() == null || inscricaoPrograma.getPessoaEntidade().getPessoa().getId() == null) {
            ex.addErrorMessage("Escolha do Representante Obrigatória.");
            throw ex;
        }

        if (inscricaoPrograma.getProgramasBem().isEmpty() && inscricaoPrograma.getProgramasKit().isEmpty()) {
            ex.addErrorMessage("MT012");
            throw ex;
        }

        inscricaoPrograma.setStatusInscricao(EnumStatusInscricao.EM_ELABORACAO);

        if (inscricaoPrograma.getId() == null) {
            inscricaoProgramaRetorno = inscricaoProgramaDAO.incluir(inscricaoPrograma, usuarioLogado);
        } else {
            inscricaoProgramaRetorno = inscricaoProgramaDAO.alterar(inscricaoPrograma, usuarioLogado);
        }
        return inscricaoProgramaRetorno;
    }

    public InscricaoPrograma sincronizarLocaisEntrega(InscricaoPrograma inscricaoPrograma, String usuarioLogado) {

        InscricaoPrograma inscricaoProgramaRetorno = null;

        validarObjetoNulo(inscricaoPrograma);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório.");
            throw ex;
        }

        validarQuantidadesParaEntrega(inscricaoPrograma, ex);

        inscricaoProgramaRetorno = inscricaoProgramaDAO.sincronizarLocaisEntrega(inscricaoPrograma, usuarioLogado);

        return inscricaoProgramaRetorno;
    }

    public void validarQuantidadesParaEntrega(InscricaoPrograma inscricaoPrograma, BusinessException ex) {
        List<ItemDto> bens = new ArrayList<ItemDto>();
        List<ItemDto> kits = new ArrayList<ItemDto>();

        // seta as quantidades de bens da proposta
        for (InscricaoProgramaBem inscricaoProgramaBem : inscricaoPrograma.getProgramasBem()) {
            ItemDto item = new ItemDto();
            item.setId(inscricaoProgramaBem.getProgramaBem().getBem().getId());
            item.setQuantidade(inscricaoProgramaBem.getQuantidade());
            bens.add(item);
        }

        // seta as quantidades de kits da proposta
        for (InscricaoProgramaKit inscricaoProgramaKit : inscricaoPrograma.getProgramasKit()) {
            ItemDto item = new ItemDto();
            item.setId(inscricaoProgramaKit.getProgramaKit().getKit().getId());
            item.setQuantidade(inscricaoProgramaKit.getQuantidade());
            kits.add(item);

        }

        Map<Long, Integer> bensEntrega = new HashMap<Long, Integer>();
        Map<Long, Integer> kitsEntrega = new HashMap<Long, Integer>();

        for (InscricaoLocalEntrega inscricaoLocalEntrega : inscricaoPrograma.getLocaisEntregaInscricao()) {

            for (InscricaoLocalEntregaBem inscricaoLocalEntregaBem : inscricaoLocalEntrega.getBensEntrega()) {
                // seta a quantidade de bens da entrega
                if (bensEntrega.get(inscricaoLocalEntregaBem.getInscricaoProgramaBem().getProgramaBem().getBem().getId()) == null) {
                    bensEntrega.put(inscricaoLocalEntregaBem.getInscricaoProgramaBem().getProgramaBem().getBem().getId(), inscricaoLocalEntregaBem.getQuantidade());
                } else {
                    bensEntrega.put(inscricaoLocalEntregaBem.getInscricaoProgramaBem().getProgramaBem().getBem().getId(), inscricaoLocalEntregaBem.getQuantidade() + bensEntrega.get(inscricaoLocalEntregaBem.getInscricaoProgramaBem().getProgramaBem().getBem().getId()));
                }
            }

            for (InscricaoLocalEntregaKit inscricaoLocalEntregaKit : inscricaoLocalEntrega.getKitsEntrega()) {
                // seta a quantidade de kits da entrega
                if (kitsEntrega.get(inscricaoLocalEntregaKit.getInscricaoProgramaKit().getProgramaKit().getKit().getId()) == null) {
                    kitsEntrega.put(inscricaoLocalEntregaKit.getInscricaoProgramaKit().getProgramaKit().getKit().getId(), inscricaoLocalEntregaKit.getQuantidade());
                } else {
                    kitsEntrega.put(inscricaoLocalEntregaKit.getInscricaoProgramaKit().getProgramaKit().getKit().getId(), inscricaoLocalEntregaKit.getQuantidade() + kitsEntrega.get(inscricaoLocalEntregaKit.getInscricaoProgramaKit().getProgramaKit().getKit().getId()));
                }

            }

        }

        for (ItemDto itemDto : bens) {
            if (bensEntrega.get(itemDto.getId()) == null || !bensEntrega.get(itemDto.getId()).equals(itemDto.getQuantidade())) {
                ex.addErrorMessage("Todos os bens disponíveis devem estar vinculados a um local de entrega.");
                throw ex;
            }
        }

        for (ItemDto itemDto : kits) {
            if (kitsEntrega.get(itemDto.getId()) == null || !kitsEntrega.get(itemDto.getId()).equals(itemDto.getQuantidade())) {

                ex.addErrorMessage("Todos os kits disponíveis devem estar vinculados a um local de entrega.");
                throw ex;
            }
        }
    }

    public InscricaoPrograma submeter(InscricaoPrograma inscricaoPrograma, String usuarioLogado) {

        InscricaoPrograma inscricaoProgramaRetorno = null;

        validarObjetoNulo(inscricaoPrograma);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório.");
            throw ex;
        }

        if (inscricaoPrograma.getPessoaEntidade() == null || inscricaoPrograma.getPessoaEntidade().getPessoa().getId() == null) {
            ex.addErrorMessage("Escolha do Representante Obrigatória.");
            throw ex;
        }

        if (inscricaoPrograma.getProgramasBem().isEmpty() && inscricaoPrograma.getProgramasKit().isEmpty()) {
            ex.addErrorMessage("A escolha de pelo menos um item da Solicitação é obrigatória.");
            throw ex;
        }

        if (!valorMaximoPropostaSupremGastosDeBensEKits(inscricaoPrograma)) {
            ex.addErrorMessage("Os itens selecionados em solicitação não podem ultrapassar o valor máximo por proposta cadastrado para o programa.");
            throw ex;
        }

        if (!atendeObrigatoriedadeAnexosElegibilidade(inscricaoPrograma)) {
            ex.addErrorMessage("Existe(m) critério(s) de Elegibilidade com anexo(s) obrigatório(s).");
            throw ex;
        }

        if (!atendeObrigatoriedadeAnexosAvaliacao(inscricaoPrograma)) {
            ex.addErrorMessage("Existe(m) critério(s) de Avaliação com anexo(s) obrigatório(s).");
            throw ex;
        }

        inscricaoPrograma.setStatusInscricao(EnumStatusInscricao.ENVIADA_ANALISE);

        if (inscricaoPrograma.getId() == null) {
            inscricaoProgramaRetorno = inscricaoProgramaDAO.incluir(inscricaoPrograma, usuarioLogado);
        } else {
            inscricaoProgramaRetorno = inscricaoProgramaDAO.alterar(inscricaoPrograma, usuarioLogado);
        }
        return inscricaoProgramaRetorno;
    }

    private boolean valorMaximoPropostaSupremGastosDeBensEKits(InscricaoPrograma inscricaoPrograma) {
        BigDecimal diferenca = BigDecimal.ZERO;
        diferenca = diferenca.add(inscricaoPrograma.getPrograma().getValorMaximoProposta());
        diferenca = diferenca.subtract(buscarSomaGastosPrograma(inscricaoPrograma));
        return BigDecimal.ZERO.compareTo(diferenca) <= 0;
    }

    private BigDecimal buscarSomaGastosPrograma(InscricaoPrograma inscricaoPrograma) {
        BigDecimal totalGastos = BigDecimal.ZERO;
        totalGastos = totalGastos.add(pegarSomaBens(inscricaoPrograma.getProgramasBem()));
        totalGastos = totalGastos.add(pegarSomaKits(inscricaoPrograma.getProgramasKit()));
        return totalGastos;
    }
    
  //Serviço utilizado para montar o relatório de acompanhamento de vinculação de bens aos locais de entrega
    public List<BensVinculadosEntidadeDto> buscarBensVinculadosAEnderecos(Programa programa){
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro programa não pode ser null");
        }
        
        //Irá buscar somente as inscrições classificadas
        List<InscricaoPrograma> inscricoesClassificadas = buscarListaClassificados(programa);
        List<BensVinculadosEntidadeDto> listaRetornar = new ArrayList<BensVinculadosEntidadeDto>();
        
        
        //Irá buscar todos os bens que já foram vinculados a endereços
        List<BensVinculadosEntidadeDto> listaVinculados = inscricaoProgramaDAO.buscarBensVinculadosAEnderecos(programa);
       
        //Irá adicionar a 'listaRetornar' somente os classificados.
        for(InscricaoPrograma ip:inscricoesClassificadas){
            for(BensVinculadosEntidadeDto dto:listaVinculados){
                if(ip.getPessoaEntidade().getEntidade().getId().longValue() == dto.getEntidade().getId().longValue()){
                    listaRetornar.add(dto);
                    break;
                }
            }
        }        
        return listaRetornar;
    }

    private BigDecimal pegarSomaBens(List<InscricaoProgramaBem> lista) {
        BigDecimal total = BigDecimal.ZERO;
        for (InscricaoProgramaBem inscricaoProgramaBem : lista) {
            total = total.add(inscricaoProgramaBem.getProgramaBem().getBem().getValorEstimadoBem().multiply(new BigDecimal(inscricaoProgramaBem.getQuantidade().toString())));
        }
        return total;
    }

    private BigDecimal pegarSomaKits(List<InscricaoProgramaKit> lista) {
        BigDecimal total = BigDecimal.ZERO;
        for (InscricaoProgramaKit inscricaoProgramaKit : lista) {
            for (KitBem kitBem : inscricaoProgramaKit.getProgramaKit().getKit().getKitsBens()) {
                total = total.add(kitBem.getBem().getValorEstimadoBem().multiply(new BigDecimal(kitBem.getQuantidade().toString()).multiply(new BigDecimal(inscricaoProgramaKit.getQuantidade().toString()))));
            }
        }
        return total;
    }

    private boolean atendeObrigatoriedadeAnexosElegibilidade(InscricaoPrograma inscricaoPrograma) {
        for (InscricaoProgramaCriterioElegibilidade inscricaoProgramaCriterioElegibilidade : inscricaoPrograma.getProgramasCriterioElegibilidade()) {
            if (inscricaoProgramaCriterioElegibilidade.getAtendeCriterioElegibilidade() && inscricaoProgramaCriterioElegibilidade.getProgramaCriterioElegibilidade().getPossuiObrigatoriedadeDeAnexo() && inscricaoProgramaCriterioElegibilidade.getAnexos().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean atendeObrigatoriedadeAnexosAvaliacao(InscricaoPrograma inscricaoPrograma) {
        for (InscricaoProgramaCriterioAvaliacao inscricaoProgramaCriterioAvaliacao : inscricaoPrograma.getProgramasCriterioAvaliacao()) {
            if (inscricaoProgramaCriterioAvaliacao.getProgramaCriterioAvaliacao().getPossuiObrigatoriedadeDeAnexo() && inscricaoProgramaCriterioAvaliacao.getAnexos().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void validarObjetoNulo(InscricaoPrograma inscricaoPrograma) {
        if (inscricaoPrograma == null) {
            throw new IllegalArgumentException("Parâmetro programa não pode ser null");
        }
    }

    public void excluir(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe Inscricao com esse id cadastrado */

        InscricaoPrograma i = genericPersister.findByUniqueProperty(InscricaoPrograma.class, "id", id);
        if (i == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN044", id);
            throw ex;
        }

        inscricaoProgramaDAO.excluir(i);

    }

    public RetornoPermiteInscricaoDto permiteInscricaoDaEntidadeNoPrograma(Programa programa, Entidade entidade) {

        PermissaoProgramaDto permissaoProgramaDto = publicizacaoService.buscarPermissoesPrograma(programa);

        if (possuiProgramaInscritoParaEntidade(programa, entidade)) {
            return new RetornoPermiteInscricaoDto(false, "Entidade já está inscrita no Programa.");
        }
        if (entidade == null) {
            return new RetornoPermiteInscricaoDto(false, "Para Inscrever-se neste programa efetue o login.");
        } else {
            if (!permissaoProgramaDto.getInscrever()) {
                return new RetornoPermiteInscricaoDto(false, "Programa não está na fase de Recebimento de Propostas.");
            } else {
                if (!atendeNaturezaJuridica(programa, entidade)) {
                    return new RetornoPermiteInscricaoDto(false, "A Entidade não atende à natureza Jurídica estabelecida no Programa.");
                }
                if (!atendeLocalizacaoGeografica(programa, entidade)) {
                    return new RetornoPermiteInscricaoDto(false, "A Entidade não atende à Localização Geográfica estabelecida no Programa.");
                }
            }
        }
        return new RetornoPermiteInscricaoDto(true, null);
    }

    private boolean atendeNaturezaJuridica(Programa programa, Entidade entidade) {
        if (programa.getTipoPersonalidadeJuridica().equals(entidade.getPersonalidadeJuridica())) {
            return true;
        } else if (programa.getTipoPersonalidadeJuridica().equals(EnumPersonalidadeJuridica.TODAS)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean atendeLocalizacaoGeografica(Programa programa, Entidade entidade) {

        // não possui limitação geografica
        if (!programa.getPossuiLimitacaoGeografica()) {
            return true;
            // possui limitacao geografica
        } else {
            List<ProgramaPotencialBeneficiarioUf> todosBeneficiariosDestePrograma = programaService.buscarProgramaPotencialBeneficiarioUf(programa);
            // valida apenas uf
            if (!programa.getPossuiLimitacaoMunicipalEspecifica()) {
                for (ProgramaPotencialBeneficiarioUf programaPotencialBeneficiarioUf : todosBeneficiariosDestePrograma) {
                    if (programaPotencialBeneficiarioUf.getUf().getId().equals(entidade.getMunicipio().getUf().getId())) {
                        return true;
                    }
                }
                return false;
                // valida município
            } else {
                for (ProgramaPotencialBeneficiarioUf programaPotencialBeneficiarioUf : todosBeneficiariosDestePrograma) {
                    if (programaPotencialBeneficiarioUf.getUf().getId().equals(entidade.getMunicipio().getUf().getId()) && programaPotencialBeneficiarioUf.getPotencialBeneficiarioMunicipios().isEmpty()) {
                        return true;
                    }
                    for (PotencialBeneficiarioMunicipio potencialBeneficiarioMunicipio : programaPotencialBeneficiarioUf.getPotencialBeneficiarioMunicipios()) {
                        if (potencialBeneficiarioMunicipio.getMunicipio().getId().equals(entidade.getMunicipio().getId())) {
                            return true;
                        }
                    }

                }
                return false;
            }

        }

    }

    public InscricaoPrograma submeterComissaoRecebimento(InscricaoPrograma inscricaoPrograma, String usuarioLogado) {

        InscricaoPrograma inscricaoProgramaRetorno = null;

        validarObjetoNulo(inscricaoPrograma);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório.");
            throw ex;
        }

        if (inscricaoPrograma.getComissaoRecebimento() == null || inscricaoPrograma.getComissaoRecebimento().size() < 1) {
            ex.addErrorMessage("Para formar uma comissão de recebimento de um programa é necessária a indicação de no mínimo 1 membro.");
            throw ex;
        }

        if (inscricaoPrograma.getId() == null) {
            ex.addErrorMessage("Parâmetro id não pode ser null");
            throw ex;
        } else {
            inscricaoProgramaRetorno = inscricaoProgramaDAO.sincronizarComissaoRecebimento(inscricaoPrograma, usuarioLogado);
        }
        return inscricaoProgramaRetorno;
    }

}

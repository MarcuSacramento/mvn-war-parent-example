package br.gov.mj.side.web.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.mail.Session;

import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.entidade.PessoaEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoLista;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;
import br.gov.mj.side.entidades.enums.EnumValidacaoCadastro;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoRecebimento;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoPrograma;
import br.gov.mj.side.entidades.programa.licitacao.contrato.HistoricoComunicacaoGeracaoOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.OrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.TermoDoacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.dao.InscricaoProgramaDAO;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.MailSender;
import br.gov.mj.side.web.view.enums.EnumSelecao;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MailService {

    private static final String ASSUNTO_SIDE_ALTERACAO_CADASTRO = "SIDE - Alteração no Cadastro de Entidade";
    private static final String ASSUNTO_SIDE_CADASTRO_ENTIDADE = "SIDE - Cadastro de Entidade";
    private static final String ASSUNTO_SIDE_CANCELAMENTO_OF = "SIDE - Cancelamento da Ordem de Fornecimento";
    private static final String ASSUNTO_SIDE_ACESSO_SISTEMA = "SIDE - Acesso ao Sistema";
    private static final String ASSUNTO_SIDE_GERACAO_ORDEM_FORNECIMENTO = "SIDE - Geração da Ordem de Fornecimento";
    private static final String ASSUNTO_SIDE_FORMACAO_COMISSAO_RECEBIMENTO = "SIDE - Formação da Comissão de Recebimento";
    private static final String ASSUNTO_SIDE_TERMO_ENTREGA = "SIDE - Entrega de Itens";
    private static final String ASSUNTO_SIDE_REGISTRAR_TERMO_ENTREGA = "SIDE - Relatório de Recebimento";
    private static final String ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_5DIAS = "SIDE - Prazo de 5 dias para envio do Relatório de Recebimento";
    private static final String ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_ATRASO = "SIDE - Ataso no envio do Relatório de Recebimento";
    private static final String ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_FINAL = "SIDE - Prazo Final para envio do Relatório de Recebimento";

    @Resource(mappedName = "java:jboss/mail/mj-mail")
    private Session session;
    @Inject
    private MailSender mailSender;
    @Inject
    private GenericEntidadeService genericEntidadeService;
    @Inject
    private InscricaoProgramaDAO inscricaoProgramaDAO;
    @Inject
    private PublicizacaoService publicizacaoService;
    @Inject
    private InscricaoProgramaService inscricaoProgramaService;
    @Inject
    private BeneficiarioService beneficiarioService;

    public void enviarEmailPublicacaoLista(Programa programa, EnumSelecao selecao, EnumTipoLista tipoLista) {

        List<InscricaoPrograma> lista = inscricaoProgramaDAO.buscarInscricaoProgramaPeloPrograma(programa);
        String assunto = "SIDE - Lista de " + selecao.getDescricao() + " - " + tipoLista.getDescricao();
        ProgramaHistoricoPublicizacao ultimoHistorico = publicizacaoService.buscarUltimoProgramaHistoricoPublicizacao(programa);
        String dataInicioRecurso = "";
        String dataFimRecurso = "";
        String dataInicioLocalEntrega = "";
        String dataFimLocalEntrega = "";

        if (selecao.getValor().equals(EnumSelecao.PROPOSTAS_ELEGIVEIS.getValor())) {
            dataInicioRecurso = DataUtil.converteDataDeLocalDateParaString(ultimoHistorico.getDataInicialRecursoElegibilidade(), "dd/MM/yyyy");
            dataFimRecurso = DataUtil.converteDataDeLocalDateParaString(ultimoHistorico.getDataFinalRecursoElegibilidade(), "dd/MM/yyyy");
        } else if (selecao.getValor().equals(EnumSelecao.CLASSIFICACAO_PROSPOSTA.getValor())) {
            dataInicioRecurso = DataUtil.converteDataDeLocalDateParaString(ultimoHistorico.getDataInicialRecursoAvaliacao(), "dd/MM/yyyy");
            dataFimRecurso = DataUtil.converteDataDeLocalDateParaString(ultimoHistorico.getDataFinalRecursoAvaliacao(), "dd/MM/yyyy");
            dataInicioLocalEntrega = DataUtil.converteDataDeLocalDateParaString(ultimoHistorico.getDataInicialCadastroLocalEntrega(), "dd/MM/yyyy");
            dataFimLocalEntrega = DataUtil.converteDataDeLocalDateParaString(ultimoHistorico.getDataFinalCadastroLocalEntrega(), "dd/MM/yyyy");
        }

        for (InscricaoPrograma inscricaoPrograma : lista) {
            mailSender.send(inscricaoPrograma.getPessoaEntidade().getPessoa().getUsuario().getEmail(), assunto,
                    geraMensagemLista(inscricaoPrograma.getPessoaEntidade().getPessoa().getNomePessoa(), inscricaoPrograma.getPrograma().getNomePrograma(), dataInicioRecurso, dataFimRecurso, tipoLista, selecao, dataInicioLocalEntrega, dataFimLocalEntrega), true);
        }
    }

    public void enviarEmailEsquecimentoSenha(Usuario usuario, String linkAplicacao) {

        mailSender.send(usuario.getEmail(), ASSUNTO_SIDE_ACESSO_SISTEMA, geraMensagemResetSenhaSistema(usuario, linkAplicacao), true);
    }

    public void enviarEmailCadastroNovaEntidadePorUsuarioInterno(Entidade entidadePersistida, Long maiorIdPessoaAtual, String linkAplicacao) {

        List<PessoaEntidade> pessoasRetorno = genericEntidadeService.buscarPessoa(entidadePersistida);

        List<PessoaEntidade> listaPessoaFinal = new ArrayList<PessoaEntidade>();

        Pessoa entidadeTitular = pegarTitular(pessoasRetorno);

        for (PessoaEntidade objPessoa : pessoasRetorno) {
            if (objPessoa.getPessoa().isRepresentante() && objPessoa.getPessoa().getId() > maiorIdPessoaAtual) {
                mailSender.send(objPessoa.getPessoa().getUsuario().getEmail(), ASSUNTO_SIDE_ACESSO_SISTEMA, geraMensagemAcessoSistema(objPessoa.getPessoa(), entidadePersistida, linkAplicacao), true);
                mailSender.send(entidadeTitular.getEmail(), ASSUNTO_SIDE_CADASTRO_ENTIDADE, geraMensagemCadastroEntidade(entidadeTitular, entidadePersistida, objPessoa.getPessoa()), true);
            }
            listaPessoaFinal.add(objPessoa);
        }
    }

    // Chamado somente quando for realizado o cadastro externo da entidade
    public void enviarEmailCadastroNovaEntidadePorUsuarioExterno(Entidade entidadePersistida, Long maiorIdPessoaAtual, String linkAplicacao) {

        List<PessoaEntidade> pessoasRetorno = genericEntidadeService.buscarPessoa(entidadePersistida);

        Pessoa entidadeTitular = pegarTitularExterno(pessoasRetorno);
        mailSender.send(entidadeTitular.getEmail(), ASSUNTO_SIDE_CADASTRO_ENTIDADE, geraMensagemCadastroExternoEntidade(entidadeTitular, entidadePersistida, pessoasRetorno), true);

        for (PessoaEntidade objPessoa : pessoasRetorno) {
            if (objPessoa.getPessoa().isRepresentante() && objPessoa.getPessoa().getId() > maiorIdPessoaAtual) {
                mailSender.send(objPessoa.getPessoa().getUsuario().getEmail(), ASSUNTO_SIDE_ACESSO_SISTEMA, geraMensagemCadastroExterno(objPessoa.getPessoa(), entidadePersistida), true);
            }
        }
    }

    public void enviarEmailQuandoOrdemFornecimentoCancelada(OrdemFornecimentoContrato ordemFornecimento, HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historico) {

        mailSender.send(ordemFornecimento.getContrato().getPreposto().getEmail(), ASSUNTO_SIDE_CANCELAMENTO_OF, geraMensagemCancelamentoOF(ordemFornecimento, historico), true);

    }

    public void enviarEmailAoComunicarFornecedor(OrdemFornecimentoContrato ordemFornecimento, String numeroSei) {

        String mensagemEnviar = geraMensagemGeracaoOrdemFornecimento(ordemFornecimento, numeroSei);
        String emailEnviar = ordemFornecimento.getContrato().getPreposto().getEmail();
        mailSender.send(emailEnviar, ASSUNTO_SIDE_GERACAO_ORDEM_FORNECIMENTO, mensagemEnviar, true);
    }

    // Chamada em FornecedorPage.java No método salvar()
    public void enviarEmailCadastroNovoFornecedor(Entidade entidadePersistida, Long maiorIdPessoaAtual, String linkAplicacao) {

        List<PessoaEntidade> pessoasRetorno = genericEntidadeService.buscarPessoa(entidadePersistida);

        Pessoa fornecedorRepresentante = pegarRepresentanteLegal(pessoasRetorno);
        mailSender.send(fornecedorRepresentante.getEmail(), ASSUNTO_SIDE_CADASTRO_ENTIDADE, geraMensagemRepresentante(fornecedorRepresentante, entidadePersistida, pessoasRetorno), true);

        for (PessoaEntidade objPessoa : pessoasRetorno) {
            if (objPessoa.getPessoa().isPreposto() && objPessoa.getPessoa().getId() > maiorIdPessoaAtual) {
                mailSender.send(objPessoa.getPessoa().getEmail(), ASSUNTO_SIDE_ACESSO_SISTEMA, geraMensagemPreposto(objPessoa.getPessoa(), entidadePersistida, linkAplicacao), true);
            }
        }
    }

    // Enviado quando o gestor aprovar ou recusar um cadastro
    public void enviarEmailAprovacaoReprovacaoCadastroEntidade(Entidade entidadePersistida, Long idPessoaReceberMensagemQuandoRecusado, String linkAplicacao) {

        List<PessoaEntidade> pessoasRetorno = genericEntidadeService.buscarPessoa(entidadePersistida);

        Pessoa entidadeTitular = pegarTitularExterno(pessoasRetorno);
        mailSender.send(entidadeTitular.getEmail(), ASSUNTO_SIDE_CADASTRO_ENTIDADE, geraMensagemParaTitularAprovacaoCadastro(entidadeTitular, entidadePersistida, pessoasRetorno), true);

        // Se o cadastro for validado então todos os representantes irão receber
        // uma senha e login de acesso, se recusado todos irão receber um token
        // para acesso e correção do cadastro.
        for (PessoaEntidade objPessoa : pessoasRetorno) {
            if (objPessoa.getPessoa().isRepresentante()) {
                mailSender.send(objPessoa.getPessoa().getUsuario().getEmail(), ASSUNTO_SIDE_ACESSO_SISTEMA, geraMensagemParaRepresentanteAprovacaoCadastro(objPessoa.getPessoa(), entidadePersistida, linkAplicacao), true);
            }
        }
    }

    // Chamado quando cadastrar um novo membro para a comissão de recebimento.
    public void enviarEmailCadastroNovoMembroComissao(Entidade entidadePersistida, List<PessoaEntidade> listaEnvio, String linkAplicacao) {

        List<PessoaEntidade> pessoasRetorno = genericEntidadeService.buscarPessoa(entidadePersistida);
        List<PessoaEntidade> listaParaEnviar = new ArrayList<PessoaEntidade>();

        boolean encontrado = false;
        for (PessoaEntidade pe : pessoasRetorno) {
            for (PessoaEntidade enviar : listaEnvio) {
                if (pe.getPessoa().getId().intValue() == enviar.getPessoa().getId().intValue()) {
                    encontrado = true;
                    break;
                }
            }

            if (encontrado) {
                listaParaEnviar.add(pe);
            }
            encontrado = false;
        }

        for (PessoaEntidade objPessoa : listaParaEnviar) {
            mailSender.send(objPessoa.getPessoa().getEmail(), ASSUNTO_SIDE_ACESSO_SISTEMA, geraMensagemCadastroMembroDaComissao(objPessoa.getPessoa(), entidadePersistida, linkAplicacao), true);
        }
    }

    public void enviarEmailAlteracaoEmail(Entidade entidadePersistida, List<PessoaEntidade> listaEmailsAlterados) {

        for (PessoaEntidade objPessoa : listaEmailsAlterados) {
            mailSender.send(objPessoa.getPessoa().getEmail(), ASSUNTO_SIDE_ALTERACAO_CADASTRO, geraMensagemAlteracaoCadastro(objPessoa.getPessoa(), entidadePersistida), true);
        }
    }

    public void enviarEmailEntidadeNaoAnalisadaEmTempoHabil(Entidade entidadeNaAnalisada) {

        List<PessoaEntidade> pessoasRetorno = genericEntidadeService.buscarPessoa(entidadeNaAnalisada);
        for (PessoaEntidade objPessoa : pessoasRetorno) {
            if (objPessoa.getPessoa().isRepresentante()) {
                mailSender.send(objPessoa.getPessoa().getUsuario().getEmail(), ASSUNTO_SIDE_CADASTRO_ENTIDADE, geraMensagemAvisoFimPrazoAnaliseCadastroExterno(objPessoa.getPessoa(), entidadeNaAnalisada), true);
            }
        }

    }

    // Dispara e-mail para novos membros
    public void enviarEmailComissaoRecebimento(InscricaoPrograma inscricaoPrograma, long ultimoIdMembrosRecebimento) {
        String codNomePrograma = inscricaoPrograma.getPrograma().getCodigoIdentificadorProgramaPublicadoENomePrograma();
        for (ComissaoRecebimento objetoComissaoRecebimento : inscricaoPrograma.getComissaoRecebimento()) {
            if (objetoComissaoRecebimento.getId() > ultimoIdMembrosRecebimento) {
                mailSender.send(objetoComissaoRecebimento.getMembroComissao().getUsuario().getEmail(), ASSUNTO_SIDE_FORMACAO_COMISSAO_RECEBIMENTO, geraMensagemComissaoRecebimento(objetoComissaoRecebimento.getMembroComissao(), codNomePrograma), true);
            }
        }
    }

    public void enviarEmailBeneficiariosTermoDeEntraga(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato, List<InscricaoPrograma> listaInscricaoPrograma, String itens) {
        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            mailSender.send(inscricaoPrograma.getPessoaEntidade().getPessoa().getEmail(), ASSUNTO_SIDE_TERMO_ENTREGA, geraMensagemBeneficiariosTermoDeEntraga(notaRemessaOrdemFornecimentoContrato, inscricaoPrograma.getPessoaEntidade().getPessoa(), itens), true);
            for (ComissaoRecebimento comissaoRecebimento : inscricaoProgramaService.buscarComissaoRecebimento(inscricaoPrograma)) {
                if (comissaoRecebimento.getMembroComissao().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                    mailSender.send(comissaoRecebimento.getMembroComissao().getEmail(), ASSUNTO_SIDE_TERMO_ENTREGA, geraMensagemBeneficiariosTermoDeEntraga(notaRemessaOrdemFornecimentoContrato, comissaoRecebimento.getMembroComissao(), itens), true);
                }
            }
        }
    }

    public void enviarEmailRegistrarTermoDeEntraga(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato, List<InscricaoPrograma> listaInscricaoPrograma, String link) {
        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            mailSender.send(inscricaoPrograma.getPessoaEntidade().getPessoa().getEmail(), ASSUNTO_SIDE_REGISTRAR_TERMO_ENTREGA, geraMensagemRegistrarTermoDeEntraga(notaRemessaOrdemFornecimentoContrato, inscricaoPrograma.getPessoaEntidade().getPessoa(), link + "?hash=" + inscricaoPrograma.getId()),
                    true);
            for (ComissaoRecebimento comissaoRecebimento : inscricaoProgramaService.buscarComissaoRecebimento(inscricaoPrograma)) {
                if (comissaoRecebimento.getMembroComissao().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                    mailSender.send(comissaoRecebimento.getMembroComissao().getEmail(), ASSUNTO_SIDE_REGISTRAR_TERMO_ENTREGA, geraMensagemRegistrarTermoDeEntraga(notaRemessaOrdemFornecimentoContrato, comissaoRecebimento.getMembroComissao(), link), true);
                }
            }
        }

    }

    // Email 5 dias em VerificarEnvioRelatorioPeloRepresentanteJob.java
    public void enviarEmail5dias(LocalDate localDate, List<InscricaoPrograma> listaInscricaoPrograma, String link) {
        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            mailSender.send(inscricaoPrograma.getPessoaEntidade().getPessoa().getEmail(), ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_5DIAS, geraEmail5dias(localDate, inscricaoPrograma.getPessoaEntidade().getPessoa(), link + "?hash=" + inscricaoPrograma.getId()), true);
            for (ComissaoRecebimento comissaoRecebimento : inscricaoProgramaService.buscarComissaoRecebimento(inscricaoPrograma)) {
                if (comissaoRecebimento.getMembroComissao().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                    mailSender.send(comissaoRecebimento.getMembroComissao().getEmail(), ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_5DIAS, geraEmail5dias(localDate, comissaoRecebimento.getMembroComissao(), link + "?hash=" + inscricaoPrograma.getId()), true);
                }
            }
        }
    }

    private String geraEmail5dias(LocalDate localDate, Pessoa pessoa, String link) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha).append(novaLinha);

        sb.append("Informamos que faltam 5 dias para finalizar o prazo para registrar o recebimento dos itens entregues no dia: " + inicioNegrito + DataUtil.converteDataDeLocalDateParaString(localDate, "dd/MM/yyyy") + fimNegrito).append(novaLinha);
        sb.append("Para efetuar este registro acesse a página \"Registrar Recebimento\" de sua inscrição pelo link " + inicioNegrito + "<a href=" + link + " target='_blank'>click aqui</a>" + fimNegrito + " .").append(novaLinha);
        sb.append("Para concluir esta etapa é preciso anexar o Relatório de Recebimento assinando. Este relatório poderá ser gerado após o cadastro de todos os itens recebidos").append(novaLinha).append(novaLinha);

        sb.append(inicioNegrito + "Importante:" + fimNegrito).append(novaLinha).append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento").append(novaLinha);
        sb.append("de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que").append(novaLinha);
        sb.append("não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    // Email Atraso em VerificarEnvioRelatorioPeloRepresentanteJob.java
    public void enviarEmailAtraso(LocalDate localDate, List<InscricaoPrograma> listaInscricaoPrograma, String link, Entidade entidade) {

        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            mailSender.send(inscricaoPrograma.getPessoaEntidade().getPessoa().getEmail(), ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_ATRASO, geraEmailAtraso(localDate, inscricaoPrograma.getPessoaEntidade().getPessoa(), link + "?hash=" + inscricaoPrograma.getId()), true);
            for (ComissaoRecebimento comissaoRecebimento : inscricaoProgramaService.buscarComissaoRecebimento(inscricaoPrograma)) {
                if (comissaoRecebimento.getMembroComissao().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                    mailSender.send(comissaoRecebimento.getMembroComissao().getEmail(), ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_ATRASO, geraEmailAtraso(localDate, comissaoRecebimento.getMembroComissao(), link + "?hash=" + inscricaoPrograma.getId()), true);
                }
            }
            for (PessoaEntidade titularEntidade : beneficiarioService.buscarTitularEntidade(entidade)) {
                mailSender.send(titularEntidade.getPessoa().getEmail(), ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_ATRASO, geraEmailAtraso(localDate, titularEntidade.getPessoa(), link + "?hash=" + inscricaoPrograma.getId()), true);
            }
        }
    }

    private String geraEmailAtraso(LocalDate localDate, Pessoa pessoa, String link) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha).append(novaLinha);

        sb.append("Até o momento não identificamos o envio do relatório de recebimento dos itens entregues no dia : " + inicioNegrito + DataUtil.converteDataDeLocalDateParaString(localDate, "dd/MM/yyyy") + fimNegrito).append(novaLinha);
        sb.append("Para efetuar este registro acesse a página \"Registrar Recebimento\" de sua inscrição pelo link " + inicioNegrito + "<a href=" + link + " target='_blank'>click aqui</a>" + fimNegrito + " .").append(novaLinha);
        sb.append("Para concluir esta etapa é preciso anexar o Relatório de Recebimento assinando. Este relatório poderá ser gerado após o cadastro de todos os itens recebidos").append(novaLinha).append(novaLinha);

        sb.append(inicioNegrito + "Importante:" + fimNegrito).append(novaLinha).append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento").append(novaLinha);
        sb.append("de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que").append(novaLinha);
        sb.append("não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    // Email Prazo Final em VerificarEnvioRelatorioPeloRepresentanteJob.java
    public void enviarEmailFinalPrazo(LocalDate localDate, List<InscricaoPrograma> listaInscricaoPrograma, String link, Entidade entidade) {
        for (InscricaoPrograma inscricaoPrograma : listaInscricaoPrograma) {
            mailSender.send(inscricaoPrograma.getPessoaEntidade().getPessoa().getEmail(), ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_FINAL, geraEmailFinalPrazo(localDate, inscricaoPrograma.getPessoaEntidade().getPessoa(), link + "?hash=" + inscricaoPrograma.getId()), true);
            for (ComissaoRecebimento comissaoRecebimento : inscricaoProgramaService.buscarComissaoRecebimento(inscricaoPrograma)) {
                if (comissaoRecebimento.getMembroComissao().getStatusPessoa() == EnumStatusPessoa.ATIVO) {
                    mailSender.send(comissaoRecebimento.getMembroComissao().getEmail(), ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_FINAL, geraEmailFinalPrazo(localDate, comissaoRecebimento.getMembroComissao(), link + "?hash=" + inscricaoPrograma.getId()), true);
                }
            }
            for (PessoaEntidade titularEntidade : beneficiarioService.buscarTitularEntidade(entidade)) {
                mailSender.send(titularEntidade.getPessoa().getEmail(), ASSUNTO_SIDE_PRAZO_TERMO_RECEBIMENTO_FINAL, geraEmailFinalPrazo(localDate, titularEntidade.getPessoa(), link + "?hash=" + inscricaoPrograma.getId()), true);
            }
        }
    }

    private String geraEmailFinalPrazo(LocalDate localDate, Pessoa pessoa, String link) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha).append(novaLinha);

        sb.append("Sua Entidade encontra-se em atraso com o envio do relatório de recebimento dos itens entregues no dia: " + inicioNegrito + DataUtil.converteDataDeLocalDateParaString(localDate, "dd/MM/yyyy") + fimNegrito).append(novaLinha);
        sb.append("Para efetuar este registro acesse a página \"Registrar Recebimento\" de sua inscrição pelo link " + inicioNegrito + "<a href=" + link + " target='_blank'>click aqui</a>" + fimNegrito + " .").append(novaLinha);
        sb.append("Para concluir esta etapa é preciso anexar o Relatório de Recebimento assinando. Este relatório poderá ser gerado após o cadastro de todos os itens recebidos").append(novaLinha).append(novaLinha);

        sb.append(inicioNegrito + "Importante:" + fimNegrito).append(novaLinha).append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento").append(novaLinha);
        sb.append("de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que").append(novaLinha);
        sb.append("não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    // Chamada em ConfirmarDoacaoPage.java - actionGerarTermoDoacao()
    public void enviarEmailConcluirDoacao(TermoDoacao termoDoacao) {
        mailSender.send(termoDoacao.getEntidade().getEmail(), "DOAÇÃO CONCLUÍDA - PREENCHER RELATÓRIO PATRIMÔNIO", geraEmailConcluirDoacao(termoDoacao), true);
    }

    private String geraEmailConcluirDoacao(TermoDoacao termoDoacao) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + termoDoacao.getNomeBeneficiario().toUpperCase() + fimNegrito + ",").append(novaLinha).append(novaLinha);
        sb.append("O Termo de Doação foi concluído com sucessso").append(novaLinha).append(novaLinha);
        sb.append("Itens Doados:").append(novaLinha);

        sb.append("<table border='1'><tr><td><center>  ID  </center></td><td><center>  ITEM  </center></td></tr>");
        for (ObjetoFornecimentoContrato obj : termoDoacao.getObjetosFornecimentoContrato()) {
            sb.append("<tr><td><center>  " + obj.getId() + "  </center>  </td><td><center>  " + obj.getItem().getNomeBem() + "  </center></td></tr>");
        }
        sb.append("</table>");
        sb.append("O preenchimento do relatório de patrimônio é obrigatório, seu prazo é de 30 dias para preenchimento das informações.").append(novaLinha).append(novaLinha);

        sb.append(inicioNegrito + "Importante:" + fimNegrito).append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento").append(novaLinha);
        sb.append("de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que").append(novaLinha);
        sb.append("não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    // Chamada em DetalharTermoEntregaPage.java - actionDevolverCorrecao()
    public void enviarEmailDevolverCorrecao(Pessoa fornecedorPreposto, Pessoa representanteTitular, List<ObjetoFornecimentoContrato> listaItensAnalisadosSelecionados) {
        mailSender.send(fornecedorPreposto.getEmail(), "DEVOLVIDO PARA CORREÇÃO", geraEmailDevolverCorrecao(fornecedorPreposto, listaItensAnalisadosSelecionados), Boolean.TRUE);
        mailSender.send(representanteTitular.getEmail(), "DEVOLVIDO PARA CORREÇÃO", geraEmailDevolverCorrecao(representanteTitular, listaItensAnalisadosSelecionados), Boolean.TRUE);
    }

    private String geraEmailDevolverCorrecao(Pessoa pessoa, List<ObjetoFornecimentoContrato> listaItensAnalisadosSelecionados) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado Senhor(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha).append(novaLinha);
        sb.append("Devolver para correção").append(novaLinha).append(novaLinha);
        sb.append("No contrato " + inicioNegrito + listaItensAnalisadosSelecionados.get(0).getOrdemFornecimento().getContrato().getNumeroContrato() + fimNegrito + " relacionada a política pública " + inicioNegrito
                + listaItensAnalisadosSelecionados.get(0).getOrdemFornecimento().getContrato().getPrograma().getNomePrograma() + fimNegrito + " deste orgão.").append(novaLinha);
        sb.append("Os itens listados a seguir não foram aceitos pelas seguintes razões.").append(novaLinha).append(novaLinha);
        sb.append("Itens Devolvidos:").append(novaLinha);

        sb.append("<table border='1'><tr><td><center>  ID  </center></td><td><center>  ITEM  </center></td><td><center>  Motivo  </center></td></tr>");
        for (ObjetoFornecimentoContrato obj : listaItensAnalisadosSelecionados) {
            sb.append("<tr><td><center>  " + obj.getId() + "  </center></td><td><center>  " + obj.getItem().getNomeBem() + "  </center></th><td><center> " + obj.getMotivo() + "  </center></td></tr>");
        }
        sb.append("</table>").append(novaLinha);

        sb.append("Solicitamos providenciar para cada um dos itens listados a sua correção ou substituição e realizar, no Sistema SIDE, todos os passos necessários à evidenciação do cumprimento da entrega destes itens.").append(novaLinha);
        sb.append("Cordialmente, ").append(novaLinha).append(novaLinha).append(novaLinha);
        sb.append(inicioNegrito + "Ministério da Justiça" + fimNegrito).append(novaLinha).append(novaLinha);

        sb.append(inicioNegrito + "Importante:" + fimNegrito).append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento").append(novaLinha);
        sb.append("de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que").append(novaLinha);
        sb.append("não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    private String geraMensagemAcessoSistema(Pessoa pessoa, Entidade entidade, String linkAplicacao) {
        String novaLinha = "<br/>";

        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        String link = inicioNegrito + "<a href=" + linkAplicacao + "?hash=" + pessoa.getUsuario().getHashEnvioTrocaSenha() + " target='_blank'>Definir sua senha para o SIDE</a>" + fimNegrito;

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("O seu cadastro da Entidade " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " foi efetuado com sucesso em nosso sistema.").append(novaLinha);
        sb.append("Seu login é:" + inicioNegrito + pessoa.getUsuario().getNumeroCpf() + fimNegrito).append(novaLinha).append(novaLinha);
        sb.append("Utilize o seguinte link para seu primeiro acesso:").append(novaLinha).append(novaLinha);
        sb.append(link).append(novaLinha).append(novaLinha);
        sb.append("Acesse o SIDE para cadastrar uma senha de acesso que o possibilitará de acessar nosso sistema e se inscrever em Programas que estejam disponibilizados.").append(novaLinha);
        sb.append("Importante:").append(novaLinha);
        sb.append("A senha de acesso ao cadastro é pessoal e intransferível e seu uso é restrito ao próprio Representante, não podendo cedê-la ou transferi-la a terceiros.").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    private String geraMensagemCadastroEntidade(Pessoa entidadeTitular, Entidade entidade, Pessoa entidadeRepresentante) {

        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + entidadeTitular.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("Informamos que o cadastro da Entidade " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " foi efetuado com sucesso em nosso sistema.").append(novaLinha);
        sb.append("O acesso ao SIDE - Sistema de Doações e Equipagem será efetuado pelo Representante " + inicioNegrito + entidadeRepresentante.getNomePessoa().toUpperCase() + fimNegrito + ".").append(novaLinha);

        return sb.toString();
    }

    private String geraMensagemResetSenhaSistema(Usuario usuario, String linkAplicacao) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        String link = inicioNegrito + "<a href=" + linkAplicacao + "?hash=" + usuario.getHashEnvioTrocaSenha() + " target='_blank'>Alterar sua senha para o SIDE</a>" + fimNegrito;
        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) usuário,").append(novaLinha);
        sb.append("A sua solicitação de reset de senha foi efetuada com sucesso em nosso sistema.").append(novaLinha);
        sb.append("Seu login é:" + inicioNegrito + usuario.getNumeroCpf() + fimNegrito).append(novaLinha).append(novaLinha);
        sb.append("Utilize o seguinte link para alterar sua senha:").append(novaLinha).append(novaLinha);
        sb.append(link).append(novaLinha).append(novaLinha);
        sb.append("Acesse o SIDE para cadastrar uma senha de acesso que o possibilitará de acessar nosso sistema e se inscrever em Programas que estejam disponibilizados.").append(novaLinha);
        sb.append("Importante:").append(novaLinha);
        sb.append("A senha de acesso ao cadastro é pessoal e intransferível e seu uso é restrito ao próprio Representante, não podendo cedê-la ou transferi-la a terceiros.").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);

        return sb.toString();
    }

    private String geraMensagemLista(String nomeRepresentante, String nomePrograma, String dataInicioRecurso, String dataFimRecurso, EnumTipoLista tipoLista, EnumSelecao selecao, String dataInicioLocalEntrega, String dataFimLocalEntrega) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + nomeRepresentante + ",").append(novaLinha).append(novaLinha);
        sb.append("Informamos que a Lista " + tipoLista.getDescricao() + " de " + selecao.getDescricao() + " para o programa " + nomePrograma + " já está disponível.").append(novaLinha);
        sb.append("Para visualizar a lista acesse em nosso sistema o referido programa.").append(novaLinha).append(novaLinha).append(novaLinha);
        if (EnumTipoLista.PRELIMINAR.equals(tipoLista)) {
            sb.append("O período para recorrer ao resultado desta fase é de " + inicioNegrito + dataInicioRecurso + fimNegrito + " a " + inicioNegrito + dataFimRecurso + fimNegrito + ".").append(novaLinha).append(novaLinha).append(novaLinha);
        }

        if (EnumSelecao.CLASSIFICACAO_PROSPOSTA.equals(selecao) && EnumTipoLista.DEFINITIVA.equals(tipoLista)) {
            sb.append("Para os classificados o período para indicar os locais de entrega dos itens é de " + inicioNegrito + dataInicioLocalEntrega + fimNegrito + " a " + inicioNegrito + dataFimLocalEntrega + fimNegrito
                    + ". Para realizar a indicação, acessar a inscrição do programa na aba \"Endereços para Entrega\".").append(novaLinha).append(novaLinha).append(novaLinha);
        }

        sb.append(inicioNegrito + "Importante:" + fimNegrito).append(novaLinha).append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    // Metodos relacionados a ordem de fornecimento
    private String geraMensagemGeracaoOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimento, String numeroSei) {
        String novaLinha = "<br/>";

        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + ordemFornecimento.getContrato().getPreposto().getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("A ordem de fornecimento de número " + inicioNegrito + numeroSei + fimNegrito + " relacionada ao contrato " + inicioNegrito + ordemFornecimento.getContrato().getNumeroContrato() + fimNegrito + " foi gerada.").append(novaLinha);
        sb.append("Para visualiza-la acesse o sistema SIDE (Sistema de Doações e Equipagem) e vá em Contrato .").append(novaLinha).append(novaLinha);
        sb.append("Importante:").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    private String geraMensagemCancelamentoOF(OrdemFornecimentoContrato ordemFornecimentoContrato, HistoricoComunicacaoGeracaoOrdemFornecimentoContrato historico) {

        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + ordemFornecimentoContrato.getContrato().getPreposto().getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("Informamos que a ordem de fornecimento " + inicioNegrito + historico.getNumeroDocumentoSei() + fimNegrito + " relacionada ao contrato " + inicioNegrito + ordemFornecimentoContrato.getContrato().getNumeroContrato() + fimNegrito + " foi cancelada.").append(novaLinha)
                .append(novaLinha);
        sb.append("Segue o motivo do cancelamento:").append(novaLinha);
        sb.append(historico.getMotivoCancelamento()).append(novaLinha).append(novaLinha);
        sb.append("Em caso de dúvidas entre em contato com o Órgão responsável para mais informações.");

        sb.append(novaLinha).append("Importante:").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);

        return sb.toString();
    }

    // Enviada no cadastro externo do beneficiário
    // ENVIADO PARA O TITULAR
    private String geraMensagemCadastroExternoEntidade(Pessoa entidadeTitular, Entidade entidade, List<PessoaEntidade> listaPessoas) {

        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + entidadeTitular.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("Informamos que a solicitação de cadastro da Entidade " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " foi efetuada com sucesso em nosso sistema.").append(novaLinha).append(novaLinha);
        sb.append("Em caso de aprovação os seguintes representantes terão acesso ao sistema SIDE - Sistema de Doações e Equipagem.").append(novaLinha);

        for (PessoaEntidade ent : listaPessoas) {
            if (ent.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE || ent.getPessoa().getPossuiFuncaoDeRepresentante()) {
                sb.append(inicioNegrito + ent.getPessoa().getNomePessoa().toUpperCase() + fimNegrito).append(novaLinha);
            }
        }

        sb.append(novaLinha).append("Importante:").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);

        return sb.toString();
    }

    // ENVIADO PARA O REPRESENTANTE
    private String geraMensagemCadastroExterno(Pessoa pessoa, Entidade entidade) {
        String novaLinha = "<br/>";

        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("A sua solicitação de cadastro pela Entidade " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " foi efetuada com sucesso em nosso sistema.").append(novaLinha);
        sb.append("O prazo para analise é de 60 dias, em caso de aprovação seus dados para o acesso serão enviados para este email.").append(novaLinha).append(novaLinha);
        sb.append("Importante:").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    //Enviada no Cadastro/Alteração do Fornecedor ENIVADO PARA O REPRESENTANTE LEGAL     
    private String geraMensagemRepresentante(Pessoa fornecedorRepresentante, Entidade entidade, List<PessoaEntidade> listaPessoas) {

        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + fornecedorRepresentante.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("Informamos que a solicitação de cadastro do Fornecedor " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " foi efetuada com sucesso em nosso sistema.").append(novaLinha).append(novaLinha);
        sb.append("Os seguintes prepostos terão acesso ao sistema SIDE - Sistema de Doações e Equipagem.").append(novaLinha);

        for (PessoaEntidade ent : listaPessoas) {
            if (ent.getPessoa().getTipoPessoa() == EnumTipoPessoa.PREPOSTO_FORNECEDOR || ent.getPessoa().getPossuiFuncaoDeRepresentante()) {
                sb.append(inicioNegrito + ent.getPessoa().getNomePessoa().toUpperCase() + fimNegrito).append(novaLinha);
            }
        }

        sb.append(novaLinha).append("Importante:").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);

        return sb.toString();
    }

    // Enviada no Cadastro/Alteração do Fornecedor ENVIADO PARA O PREPOSTO
    private String geraMensagemPreposto(Pessoa pessoa, Entidade entidade, String linkAplicacao) {
        String novaLinha = "<br/>";

        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        String link = inicioNegrito + "<a href=" + linkAplicacao + "?hash=" + pessoa.getUsuario().getHashEnvioTrocaSenha() + " target='_blank'>Definir sua senha para o SIDE</a>" + fimNegrito;

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("O seu cadastro do Fornecedor " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " foi efetuado com sucesso em nosso sistema.").append(novaLinha);
        sb.append("Seu login é:" + inicioNegrito + pessoa.getUsuario().getNumeroCpf() + fimNegrito).append(novaLinha).append(novaLinha);
        sb.append("Utilize o seguinte link para seu primeiro acesso:").append(novaLinha).append(novaLinha);
        sb.append(link).append(novaLinha).append(novaLinha);
        sb.append("Acesse o SIDE para cadastrar uma senha de acesso que o possibilitará de acessar nosso sistema.").append(novaLinha);
        sb.append("Importante:").append(novaLinha);
        sb.append("A senha de acesso ao cadastro é pessoal e intransferível e seu uso é restrito ao próprio Preposto, não podendo cedê-la ou transferi-la a terceiros.").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    // ENVIADO PARA OS REPRESENTANTES INFORMANDO QUE O PRAZO
    // para analise de 30 dias do cadastro externo terminou
    private String geraMensagemAvisoFimPrazoAnaliseCadastroExterno(Pessoa pessoa, Entidade entidade) {
        String novaLinha = "<br/>";

        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("O prazo de 60 dias para a análise de sua solicitação de cadastro pela Entidade " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " no sitema SIDE - Sistema de Doações e Equipagem - expirou, favor entrar "
                + "em contato com o Órgão responsável para mais informações.").append(novaLinha);
        sb.append(novaLinha).append("Importante:").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    // Enviada na aprovação ou reprovação do cadastro
    // ENVIADO PARA O TITULAR
    private String geraMensagemParaTitularAprovacaoCadastro(Pessoa entidadeTitular, Entidade entidade, List<PessoaEntidade> listaDePessoas) {

        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        StringBuffer sb = new StringBuffer();

        if (entidade.getValidacaoCadastro() == EnumValidacaoCadastro.VALIDADO) {
            sb.append("Prezado(a) " + inicioNegrito + entidadeTitular.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
            sb.append("Informamos que a solicitação de cadastro da Entidade " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " foi aprovada.").append(novaLinha).append(novaLinha);
            sb.append("Os seguintes representantes terão acesso ao sistema SIDE - Sistema de Doações e Equipagem.").append(novaLinha);

            for (PessoaEntidade ent : listaDePessoas) {
                if (ent.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE || ent.getPessoa().getPossuiFuncaoDeRepresentante()) {
                    sb.append(inicioNegrito + ent.getPessoa().getNomePessoa().toUpperCase() + fimNegrito).append(novaLinha);
                }
            }

        } else {
            sb.append("Prezado(a) " + inicioNegrito + entidadeTitular.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
            sb.append("Informamos que a solicitação de cadastro da Entidade " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " ao sistema SIDE - Sistema de Doações e Equipagem foi recusada.").append(novaLinha).append(novaLinha);
            sb.append("O motivo apresentado foi o seguinte:").append(novaLinha);
            sb.append(entidade.getMotivoValidacao()).append(novaLinha).append(novaLinha);
            sb.append("A partir da data do envio desta mensagem os representantes listados abaixo terão 5 dias para corrigir os pontos que foram motivo da recusa do cadastro.").append(novaLinha);

            for (PessoaEntidade ent : listaDePessoas) {
                if (ent.getPessoa().getTipoPessoa() == EnumTipoPessoa.REPRESENTANTE_ENTIDADE || ent.getPessoa().getPossuiFuncaoDeRepresentante()) {
                    sb.append(inicioNegrito + ent.getPessoa().getNomePessoa().toUpperCase() + fimNegrito).append(novaLinha);
                }
            }

            sb.append(novaLinha);
            sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        }

        return sb.toString();
    }

    // ENVIADO PARA O REPRESENTANTE
    private String geraMensagemParaRepresentanteAprovacaoCadastro(Pessoa pessoa, Entidade entidade, String linkAplicacao) {
        String novaLinha = "<br/>";

        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        String link = "";
        StringBuffer sb = new StringBuffer();

        if (entidade.getValidacaoCadastro() == EnumValidacaoCadastro.VALIDADO) {

            link = inicioNegrito + "<a href=" + linkAplicacao + "?hash=" + pessoa.getUsuario().getHashEnvioTrocaSenha() + " target='_blank'>Definir sua senha para o SIDE</a>" + fimNegrito;

            sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
            sb.append("O cadastro da Entidade " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " foi aprovado.").append(novaLinha);
            sb.append("Seu login é: " + inicioNegrito + pessoa.getUsuario().getNumeroCpf() + fimNegrito).append(novaLinha).append(novaLinha);
            sb.append("Utilize o seguinte link para seu primeiro acesso: ").append(novaLinha).append(novaLinha);
            sb.append(link).append(novaLinha).append(novaLinha);
            sb.append("Acesse o SIDE para cadastrar uma senha de acesso que o possibilitará de acessar nosso sistema e se inscrever em Programas que estejam disponibilizados.").append(novaLinha);
            sb.append("Importante:").append(novaLinha);
            sb.append("A senha de acesso ao cadastro é pessoal e intransferível e seu uso é restrito ao próprio Representante, não podendo cedê-la ou transferi-la a terceiros.").append(novaLinha);
            sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
            return sb.toString();
        } else {

            link = inicioNegrito + "<a href=" + linkAplicacao + "?hash=" + pessoa.getUsuario().getHashEnvioAlteraEntidade() + " target='_blank'>Clique neste link para acessar a página de cadastro do SIDE</a>" + fimNegrito;

            sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
            sb.append("Informamos que a solicitação de cadastro da Entidade " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " ao sistema SIDE - Sistema de Doações e Equipagem foi recusada.").append(novaLinha).append(novaLinha);
            sb.append("O motivo apresentado foi o seguinte:").append(novaLinha);
            sb.append(entidade.getMotivoValidacao()).append(novaLinha).append(novaLinha);
            sb.append("A partir da data do envio desta mensagem você terá 5 dias para corrigir os pontos que foram motivo da recusa do cadastro.").append(novaLinha);
            sb.append(link).append(novaLinha).append(novaLinha);

            sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);

            return sb.toString();
        }

    }

    // Cadastro para novo membro de comissão
    private String geraMensagemCadastroMembroDaComissao(Pessoa pessoa, Entidade entidade, String linkAplicacao) {
        String novaLinha = "<br/>";

        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";
        String link = inicioNegrito + "<a href=" + linkAplicacao + "?hash=" + pessoa.getUsuario().getHashEnvioTrocaSenha() + " target='_blank'>Definir sua senha para o SIDE</a>" + fimNegrito;

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("O seu cadastro como membro da Comissão de Recebimento da Entidade  " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " foi efetuado com sucesso em nosso sistema.").append(novaLinha);

        // Se a pessoa já for representante ou titular com função de representante
        // Então ele já tem acesso ao sistema, esta mensagem abaixo não irá aparecer
        if (!pessoa.getPossuiFuncaoDeRepresentante()) {
            sb.append("Seu login é:" + inicioNegrito + pessoa.getUsuario().getNumeroCpf() + fimNegrito).append(novaLinha).append(novaLinha);
            sb.append("Utilize o seguinte link para seu primeiro acesso:").append(novaLinha).append(novaLinha);
            sb.append(link).append(novaLinha).append(novaLinha);
        } else {
            sb.append("Utilize o seu login e senhas atuais para ter acesso as funcionalidades.").append(novaLinha).append(novaLinha);
        }

        sb.append("Importante:").append(novaLinha);
        sb.append("A senha de acesso ao cadastro é pessoal e intransferível e seu uso é restrito ao próprio Representante, não podendo cedê-la ou transferi-la a terceiros.").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    // Mensagem que informa alteração feita no email
    private String geraMensagemAlteracaoCadastro(Pessoa pessoa, Entidade entidade) {
        String novaLinha = "<br/>";

        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("Informamos que seu e-mail de cadastro na Entidade " + inicioNegrito + entidade.getNomeEntidade().toUpperCase() + fimNegrito + " foi alterado em nosso sistema.").append(novaLinha);
        sb.append("Importante:").append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    // Cadastro para nova comissão de recebimento
    private String geraMensagemComissaoRecebimento(Pessoa pessoa, String codNomePrograma) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha);
        sb.append("Você foi indicado como membro da comissão de recebimento do programa " + inicioNegrito + codNomePrograma.toUpperCase() + fimNegrito + ".").append(novaLinha).append(novaLinha);
        sb.append("Posteriormente receberá maiores informações referente ao registro dos itens recebidos. ").append(novaLinha).append(novaLinha);
        sb.append(inicioNegrito + "Importante:" + fimNegrito).append(novaLinha).append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento").append(novaLinha);
        sb.append("de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que").append(novaLinha);
        sb.append("não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    private String geraMensagemBeneficiariosTermoDeEntraga(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato, Pessoa pessoa, String itens) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha).append(novaLinha);

        sb.append("Informamos que os itens " + inicioNegrito + itens + fimNegrito + " provavelmente serão entregues no dia " + inicioNegrito + DataUtil.converteDataDeLocalDateParaString(notaRemessaOrdemFornecimentoContrato.getDataPrevistaEntrega(), "dd/MM/yyyy") + fimNegrito + ".")
                .append(novaLinha);
        sb.append("Seu código para confirmação de recebimento é " + inicioNegrito + notaRemessaOrdemFornecimentoContrato.getCodigoGerado() + fimNegrito + " , favor registrá-la no").append(novaLinha);
        sb.append("termo de entrega juntamente com sua assinatura, para confirmar o recebimento dos itens entregue").append(novaLinha);
        sb.append("pelo fornecedor.").append(novaLinha).append(novaLinha);

        sb.append(inicioNegrito + "Importante:" + fimNegrito).append(novaLinha).append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento").append(novaLinha);
        sb.append("de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que").append(novaLinha);
        sb.append("não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    private String geraMensagemRegistrarTermoDeEntraga(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato, Pessoa pessoa, String link) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + pessoa.getNomePessoa().toUpperCase() + fimNegrito + ",").append(novaLinha).append(novaLinha);

        sb.append("Informamos que o prazo para registrar o recebimento dos itens entregues no dia " + inicioNegrito + DataUtil.converteDataDeLocalDateParaString(notaRemessaOrdemFornecimentoContrato.getDataEfetivaEntrega(), "dd/MM/yyyy") + fimNegrito + " são de 10 dias a partir desta data.")
                .append(novaLinha);
        sb.append("Para efetuar este registro acesse a página \"Registrar Recebimento\" de sua inscrição pelo link " + inicioNegrito + "<a href=" + link + " target='_blank'>click aqui</a>" + fimNegrito + " .").append(novaLinha);
        sb.append("Para concluir esta etapa é preciso anexar o Relatório de Recebimento assinando. Este relatório").append(novaLinha);
        sb.append("poderá ser gerado após o cadastro de todos os itens recebidos.").append(novaLinha).append(novaLinha);

        sb.append(inicioNegrito + "Importante:" + fimNegrito).append(novaLinha).append(novaLinha);
        sb.append("Verifique regularmente a capacidade de sua caixa de mensagens, a fim de evitar o não recebimento").append(novaLinha);
        sb.append("de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que").append(novaLinha);
        sb.append("não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    // Chamada em DetalharTermoEntregaPage.java -
    // gerarGerarTermoRecebimentoDefinitivo()
    public void enviarEmailFornecedorInserirNotaFiscal(Pessoa fornecedorPreposto) {
        mailSender.send(fornecedorPreposto.getEmail(), "Inserir Nota Fiscal", this.geraEmailFornecedorInserirNotaFiscal(fornecedorPreposto), Boolean.TRUE);
    }

    private String geraEmailFornecedorInserirNotaFiscal(Pessoa fornecedorPreposto) {
        String novaLinha = "<br/>";
        String inicioNegrito = "<b>";
        String fimNegrito = "</b>";

        StringBuffer sb = new StringBuffer();
        sb.append("Prezado(a) " + inicioNegrito + fornecedorPreposto.getNomePessoa() + fimNegrito + ",").append(novaLinha).append(novaLinha);
        sb.append("Inserir a Nota Fiscal").append(novaLinha).append(novaLinha);

        sb.append(inicioNegrito + "Importante:" + fimNegrito).append(novaLinha);
        sb.append("Acesse o sistema e insira a nota fiscal.").append(novaLinha);
        sb.append("de mensagens encaminhadas por nosso sistema. Não nos responsabilizaremos por mensagens que").append(novaLinha);
        sb.append("não venham a ser entregues devido a fatores dessa natureza.").append(novaLinha);
        return sb.toString();
    }

    @SuppressWarnings("unused")
    private Pessoa pegarRepresentante(Entidade entidade) {
        for (PessoaEntidade pessoa : entidade.getPessoas()) {
            if (pessoa.getPessoa().isRepresentante() && pessoa.getPessoa().getStatusPessoa().equals(EnumStatusPessoa.ATIVO)) {
                return pessoa.getPessoa();
            }
        }
        return null;
    }

    private Pessoa pegarTitular(List<PessoaEntidade> pessoas) {
        for (PessoaEntidade pessoa : pessoas) {
            if (pessoa.getPessoa().isTitular() && pessoa.getPessoa().getStatusPessoa().equals(EnumStatusPessoa.ATIVO)) {
                return pessoa.getPessoa();
            }
        }
        return null;
    }

    private Pessoa pegarTitularExterno(List<PessoaEntidade> pessoas) {
        for (PessoaEntidade pessoa : pessoas) {
            if (pessoa.getPessoa().isTitular()) {
                return pessoa.getPessoa();
            }
        }
        return null;
    }

    private Pessoa pegarRepresentanteLegal(List<PessoaEntidade> pessoas) {
        for (PessoaEntidade pessoa : pessoas) {
            if (pessoa.getPessoa().isRepresentanteFornecedor()) {
                return pessoa.getPessoa();
            }
        }
        return null;
    }

}

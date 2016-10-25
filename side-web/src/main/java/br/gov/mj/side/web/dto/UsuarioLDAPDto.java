package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class UsuarioLDAPDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String login;
    private String primeiroNome;
    private String numeroCpf;
    private String nomeCompleto;
    private String localTrabalho;
    private String ramal;

    public UsuarioLDAPDto(String email, String login, String primeiroNome, String numeroCpf, String nomeCompleto, String localTrabalho, String ramal) {
        super();
        this.email = email;
        this.login = login;
        this.primeiroNome = primeiroNome;
        this.numeroCpf = numeroCpf;
        this.nomeCompleto = nomeCompleto;
        this.localTrabalho = localTrabalho;
        this.ramal = ramal;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPrimeiroNome() {
        return primeiroNome;
    }

    public String getNumeroCpf() {
        return numeroCpf;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getLocalTrabalho() {
        return localTrabalho;
    }

    public String getRamal() {
        return ramal;
    }

}

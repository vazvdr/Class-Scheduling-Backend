package com.class_project.backend_class.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class EmailService {

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    @Value("${BREVO_FROM_EMAIL}")
    private String fromEmail;

    @Value("${BREVO_FROM_NAME}")
    private String fromName;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    public void enviarEmailAgendamento(String para, String assunto, String conteudo) {
        try {
            Map<String, Object> body = new HashMap<>();
            Map<String, String> sender = new HashMap<>();
            Map<String, String> to = new HashMap<>();

            sender.put("email", fromEmail);
            sender.put("name", fromName);

            to.put("email", para);

            body.put("sender", sender);
            body.put("to", Collections.singletonList(to));
            body.put("subject", assunto);
            body.put("textContent", conteudo);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(BREVO_URL, request, String.class);
            System.out.println("✅ Email de agendamento enviado para: " + para);

        } catch (Exception e) {
            System.out.println("❌ Erro ao enviar email de agendamento: " + e.getMessage());
            throw new RuntimeException("Erro ao enviar email de agendamento", e);
        }
    }

    
    public void enviarEmailRecuperarSenha(String para, String assunto, String conteudo) {
        try {
            Map<String, Object> body = new HashMap<>();
            Map<String, String> sender = new HashMap<>();
            Map<String, String> to = new HashMap<>();

            sender.put("email", fromEmail);
            sender.put("name", fromName);

            to.put("email", para);

            body.put("sender", sender);
            body.put("to", Collections.singletonList(to));
            body.put("subject", assunto);
            body.put("textContent", conteudo);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(BREVO_URL, request, String.class);
            System.out.println("✅ Email de recuperação de senha enviado para: " + para);

        } catch (Exception e) {
            System.out.println("❌ Erro ao enviar email de recuperação de senha: " + e.getMessage());
            throw new RuntimeException("Erro ao enviar email de recuperação de senha", e);
        }
    }
}

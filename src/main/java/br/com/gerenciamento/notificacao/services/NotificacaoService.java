package br.com.gerenciamento.notificacao.services;

import br.com.gerenciamento.notificacao.dto.NotificaoDto;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

	@Autowired
	private Session session;

	@Value("${app.email.remetente}")
	private String remetente;

	@KafkaListener(
		topicPartitions = @TopicPartition(
			topic = "insumos",
			partitions = { "1" }),
			containerFactory = "emailKafkaListenerContainerFactory")
	public void notificacaoListener(NotificaoDto notificao) {
		enviarEmail(notificao);
	}
	
	private void enviarEmail(NotificaoDto notificao) {
		try {
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(remetente));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(notificao.destinatario()));
			message.setSubject(notificao.assunto());
			message.setText(notificao.mensagem());

			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Erro ao tentar enviar email " + e.getLocalizedMessage());
		}
	}
}
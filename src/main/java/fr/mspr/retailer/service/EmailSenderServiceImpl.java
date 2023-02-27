package fr.mspr.retailer.service;

import fr.mspr.retailer.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.util.annotation.Nullable;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Base64;

@Service
@AllArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailSenderServiceImpl.class);
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your registration");
            helper.setFrom("jojosender@yopmail.com");

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }

    @Override
    public String buildEmail(String name, String link, byte[] byteImage) {

        String qrImage = "";
        String logoImage = "";
        String token = link.split("=")[1].replace("/", "");
        if (byteImage != null) {
            String base64Image = Base64.getEncoder().encodeToString(byteImage);
            qrImage = String.format("<img style=\"width: 200px;\" src=\"data:image/png;base64,%s\" alt=\"qrcode\"/>\n", base64Image);
        }

        InputStream inputStream = null;
        inputStream = ClassLoader.class.getResourceAsStream("ptk_logo.png");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (inputStream != null) {
            try {
                outputStream.write(inputStream.readAllBytes());
                logoImage = String.format("<img src=\"data:image/png;base64,%s\" alt=\"Logo\" border=\"0\"  style=\"display: block; width: 100px; min-width: 50px;\">\n",
                        Base64.getEncoder().encodeToString(outputStream.toByteArray()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        String htmlTemplate = " \n" +
                "<body style=\"background-color: white; padding: 0px;;\"> \n" +
                "  <!-- start body -->\n" +
                "  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "\n" +
                "    <!-- start logo -->\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"white\">\n" +
                "    \n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "          <tr>\n" +
                "            <td align=\"center\" valign=\"top\" style=\"padding: 36px 24px;\">\n" +
                logoImage +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "    \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <!-- end logo -->\n" +
                "\n" +
                "    <!-- start hero -->\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"white\">\n" +
                " \n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">\n" +
                "              <h1 style=\"margin: 0; font-size: 24px; font-weight: 700; letter-spacing: -1px; line-height: 30px;\">Validez votre inscription pour accéder au service ! </h1>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                " \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <!-- end hero -->\n" +
                "\n" +
                "    <!-- start copy block -->\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"white\">\n" +
                " \n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "\n" +
                "          <!-- start copy -->\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
                "              <p style=\"margin: 0;\">Scannez le QRcode à l'aide de l'application mobile pour vous connecter. \n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- end copy -->\n" +
                "\n" +
                "          <tr>\n" +
                "            <td style=\"display: flex; justify-content: center; background: white;\">\n" +
                qrImage +
                "            </td>\n" +
                "          </tr> \n" +
                "\n" +
                "          <!-- start copy -->\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
                "              <p style=\"margin: 0;\">(Si l'image ne charge pas, vous pouvez copier ce jeton dans votre presse papier et remplir le champ adéquat dans l'application).</p>\n" +
                "              <p style=\"margin:20px 0px; background: rgb(235, 235, 235);\">" + token + "</p>\n" +
                "              <p style=\"margin: 0;\"><strong>ATTENTION : </strong> ne partagez sous aucun prétexte ce jeton.</p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- end copy -->\n" +
                "\n" +
                "          <!-- start copy -->\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px; border-bottom: 3px solid #d4dadf\">\n" +
                "              <p style=\"margin: 0;\">Cordialement,<br> L'équipe PayeTonKawa ! &#9749; </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- end copy -->\n" +
                "\n" +
                "        </table> \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <!-- end copy block -->\n" +
                "\n" +
                "    <!-- start footer -->\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"white\" style=\"padding: 24px;\">\n" +
                "        \n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                " \n" +
                "\n" +
                "  </table>\n" +
                "  <!-- end body -->\n" +
                "\n" +
                "</body> ";
        return htmlTemplate.replace("[QRCODEIMAGE]", qrImage).replace("[LOGOIMAGE]", logoImage);
    }
}

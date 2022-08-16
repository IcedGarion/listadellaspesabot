package it.garion.telegram.bot;

import it.garion.telegram.bot.functions.NgrokUrlFromLogFile;
import it.garion.telegram.bot.functions.TelegramBotFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBotRunner extends TelegramLongPollingBot {
    @Value("${ngrok.log-file-path}")
    private String ngrokLogFilePath;

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String from = update.getMessage().getFrom().getFirstName() + " " + (update.getMessage().getFrom().getLastName() == null ? "" : update.getMessage().getFrom().getLastName());

            if (update.getMessage().hasText()) {

                switch (update.getMessage().getText()) {
                    case "/url":
                        sendNgrokUrl(update);
                        break;
                    default:
                        sendError(update, "Non existent command: " + update.getMessage().getText());
                        break;
                }

                log.info("Received message: {}\nFrom: {}", update.getMessage().getText(), from);
            } else {
                log.info("Received non-text message: {}\nFrom: {}", update, from);
                sendError(update, "Only string commands are accepted");
            }
        }
    }

    private void sendNgrokUrl(Update update) {
        TelegramBotFunction ngrokUrlFromLogFile = new NgrokUrlFromLogFile(this.ngrokLogFilePath);
        String reply = ngrokUrlFromLogFile.exec(this.ngrokLogFilePath);

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(reply);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error in sending message: {}", e.getMessage());
        }

        log.info("Reply: {}", reply);
    }

    private void sendError(Update update, String errorMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(errorMessage);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error in sending message: {}", e.getMessage());
        }

        log.info("Reply: {}", errorMessage);
    }
}
package it.garion.telegram;

import it.garion.telegram.bot.TelegramBotRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@SpringBootApplication
public class TelegramApplication implements ApplicationRunner {

	@Autowired
	TelegramBotRunner bot;

	public static void main(String[] args) {
		SpringApplication.run(TelegramApplication.class, args).close();
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Starting bot...");
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(bot);
		log.info("Bot started");
	}
}

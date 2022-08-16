package it.garion.telegram.bot.functions;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class NgrokUrlFromLogFile implements TelegramBotFunction {
    private static final String urlFindRegex = "url=https://[\\d\\w]{4}-\\d{3}-\\d{3}-\\d{3}-\\d{3}.eu.ngrok.io";
    private BufferedReader reader;
    private String ngrokLogFilePath;

    public NgrokUrlFromLogFile(String filePath) {
        this.ngrokLogFilePath = filePath;

        try {
            this.reader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            log.error("Cannot find ngrok log file: {}", filePath);
            throw new RuntimeException(e);
        }
    }

    public String exec(String message) {
        String line, url = "Error: url not found in: " + this.ngrokLogFilePath;

        // read file
        try {
            while((line = this.reader.readLine()) != null) {
                // apply regex
                Matcher m = Pattern.compile(urlFindRegex).matcher(line);
                if(m.find()) {
                    url = m.group(0).substring(4);
                }
            }
        } catch (IOException e) {
            log.error("Cannot read from ngrok log file: {}", this.ngrokLogFilePath);
            throw new RuntimeException(e);
        }

        return url;
    }
}
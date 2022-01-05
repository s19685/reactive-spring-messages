package pl.szer.szerowniamessages.Models;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
public class ThreadDetails {

    private final boolean isAboutAd;
    private final String receiver;
    private final Long val;

    public ThreadDetails(String threadDetails) {
        if (!threadDetails.contains("A") && !threadDetails.contains("T")) throw new BadRequest();

        String[] split = threadDetails.split("A|T");

        this.isAboutAd = threadDetails.contains("A");
        this.receiver = split[0];
        this.val = Long.valueOf(split[1]);
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "cant process your request")
    private static class BadRequest extends RuntimeException {
    }
}

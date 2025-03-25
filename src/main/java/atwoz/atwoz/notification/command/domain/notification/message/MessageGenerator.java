package atwoz.atwoz.notification.command.domain.notification.message;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class MessageGenerator {

    public String createTitle(MessageTemplate template) {
        return template.getTitle();
    }

    public String createContent(MessageTemplate template) {
        return template.getContent();
    }
}

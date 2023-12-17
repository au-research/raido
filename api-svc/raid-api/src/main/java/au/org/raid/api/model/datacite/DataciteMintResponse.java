package au.org.raid.api.model.datacite;

import au.org.raid.api.util.ObjectUtil;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class DataciteMintResponse {

    public String type;
    public Identifier identifier;
    public LocalDateTime timestamp;
    public Message message;

    @Override
    public String toString() {
        return ObjectUtil.jsonToString(this);
    }

    @Setter
    public static class Identifier {
        public String handle;
        public Property property;

        public static class Property {
            public Integer index;
            public String type;
            public String value;
        }
    }

    public static class Message {
        public String type;
        @JacksonXmlText
        public String value;

        @Override
        public String toString() {
            return ObjectUtil.jsonToString(this);
        }
    }

}

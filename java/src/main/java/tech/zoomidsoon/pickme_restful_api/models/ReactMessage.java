package tech.zoomidsoon.pickme_restful_api.models;

import java.util.Objects;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReactMessage extends Entity {

    private Integer messageId;
    private String react;

    @Override
    public boolean isEmpty() {
        return this.react == null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ReactMessage)) {
            return false;
        }
        return ((ReactMessage) o).messageId==this.messageId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, react);
    }
}

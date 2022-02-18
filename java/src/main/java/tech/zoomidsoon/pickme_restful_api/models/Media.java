package tech.zoomidsoon.pickme_restful_api.models;
import java.util.Objects;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class Media extends Entity{

    private String mediaName;
    private Integer userId;
    private String mediaType;

    @Override
    public boolean isEmpty(){
        return this.mediaName == null;
    }
}

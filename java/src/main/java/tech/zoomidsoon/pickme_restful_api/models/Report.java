package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Report extends Entity {

    private Long reportId;
    private Integer reporter;
    private Integer reported;
    private Long time;
    private String message;
    private Boolean done;

    @Override
    public boolean isEmpty() {

        if (reportId == null)
            return true;
        return false;
    }

}

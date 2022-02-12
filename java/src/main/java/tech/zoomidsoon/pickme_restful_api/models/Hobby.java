package tech.zoomidsoon.pickme_restful_api.models;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Hobby extends Entity {
    String hobbyName;
    String decription;
    @Override
	public boolean isEmpty() {
		return this.hobbyName == null;
	}
}

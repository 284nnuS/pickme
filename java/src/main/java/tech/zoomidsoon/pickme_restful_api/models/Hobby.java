package tech.zoomidsoon.pickme_restful_api.models;

import java.util.Objects;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Hobby extends Entity {
	private String hobbyName;
	private String description;

	@Override
	public boolean isEmpty() {
		return this.hobbyName == null;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Hobby)) {
			return false;
		}
		return ((Hobby) o).hobbyName.equals(this.hobbyName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hobbyName, description);
	}
}

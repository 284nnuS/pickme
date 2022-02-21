package tech.zoomidsoon.pickme_restful_api.helpers;

import lombok.*;

@AllArgsConstructor
@Getter
public class Pair<T1, T2> {
	private T1 one;
	private T2 two;
}
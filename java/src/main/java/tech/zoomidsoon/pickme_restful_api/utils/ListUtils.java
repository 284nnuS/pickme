package tech.zoomidsoon.pickme_restful_api.utils;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {
	public static <T> boolean equalList(List<T> listone, List<T> listtwo) {
		return listone != null && listtwo != null && listone.size() == listtwo.size() && listone.containsAll(listtwo)
				&& listtwo.containsAll(listone);
	}

	public static <T> T getLastItem(List<T> list) {
		if (list == null || list.isEmpty())
			return null;
		return list.get(list.size() - 1);
	}

	public static <T> void diffList(List<T> oldList, List<T> newList, List<T> add, List<T> remove, List<T> merge) {
		if (oldList == null)
			throw new IllegalArgumentException("Old list cannot be null");

		if (newList != null && !newList.isEmpty())
			merge.addAll(newList);
		else if (!oldList.isEmpty())
			merge.addAll(oldList);

		if (newList == null)
			return;

		if (oldList.isEmpty()) {
			if (newList != null && !newList.isEmpty())
				add.addAll(newList);
			return;
		}

		if (newList != null && newList.isEmpty()) {
			if (!oldList.isEmpty())
				remove.addAll(oldList);
			return;
		}

		add.addAll(newList.stream().filter(el -> !oldList.contains(el)).collect(Collectors.toList()));
		remove.addAll(oldList.stream().filter(el -> !newList.contains(el)).collect(Collectors.toList()));
	}
}
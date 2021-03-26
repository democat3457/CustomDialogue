package democat.customdialogue.util;

import java.util.function.Predicate;

public class PredicateHelper {
	public static <T> Predicate<T> always(boolean toSet) {
		return new Predicate<T>() {
			@Override
			public boolean test(T t) {
				return toSet;
			}
		};
	}
}

package tech.criasystem.specs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import tech.criasystem.dto.filter.PostFilter;
import tech.criasystem.model.Post;

public class PostSpecs extends BaseSpecs {

	public static Specification<Post> specByFilter(Optional<PostFilter> filter) {
		return filter.isEmpty() ? null : (root, query, builder) -> {
			Collection<Predicate> predicates = new ArrayList<>();

			predicates.add(equal(builder, root.get("id"), filter.map(PostFilter::getId)));
			predicates.add(contains(builder, root.get("title"), filter.map(PostFilter::getTitle)));
			predicates.add(contains(builder, root.get("body"), filter.map(PostFilter::getBody)));

			Expression<String> allCols = concatAll(builder, root.get("id"), root.get("title"),
					root.get("body"));
			predicates.add(contains(builder, allCols, filter.map(PostFilter::getAny)));
			return toAndArray(builder, predicates);
		};
	}
}

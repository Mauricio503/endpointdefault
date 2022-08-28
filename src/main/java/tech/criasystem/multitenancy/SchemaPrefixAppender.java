package tech.criasystem.multitenancy;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class SchemaPrefixAppender {

	private static final Pattern patternCreateAlterTable = Pattern.compile("table (.*?) ");
	private static final Pattern patternDropTable = Pattern.compile("drop table (.*?)$");
	private static final Pattern patternFK = Pattern.compile("references (.*?)$");
	private static final Pattern patternCreateSequence = Pattern.compile("sequence (.*?)$");
	private static final Pattern[] availablePatterns = new Pattern[] { patternCreateAlterTable, patternCreateSequence, patternDropTable };
	private static final String point = ".";
	private final StringBuilder schemaPrefixBuilder;
	private final String schemaName;

	public SchemaPrefixAppender(String schemaPrefix) {
		super();
		this.schemaName = schemaPrefix;

		if (!StringUtils.isEmpty(schemaName)) {
			this.schemaPrefixBuilder = new StringBuilder(schemaPrefix).append(point);
		} else {
			this.schemaPrefixBuilder = null;
		}
	}

	public String addPrefix(final String sqlOriginal) {
		String sqlPrefix = sqlOriginal;
		if (!StringUtils.isEmpty(schemaName)) {
			for (Pattern pattern : availablePatterns) {
				sqlPrefix = applyPrefixByPattern(pattern, sqlOriginal);

				if (sqlPrefix != sqlOriginal) {// aqui indica se houve mudanças no processo, como a adição do prefixo.
					return sqlPrefix;
				}
			}
		}

		return sqlPrefix;
	}

	private String applyPrefixByPattern(Pattern pattern, final String sqlOriginal) {
		StringBuilder sqlPrefix = new StringBuilder(sqlOriginal);
		Matcher matcher = pattern.matcher(sqlOriginal);
		if (matcher.find()) {
			String match = matcher.group(1);
			if (match.startsWith(schemaPrefixBuilder.toString())) {
				return sqlOriginal;
			}

			int index = sqlOriginal.indexOf(match);
			sqlPrefix.insert(index, schemaPrefixBuilder.toString());

			if (pattern == patternCreateAlterTable) {
				Matcher matchFK = patternFK.matcher(sqlPrefix.toString());
				if (matchFK.find()) {
					String references = matchFK.group(1);
					int indexFK = sqlPrefix.toString().indexOf(references);
					sqlPrefix.insert(indexFK, schemaPrefixBuilder.toString());
				}
			}

			return sqlPrefix.toString();
		}
		return sqlOriginal;
	}
}

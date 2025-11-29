package deepple.deepple.auth.presentation.filter;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

public class PathMatcherHelper {

    private final List<PathPattern> excludePatterns;

    public PathMatcherHelper(List<String> excludePatterns) {
        PathPatternParser parser = new PathPatternParser();

        this.excludePatterns = excludePatterns.stream()
            .map(parser::parse)
            .toList();
    }

    public boolean isExcluded(String uri) {
        PathContainer path = PathContainer.parsePath(uri);

        return excludePatterns.stream()
            .anyMatch(pattern -> pattern.matches(path));
    }
}

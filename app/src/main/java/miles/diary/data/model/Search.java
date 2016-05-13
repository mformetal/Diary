package miles.diary.data.model;

import io.realm.Case;
import io.realm.Sort;

/**
 * Created by mbpeele on 5/8/16.
 */
public class Search {

    public String constraint;
    public final Case casing;
    public final boolean useOr;
    public final String[] fieldNames;
    public final Sorter sorter;

    private Search(SearchBuilder searchBuilder) {
        constraint = searchBuilder.constraint;
        casing = searchBuilder.casing;
        useOr = searchBuilder.useOr;
        fieldNames = searchBuilder.fieldNames;
        sorter = new Sorter(searchBuilder.sortKeys, searchBuilder.sortOrders);
    }

    public Throwable validate() {
        if (fieldNames.length == 0) {
            return new IllegalAccessException("Search must have fieldNames.");
        }

        return null;
    }

    public static SearchBuilder builder() {
        return new SearchBuilder();
    }

    public final static class SearchBuilder {

        private String constraint = "";
        private Case casing = Case.INSENSITIVE;
        private boolean useOr = true;
        private String[] fieldNames = new String[0];
        private String[] sortKeys = new String[0];
        private Sort[] sortOrders = new Sort[0];

        public SearchBuilder setConstraint(String constraint) {
            this.constraint = constraint;
            return this;
        }

        public SearchBuilder setCasing(Case casing) {
            this.casing = casing;
            return this;
        }

        public SearchBuilder setUseOr(boolean usOr) {
            this.useOr = usOr;
            return this;
        }

        public SearchBuilder setFieldNames(String... fieldNames) {
            this.fieldNames = fieldNames;
            return this;
        }

        public SearchBuilder setSortKeys(String... sortKeys) {
            this.sortKeys = sortKeys;
            return this;
        }

        public SearchBuilder setSortOrders(Sort... sortOrders) {
            this.sortOrders = sortOrders;
            return this;
        }

        public Search createRealmSearch() {
            return new Search(this);
        }
    }
}

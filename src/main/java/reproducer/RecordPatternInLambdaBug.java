package reproducer;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Reproduces JDTLS NPE: "Cannot read field resolvedType because expectedTypeRef is null"
 * in IGenerateTypeCheck.generateTypeCheck when using record patterns
 * inside switch expressions inside lambdas.
 */
public class RecordPatternInLambdaBug {

    public <Item, Res> Predicate<Item> filter(Function<Item, Result<Res>> validator) {
        return item -> {
            var validation = validator.apply(item);
            return switch (validation) {
                case Result.Valid<?> _ -> true;
                case Result.ValidWarning<?>(_, var warning) -> {
                    System.out.println(warning);
                    yield true;
                }
                case Result.InvalidWarning<?>(var warning) -> {
                    System.out.println(warning);
                    yield false;
                }
                case Result.InvalidError<?>(var error) -> {
                    System.out.println(error);
                    yield false;
                }
            };
        };
    }

    public <Item, Res> Function<Item, Stream<Res>> filterMap(Function<Item, Result<Res>> validator) {
        return item -> {
            var validation = validator.apply(item);
            return switch (validation) {
                case Result.Valid<Res>(var success) -> Stream.of(success);
                case Result.ValidWarning<Res>(var value, var warning) -> {
                    System.out.println(warning);
                    yield Stream.of(value);
                }
                case Result.InvalidWarning<?>(var warning) -> {
                    System.out.println(warning);
                    yield Stream.empty();
                }
                case Result.InvalidError<Res>(var error) -> {
                    System.out.println(error);
                    yield Stream.empty();
                }
            };
        };
    }
}

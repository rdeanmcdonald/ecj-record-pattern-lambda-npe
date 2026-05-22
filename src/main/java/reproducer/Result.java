package reproducer;

public sealed interface Result<T> {
    record Valid<T>(T value) implements Result<T> {}
    record ValidWarning<T>(T value, String warning) implements Result<T> {}
    record InvalidWarning<T>(String warning) implements Result<T> {}
    record InvalidError<T>(String error) implements Result<T> {}
}

import com.memoizrlabs.functions.Action0;
import com.memoizrlabs.functions.Action1;
import com.memoizrlabs.functions.Func0;
import com.memoizrlabs.functions.Func1;

public abstract class Try<T> {

    public static <T> Try<T> given(Func0<T> func) {
        try {
            return new Success<>(func.call());
        } catch (Throwable throwable) {
            return new Failure<>(throwable);
        }
    }

    public abstract T get();

    public abstract <K> Try<K> map(Func1<T, K> func);

    public abstract boolean isSuccess();

    public boolean isFailure() {
        return !isSuccess();
    }

    public abstract T getOrElse(T alternative);

    public abstract Try<T> doIfSuccess(Action1<T> action);

    public abstract Try<T> doIfFailed(Action0 action);

    private static final class Failure<T> extends Try<T> {

        private Throwable throwable;

        Failure(Throwable throwable) {
            this.throwable = throwable;
        }

        @Override
        public T get() {
            throw new RuntimeException(throwable);
        }

        @Override
        public <K> Try<K> map(Func1<T, K> func) {
            return new Failure<>(throwable);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T getOrElse(T alternative) {
            return alternative;
        }

        @Override
        public Try<T> doIfSuccess(Action1<T> action) {
            return this;
        }

        @Override
        public Try<T> doIfFailed(Action0 action) {
            action.call();
            return this;
        }
    }

    private static final class Success<T> extends Try<T> {

        private final T content;

        Success(T content) {
            this.content = content;
        }

        @Override
        public T get() {
            return content;
        }

        @Override
        public <K> Try<K> map(final Func1<T, K> func) {
            return Try.given(new Func0<K>() {
                @Override
                public K call() {
                    return func.call(content);
                }
            });
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T getOrElse(T alternative) {
            return content;
        }

        @Override
        public Try<T> doIfSuccess(Action1<T> action) {
            action.call(content);
            return this;
        }

        @Override
        public Try<T> doIfFailed(Action0 action) {
            return this;
        }
    }
}

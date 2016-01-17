import com.memoizrlabs.functions.Action0;
import com.memoizrlabs.functions.Action1;
import com.memoizrlabs.functions.Func0;
import com.memoizrlabs.functions.Func1;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class TryTest {

    private final Func0<String> successfulFunc = new Func0<String>() {
        @Override
        public String call() {
            return "result";
        }
    };

    private Func0<String> failedFunc = new Func0<String>() {
        @Override
        public String call() {
            throw new RuntimeException("failedFunc");
        }
    };

    @Test
    public void isFailure_whenFunctionThrowsError_then_returnsTrue() {
        assertTrue(getFailedTry().isFailure());
    }

    private Try<String> getFailedTry() {
        return Try.given(failedFunc);
    }

    @Test
    public void isFailure_whenFunctionSucceds_then_returnsFalse() {
        assertFalse(getSuccessfulTry().isFailure());
    }

    private Try<String> getSuccessfulTry() {
        return Try.given(successfulFunc);
    }

    @Test
    public void isSuccess_whenFunctionThrowsError_then_returnsFalse() {
        assertFalse(getFailedTry().isSuccess());
    }

    @Test
    public void isSuccess_whenFunctionSucceds_then_returnsTrue() {
        assertTrue(getSuccessfulTry().isSuccess());
    }

    @Test
    public void doIfSuccess_whenSuccess_executesFunction() {
        final AtomicReference<String> reference = new AtomicReference<>();

        final boolean isSuccess = Try.given(successfulFunc).doIfSuccess(new Action1<String>() {
            @Override
            public void call(String a) {
                reference.set(a);
            }
        }).isSuccess();

        assertEquals(reference.get(), successfulFunc.call());
        assertTrue(isSuccess);
    }

    @Test
    public void doIfSuccess_whenFailure_executesFunction() {
        final AtomicReference<String> reference = new AtomicReference<>();

        final boolean isSuccess = Try.given(failedFunc).doIfSuccess(new Action1<String>() {
            @Override
            public void call(String a) {
                reference.set(a);
            }
        }).isSuccess();

        assertEquals(reference.get(), null);
        assertFalse(isSuccess);
    }

    @Test
    public void doIfFailure_whenSuccess_executesFunction() {
        final AtomicReference<String> reference = new AtomicReference<>();

        final boolean isSuccess = Try.given(successfulFunc).doIfFailed(new Action0() {
            @Override
            public void call() {
                reference.set("failed");
            }
        }).isSuccess();

        assertEquals(reference.get(), null);
        assertTrue(isSuccess);
    }

    @Test
    public void doIfFailure_whenFailure_executesFunction() {
        final AtomicReference<String> reference = new AtomicReference<>();

        final boolean isSuccess = Try.given(failedFunc).doIfFailed(new Action0() {
            @Override
            public void call() {
                reference.set("failed");
            }
        }).isSuccess();

        assertEquals(reference.get(), "failed");
        assertFalse(isSuccess);
    }

    @Test(expected = RuntimeException.class)
    public void get_whenFunctionThrowsError_then_throwException() {
        getFailedTry().get();
    }

    @Test
    public void getOrElse_whenFunctionThrowsError_then_returnSpecifiedValue() {
        String alternative = "alternative";
        assertEquals(alternative, getFailedTry().getOrElse(alternative));
    }

    @Test
    public void getOrElse_whenFunctionSucceeds_then_returnValue() {
        String alternative = "alternative";
        assertEquals(successfulFunc.call(), getSuccessfulTry().getOrElse(alternative));
    }

    @Test
    public void get_whenFunctionSucceeds_then_returnSuccess() {
        assertEquals(successfulFunc.call(), getSuccessfulTry().get());
    }

    @Test
    public void map_whenFunctionsSucceed_then_mapToNewValue() {
        Func0<Integer> func = new Func0<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        };

        Try<Integer> test = Try.given(func);

        Try<Integer> mappedTest = test.map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer x) {
                return 2 * x;
            }
        });
        assertEquals(2, (int) mappedTest.get());
    }

    @Test
    public void map_whenFirstFunctionsFails_then_mapToFailed() {
        Func0<Integer> func = new Func0<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        };

        Try<Integer> test = Try.given(func);
        Try<Integer> mappedTest = test.map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer x) {
                return 2 * x;
            }
        });
        assertEquals(2, (int) mappedTest.get());
    }

}

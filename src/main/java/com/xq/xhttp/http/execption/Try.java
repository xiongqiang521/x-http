package com.xq.xhttp.http.execption;

import java.util.function.Function;

/**
 * 捕获 lambda 表达式中的异常
 * <p>
 * <p/>
 * Try.check(带异常的方法)
 * <code>
 * list.stream() <br>
 * .map(Try.check(this::doSomething)) <br>
 * .filter(Try::isSuccess) <br>
 * .map(Try::getResult) <br>
 * .forEach(System.out::println); <br>
 * </code>
 */
public class Try<T, R> {
    private final Exception ex;
    private final T input;
    private final R result;

    public static <T, R> Function<T, R> ignoreException(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Try.success(function.apply(t)).getResult();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        };
    }

    public static <T, R> Function<T, Try<T, R>> check(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Try.success(function.apply(t));
            } catch (Exception ex) {
                return Try.exception(ex, t);
            }
        };
    }

    public static <T, R> Try<T, R> success(R result) {
        return new Try<>(null, null, result);
    }

    public static <T, R> Try<T, R> exception(Exception exception, T input) {
        return new Try<>(exception, input, null);
    }

    private Try(Exception ex, T input, R result) {
        this.ex = ex;
        this.input = input;
        this.result = result;
    }

    public boolean isException() {
        return ex != null;
    }

    /**
     * 使用 Exception 判断是否执行成功，因为执行的结果可能返回为null
     *
     * @return
     */
    public boolean isSuccess() {
        return ex == null;
    }

    public Function<T, R> mapSuccess(Try<T, R> trTry) {
        return t -> {
            if (trTry.isSuccess()) {
                return trTry.getResult();
            }
            return null;
        };
    }

    public Exception getEx() {
        return ex;
    }

    public T getInput() {
        return input;
    }

    public R getResult() {
        return result;
    }
}

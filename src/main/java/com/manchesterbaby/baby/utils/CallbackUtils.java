package com.manchesterbaby.baby.utils;

/**
 * Utility class for common callback handling operations.
 */
public class CallbackUtils {

    /**
     * Executes the given callback if it is not {@code null}.
     * <p>
     * This utility method ensures that the provided {@link Runnable} is safely
     * executed only when it is non-null, preventing potential {@link NullPointerException}.
     *
     * @param callback the {@link Runnable} to execute; can be {@code null}.
     */
    public static void runCallback(Runnable callback) {
        if (callback != null) {
            callback.run();
        }
    }
}
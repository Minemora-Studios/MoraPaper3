package org.bukkit;

import com.google.common.collect.ImmutableMap;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This designates the warning state for a specific item.
 * <p>
 * When the server settings dictate 'default' warnings, warnings are printed
 * if the {@link #value()} is true.
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Warning {

    /**
     * This represents the states that server verbose for warnings may be.
     */
    public enum WarningState {

        /**
         * Indicates all warnings should be printed for deprecated items.
         */
        ON,
        /**
         * Indicates no warnings should be printed for deprecated items.
         */
        OFF,
        /**
         * Indicates each warning would default to the configured {@link
         * Warning} annotation, or always if annotation not found.
         */
        DEFAULT;

        private static final Map<String, WarningState> values = ImmutableMap.<String, WarningState>builder()
                .put("off", OFF)
                .put("false", OFF)
                .put("f", OFF)
                .put("no", OFF)
                .put("n", OFF)
                .put("on", ON)
                .put("true", ON)
                .put("t", ON)
                .put("yes", ON)
                .put("y", ON)
                .put("", DEFAULT)
                .put("d", DEFAULT)
                .put("default", DEFAULT)
                .build();

        /**
         * This method checks the provided warning should be printed for this
         * state
         *
         * @param warning The warning annotation added to a deprecated item
         * @return <ul>
         *     <li>ON is always True
         *     <li>OFF is always false
         *     <li>DEFAULT is false if and only if annotation is not null and
         *     specifies false for {@link Warning#value()}, true otherwise.
         *     </ul>
         */
        public boolean printFor(@Nullable Warning warning) {
            if (Boolean.getBoolean("paper.alwaysPrintWarningState")) return true; // Paper
            if (this == DEFAULT) {
                return warning == null || warning.value();
            }
            return this == ON;
        }

        /**
         * This method returns the corresponding warning state for the given
         * string value.
         *
         * @param value The string value to check
         * @return {@link #DEFAULT} if not found, or the respective
         *     WarningState
         */
        @NotNull
        public static WarningState value(@Nullable final String value) {
            if (value == null) {
                return DEFAULT;
            }
            WarningState state = values.get(value.toLowerCase(Locale.ROOT));
            if (state == null) {
                return DEFAULT;
            }
            return state;
        }
    }

    /**
     * This sets if the deprecation warnings when registering events gets
     * printed when the setting is in the default state.
     *
     * @return false normally, or true to encourage warning printout
     */
    boolean value() default false;

    /**
     * This can provide detailed information on why the event is deprecated.
     *
     * @return The reason an event is deprecated
     */
    String reason() default "";
}

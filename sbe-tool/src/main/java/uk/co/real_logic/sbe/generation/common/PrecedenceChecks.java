/*
 * Copyright 2013-2023 Real Logic Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe.generation.common;

import uk.co.real_logic.sbe.SbeTool;
import uk.co.real_logic.sbe.ir.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.IntStream;

import static uk.co.real_logic.sbe.ir.GenerationUtil.collectFields;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectGroups;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectVarData;

/**
 * A factory for creating a {@link FieldPrecedenceModel} for a message from its IR tokens.
 */
public final class PrecedenceChecks
{
    private static final Function<IntStream, IntStream> SELECT_LATEST_VERSION_ONLY = versions ->
    {
        final OptionalInt max = versions.max();
        return max.isPresent() ? IntStream.of(max.getAsInt()) : IntStream.empty();
    };

    private final Context context;

    private PrecedenceChecks(final Context context)
    {
        this.context = context;
    }

    /**
     * Creates a {@link FieldPrecedenceModel} for the given message tokens,
     * unless precedence checks are disabled, in which case {@code null} is returned.
     * <p>
     * Only the latest version of the message is considered when creating the model.
     * </p>
     *
     * @param msgTokens the tokens of the message.
     * @return a {@link FieldPrecedenceModel} for the given message tokens or {@code null} if precedence checks
     * are disabled.
     */
    public FieldPrecedenceModel createEncoderModel(final List<Token> msgTokens)
    {
        return createModel(msgTokens, SELECT_LATEST_VERSION_ONLY);
    }

    /**
     * Creates a {@link FieldPrecedenceModel} for the given message tokens,
     * unless precedence checks are disabled, in which case {@code null} is returned.
     * <p>
     * All versions of the message are considered when creating the model.
     * </p>
     *
     * @param msgTokens the tokens of the message.
     * @return a {@link FieldPrecedenceModel} for the given message tokens or {@code null} if precedence checks
     * are disabled.
     */
    public FieldPrecedenceModel createDecoderModel(final List<Token> msgTokens)
    {
        return createModel(msgTokens, Function.identity());
    }

    /**
     * Creates a {@link FieldPrecedenceModel} for the given message tokens,
     * unless precedence checks are disabled, in which case {@code null} is returned.
     * <p>
     * All versions of the message are considered when creating the model.
     * </p>
     *
     * @param msgTokens the tokens of the message.
     * @return a {@link FieldPrecedenceModel} for the given message tokens or {@code null} if precedence checks
     * are disabled.
     */
    public FieldPrecedenceModel createCodecModel(final List<Token> msgTokens)
    {
        return createModel(msgTokens, Function.identity());
    }

    /**
     * Returns the {@link Context} describing how precedence checks should be generated.
     *
     * @return the {@link Context} describing how precedence checks should be generated.
     */
    public Context context()
    {
        return context;
    }

    /**
     * Creates a new {@link PrecedenceChecks} instance.
     *
     * @param context the {@link Context} describing how precedence checks should be generated.
     * @return a new {@link PrecedenceChecks} instance.
     */
    public static PrecedenceChecks newInstance(final Context context)
    {
        return new PrecedenceChecks(context);
    }

    private FieldPrecedenceModel createModel(
        final List<Token> tokens,
        final Function<IntStream, IntStream> versionsSelector
    )
    {
        if (context.shouldGeneratePrecedenceChecks())
        {
            final Token msgToken = tokens.get(0);

            final List<Token> messageBody = tokens.subList(1, tokens.size() - 1);
            int i = 0;

            final List<Token> fields = new ArrayList<>();
            i = collectFields(messageBody, i, fields);

            final List<Token> groups = new ArrayList<>();
            i = collectGroups(messageBody, i, groups);

            final List<Token> varData = new ArrayList<>();
            collectVarData(messageBody, i, varData);

            return FieldPrecedenceModel.newInstance(msgToken, fields, groups, varData, versionsSelector);
        }

        return null;
    }

    static final class Configuration
    {
        private static final boolean GENERATE_PRECEDENCE_CHECKS_VALUE =
            Boolean.parseBoolean(System.getProperty(SbeTool.GENERATE_PRECEDENCE_CHECKS, "false"));

        private static final String PRECEDENCE_CHECKS_FLAG_NAME_VALUE =
            System.getProperty(SbeTool.PRECEDENCE_CHECKS_FLAG_NAME, "SBE_ENABLE_PRECEDENCE_CHECKS");

        private static final String PRECEDENCE_CHECKS_PROPERTY_NAME_VALUE =
            System.getProperty(SbeTool.JAVA_PRECEDENCE_CHECKS_PROPERTY_NAME, "sbe.enable.precedence.checks");

        private Configuration()
        {
        }

        static boolean shouldGeneratePrecedenceChecks()
        {
            return GENERATE_PRECEDENCE_CHECKS_VALUE;
        }

        static String precedenceChecksFlagName()
        {
            return PRECEDENCE_CHECKS_FLAG_NAME_VALUE;
        }

        static String precedenceChecksPropName()
        {
            return PRECEDENCE_CHECKS_PROPERTY_NAME_VALUE;
        }
    }

    /**
     * The context describing how precedence checks should be generated.
     */
    public static final class Context
    {
        private boolean shouldGeneratePrecedenceChecks = Configuration.shouldGeneratePrecedenceChecks();
        private String precedenceChecksFlagName = Configuration.precedenceChecksFlagName();
        private String precedenceChecksPropName = Configuration.precedenceChecksPropName();

        /**
         * Returns {@code true} if precedence checks should be generated; {@code false} otherwise.
         *
         * @return {@code true} if precedence checks should be generated; {@code false} otherwise.
         */
        public boolean shouldGeneratePrecedenceChecks()
        {
            return shouldGeneratePrecedenceChecks;
        }

        /**
         * Sets whether field precedence checks should be generated.
         *
         * @param shouldGeneratePrecedenceChecks {@code true} if precedence checks should be generated;
         *                                       {@code false} otherwise.
         * @return this {@link Context} instance.
         */
        public Context shouldGeneratePrecedenceChecks(final boolean shouldGeneratePrecedenceChecks)
        {
            this.shouldGeneratePrecedenceChecks = shouldGeneratePrecedenceChecks;
            return this;
        }

        /**
         * Returns the name of the flag that can be used to enable precedence checks at runtime.
         *
         * @return the name of the flag that can be used to enable precedence checks at runtime.
         */
        public String precedenceChecksFlagName()
        {
            return precedenceChecksFlagName;
        }

        /**
         * Sets the name of the flag that can be used to enable precedence checks at runtime.
         *
         * @param precedenceChecksFlagName the name of the flag that can be used to enable
         *                                 precedence checks at runtime.
         * @return this {@link Context} instance.
         */
        public Context precedenceChecksFlagName(final String precedenceChecksFlagName)
        {
            this.precedenceChecksFlagName = precedenceChecksFlagName;
            return this;
        }

        /**
         * Returns the name of the system property that can be used to enable precedence checks
         * at runtime.
         *
         * @return the name of the system property that can be used to enable precedence checks
         * at runtime.
         */
        public String precedenceChecksPropName()
        {
            return precedenceChecksPropName;
        }

        /**
         * Sets the name of the system property that can be used to enable precedence checks
         * at runtime.
         *
         * @param precedenceChecksPropName the name of the system property that can be used to
         *                                 enable precedence checks at runtime.
         * @return this {@link Context} instance.
         */
        public Context precedenceChecksPropName(final String precedenceChecksPropName)
        {
            this.precedenceChecksPropName = precedenceChecksPropName;
            return this;
        }
    }
}

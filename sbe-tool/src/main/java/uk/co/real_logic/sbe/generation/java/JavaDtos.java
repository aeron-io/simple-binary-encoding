/*
 * Copyright 2013-2024 Real Logic Limited.
 * Copyright (C) 2017 MarketFactory, Inc
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
package uk.co.real_logic.sbe.generation.java;

import uk.co.real_logic.sbe.generation.CodeGenerator;
import uk.co.real_logic.sbe.generation.TargetCodeGenerator;
import uk.co.real_logic.sbe.ir.Ir;

import static uk.co.real_logic.sbe.SbeTool.TYPES_PACKAGE_OVERRIDE;

/**
 * {@link CodeGenerator} factory for Java DTOs.
 */
public class JavaDtos implements TargetCodeGenerator
{
    /**
     * {@inheritDoc}
     */
    public CodeGenerator newInstance(final Ir ir, final String outputDir)
    {
        return new JavaDtoGenerator(
            ir,
            Boolean.getBoolean(TYPES_PACKAGE_OVERRIDE),
            new JavaOutputManager(outputDir, ir.applicableNamespace()));
    }
}

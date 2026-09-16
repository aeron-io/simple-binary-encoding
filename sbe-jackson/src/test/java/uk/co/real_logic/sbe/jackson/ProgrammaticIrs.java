/*
 * Copyright 2013-2025 Real Logic Limited.
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
package uk.co.real_logic.sbe.jackson;

import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.ir.Encoding;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.ir.Signal;
import uk.co.real_logic.sbe.ir.Token;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * Hand-built IRs for layouts the XML front end cannot express: composite members with their own
 * {@code sinceVersion}, per-member byte orders in headers and group dimensions, and uint32 header members.
 * Token shapes follow {@code IrGenerator}: a field is {@code BEGIN_FIELD, type tokens, END_FIELD}; a group is
 * {@code BEGIN_GROUP (size = blockLength), dimension composite, fields, END_GROUP}; var-data is
 * {@code BEGIN_VAR_DATA, composite(length, varData), END_VAR_DATA}.
 */
final class ProgrammaticIrs
{
    static final int SCHEMA_ID = 77;

    private static final Encoding NONE = new Encoding();

    private ProgrammaticIrs()
    {
    }

    /**
     * Schema version 1: message {@code Msg} (template 1, block length 8) with one composite field {@code comp}
     * whose member {@code a} exists since version 0 and member {@code b} since version 1.
     *
     * @return the IR.
     */
    static Ir versionedComposite()
    {
        final List<Token> msg = new ArrayList<>();
        msg.add(token(Signal.BEGIN_MESSAGE, "Msg", 1, 8, 0, 0, NONE));
        msg.add(token(Signal.BEGIN_FIELD, "comp", 1, 0, 0, 0, NONE));
        msg.add(token(Signal.BEGIN_COMPOSITE, "Comp", 0, 8, 0, 0, NONE));
        msg.add(token(Signal.ENCODING, "a", 0, 4, 0, 0, encoding(PrimitiveType.INT32, LITTLE_ENDIAN)));
        msg.add(token(Signal.ENCODING, "b", 0, 4, 4, 1, encoding(PrimitiveType.INT32, LITTLE_ENDIAN)));
        msg.add(token(Signal.END_COMPOSITE, "Comp", 0, 8, 0, 0, NONE));
        msg.add(token(Signal.END_FIELD, "comp", 1, 0, 0, 0, NONE));
        msg.add(token(Signal.END_MESSAGE, "Msg", 1, 8, 0, 0, NONE));

        return ir(1, header(PrimitiveType.UINT16, LITTLE_ENDIAN, LITTLE_ENDIAN, LITTLE_ENDIAN, LITTLE_ENDIAN), msg);
    }

    /**
     * Header members alternate little and big endian (blockLength LE, templateId BE, schemaId LE, version BE).
     * Message {@code Mixed} (template 1, block length 4): {@code x} int32 LE; group {@code g} with dimensions
     * blockLength uint16 LE and numInGroup uint16 BE holding {@code y} int16 BE; var-data {@code d} with a
     * uint16 BE length prefix and UTF-8 payload.
     *
     * @return the IR.
     */
    static Ir mixedByteOrders()
    {
        final List<Token> msg = new ArrayList<>();
        msg.add(token(Signal.BEGIN_MESSAGE, "Mixed", 1, 4, 0, 0, NONE));
        msg.add(token(Signal.BEGIN_FIELD, "x", 1, 0, 0, 0, NONE));
        msg.add(token(Signal.ENCODING, "int32", 0, 4, 0, 0, encoding(PrimitiveType.INT32, LITTLE_ENDIAN)));
        msg.add(token(Signal.END_FIELD, "x", 1, 0, 0, 0, NONE));

        msg.add(token(Signal.BEGIN_GROUP, "g", 2, 2, 0, 0, NONE));
        msg.add(token(Signal.BEGIN_COMPOSITE, "groupSizeEncoding", 0, 4, 0, 0, NONE));
        msg.add(token(Signal.ENCODING, "blockLength", 0, 2, 0, 0, encoding(PrimitiveType.UINT16, LITTLE_ENDIAN)));
        msg.add(token(Signal.ENCODING, "numInGroup", 0, 2, 2, 0, encoding(PrimitiveType.UINT16, BIG_ENDIAN)));
        msg.add(token(Signal.END_COMPOSITE, "groupSizeEncoding", 0, 4, 0, 0, NONE));
        msg.add(token(Signal.BEGIN_FIELD, "y", 3, 0, 0, 0, NONE));
        msg.add(token(Signal.ENCODING, "int16", 0, 2, 0, 0, encoding(PrimitiveType.INT16, BIG_ENDIAN)));
        msg.add(token(Signal.END_FIELD, "y", 3, 0, 0, 0, NONE));
        msg.add(token(Signal.END_GROUP, "g", 2, 2, 0, 0, NONE));

        msg.add(token(Signal.BEGIN_VAR_DATA, "d", 4, 0, 0, 0, NONE));
        msg.add(token(Signal.BEGIN_COMPOSITE, "varStringEncoding", 0, Token.VARIABLE_LENGTH, 0, 0, NONE));
        msg.add(token(Signal.ENCODING, "length", 0, 2, 0, 0, encoding(PrimitiveType.UINT16, BIG_ENDIAN)));
        msg.add(token(Signal.ENCODING, "varData", 0, Token.VARIABLE_LENGTH, 2, 0,
            new Encoding.Builder()
                .primitiveType(PrimitiveType.UINT8)
                .byteOrder(LITTLE_ENDIAN)
                .characterEncoding("UTF-8")
                .build()));
        msg.add(token(Signal.END_COMPOSITE, "varStringEncoding", 0, Token.VARIABLE_LENGTH, 0, 0, NONE));
        msg.add(token(Signal.END_VAR_DATA, "d", 4, 0, 0, 0, NONE));
        msg.add(token(Signal.END_MESSAGE, "Mixed", 1, 4, 0, 0, NONE));

        return ir(0, header(PrimitiveType.UINT16, LITTLE_ENDIAN, BIG_ENDIAN, LITTLE_ENDIAN, BIG_ENDIAN), msg);
    }

    /**
     * Header with four uint32 members (16 bytes) and message {@code M} (template 1) holding one uint8 {@code a}.
     *
     * @return the IR.
     */
    static Ir uint32Header()
    {
        final List<Token> msg = new ArrayList<>();
        msg.add(token(Signal.BEGIN_MESSAGE, "M", 1, 1, 0, 0, NONE));
        msg.add(token(Signal.BEGIN_FIELD, "a", 1, 0, 0, 0, NONE));
        msg.add(token(Signal.ENCODING, "uint8", 0, 1, 0, 0, encoding(PrimitiveType.UINT8, LITTLE_ENDIAN)));
        msg.add(token(Signal.END_FIELD, "a", 1, 0, 0, 0, NONE));
        msg.add(token(Signal.END_MESSAGE, "M", 1, 1, 0, 0, NONE));

        return ir(0, header(PrimitiveType.UINT32, LITTLE_ENDIAN, LITTLE_ENDIAN, LITTLE_ENDIAN, LITTLE_ENDIAN), msg);
    }

    private static List<Token> header(
        final PrimitiveType type,
        final ByteOrder blockLengthOrder,
        final ByteOrder templateIdOrder,
        final ByteOrder schemaIdOrder,
        final ByteOrder versionOrder)
    {
        final int size = type.size();
        final List<Token> tokens = new ArrayList<>();
        tokens.add(token(Signal.BEGIN_COMPOSITE, "messageHeader", 0, 4 * size, 0, 0, NONE));
        tokens.add(token(Signal.ENCODING, "blockLength", 0, size, 0, 0, encoding(type, blockLengthOrder)));
        tokens.add(token(Signal.ENCODING, "templateId", 0, size, size, 0, encoding(type, templateIdOrder)));
        tokens.add(token(Signal.ENCODING, "schemaId", 0, size, 2 * size, 0, encoding(type, schemaIdOrder)));
        tokens.add(token(Signal.ENCODING, "version", 0, size, 3 * size, 0, encoding(type, versionOrder)));
        tokens.add(token(Signal.END_COMPOSITE, "messageHeader", 0, 4 * size, 0, 0, NONE));

        return tokens;
    }

    private static Ir ir(final int version, final List<Token> header, final List<Token> message)
    {
        Ir.updateComponentTokenCounts(header);
        final Ir ir = new Ir("test", "test", SCHEMA_ID, version, "programmatic", "1.0", LITTLE_ENDIAN, header);
        ir.addMessage(message.get(0).id(), message);

        return ir;
    }

    private static Encoding encoding(final PrimitiveType type, final ByteOrder byteOrder)
    {
        return new Encoding.Builder().primitiveType(type).byteOrder(byteOrder).build();
    }

    private static Token token(
        final Signal signal,
        final String name,
        final int id,
        final int size,
        final int offset,
        final int version,
        final Encoding encoding)
    {
        return new Token.Builder()
            .signal(signal)
            .name(name)
            .id(id)
            .size(size)
            .offset(offset)
            .version(version)
            .encoding(encoding)
            .build();
    }
}

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

package uk.co.real_logic.sbe.generation.java;

import dto_test.KeywordsDto;
import dto_test.KeywordsEncoder;
import dto_test.MessageHeaderDecoder;
import dto_test.MessageHeaderEncoder;
import extension.*;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DtoTest
{
    @Test
    void shouldRoundTripCar1()
    {
        final ExpandableArrayBuffer inputBuffer = new ExpandableArrayBuffer();
        encodeCar(inputBuffer, 0);

        final CarDecoder decoder = new CarDecoder();
        decoder.wrap(inputBuffer, 0, CarDecoder.BLOCK_LENGTH, CarDecoder.SCHEMA_VERSION);
        final String decoderString = decoder.toString();

        final CarDto dto = new CarDto();
        CarDto.decodeWith(decoder, dto);

        final ExpandableArrayBuffer outputBuffer = new ExpandableArrayBuffer();
        final CarEncoder encoder = new CarEncoder();
        encoder.wrap(outputBuffer, 0);
        CarDto.encodeWith(encoder, dto);

        final String dtoString = dto.toString();

        assertThat(outputBuffer.byteArray(), equalTo(inputBuffer.byteArray()));
        assertThat(dtoString, equalTo(decoderString));
    }

    @Test
    void shouldRoundTripCar2()
    {
        final ExpandableArrayBuffer inputBuffer = new ExpandableArrayBuffer();
        final int inputLength = encodeCar(inputBuffer, 0);

        final CarDto dto = CarDto.decodeFrom(inputBuffer, 0, CarDecoder.BLOCK_LENGTH, CarDecoder.SCHEMA_VERSION);

        final ExpandableArrayBuffer outputBuffer = new ExpandableArrayBuffer();
        final int outputLength = CarDto.encodeWith(dto, outputBuffer, 0);

        assertThat(outputLength, equalTo(inputLength));
        assertThat(outputBuffer.byteArray(), equalTo(inputBuffer.byteArray()));
    }

    @Test
    void shouldRoundTripCustomCar1()
    {
        final ExpandableArrayBuffer inputBuffer = new ExpandableArrayBuffer();
        encodeCustomCar(inputBuffer, 0);

        final CustomCarDecoder decoder = new CustomCarDecoder();
        decoder.wrap(inputBuffer, 0, CustomCarDecoder.BLOCK_LENGTH, CustomCarDecoder.SCHEMA_VERSION);
        final String decoderString = decoder.toString();

        final CustomCarDto dto = new CustomCarDto();
        CustomCarDto.decodeWith(decoder, dto);

        final ExpandableArrayBuffer outputBuffer = new ExpandableArrayBuffer();
        final CustomCarEncoder encoder = new CustomCarEncoder();
        encoder.wrap(outputBuffer, 0);
        CustomCarDto.encodeWith(encoder, dto);

        final String dtoString = dto.toString();

        assertThat(outputBuffer.byteArray(), equalTo(inputBuffer.byteArray()));
        assertThat(dtoString, equalTo(decoderString));
    }

    @Test
    void shouldRoundTripCustomCar2()
    {
        final ExpandableArrayBuffer inputBuffer = new ExpandableArrayBuffer();
        final int inputLength = encodeCustomCar(inputBuffer, 0);

        final CustomCarDto dto = CustomCarDto.decodeFrom(inputBuffer, 0,
            CustomCarDecoder.BLOCK_LENGTH, CustomCarDecoder.SCHEMA_VERSION);

        final ExpandableArrayBuffer outputBuffer = new ExpandableArrayBuffer();
        final int outputLength = CustomCarDto.encodeWith(dto, outputBuffer, 0);

        assertThat(outputLength, equalTo(inputLength));
        assertThat(outputBuffer.byteArray(), equalTo(inputBuffer.byteArray()));
    }

    @Test
    void shouldReturnCarBytes()
    {
        final ExpandableArrayBuffer inputBuffer = new ExpandableArrayBuffer();
        encodeCar(inputBuffer, 0);

        final CarDecoder decoder = new CarDecoder();
        decoder.wrap(inputBuffer, 0, CarDecoder.BLOCK_LENGTH, CarDecoder.SCHEMA_VERSION);

        final CarDto dto = new CarDto();
        CarDto.decodeWith(decoder, dto);

        final byte[] bytes = CarDto.bytes(dto);
        assertEquals(251, bytes.length);

        final byte[] bytesWithHeader = CarDto.bytesWithHeader(dto);
        assertEquals(259, bytesWithHeader.length);
    }

    @Test
    void shouldReturnCustomCarBytes()
    {
        final ExpandableArrayBuffer inputBuffer = new ExpandableArrayBuffer();
        encodeCustomCar(inputBuffer, 0);

        final CustomCarDecoder decoder = new CustomCarDecoder();
        decoder.wrap(inputBuffer, 0, CustomCarDecoder.BLOCK_LENGTH, CustomCarDecoder.SCHEMA_VERSION);

        final CustomCarDto dto = new CustomCarDto();
        CustomCarDto.decodeWith(decoder, dto);

        final byte[] bytes = CustomCarDto.bytes(dto);
        assertEquals(235, bytes.length);

        final byte[] bytesWithHeader = CustomCarDto.bytesWithHeader(dto);
        assertEquals(247, bytesWithHeader.length);
    }

    @Test
    void shouldAcceptValueInRange()
    {
        final CarDto car = new CarDto();

        // UINT64 [0, 18446744073709551614] (implicit, implicit)
        car.serialNumber(0L);
        car.serialNumber(Long.MAX_VALUE);
        car.serialNumber(Long.parseUnsignedLong("18446744073709551614"));

        // UINT64 / Kilometers [0, 150000000] (implicit, explicit)
        car.mileage(0L);
        car.mileage(10L);
        car.mileage(149999999L);
        car.mileage(150000000L);

        // UINT64 / Cents  [10000000000000000000, 15000000000000000000] (explicit, explicit)
        car.price(Long.parseUnsignedLong("10000000000000000000"));
        car.price(Long.parseUnsignedLong("10000000000000000001"));
        car.price(Long.parseUnsignedLong("14999999999999999999"));
        car.price(Long.parseUnsignedLong("15000000000000000000"));
    }

    @Test
    void shouldThrowExceptionWhenValueOutsideOfRange()
    {
        final CarDto car = new CarDto();

        // UINT64 [0, 18446744073709551614] (implicit, implicit)
        assertThrows(IllegalArgumentException.class, () -> car.serialNumber(CarEncoder.serialNumberNullValue()));

        // UINT64 / Kilometers [0, 150000000] (implicit, explicit)
        assertThrows(IllegalArgumentException.class, () -> car.mileage(150000001L));
        assertThrows(IllegalArgumentException.class, () -> car.mileage(Long.parseUnsignedLong("18446744073709551614")));
        assertThrows(IllegalArgumentException.class, () -> car.mileage(Long.parseUnsignedLong("18446744073709551615")));

        // UINT64 / Cents  [10000000000000000000, 15000000000000000000] (explicit, explicit)
        assertThrows(IllegalArgumentException.class, () -> car.price(Long.parseUnsignedLong("9999999999999999999")));
        assertThrows(IllegalArgumentException.class, () -> car.price(Long.parseUnsignedLong("15000000000000000001")));
        assertThrows(IllegalArgumentException.class, () -> car.price(Long.parseUnsignedLong("18446744073709551614")));
        assertThrows(IllegalArgumentException.class, () -> car.price(Long.parseUnsignedLong("18446744073709551615")));
    }

    @Test
    void dtoWithKeywords()
    {
        final ExpandableArrayBuffer input = new ExpandableArrayBuffer();
        final KeywordsEncoder encoder = new KeywordsEncoder();
        encoder.wrapAndApplyHeader(input, 0, new MessageHeaderEncoder());

        final KeywordsDto dto = new KeywordsDto();
        dto.assert$((byte)42);
        dto.final$((byte)7);
        dto.abstract$((byte)-81);
        dto.false$((byte)0);
        dto.falsE((byte)1);
        dto.import$("char nine");
        dto.void$(new int[] {1, 2, 3});
        dto.continue$(dto_test.Model.B);

        final KeywordsDto.DataDto dto1 = new KeywordsDto.DataDto();
        dto1.this$((byte)90);
        final KeywordsDto.DataDto.SuperDto superDto = new KeywordsDto.DataDto.SuperDto();
        superDto.try$(0.5f);
        dto1.super$(List.of(superDto));
        final KeywordsDto.DataDto dto2 = new KeywordsDto.DataDto();
        dto2.this$((byte)100);
        dto.data(List.of(dto1, dto2));

        KeywordsDto.encodeWith(encoder, dto);

        final byte[] originalBytes = new byte[encoder.encodedLength()];
        input.getBytes(0, originalBytes);

        final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();
        messageHeaderDecoder.wrap(input, 0);

        final KeywordsDto otherDto = KeywordsDto.decodeFrom(
            input,
            MessageHeaderDecoder.ENCODED_LENGTH,
            messageHeaderDecoder.blockLength(),
            messageHeaderDecoder.version());
        final KeywordsEncoder encoder2 = new KeywordsEncoder();
        encoder2.wrapAndApplyHeader(input, 0, new MessageHeaderEncoder());
        KeywordsDto.encodeWith(encoder2, otherDto);

        final byte[] otherBytes = new byte[encoder2.encodedLength()];
        input.getBytes(0, otherBytes);

        assertArrayEquals(originalBytes, otherBytes);
    }

    private static int encodeCar(final MutableDirectBuffer buffer, final int offset)
    {
        final CarEncoder car = new CarEncoder();
        car.wrap(buffer, offset);
        car.serialNumber(1234);
        car.modelYear(2013);
        car.available(BooleanType.T);
        car.code(Model.A);
        car.vehicleCode("ABCDEF");

        for (int i = 0, size = CarEncoder.someNumbersLength(); i < size; i++)
        {
            car.someNumbers(i, i);
        }

        car.extras().cruiseControl(true).sportsPack(true);

        car.cupHolderCount((short)119);
        car.mileage(100_000L);
        car.price(Long.parseUnsignedLong("10000000000000000005"));

        car.engine().capacity(2000)
            .numCylinders((short)4)
            .manufacturerCode("ABC")
            .efficiency((byte)35)
            .boosterEnabled(BooleanType.T)
            .booster().boostType(BoostType.NITROUS)
            .horsePower((short)200);

        final CarEncoder.FuelFiguresEncoder fuelFigures = car.fuelFiguresCount(3);
        fuelFigures.next()
            .speed(30)
            .mpg(35.9f)
            .usageDescription("this is a description");

        fuelFigures.next()
            .speed(55)
            .mpg(49.0f)
            .usageDescription("this is a description");

        fuelFigures.next()
            .speed(75)
            .mpg(40.0f)
            .usageDescription("this is a description");

        final CarEncoder.PerformanceFiguresEncoder perfFigures = car.performanceFiguresCount(2);

        perfFigures.next()
            .octaneRating((short)95);

        CarEncoder.PerformanceFiguresEncoder.AccelerationEncoder acceleration = perfFigures.accelerationCount(3);

        acceleration.next()
            .mph(30)
            .seconds(4.0f);

        acceleration.next()
            .mph(60)
            .seconds(7.5f);

        acceleration.next()
            .mph(100)
            .seconds(12.2f);

        perfFigures.next()
            .octaneRating((short)99);

        acceleration = perfFigures.accelerationCount(3);

        acceleration.next()
            .mph(30)
            .seconds(3.8f);

        acceleration.next()
            .mph(60)
            .seconds(7.1f);

        acceleration.next()
            .mph(100)
            .seconds(11.8f);

        car.manufacturer("Ford");
        car.model("Fiesta");
        car.activationCode("1234");

        return car.limit();
    }

    private static int encodeCustomCar(final MutableDirectBuffer buffer, final int offset)
    {
        final CustomMessageHeaderEncoder headerEncoder = new CustomMessageHeaderEncoder();

        final CustomCarEncoder car = new CustomCarEncoder();
        car.wrapAndApplyHeader(buffer, offset, headerEncoder);
        headerEncoder.extra(1234_5678L);
        car.wrap(buffer, offset);
        car.serialNumber(1234);
        car.modelYear(2013);
        car.available(BooleanType.T);
        car.code(Model.A);
        car.vehicleCode("ABCDEF");

        for (int i = 0, size = CarEncoder.someNumbersLength(); i < size; i++)
        {
            car.someNumbers(i, i);
        }

        car.extras().cruiseControl(true).sportsPack(true);

        car.cupHolderCount((short)119);

        car.engine().capacity(2000)
                .numCylinders((short)4)
                .manufacturerCode("ABC")
                .efficiency((byte)35)
                .boosterEnabled(BooleanType.T)
                .booster().boostType(BoostType.NITROUS)
                .horsePower((short)200);

        final CustomCarEncoder.FuelFiguresEncoder fuelFigures = car.fuelFiguresCount(3);
        fuelFigures.next()
                .speed(30)
                .mpg(35.9f)
                .usageDescription("this is a description");

        fuelFigures.next()
                .speed(55)
                .mpg(49.0f)
                .usageDescription("this is a description");

        fuelFigures.next()
                .speed(75)
                .mpg(40.0f)
                .usageDescription("this is a description");

        final CustomCarEncoder.PerformanceFiguresEncoder perfFigures = car.performanceFiguresCount(2);

        perfFigures.next()
                .octaneRating((short)95);

        CustomCarEncoder.PerformanceFiguresEncoder.AccelerationEncoder acceleration =
            perfFigures.accelerationCount(3);

        acceleration.next()
                .mph(30)
                .seconds(4.0f);

        acceleration.next()
                .mph(60)
                .seconds(7.5f);

        acceleration.next()
                .mph(100)
                .seconds(12.2f);

        perfFigures.next()
                .octaneRating((short)99);

        acceleration = perfFigures.accelerationCount(3);

        acceleration.next()
                .mph(30)
                .seconds(3.8f);

        acceleration.next()
                .mph(60)
                .seconds(7.1f);

        acceleration.next()
                .mph(100)
                .seconds(11.8f);

        car.manufacturer("Ford");
        car.model("Fiesta");
        car.activationCode("1234");

        return car.limit();
    }
}

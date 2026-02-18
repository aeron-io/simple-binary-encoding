use examples_extension as ext;
use examples_uk_co_real_logic_sbe_benchmarks_fix as mqfix;
use ext::{
    boolean_type::BooleanType as ExtBooleanType,
    boost_type::BoostType as ExtBoostType,
    car_codec::{
        encoder::FuelFiguresEncoder as ExtFuelFiguresEncoder, CarDecoder as ExtCarDecoder,
        CarEncoder as ExtCarEncoder,
    },
    message_header_codec::{self as ext_header, MessageHeaderDecoder as ExtHeaderDecoder},
    model::Model as ExtModel,
    optional_extras::OptionalExtras as ExtOptionalExtras,
    ReadBuf as ExtReadBuf, SbeResult as ExtSbeResult, WriteBuf as ExtWriteBuf,
};
use i895::{
    issue_895_codec::{Issue895Decoder, Issue895Encoder},
    message_header_codec::{self as i895_header, MessageHeaderDecoder as I895HeaderDecoder},
    ReadBuf as I895ReadBuf, WriteBuf as I895WriteBuf,
};
use i972::{
    issue_972_codec::{Issue972Decoder, Issue972Encoder},
    message_header_codec::{self as i972_header, MessageHeaderDecoder as I972HeaderDecoder},
    Either as I972Either, ReadBuf as I972ReadBuf, WriteBuf as I972WriteBuf,
};
use issue_895 as i895;
use issue_972 as i972;
use mqfix::{
    mass_quote_codec::{
        encoder::{QuoteEntriesEncoder, QuoteSetsEncoder},
        MassQuoteDecoder, MassQuoteEncoder,
    },
    message_header_codec::{self as mqfix_header, MessageHeaderDecoder as MassQuoteHeaderDecoder},
    ReadBuf as MassQuoteReadBuf, WriteBuf as MassQuoteWriteBuf,
};
use oen::{
    enum_type::EnumType as OenEnumType,
    message_header_codec::{
        self as oen_header, MessageHeaderDecoder as OptionalEnumNullifyHeaderDecoder,
    },
    optional_encoding_enum_type::OptionalEncodingEnumType as OenOptionalEncodingEnumType,
    optional_enum_nullify_codec::{OptionalEnumNullifyDecoder, OptionalEnumNullifyEncoder},
    ReadBuf as OenReadBuf, WriteBuf as OenWriteBuf,
};
use optional_enum_nullify as oen;

type TestResult = Result<(), Box<dyn std::error::Error>>;

macro_rules! create_encoder_with_header_parent {
    ($buffer:expr, $encoder_ty:ty, $write_buf_ty:ty, $encoded_len:expr) => {{
        let encoder = <$encoder_ty>::default()
            .wrap(<$write_buf_ty>::new($buffer.as_mut_slice()), $encoded_len);
        let mut header = encoder.header(0);
        header.parent().unwrap()
    }};
}

fn create_issue_895_encoder(buffer: &mut Vec<u8>) -> Issue895Encoder<'_> {
    create_encoder_with_header_parent!(
        buffer,
        Issue895Encoder<'_>,
        I895WriteBuf<'_>,
        i895_header::ENCODED_LENGTH
    )
}

fn create_issue_972_encoder(buffer: &mut Vec<u8>) -> Issue972Encoder<'_> {
    create_encoder_with_header_parent!(
        buffer,
        Issue972Encoder<'_>,
        I972WriteBuf<'_>,
        i972_header::ENCODED_LENGTH
    )
}

fn create_optional_enum_nullify_encoder(buffer: &mut Vec<u8>) -> OptionalEnumNullifyEncoder<'_> {
    create_encoder_with_header_parent!(
        buffer,
        OptionalEnumNullifyEncoder<'_>,
        OenWriteBuf<'_>,
        oen_header::ENCODED_LENGTH
    )
}

fn create_mass_quote_encoder(buffer: &mut Vec<u8>) -> MassQuoteEncoder<'_> {
    create_encoder_with_header_parent!(
        buffer,
        MassQuoteEncoder<'_>,
        MassQuoteWriteBuf<'_>,
        mqfix_header::ENCODED_LENGTH
    )
}

fn decode_issue_895(buffer: &[u8]) -> Issue895Decoder<'_> {
    let buf = I895ReadBuf::new(buffer);
    let header = I895HeaderDecoder::default().wrap(buf, 0);
    Issue895Decoder::default().header(header, 0)
}

fn decode_issue_972(buffer: &[u8]) -> Issue972Decoder<'_> {
    let buf = I972ReadBuf::new(buffer);
    let header = I972HeaderDecoder::default().wrap(buf, 0);
    Issue972Decoder::default().header(header, 0)
}

fn decode_optional_enum_nullify(buffer: &[u8]) -> OptionalEnumNullifyDecoder<'_> {
    let buf = OenReadBuf::new(buffer);
    let header = OptionalEnumNullifyHeaderDecoder::default().wrap(buf, 0);
    OptionalEnumNullifyDecoder::default().header(header, 0)
}

fn encode_extension_car_for_optional_field_test(
    cup_holder_count: Option<u8>,
    nullify: bool,
) -> ExtSbeResult<Vec<u8>> {
    use examples_extension::Encoder;
    let mut buffer = vec![0u8; 256];
    let mut car = ExtCarEncoder::default();
    let mut extras = ExtOptionalExtras::default();

    car = car.wrap(
        ExtWriteBuf::new(buffer.as_mut_slice()),
        ext_header::ENCODED_LENGTH,
    );
    car = car.header(0).parent()?;

    car.cup_holder_count_opt(cup_holder_count);
    if nullify {
        // When enabled, tests expect optional message fields to be encoded as "absent".
        car.nullify_optional_fields();
    }
    car.serial_number(1234);
    car.model_year(2013);
    car.available(ExtBooleanType::T);
    car.code(ExtModel::A);
    car.some_numbers(&[0, 1, 2, 3]);
    car.vehicle_code(b"abcdef");

    extras.set_cruise_control(true);
    extras.set_sports_pack(true);
    extras.set_sun_roof(false);
    car.extras(extras);

    let mut engine = car.engine_encoder();
    engine.capacity(2000);
    engine.num_cylinders(4);
    engine.manufacturer_code(b"123");
    engine.efficiency(35);
    engine.booster_enabled(ExtBooleanType::T);
    let mut booster = engine.booster_encoder();
    booster.boost_type(ExtBoostType::NITROUS);
    booster.horse_power(200);
    engine = booster.parent()?;
    let _car = engine.parent()?;

    Ok(buffer)
}

macro_rules! with_decoded_extension_car {
    ($buffer:expr, $car:ident, $body:block) => {{
        let buf = ExtReadBuf::new($buffer);
        let header = ExtHeaderDecoder::default().wrap(buf, 0);
        let $car = ExtCarDecoder::default().header(header, 0);
        $body
    }};
}

#[test]
fn extension_encoder_cup_holder_count_opt_sets_some_and_none() -> TestResult {
    let buffer = encode_extension_car_for_optional_field_test(Some(7), false)?;
    with_decoded_extension_car!(buffer.as_slice(), car, {
        assert_eq!(Some(7), car.cup_holder_count());
    });

    let buffer = encode_extension_car_for_optional_field_test(None, false)?;
    with_decoded_extension_car!(buffer.as_slice(), car, {
        assert_eq!(None, car.cup_holder_count());
    });

    Ok(())
}

#[test]
fn extension_encoder_nullify_optional_fields_sets_cup_holder_count_to_none() -> TestResult {
    // Control: without nullify, the explicit optional value should remain present.
    let buffer = encode_extension_car_for_optional_field_test(Some(7), false)?;
    with_decoded_extension_car!(buffer.as_slice(), car, {
        assert_eq!(Some(7), car.cup_holder_count());
    });

    // Contract under test: nullify marks optional fields as absent in the encoded payload.
    let buffer = encode_extension_car_for_optional_field_test(Some(7), true)?;
    with_decoded_extension_car!(buffer.as_slice(), car, {
        assert_eq!(None, car.cup_holder_count());
    });

    Ok(())
}

#[test]
fn nullify_on_composite_and_group_without_optional_fields_has_no_effect() -> TestResult {
    use examples_extension::Encoder;
    let mut buffer = vec![0u8; 256];
    let mut car = ExtCarEncoder::default();
    let mut fuel_figures = ExtFuelFiguresEncoder::default();
    let mut extras = ExtOptionalExtras::default();

    car = car.wrap(
        ExtWriteBuf::new(buffer.as_mut_slice()),
        ext_header::ENCODED_LENGTH,
    );
    car = car.header(0).parent()?;

    car.serial_number(1234);
    car.model_year(2013);
    car.available(ExtBooleanType::T);
    car.code(ExtModel::A);
    car.some_numbers(&[0, 1, 2, 3]);
    car.vehicle_code(b"abcdef");

    extras.set_cruise_control(true);
    extras.set_sports_pack(true);
    extras.set_sun_roof(false);
    car.extras(extras);

    let mut engine = car.engine_encoder();
    engine.capacity(2000);
    engine.num_cylinders(4);
    engine.manufacturer_code(b"123");
    engine.efficiency(35);
    engine.booster_enabled(ExtBooleanType::T);
    // Engine has no optional primitive scalar fields, so nullify must be a no-op.
    engine.nullify_optional_fields();
    let mut booster = engine.booster_encoder();
    booster.boost_type(ExtBoostType::NITROUS);
    booster.horse_power(200);
    engine = booster.parent()?;
    car = engine.parent()?;

    fuel_figures = car.fuel_figures_encoder(1, fuel_figures);
    assert_eq!(Some(0), fuel_figures.advance()?);
    fuel_figures.speed(77);
    fuel_figures.mpg(12.5);
    // FuelFigures group also has no optional primitive scalar fields; expect no change.
    fuel_figures.nullify_optional_fields();
    let _car = fuel_figures.parent()?;

    with_decoded_extension_car!(buffer.as_slice(), car, {
        let mut engine = car.engine_decoder();
        assert_eq!(2000, engine.capacity());
        assert_eq!(4, engine.num_cylinders());
        assert_eq!(35, engine.efficiency());

        let mut fuel_figures = engine.parent().unwrap().fuel_figures_decoder();
        assert_eq!(Some(0), fuel_figures.advance().unwrap());
        assert_eq!(77, fuel_figures.speed());
        assert_eq!(12.5, fuel_figures.mpg());
    });

    Ok(())
}

#[test]
fn opt_setters_work_as_expected() -> TestResult {
    let mut some_buf = vec![0u8; 256];
    let mut encoder = create_issue_895_encoder(&mut some_buf);
    encoder.optional_float_opt(Some(2.07));
    encoder.optional_double_opt(Some(4.12));

    let decoder = decode_issue_895(some_buf.as_slice());
    assert_eq!(Some(2.07), decoder.optional_float());
    assert_eq!(Some(4.12), decoder.optional_double());

    let mut none_buf = vec![0u8; 256];
    let mut encoder = create_issue_895_encoder(&mut none_buf);
    encoder.optional_float_opt(None);
    encoder.optional_double_opt(None);

    let decoder = decode_issue_895(none_buf.as_slice());
    assert_eq!(None, decoder.optional_float());
    assert_eq!(None, decoder.optional_double());

    Ok(())
}

#[test]
fn nullify_optional_fields_sets_all_to_none() -> TestResult {
    use issue_895::Encoder;
    let mut buf = vec![0u8; 256];
    let mut encoder = create_issue_895_encoder(&mut buf);
    encoder.optional_float_opt(Some(2.07));
    encoder.optional_double_opt(Some(4.12));
    // Message-level nullify should clear every optional primitive scalar field.
    encoder.nullify_optional_fields();

    let decoder = decode_issue_895(buf.as_slice());
    // Both optional fields are expected to decode as absent after nullify.
    assert_eq!(None, decoder.optional_float());
    assert_eq!(None, decoder.optional_double());

    Ok(())
}

#[test]
fn composite_nullify_optional_fields_sets_fields_to_none() -> TestResult {
    let mut buffer = vec![0u8; 256];
    let mut encoder = create_issue_972_encoder(&mut buffer);
    encoder.old_field(777);
    let mut new_composite_encoder = encoder.new_field_encoder();
    new_composite_encoder.f1(2007);
    new_composite_encoder.f2(2012);
    // Composite-level nullify should clear composite optional primitive fields.
    new_composite_encoder.nullify_optional_fields();
    let _encoder = new_composite_encoder.parent()?;

    let decoder = decode_issue_972(buffer.as_slice());
    match decoder.new_field_decoder() {
        I972Either::Right(composite) => {
            assert_eq!(None, composite.f1());
            assert_eq!(None, composite.f2());
        }
        I972Either::Left(_) => panic!("expected new_field_decoder to return Right(composite)"),
    }

    Ok(())
}

#[test]
fn message_nullify_without_optional_primitive_fields_has_no_effect() -> TestResult {
    use issue_972::Encoder;
    let mut buffer = vec![0u8; 256];
    let mut encoder = create_issue_972_encoder(&mut buffer);
    encoder.old_field(42);
    // Issue972 message has no optional primitive scalar fields; nullify should be a no-op.
    encoder.nullify_optional_fields();

    let decoder = decode_issue_972(buffer.as_slice());
    assert_eq!(42, decoder.old_field());

    Ok(())
}

#[test]
fn group_nullify_optional_fields_sets_optional_fields_to_none() -> TestResult {
    use examples_uk_co_real_logic_sbe_benchmarks_fix::Encoder;
    let mut buffer = vec![0u8; 1024];
    let mass_quote = create_mass_quote_encoder(&mut buffer);
    let mut quote_sets = QuoteSetsEncoder::default();
    let mut quote_entries = QuoteEntriesEncoder::default();

    quote_sets = mass_quote.quote_sets_encoder(1, quote_sets);
    assert_eq!(Some(0), quote_sets.advance()?);
    quote_sets.tot_quote_entries(1);

    quote_entries = quote_sets.quote_entries_encoder(1, quote_entries);
    assert_eq!(Some(0), quote_entries.advance()?);
    quote_entries.security_id_opt(Some(111));
    quote_entries.bid_size_opt(Some(222));
    quote_entries.offer_size_opt(Some(333));
    // Group-level nullify should clear all optional primitive scalar fields in the entry.
    quote_entries.nullify_optional_fields();

    quote_sets = quote_entries.parent()?;
    let _mass_quote = quote_sets.parent()?;

    let buf = MassQuoteReadBuf::new(buffer.as_slice());
    let header = MassQuoteHeaderDecoder::default().wrap(buf, 0);
    let decoder = MassQuoteDecoder::default().header(header, 0);

    let mut quote_sets = decoder.quote_sets_decoder();
    assert_eq!(Some(0), quote_sets.advance()?);
    let mut quote_entries = quote_sets.quote_entries_decoder();
    assert_eq!(Some(0), quote_entries.advance()?);
    // Optional fields must decode as absent once group nullify has been applied.
    assert_eq!(None, quote_entries.security_id());
    assert_eq!(None, quote_entries.bid_size());
    assert_eq!(None, quote_entries.offer_size());

    Ok(())
}

#[test]
fn nullify_optional_fields_sets_optional_enum_field_to_null_value() -> TestResult {
    use oen::Encoder;
    let mut control_buffer = vec![0u8; 256];
    let mut control_encoder = create_optional_enum_nullify_encoder(&mut control_buffer);
    // Optionality is declared on the field, matching IR optional enum semantics.
    control_encoder.optional_enum_opt(Some(OenEnumType::One));
    // This field is required, even if its enum type encoding is optional.
    control_encoder.required_enum_from_optional_type(OenOptionalEncodingEnumType::Alpha);

    let control_decoder = decode_optional_enum_nullify(control_buffer.as_slice());
    assert_eq!(OenEnumType::One, control_decoder.optional_enum());
    assert_eq!(
        OenOptionalEncodingEnumType::Alpha,
        control_decoder.required_enum_from_optional_type()
    );

    let mut none_buffer = vec![0u8; 256];
    let mut none_encoder = create_optional_enum_nullify_encoder(&mut none_buffer);
    none_encoder.optional_enum_opt(None);
    none_encoder.required_enum_from_optional_type(OenOptionalEncodingEnumType::Beta);

    let none_decoder = decode_optional_enum_nullify(none_buffer.as_slice());
    assert_eq!(OenEnumType::NullVal, none_decoder.optional_enum());
    assert_eq!(
        OenOptionalEncodingEnumType::Beta,
        none_decoder.required_enum_from_optional_type()
    );

    let mut nullified_buffer = vec![0u8; 256];
    let mut nullified_encoder = create_optional_enum_nullify_encoder(&mut nullified_buffer);
    nullified_encoder.optional_enum_opt(Some(OenEnumType::Two));
    nullified_encoder.required_enum_from_optional_type(OenOptionalEncodingEnumType::Beta);
    // This is the behavior under test: nullify should route through *_opt(None)
    // only for field-optional members, so `optional_enum` becomes NullVal while
    // the required field remains unchanged even if its enum type encoding is optional.
    nullified_encoder.nullify_optional_fields();

    let nullified_decoder = decode_optional_enum_nullify(nullified_buffer.as_slice());
    assert_eq!(OenEnumType::NullVal, nullified_decoder.optional_enum());
    assert_eq!(
        OenOptionalEncodingEnumType::Beta,
        nullified_decoder.required_enum_from_optional_type()
    );

    Ok(())
}

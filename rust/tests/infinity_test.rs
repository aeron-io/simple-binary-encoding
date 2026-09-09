use infinity_test::{
    float_constants_codec::FloatConstantsDecoder,
    infinity_values_codec::{self, InfinityValuesDecoder, InfinityValuesEncoder},
    ReadBuf, WriteBuf,
};

#[test]
fn should_render_width_and_sign_correct_constants() {
    let constants = FloatConstantsDecoder::default();
    assert_eq!(constants.positive().to_bits(), 0x7f800000);
    assert_eq!(constants.negative().to_bits(), 0xff800000);

    let values = InfinityValuesDecoder::default();
    assert_eq!(values.float_positive_infinity().to_bits(), 0x7f800000);
    assert_eq!(values.float_negative_infinity().to_bits(), 0xff800000);
    assert_eq!(
        values.double_positive_infinity().to_bits(),
        0x7ff0000000000000
    );
    assert_eq!(
        values.double_negative_infinity().to_bits(),
        0xfff0000000000000
    );
}

#[test]
fn should_encode_and_decode_width_and_sign_correct_optional_null_values() {
    let mut buffer = vec![0u8; infinity_values_codec::SBE_BLOCK_LENGTH as usize];
    {
        let mut encoder =
            InfinityValuesEncoder::default().wrap(WriteBuf::new(buffer.as_mut_slice()), 0);
        encoder
            .float_positive_null_opt(None)
            .float_negative_null_opt(None)
            .double_positive_null_opt(None)
            .double_negative_null_opt(None);
    }

    let decoder = InfinityValuesDecoder::default().wrap(
        ReadBuf::new(&buffer),
        0,
        infinity_values_codec::SBE_BLOCK_LENGTH,
        0,
    );
    assert!(decoder.float_positive_null().is_none());
    assert!(decoder.float_negative_null().is_none());
    assert!(decoder.double_positive_null().is_none());
    assert!(decoder.double_negative_null().is_none());
}

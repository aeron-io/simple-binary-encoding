use issue_1060::{
    issue_1060_codec,
    issue_1060_codec::{Issue1060Decoder, Issue1060Encoder},
    *,
};

#[test]
fn decode_optional_primitive_field() {
    let mut buffer = vec![0u8; 256];

    // Create a message with `field` set.
    let mut encoder = Issue1060Encoder::default().wrap(
        WriteBuf::new(&mut buffer),
        message_header_codec::ENCODED_LENGTH,
    );
    encoder.field(10);

    // Drop encoder to drop the mut reference for the buffer.
    drop(encoder);

    // Create a decoder for the message, but stating that the version is 1, instead of 2
    // which is the acting version for `field`. Thus, `field()` must return `None`.
    let decoder = Issue1060Decoder::default().wrap(
        ReadBuf::new(&mut buffer),
        message_header_codec::ENCODED_LENGTH,
        issue_1060_codec::SBE_BLOCK_LENGTH,
        1,
    );
    assert!(decoder.field().is_none());

    // Create a decoder for the message, but stating that the version is 1, instead of 2
    // which is the acting version for `field`. Thus, `field()` must return `None`.
    let decoder = Issue1060Decoder::default().wrap(
        ReadBuf::new(&mut buffer),
        message_header_codec::ENCODED_LENGTH,
        issue_1060_codec::SBE_BLOCK_LENGTH,
        2,
    );
    assert_eq!(decoder.field(), Some(10));
}

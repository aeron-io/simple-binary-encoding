use issue_1116::{
    foo_bar::FooBar,
    issue_1116_codec::{self, Issue1116Decoder, Issue1116Encoder},
    message_header_codec, ReadBuf, WriteBuf,
};

/// The schema declares `nullValue="254"` on the type `FooBar` encodes to, so the generated null
/// value has to be 254 rather than the uint8 default of 255. The Java, C++ and Go generators all
/// emit 254 for this schema.
#[test]
fn enum_null_value_comes_from_the_schema() {
    // The discriminant, which is what the encoder writes.
    assert_eq!(FooBar::NullVal as u8, 254);

    // The `From` conversion, which is generated separately from the discriminant.
    assert_eq!(u8::from(FooBar::NullVal), 254);

    assert_eq!(FooBar::from(254), FooBar::NullVal);
}

/// A peer decoding this message in another language reads the schema's null value off the wire, so
/// that is the byte the Rust encoder has to write.
#[test]
fn encodes_and_decodes_the_schema_null_value() {
    let mut buffer = vec![0u8; 256];

    {
        let mut encoder = Issue1116Encoder::default().wrap(
            WriteBuf::new(&mut buffer),
            message_header_codec::ENCODED_LENGTH,
        );
        encoder.foo_bar(FooBar::NullVal);
    }

    assert_eq!(buffer[message_header_codec::ENCODED_LENGTH], 254);

    let decoder = Issue1116Decoder::default().wrap(
        ReadBuf::new(&buffer),
        message_header_codec::ENCODED_LENGTH,
        issue_1116_codec::SBE_BLOCK_LENGTH,
        0,
    );
    assert_eq!(decoder.foo_bar(), FooBar::NullVal);
}

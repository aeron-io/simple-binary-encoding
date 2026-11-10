use custom_header::{
    custom_message_header_codec::{self, CustomMessageHeaderDecoder},
    test_message_codec::{TestMessageDecoder, TestMessageEncoder},
    ReadBuf, SbeResult, WriteBuf,
};

#[test]
fn encode_and_decode_with_custom_header() -> SbeResult<()> {
    let mut buffer = [0u8; 64];
    let offset = 5;
    assert_eq!(12, custom_message_header_codec::ENCODED_LENGTH);

    let encoder = TestMessageEncoder::default().wrap(
        WriteBuf::new(&mut buffer),
        offset + custom_message_header_codec::ENCODED_LENGTH,
    );
    let mut header = encoder.header(offset);
    header.extra(123);
    let mut encoder = header.parent()?;
    encoder.value(456);

    assert_eq!(
        [8, 0, 7, 0, 42, 0, 3, 0, 123, 0, 0, 0],
        buffer[offset..offset + 12]
    );
    let header = CustomMessageHeaderDecoder::default().wrap(ReadBuf::new(&buffer), offset);
    assert_eq!(8, header.block_length());
    assert_eq!(7, header.template_id());
    assert_eq!(42, header.schema_id());
    assert_eq!(3, header.version());
    assert_eq!(123, header.extra());

    let decoder = TestMessageDecoder::default().header(header, offset);
    assert_eq!(456, decoder.value());
    assert_eq!(8, decoder.encoded_length());
    Ok(())
}

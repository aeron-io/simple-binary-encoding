use sbe_tests::{
    message_header_codec::MessageHeaderDecoder,
    test_message_1_codec::{
        TestMessage1Decoder, SBE_BLOCK_LENGTH, SBE_SCHEMA_ID, SBE_SCHEMA_VERSION, SBE_TEMPLATE_ID,
    },
};
use sbe_tests::{
    WriteBuf,
    {message_header_codec, test_message_1_codec::TestMessage1Encoder, Encoder, ReadBuf},
};

#[test]
fn should_limit_var_data_length() {
    // encode...
    let mut buffer = vec![0u8; 1024];
    let mut encoder = TestMessage1Encoder::default();
    encoder = encoder.wrap(
        WriteBuf::new(buffer.as_mut_slice()),
        message_header_codec::ENCODED_LENGTH,
    );
    encoder = encoder.header(0).parent().unwrap();

    let password: String = (0..1024).map(|_| 'x' as char).collect();
    encoder.encrypted_new_password(password.as_bytes());
    assert_eq!(263, encoder.get_limit());

    // decode...
    let buf = ReadBuf::new(buffer.as_slice());
    let header = MessageHeaderDecoder::default().wrap(buf, 0);
    assert_eq!(SBE_BLOCK_LENGTH, header.block_length());
    assert_eq!(SBE_SCHEMA_VERSION, header.version());
    assert_eq!(SBE_TEMPLATE_ID, header.template_id());
    assert_eq!(SBE_SCHEMA_ID, header.schema_id());

    let mut decoder = TestMessage1Decoder::default().header(header, 0);
    let coord = decoder.encrypted_new_password_decoder();
    let password = String::from_utf8_lossy(decoder.encrypted_new_password_slice(coord));
    assert_eq!(254, password.len());
    password
        .as_bytes()
        .iter()
        .for_each(|x| assert_eq!(b'x', *x));
}

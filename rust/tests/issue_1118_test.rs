use issue_1118::{
    flags::Flags,
    issue_1118_codec::{self, Issue1118Decoder, Issue1118Encoder},
    message_header_codec, ReadBuf, WriteBuf,
};

/// Choices named after a Rust keyword are reached through `get_` and `set_` prefixed accessors,
/// so the prefixed name is a plain identifier and the choice name is not escaped. This module
/// failing to compile is itself the regression this guards, since `get_r#type` does not parse.
#[test]
fn keyword_named_choices_have_usable_accessors() {
    let mut flags = Flags::new(0);

    flags.set_type(true);
    assert!(flags.get_type());
    assert!(!flags.get_match());
    assert!(!flags.get_ordinary());

    flags.set_match(true);
    flags.set_type(false);
    assert!(!flags.get_type());
    assert!(flags.get_match());

    flags.clear();
    assert_eq!(flags.0, 0);
}

/// The choice name is written into `Debug` as a label, so it should not carry the raw identifier
/// escaping either.
#[test]
fn debug_uses_the_unescaped_choice_names() {
    let mut flags = Flags::new(0);
    flags.set_type(true).set_ordinary(true);

    assert_eq!(
        format!("{flags:?}"),
        "Flags[type(0)=true,match(1)=false,ordinary(2)=true]"
    );
}

#[test]
fn round_trips_a_keyword_named_choice() {
    let mut buffer = vec![0u8; 256];

    {
        let mut encoder = Issue1118Encoder::default().wrap(
            WriteBuf::new(&mut buffer),
            message_header_codec::ENCODED_LENGTH,
        );
        let mut flags = Flags::new(0);
        flags.set_match(true);
        encoder.flags(flags);
    }

    let decoder = Issue1118Decoder::default().wrap(
        ReadBuf::new(&buffer),
        message_header_codec::ENCODED_LENGTH,
        issue_1118_codec::SBE_BLOCK_LENGTH,
        0,
    );
    let flags = decoder.flags();
    assert!(flags.get_match());
    assert!(!flags.get_type());
}

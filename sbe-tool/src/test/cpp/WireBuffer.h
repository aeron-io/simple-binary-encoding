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
#ifndef _SBE_TEST_WIRE_BUFFER_H
#define _SBE_TEST_WIRE_BUFFER_H

#include <cstdint>
#include <string>
#include <vector>

/*
 * Buffers are built byte by byte in little-endian order and sized exactly to the bytes written, so a read past
 * the end of the buffer is a read past the end of the allocation.
 */
class WireBuffer
{
public:
    WireBuffer &u8(const std::uint8_t value)
    {
        m_bytes.push_back(static_cast<char>(value));
        return *this;
    }

    WireBuffer &u16(const std::uint16_t value)
    {
        return putLittleEndian(value, 2);
    }

    WireBuffer &u32(const std::uint32_t value)
    {
        return putLittleEndian(value, 4);
    }

    WireBuffer &u64(const std::uint64_t value)
    {
        return putLittleEndian(value, 8);
    }

    WireBuffer &bytes(const std::string &value)
    {
        m_bytes.insert(m_bytes.end(), value.begin(), value.end());
        return *this;
    }

    char *data()
    {
        return m_bytes.data();
    }

    std::uint64_t size() const
    {
        return m_bytes.size();
    }

private:
    WireBuffer &putLittleEndian(std::uint64_t value, const int length)
    {
        for (int i = 0; i < length; i++)
        {
            m_bytes.push_back(static_cast<char>(value & 0xFF));
            value >>= 8;
        }
        return *this;
    }

    std::vector<char> m_bytes;
};

#endif

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

/**
 * Header fields of the last message decoded on a {@link SbeJsonDecoder}. One instance per decoder, overwritten
 * by every decode (including decodes that fail after the header was read), so it is thread-confined like the
 * decoder and must be read before the next decode.
 */
public final class HeaderView
{
    private int templateId;
    private int schemaId;
    private int actingVersion;
    private int blockLength;
    private boolean populated;

    HeaderView()
    {
    }

    void set(final int templateId, final int schemaId, final int actingVersion, final int blockLength)
    {
        this.templateId = templateId;
        this.schemaId = schemaId;
        this.actingVersion = actingVersion;
        this.blockLength = blockLength;
        this.populated = true;
    }

    void clear()
    {
        populated = false;
    }

    /**
     * Whether a header has been read on this decoder since it was created.
     *
     * @return true once the first decode has read a header.
     */
    public boolean populated()
    {
        return populated;
    }

    /**
     * Template id from the header of the last decode.
     *
     * @return the template id.
     */
    public int templateId()
    {
        return templateId;
    }

    /**
     * Schema id from the header of the last decode.
     *
     * @return the schema id.
     */
    public int schemaId()
    {
        return schemaId;
    }

    /**
     * Acting (schema) version from the header of the last decode.
     *
     * @return the acting version.
     */
    public int actingVersion()
    {
        return actingVersion;
    }

    /**
     * Root block length from the header of the last decode.
     *
     * @return the acting block length of the root.
     */
    public int blockLength()
    {
        return blockLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "HeaderView{" +
            "templateId=" + templateId +
            ", schemaId=" + schemaId +
            ", actingVersion=" + actingVersion +
            ", blockLength=" + blockLength +
            ", populated=" + populated +
            '}';
    }
}

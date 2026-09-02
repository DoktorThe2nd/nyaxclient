package com.doktorthe2nd.nyax.net;

import com.doktorthe2nd.nyax.Consts;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LZ4 {
    public static final LZ4Factory factory = LZ4Factory.fastestInstance();
    public static final LZ4Compressor compressor = factory.fastCompressor();
    public static final LZ4FastDecompressor decompressor = factory.fastDecompressor();

    public static byte[] compress(byte[] raw) {
       /* int maxCompressedLength = compressor.maxCompressedLength(raw.length);
        byte[] compressedData = new byte[maxCompressedLength];
        compressor.compress(
                raw, 0, raw.length,
                compressedData, 0, maxCompressedLength
        );
        return compressedData;*/
        return compressBlock(raw);
    }

    public static byte[] decompress(byte[] compressed) {
        return decompress(compressed, Consts.max_compressed_size);
    }
    public static byte[] decompress(byte[] compressed, int max_size) {
        byte[] decompressedData = new byte[max_size];
        int bytesRead = decompressor.decompress(
                compressed, 0,
                decompressedData, 0,
                max_size
        );
        byte[] smallerData = new byte[bytesRead];
        System.arraycopy(decompressedData, 0, smallerData, 0, bytesRead);
        return smallerData;
    }

    public static byte[] decompressBlock(byte[] src) throws IOException {
        return decompressBlock(src, Consts.max_compressed_size);
    }
    public static byte[] decompressBlock(byte[] src, int maxOutput) throws IOException {
        List<Byte> dst = new ArrayList<>();
        int pos = 0;
        int srcLen = src.length;

        while (pos < srcLen) {
            int token = src[pos] & 0xFF;
            pos++;

            // Длина литерала
            int litLen = token >>> 4;
            if (litLen == 15) {
                while (pos < srcLen) {
                    int b = src[pos] & 0xFF;
                    pos++;
                    litLen += b;
                    if (b != 255) break;
                }
            }

            // Копируем литералы
            if (litLen > 0) {
                if (pos + litLen > srcLen) {
                    throw new IllegalArgumentException("LZ4: literal length out of bounds");
                }
                // Проверка на превышение maxOutput
                if (dst.size() + litLen > maxOutput) {
                    throw new IllegalArgumentException("LZ4: output too large");
                }
                for (int i = 0; i < litLen; i++) {
                    dst.add(src[pos + i]);
                }
                pos += litLen;
            }

            if (pos >= srcLen) {
                break;
            }

            // Смещение
            if (pos + 1 >= srcLen) {
                throw new IllegalArgumentException("LZ4: incomplete offset");
            }
            int offset = (src[pos] & 0xFF) | ((src[pos + 1] & 0xFF) << 8);
            pos += 2;

            if (offset == 0) {
                throw new IllegalArgumentException("LZ4: zero offset");
            }

            // Длина совпадения
            int matchLen = (token & 0x0F) + 4;
            if ((token & 0x0F) == 0x0F) {
                while (pos < srcLen) {
                    int b = src[pos] & 0xFF;
                    pos++;
                    matchLen += b;
                    if (b != 255) break;
                }
            }

            int matchPos = dst.size() - offset;

            if (matchPos < 0) {
                throw new IllegalArgumentException("LZ4: match out of bounds");
            }

            // Копируем совпадение
            if (dst.size() + matchLen > maxOutput) {
                throw new IllegalArgumentException("LZ4: output too large");
            }
            for (int i = 0; i < matchLen; i++) {
                dst.add(dst.get(matchPos + (i % offset)));
            }
        }

        // Преобразуем List<Byte> в byte[]
        byte[] result = new byte[dst.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = dst.get(i);
        }
        return result;
    }

    public static byte[] compressBlock(byte[] src) {
        if (src.length == 0) return new byte[0];

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int srcLen = src.length;
        int anchor = 0;
        int i = 0;

        // Hash table: 2^16 entries, stores positions
        int HASH_BITS = 16;
        int HASH_SIZE = 1 << HASH_BITS;
        int[] table = new int[HASH_SIZE];
        Arrays.fill(table, -1);

        while (i + 3 < srcLen) { // need at least 4 bytes for a match
            // Compute 4-byte hash
            int h = ((src[i] & 0xFF) | ((src[i + 1] & 0xFF) << 8) |
                    ((src[i + 2] & 0xFF) << 16) | ((src[i + 3] & 0xFF) << 24));
            h = (h * 0x9E3779B1) >>> (32 - HASH_BITS);

            int candidate = table[h];
            table[h] = i;  // store current position

            if (candidate >= 0 && (i - candidate) <= 65535) {
                // Try to extend match
                int maxMatch = srcLen - i;
                int maxCandidate = srcLen - candidate;
                int maxPossible = Math.min(maxMatch, maxCandidate);
                int matchLen = 0;
                while (matchLen < maxPossible &&
                        src[candidate + matchLen] == src[i + matchLen]) {
                    matchLen++;
                }

                if (matchLen >= 4) {
                    int litLen = i - anchor;
                    int matchLenCode = matchLen - 4; // for token low nibble

                    // Write token
                    int token = 0;
                    if (litLen >= 15) {
                        token = 0xF0; // literal length = 15 (extended)
                    } else {
                        token = (litLen << 4) & 0xF0;
                    }
                    if (matchLenCode >= 15) {
                        token |= 0x0F; // match length = 19 (extended)
                    } else {
                        token |= matchLenCode;
                    }
                    out.write(token);

                    // Write extended literal length
                    if (litLen >= 15) {
                        int len = litLen - 15;
                        while (len >= 255) {
                            out.write(255);
                            len -= 255;
                        }
                        out.write(len);
                    }

                    // Write literals
                    out.write(src, anchor, litLen);

                    // Write offset (little-endian, 2 bytes)
                    int offset = i - candidate;
                    out.write(offset & 0xFF);
                    out.write((offset >> 8) & 0xFF);

                    // Write extended match length
                    if (matchLenCode >= 15) {
                        int len = matchLenCode - 15;
                        while (len >= 255) {
                            out.write(255);
                            len -= 255;
                        }
                        out.write(len);
                    }

                    // Move forward
                    i += matchLen;
                    anchor = i;
                    continue;
                }
            }
            i++; // no match found, advance one position
        }

        // Write remaining literals (no match)
        int remaining = srcLen - anchor;
        if (remaining > 0) {
            int token = 0;
            if (remaining >= 15) {
                token = 0xF0;
            } else {
                token = (remaining << 4) & 0xF0;
            }
            out.write(token);

            if (remaining >= 15) {
                int len = remaining - 15;
                while (len >= 255) {
                    out.write(255);
                    len -= 255;
                }
                out.write(len);
            }
            out.write(src, anchor, remaining);
        }

        return out.toByteArray();
    }
}

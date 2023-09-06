package msum;

import java.io.IOException;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class MarrySum {
    private static final int PKG_MAGIC = 0x7f434e54;
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    private static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public static String getChecksum(File f) {
        try (
            var file = new RandomAccessFile(f.getAbsolutePath(), "r");
            var channel = file.getChannel();) {
            var buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, 0x1000);

            var magic = buf.getInt();
            if (magic != PKG_MAGIC)
                return "-";

            buf.position(0x10);
            var entry_count = buf.getInt();
            buf.position(0x18);
            var table_offset = buf.getInt();
            buf.position(0x74);
            var content_type = buf.getInt();
            if (content_type == 27)
                return "-";
            var content_flags = buf.getInt();

            int target_id;
            switch (content_flags & 0x0F000000) {
                case 0x0A000000:
                    target_id = 0x1001;
                    break;
                case 0x02000000:
                    target_id = 0x1008;
                    break;
                default:
                    return "-";
            }

            buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, table_offset + 20);
            buf.position(table_offset + 16);
            int digest_offset = buf.getInt();
            buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, digest_offset + entry_count * 32);

            for (int i = 1; i < entry_count; i++) {
                buf.position(table_offset + i * 32);
                var id = buf.getInt();
                if (id == target_id) {
                    buf.position(digest_offset + i * 32);
                    byte[] raw = new byte[32];
                    buf.get(raw);
                    return bytesToHex(raw);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "-";
    }
}
